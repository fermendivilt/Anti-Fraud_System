package antifraud.service;

import antifraud.adapter.AppUserAdapter;
import antifraud.data.AppUser;
import antifraud.dto.request.UserAccessPutDTO;
import antifraud.dto.request.UserRolePutDTO;
import antifraud.enums.NewUserRole;
import antifraud.enums.TransactionEvaluation;
import antifraud.repository.AppUserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;

@Service
public class AppUserDetailsServiceImpl implements UserDetailsService {
    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private static boolean adminAvailable = true;
    private final String ADMINISTRATOR_ROLE_NAME = "ADMINISTRATOR";

    public AppUserDetailsServiceImpl(AppUserRepository repository,
                                     PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository
                .findAppUserByUsername(username)
                .map(AppUserAdapter::new)
                .orElseThrow(() -> new UsernameNotFoundException("Not found"));
    }

    public AppUser newUser(AppUser user) throws EntityExistsException {
        if(repository.existsAppUserByUsername(user.getUsername()))
            throw new EntityExistsException();

        String ignoreUsernameCase = user.getUsername().toLowerCase();
        user.setUsername(ignoreUsernameCase);
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        if(adminAvailable) {
            user.setAuthority("ROLE_" + ADMINISTRATOR_ROLE_NAME);
            user.setAccountNonLocked(true);
            adminAvailable = false;
        }
        else {
            user.setAuthority("ROLE_" + NewUserRole.MERCHANT);
        }

        return repository.save(user);
    }

    public List<AppUser> getAllUsers() {
        return repository.findAllByOrderByIdAsc();
    }

    public AppUser updateRole(UserRolePutDTO dto) throws IllegalArgumentException, EntityNotFoundException {
        AppUser result = search(dto.getUsername());

        if(result.getAuthority().contains(dto.getRole().toString()))
            throw new IllegalArgumentException("New role same as existing.");

        result.setAuthority("ROLE_" + dto.getRole().toString());

        return repository.save(result);
    }

    public AppUser updateStatus(UserAccessPutDTO dto) throws IllegalArgumentException, EntityNotFoundException {
        AppUser result = search(dto.getUsername());

        if(result.getAuthority().contains(ADMINISTRATOR_ROLE_NAME))
            throw new IllegalArgumentException("Can't block administrator.");

        switch (dto.getOperation()){
            case LOCK -> result.setAccountNonLocked(false);
            case UNLOCK -> result.setAccountNonLocked(true);
        }

        return repository.save(result);
    }

    public AppUser deleteUser(String username) throws InvalidParameterException, EntityNotFoundException {
        if(username == null || username.isBlank())
            throw new InvalidParameterException("Username can't be null or blank.");

        AppUser result = search(username);

        repository.delete(result);

        return result;
    }

    AppUser search(String username) throws EntityNotFoundException{
        return repository.findAppUserByUsername(username).orElseThrow(EntityNotFoundException::new);
    }
}