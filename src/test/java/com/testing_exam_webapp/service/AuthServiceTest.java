package com.testing_exam_webapp.service;

import com.testing_exam_webapp.config.JwtTokenProvider;
import com.testing_exam_webapp.dto.LoginRequest;
import com.testing_exam_webapp.dto.LoginResponse;
import com.testing_exam_webapp.dto.RegisterRequest;
import com.testing_exam_webapp.exception.UnauthorizedException;
import com.testing_exam_webapp.exception.ValidationException;
import com.testing_exam_webapp.model.mysql.User;
import com.testing_exam_webapp.model.types.Role;
import com.testing_exam_webapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test suite for AuthService.
 * Demonstrates equivalence partitioning for login scenarios and boundary analysis for username/password.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "$2a$10$encoded";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(UUID.randomUUID());
        testUser.setUsername(USERNAME);
        testUser.setPassword(ENCODED_PASSWORD);
        testUser.setRole(Role.USER);
    }

    @Test
    @DisplayName("login - Should return token for valid credentials (Equivalence Partition: Valid Input)")
    void login_ValidCredentials_ReturnsToken() {
        LoginRequest request = new LoginRequest();
        request.setUsername(USERNAME);
        request.setPassword(PASSWORD);

        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(jwtTokenProvider.generateToken(USERNAME, Role.USER.name())).thenReturn("test-token");

        LoginResponse result = authService.login(request);

        assertNotNull(result);
        assertEquals("test-token", result.getToken());
        assertEquals(Role.USER.name(), result.getRole());
    }

    @Test
    @DisplayName("login - Should throw exception for invalid username (Equivalence Partition: Invalid Username)")
    void login_InvalidUsername_ThrowsUnauthorizedException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistent");
        request.setPassword(PASSWORD);

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            authService.login(request);
        });
        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    @DisplayName("login - Should throw exception for invalid password (Equivalence Partition: Invalid Password)")
    void login_InvalidPassword_ThrowsUnauthorizedException() {
        LoginRequest request = new LoginRequest();
        request.setUsername(USERNAME);
        request.setPassword("wrongpassword");

        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", ENCODED_PASSWORD)).thenReturn(false);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            authService.login(request);
        });
        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    @DisplayName("register - Should create user for valid request")
    void register_ValidRequest_CreatesUser() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setUserId(UUID.randomUUID());
            return u;
        });

        User result = authService.register(request);

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals(Role.USER, result.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("register - Should throw exception for duplicate username")
    void register_DuplicateUsername_ThrowsValidationException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(USERNAME);
        request.setPassword(PASSWORD);

        when(userRepository.existsByUsername(USERNAME)).thenReturn(true);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            authService.register(request);
        });
        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    static java.util.stream.Stream<Arguments> usernameBoundaryValues() {
        return java.util.stream.Stream.of(
            Arguments.of("abc", "Minimum length (3 chars)"),
            Arguments.of("a".repeat(50), "Maximum length (50 chars)"),
            Arguments.of("testuser", "Normal length")
        );
    }

    @ParameterizedTest
    @MethodSource("usernameBoundaryValues")
    @DisplayName("register - Boundary Analysis: Username length boundary values")
    void register_UsernameLengthBoundaryValues_CreatesUser(String username, String description) {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setPassword("password123");

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setUserId(UUID.randomUUID());
            return u;
        });

        User result = authService.register(request);
        assertNotNull(result);
        assertEquals(username, result.getUsername());
    }

    static java.util.stream.Stream<Arguments> passwordBoundaryValues() {
        return java.util.stream.Stream.of(
            Arguments.of("pass12", "Minimum length (6 chars)"),
            Arguments.of("password123", "Normal length"),
            Arguments.of("verylongpassword123456", "Long password")
        );
    }

    @ParameterizedTest
    @MethodSource("passwordBoundaryValues")
    @DisplayName("register - Boundary Analysis: Password length boundary values")
    void register_PasswordLengthBoundaryValues_CreatesUser(String password, String description) {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword(password);

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setUserId(UUID.randomUUID());
            return u;
        });

        User result = authService.register(request);
        assertNotNull(result);
    }
}

