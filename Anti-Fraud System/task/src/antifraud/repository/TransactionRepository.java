package antifraud.repository;

import antifraud.data.Transaction;
import antifraud.enums.RegionCode;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    @Query("SELECT t.region, COUNT(t) FROM Transaction t WHERE t.date BETWEEN :lastHour AND :present AND t.region <> :region GROUP BY t.region")
    List<Object[]> findByRegionFromLastHour(@Param("lastHour") LocalDateTime lastHour, @Param("present") LocalDateTime present, @Param("region") RegionCode region);
    @Query("SELECT t.ip, COUNT(t) FROM Transaction t WHERE t.date BETWEEN :lastHour AND :present AND t.ip <> :ip GROUP BY t.ip")
    List<Object[]> findByIpFromLastHour(@Param("lastHour") LocalDateTime lastHour, @Param("present") LocalDateTime present, @Param("ip") String ip);

    List<Transaction> findAllByOrderByIdAsc();

    List<Transaction> findAllByNumberOrderByIdAsc(String number);
}
