package rs.raf.taskboard.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import rs.raf.taskboard.dto.AuthResponse;
import rs.raf.taskboard.dto.LoginRequest;
import rs.raf.taskboard.dto.RegisterRequest;
import rs.raf.taskboard.dto.UserDto;
import rs.raf.taskboard.exception.EmailAlreadyUsedException;
import rs.raf.taskboard.exception.InvalidCredentialsException;
import rs.raf.taskboard.model.User;
import rs.raf.taskboard.repository.UserRepository;

import java.util.Locale;

@Service
@Validated
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Transactional
    public AuthResponse register(@Valid RegisterRequest request) {
        String email = normalize(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyUsedException(email);
        }
        User user = new User();
        user.setName(request.name());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        return response(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(@Valid LoginRequest request) {
        User user = userRepository.findByEmail(normalize(request.email()))
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(InvalidCredentialsException::new);
        return response(user);
    }

    private AuthResponse response(User user) {
        return new AuthResponse(tokenService.createToken(user), UserDto.from(user));
    }

    // Email se čuva malim slovima, da Ana@x.com i ana@x.com budu isti nalog
    private static String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}