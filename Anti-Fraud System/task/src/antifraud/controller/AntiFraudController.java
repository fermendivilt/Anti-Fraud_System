package antifraud.controller;

import antifraud.data.StolenCard;
import antifraud.data.SuspiciousIP;
import antifraud.data.Transaction;
import antifraud.dto.request.FeedbackPutDTO;
import antifraud.dto.request.StolenCardPostDTO;
import antifraud.dto.request.SuspiciousIPPostDTO;
import antifraud.dto.request.TransactionPostDTO;
import antifraud.dto.response.*;
import antifraud.exception.InvalidEntityValueException;
import antifraud.service.AntiFraudService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;
import java.util.List;

@RestController
@RequestMapping("/api/antifraud")
public class AntiFraudController {
    AntiFraudService antiFraudService;

    @Autowired
    public AntiFraudController(AntiFraudService antiFraudService) {
        this.antiFraudService = antiFraudService;
    }

    @GetMapping("/suspicious-ip")
    public ResponseEntity<List<SuspiciousIPPostResponseDTO>> getAllSuspiciousIP() {
        return ResponseEntity.ok(
                antiFraudService.getAllSuspiciousIPs()
                        .stream().map(SuspiciousIPPostResponseDTO::new)
                        .toList());
    }

    @GetMapping("/stolencard")
    public ResponseEntity<List<StolenCardPostResponseDTO>> getAllStolenCard() {
        return ResponseEntity.ok(
            antiFraudService.getAllStolenCards()
                .stream().map(StolenCardPostResponseDTO::new)
                .toList());
    }

    @GetMapping("/history")
    public ResponseEntity<List<TransactionGetResponseDTO>> getAllTransaction() {
        return ResponseEntity.ok(
            antiFraudService.getAllTransactions()
                .stream().map(TransactionGetResponseDTO::new)
                .toList());
    }

    @GetMapping("/history/{number}")
    public ResponseEntity<List<TransactionGetResponseDTO>> getAllTransaction(@PathVariable String number) {
        try {
            return ResponseEntity.ok(
                antiFraudService.getAllTransactionsByNumber(number)
                    .stream().map(TransactionGetResponseDTO::new)
                    .toList());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();

        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/transaction")
    public ResponseEntity<TransactionPostResponseDTO> postTransaction(@RequestBody @Valid TransactionPostDTO dto) {
        try {
            TransactionPostResponseDTO result = antiFraudService.transactionEvaluation(dto);

            return ResponseEntity.ok(result);

        }
        catch (ValidationException ex){
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/stolencard")
    public ResponseEntity<StolenCardPostResponseDTO> postStolenCard(@RequestBody @Valid StolenCardPostDTO dto) {
        try {
            StolenCard result = antiFraudService.newStolenCard(dto);

            return ResponseEntity.ok(new StolenCardPostResponseDTO(result));

        } catch (EntityExistsException e) {
            return ResponseEntity.status(409).build();
        }
    }

    @PostMapping("/suspicious-ip")
    public ResponseEntity<SuspiciousIPPostResponseDTO> postSuspiciousIP(@RequestBody @Valid SuspiciousIPPostDTO dto) {
        try {
            SuspiciousIP result = antiFraudService.newSuspiciousIP(dto);

            return ResponseEntity.ok(new SuspiciousIPPostResponseDTO(result));

        } catch (EntityExistsException e) {
            return ResponseEntity.status(409).build();
        }
    }

    @PutMapping("/transaction")
    public ResponseEntity<FeedbackPutResponseDTO> putFeedback(@RequestBody @Valid FeedbackPutDTO dto) {
        try {
            Transaction result = antiFraudService.transactionFeedback(dto);

            return ResponseEntity.ok(new FeedbackPutResponseDTO(result));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();

        } catch (InvalidEntityValueException e) {
            return ResponseEntity.status(409).build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @DeleteMapping("/suspicious-ip/{ip}")
    public ResponseEntity<SuspiciousIPDeleteResponseDTO> deleteSuspiciousIP(@PathVariable String ip) {
        try {
            SuspiciousIP result = antiFraudService.deleteSuspiciousIP(ip);

            return ResponseEntity.ok(new SuspiciousIPDeleteResponseDTO(result));

        } catch (InvalidParameterException e) {
            return ResponseEntity.badRequest().build();

        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/stolencard/{number}")
    public ResponseEntity<StolenCardDeleteResponseDTO> deleteStolenCard(@PathVariable String number) {
        try {
            StolenCard result = antiFraudService.deleteStolenCard(number);

            return ResponseEntity.ok(new StolenCardDeleteResponseDTO(result));

        } catch (InvalidParameterException e) {
            return ResponseEntity.badRequest().build();

        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
