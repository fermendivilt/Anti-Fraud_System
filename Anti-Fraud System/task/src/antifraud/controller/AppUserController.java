package antifraud.controller;

import antifraud.data.AppUser;
import antifraud.dto.request.UserAccessPutDTO;
import antifraud.dto.request.UserPostDTO;
import antifraud.dto.request.UserRolePutDTO;
import antifraud.dto.response.UserAccessPutResponseDTO;
import antifraud.dto.response.UserDeleteResponseDTO;
import antifraud.dto.response.UserPostResponseDTO;
import antifraud.service.AppUserDetailsServiceImpl;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AppUserController {
    AppUserDetailsServiceImpl appUserDetailsService;

    @Autowired
    public AppUserController(AppUserDetailsServiceImpl appUserDetailsService) {
        this.appUserDetailsService = appUserDetailsService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserPostResponseDTO>> getList() {
        return ResponseEntity.ok(appUserDetailsService.getAllUsers().stream().map(UserPostResponseDTO::new).toList());
    }

    @PostMapping("/user")
    public ResponseEntity<UserPostResponseDTO> postUser(@RequestBody @Valid UserPostDTO dto) {
        try {
            AppUser result = appUserDetailsService.newUser(new AppUser(dto));

            return ResponseEntity.status(201).body(new UserPostResponseDTO(result));

        } catch (EntityExistsException e) {
            return ResponseEntity.status(409).build();
        }
    }

    @PutMapping("/role")
    public ResponseEntity<UserPostResponseDTO> putUserRole(@RequestBody @Valid UserRolePutDTO dto) {
        try {
            AppUser result = appUserDetailsService.updateRole(dto);

            return ResponseEntity.ok(new UserPostResponseDTO(result));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).build();
        }
    }

    @PutMapping("/access")
    public ResponseEntity<UserAccessPutResponseDTO> putUserAccess(@RequestBody @Valid UserAccessPutDTO dto) {
        try {
            AppUser result = appUserDetailsService.updateStatus(dto);

            return ResponseEntity.ok(new UserAccessPutResponseDTO(result.getUsername(), dto.getOperation()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<UserDeleteResponseDTO> deleteUser(@PathVariable @NotBlank String username) {
        try {
            AppUser result = appUserDetailsService.deleteUser(username);

            return ResponseEntity.ok(new UserDeleteResponseDTO(result));

        } catch (InvalidParameterException e) {
            return ResponseEntity.status(400).build();

        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
