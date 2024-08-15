package antifraud.service;

import antifraud.data.StolenCard;
import antifraud.data.SuspiciousIP;
import antifraud.data.Transaction;
import antifraud.dto.request.FeedbackPutDTO;
import antifraud.dto.request.StolenCardPostDTO;
import antifraud.dto.request.SuspiciousIPPostDTO;
import antifraud.dto.request.TransactionPostDTO;
import antifraud.dto.response.TransactionPostResponseDTO;
import antifraud.exception.InvalidEntityValueException;
import antifraud.repository.StolenCardRepository;
import antifraud.repository.SuspiciousIPRepository;
import antifraud.repository.TransactionRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static antifraud.config.Constants.ipValidRegEx;
import static antifraud.enums.TransactionEvaluation.*;

@Service
public class AntiFraudService {
    private TransactionRepository transactionRepository;
    private TransactionLimitConfigService transactionLimitConfigService;
    private SuspiciousIPRepository suspiciousIPRepository;
    private StolenCardRepository stolenCardRepository;

    @Autowired
    public AntiFraudService(TransactionRepository transactionRepository,
                            TransactionLimitConfigService transactionLimitConfigService,
                            SuspiciousIPRepository suspiciousIPRepository,
                            StolenCardRepository stolenCardRepository) {
        this.transactionRepository = transactionRepository;
        this.transactionLimitConfigService = transactionLimitConfigService;
        this.suspiciousIPRepository = suspiciousIPRepository;
        this.stolenCardRepository = stolenCardRepository;
    }

    public List<SuspiciousIP> getAllSuspiciousIPs() {
        return suspiciousIPRepository.findAllByOrderByIdAsc();
    }

    public List<StolenCard> getAllStolenCards() {
        return stolenCardRepository.findAllByOrderByIdAsc();
    }

    public List<Transaction> getAllTransactions() { return transactionRepository.findAllByOrderByIdAsc(); }

    public List<Transaction> getAllTransactionsByNumber(String number)
            throws IllegalArgumentException, EntityNotFoundException {
        if(number == null || number.isBlank() ||invalidLuhnNumber(number))
            throw new IllegalArgumentException("Number not Luhn valid.");

        List<Transaction> result = transactionRepository.findAllByNumberOrderByIdAsc(number);

        if(result.isEmpty()) throw new EntityNotFoundException();

        return result;
    }

    public TransactionPostResponseDTO transactionEvaluation(TransactionPostDTO dto) throws ValidationException {
        if(dto.getAmount() <= 0) throw new ValidationException("Not a valid amount.");

        LocalDateTime oneHourAgo = dto.getDate().minusHours(1);
        int regions = transactionRepository.findByRegionFromLastHour(oneHourAgo, dto.getDate(), dto.getRegion()).size();
        int ips = transactionRepository.findByIpFromLastHour(oneHourAgo, dto.getDate(), dto.getIp()).size();
        System.out.println("regions " + regions);
        System.out.println("ips " + ips);

        List<String> flags = new ArrayList<>();
        TransactionPostResponseDTO result = new TransactionPostResponseDTO();
        int evaluation = 0; // Representing ALLOWED, 1 represents MANUAL PROCESSING, 2 represents PROHIBITED
        Long allowedLimit = transactionLimitConfigService.getTransactionLimit(ALLOWED).getTransactionLimit();
        Long manualLimit = transactionLimitConfigService.getTransactionLimit(MANUAL_PROCESSING).getTransactionLimit();

        if(dto.getAmount() > allowedLimit) {
            if(dto.getAmount() != 1000) flags.add("amount");
            if(dto.getAmount() > manualLimit) evaluation = 2;
            else evaluation = 1;
        }
        if(stolenCardRepository.existsByNumber(dto.getNumber())) {
            flags.add("card-number");
            evaluation = 2;
        }
        if(suspiciousIPRepository.existsByIp(dto.getIp())) {
            flags.add("ip");
            evaluation = 2;
        }
        if(ips > 1) {
            flags.add("ip-correlation");
            if(ips > 2) evaluation = 2;
            else if(evaluation != 2) evaluation = 1;
        }
        if(regions > 1) {
            flags.add("region-correlation");
            if(regions > 2) evaluation = 2;
            else if(evaluation != 2) evaluation = 1;
        }

        if(flags.isEmpty())
            result.setInfo("none");
        else
            result.setInfo(String.join(", ", flags));

        switch (evaluation) {
            case 0 -> result.setResult(ALLOWED);
            case 1 -> result.setResult(MANUAL_PROCESSING);
            case 2 -> result.setResult(PROHIBITED);
        }

        transactionRepository.save(new Transaction(dto, result.getResult()));

        return result;
    }

    public SuspiciousIP newSuspiciousIP(SuspiciousIPPostDTO dto) throws EntityExistsException {
        if(suspiciousIPRepository.existsByIp(dto.getIp()))
            throw new EntityExistsException();

        return suspiciousIPRepository.save(new SuspiciousIP(dto));
    }

    public StolenCard newStolenCard(StolenCardPostDTO dto) throws EntityExistsException {
        if(stolenCardRepository.existsByNumber(dto.getNumber()))
            throw new EntityExistsException();

        return stolenCardRepository.save(new StolenCard(dto));
    }

    public Transaction transactionFeedback(FeedbackPutDTO dto)
            throws EntityNotFoundException, InvalidEntityValueException, IllegalArgumentException {
        Transaction transaction = transactionRepository.findById(dto.getTransactionId())
            .orElseThrow(() -> new EntityNotFoundException(("Transaction not found.")));

        if(transaction.getFeedback() != null)
            throw new InvalidEntityValueException("Transaction already had feedback.");

        transactionLimitConfigService.updateTransactionLimit(transaction.getResult(),
            dto.getFeedback(), transaction.getAmount());

        transaction.setFeedback(dto.getFeedback());

        return transactionRepository.save(transaction);
    }

    public SuspiciousIP deleteSuspiciousIP(String ip) throws InvalidParameterException, EntityNotFoundException {
        if(ip == null || ip.isBlank() || !ip.matches(ipValidRegEx))
            throw new InvalidParameterException("IPv4 can't be null, blank or wrong format.");

        SuspiciousIP result = suspiciousIPRepository.findByIp(ip)
                .orElseThrow(EntityNotFoundException::new);

        suspiciousIPRepository.delete(result);

        return result;
    }

    public StolenCard deleteStolenCard(String number) {
        if(number == null || number.isBlank() || invalidLuhnNumber(number))
            throw new InvalidParameterException("IPv4 can't be null, blank or wrong format.");

        StolenCard result = stolenCardRepository.findByNumber(number)
                .orElseThrow(EntityNotFoundException::new);

        stolenCardRepository.delete(result);

        return result;
    }

    static boolean invalidLuhnNumber(String number) {
        int sum = 0;
        boolean alternate = false;
        for (int i = number.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(number.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 != 0);
    }
}
