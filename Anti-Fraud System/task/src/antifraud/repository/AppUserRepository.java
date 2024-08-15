package antifraud.repository;

import antifraud.data.AppUser;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends CrudRepository<AppUser, Long> {
    Optional<AppUser> findAppUserByUsername(String username);
    boolean existsAppUserByUsername(String username);
    List<AppUser> findAllByOrderByIdAsc();
}
