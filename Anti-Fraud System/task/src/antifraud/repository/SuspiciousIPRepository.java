package antifraud.repository;

import antifraud.data.SuspiciousIP;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SuspiciousIPRepository extends CrudRepository<SuspiciousIP, Long> {
    Optional<SuspiciousIP> findByIp(String Ip);
    boolean existsByIp(String Ip);
    List<SuspiciousIP> findAllByOrderByIdAsc();
}
