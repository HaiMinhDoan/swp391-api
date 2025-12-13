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

import java.util.*;

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
                    .message("Tài khoản hoặc mật khẩu sai")
                    .build();
        }
        User user = existing.get();

        if (user.getStatus() == null || !user.getStatus().equals(1)) {
            return AuthenticationResponse.builder()
                    .authenticated(false)
                    .message("Tài khoản chưa được kích hoạt")
                    .build();
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return AuthenticationResponse.builder()
                    .authenticated(false)
                    .message("Tài khoản hoặc mật khẩu sai")
                    .build();
        }

        Set<String> roles = new HashSet<>();
        roles.add(user.getRole());
        String token = jwtService.generateToken(user.getId().toString(), user.getEmail(), roles, userAgent);

        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(token)
                .message("Success")
                .role(user.getRole())
                .userId(user.getId())
                .username(user.getUsername())
                .avt(user.getAvt())
                .build();
    }


    public List<User> getUserByOtp(String otp) {
        List<User> users = userRepository.findByOtpAndStatus(otp, 0);
        return users;
    }


    // Helper methods
}
