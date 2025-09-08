package ar.edu.huergo.tombers.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Servicio para manejar operaciones relacionadas con tokens JWT.
 * Proporciona métodos para generar, validar y extraer información de tokens JWT.
 */
@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secretKey;

    @Value("${security.jwt.expiration-ms}")
    private long jwtExpiration;

    /**
     * Extrae el nombre de usuario del token JWT.
     *
     * @param token el token JWT del cual extraer el nombre de usuario
     * @return el nombre de usuario contenido en el token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae una reclamación específica del token JWT.
     *
     * @param token el token JWT
     * @param claimsResolver función para resolver la reclamación deseada
     * @return el valor de la reclamación extraída
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Genera un token JWT para el usuario proporcionado.
     *
     * @param userDetails los detalles del usuario para el cual generar el token
     * @return el token JWT generado
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Genera un token JWT con reclamaciones adicionales para el usuario proporcionado.
     *
     * @param extraClaims reclamaciones adicionales a incluir en el token
     * @param userDetails los detalles del usuario para el cual generar el token
     * @return el token JWT generado
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Construye un token JWT con las reclamaciones, usuario y tiempo de expiración especificados.
     *
     * @param extraClaims reclamaciones adicionales
     * @param userDetails detalles del usuario
     * @param expiration tiempo de expiración en milisegundos
     * @return el token JWT construido
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * Verifica si el token JWT es válido para el usuario proporcionado.
     *
     * @param token el token JWT a validar
     * @param userDetails los detalles del usuario
     * @return true si el token es válido, false en caso contrario
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Verifica si el token JWT ha expirado.
     *
     * @param token el token JWT a verificar
     * @return true si el token ha expirado, false en caso contrario
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrae la fecha de expiración del token JWT.
     *
     * @param token el token JWT
     * @return la fecha de expiración del token
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae todas las reclamaciones del token JWT.
     *
     * @param token el token JWT
     * @return las reclamaciones contenidas en el token
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Obtiene la clave secreta para firmar los tokens JWT.
     *
     * @return la clave secreta decodificada
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
