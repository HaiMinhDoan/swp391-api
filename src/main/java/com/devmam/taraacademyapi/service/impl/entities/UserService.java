package com.devmam.taraacademyapi.service.impl.entities;

import com.devmam.taraacademyapi.models.dto.response.AuthenticationResponse;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.repository.UserRepository;
import com.devmam.taraacademyapi.service.JwtService;
import com.devmam.taraacademyapi.service.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService extends BaseServiceImpl<User, UUID> {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public UserService(UserRepository repository) {
        super(repository);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUsernameOrEmailOrPhone(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email);
    }

    public AuthenticationResponse authenticate(String account, String password, String userAgent) {
        Optional<User> existing = findByUsernameOrEmailOrPhone(account, account);
        if (existing.isEmpty()) {
            return AuthenticationResponse.builder()
                    .authenticated(false)
                    .message("User not found")
                    .build();
        }
        User user = existing.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return AuthenticationResponse.builder()
                    .authenticated(false)
                    .message("Invalid password")
                    .build();
        }
        Set<String> roles = new HashSet<>();
        roles.add(user.getRole());
        String token = jwtService.generateToken(user.getId().toString(), user.getEmail(), roles, userAgent);

        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(token)
                .message("Success")
                .build();
    }


    // Helper methods
}
