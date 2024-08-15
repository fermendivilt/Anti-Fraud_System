package antifraud.repository;

import antifraud.data.StolenCard;
import antifraud.data.SuspiciousIP;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface StolenCardRepository extends CrudRepository<StolenCard, Long> {
    Optional<StolenCard> findByNumber(String Number);
    boolean existsByNumber(String Number);
    List<StolenCard> findAllByOrderByIdAsc();
}
