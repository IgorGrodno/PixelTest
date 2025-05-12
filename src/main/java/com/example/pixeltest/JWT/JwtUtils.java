package com.example.pixeltest.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwtSecret}")
    private String jwtSecret;

    @Value("${jwtExpirationMs}")
    private long jwtExpirationMs;

    /**
     * Генерация JWT токена с userId.
     *
     * @param userId идентификатор пользователя
     * @return сгенерированный JWT токен
     */
    public String generateToken(Long userId) {
        return Jwts.builder()
                .claim("userId", userId) // Добавляем userId в payload
                .setIssuedAt(new Date())  // Устанавливаем дату выпуска
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs)) // Срок действия токена
                .signWith(SignatureAlgorithm.HS512, jwtSecret)  // Подпись с использованием секретного ключа
                .compact();
    }

    /**
     * Извлечение userId из JWT токена.
     *
     * @param token JWT токен
     * @return userId
     */
    public Long getUserIdFromJwtToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret)  // Устанавливаем ключ для проверки подписи
                .build()
                .parseClaimsJws(token)  // Парсим токен
                .getBody();  // Получаем тело (claims)

        return claims.get("userId", Long.class);  // Возвращаем userId из токена
    }

    /**
     * Проверка валидности JWT токена.
     *
     * @param authToken JWT токен
     * @return true, если токен валиден, иначе false
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtSecret)
                    .build()
                    .parseClaimsJws(authToken);  // Проверяем подпись токена
            return true;
        } catch (Exception e) {
            // В случае ошибок (например, если токен истек или подпись неверна)
            return false;
        }
    }
}
