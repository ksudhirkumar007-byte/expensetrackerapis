package com.expensetracker.service;

import com.expensetracker.dto.CategoryDTO;
import com.expensetracker.dto.LoginResponse;
import com.expensetracker.dto.UserDTO;
import com.expensetracker.model.Category;
import com.expensetracker.model.RefreshTokens;
import com.expensetracker.model.Users;
import com.expensetracker.repository.RefreshTokenRepository;
import com.expensetracker.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Override
    public boolean registerUser(String email, String password) {

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(email);
        userDTO.setPasswordHash(password);
        Users user = convertToEntity(userDTO);
        userRepository.save(user);
        return false;
    }
    @Override
    public LoginResponse checkLoginStatus(String email,String password){
        List<Users> users = userRepository.findByEmail(email);
        Users users1 = users.stream().findFirst().get();
        System.out.println("user from database is "+users1.getEmail()+" and its id is "+users1.getId());
        UserDTO userDTO = convertToDTO(users1);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean passwordMatched = encoder.matches(password, userDTO.getPasswordHash());
        System.out.println("passwords matched "+passwordMatched);
        String accessToken = generateAccessToken(userDTO);
        System.out.println("accessToken is "+accessToken);
        String refreshToken = generateRefreshToken();
        RefreshTokens refreshTokens = new RefreshTokens();
        refreshTokens.setRevoked(false);
        refreshTokens.setUserId(users1.getId());
        refreshTokens.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));
        refreshTokens.setToken(refreshToken);
        refreshTokenRepository.save(refreshTokens);
        return new LoginResponse(accessToken,refreshToken);

    }

    String generateAccessToken(UserDTO user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + 15 * 60 * 1000); // 15 minutes
        System.out.println("userid from DTO is "+user.getId());
        return Jwts.builder()
                .setSubject(user.getEmail())           // or email
                .claim("email", user.getEmail())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(SignatureAlgorithm.HS256, "u1d5zG3K8b4Q2xF7s9J4tV8wL1p6Z3m2c7n8r5a6d9f1g3h4j5k6l7m8n9p0q1r") // use a strong secretKey from config
                .compact();
    }
    String generateRefreshToken() {
        return UUID.randomUUID().toString(); // or secure random bytes encoded as base64
    }

    @Override
    public LoginResponse refresh(String refreshToken) {
        RefreshTokens rt = refreshTokenRepository
                .findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (rt.isRevoked() || rt.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired or revoked");
        }

        Users user = userRepository.findById(rt.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserDTO userDTO = convertToDTO(user);
        String newAccessToken = generateAccessToken(userDTO);
        // optional: rotate refresh token
        return new LoginResponse(newAccessToken, refreshToken);
    }
    // Check if token is valid for this user
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String subject = extractSubject(token);
        return subject.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // Extract subject (user id/email) from token
//    @Override
//    public String extractSubject(String token) {
//        return extractClaim(token, claims -> claims.get("email", String.class));
//    }

    @Override
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = parseAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims parseAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey("u1d5zG3K8b4Q2xF7s9J4tV8wL1p6Z3m2c7n8r5a6d9f1g3h4j5k6l7m8n9p0q1r")
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private Users convertToEntity(UserDTO dto) {
        return Users.builder()
                .email(dto.getEmail())
                .passwordHash(dto.getPasswordHash())
                .build();
    }

    private UserDTO convertToDTO(Users user) {
        return UserDTO.builder()
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .build();
    }
}
