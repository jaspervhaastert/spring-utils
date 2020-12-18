package nl.jvhaastert.springutils.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import nl.jvhaastert.springutils.security.models.User
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class JwtService(
    private val jwtConfigurationProperties: JwtConfigurationProperties
) {

    fun generateJwtFromUser(user: User): String {
        val nowInstant = Instant.now()
        val nowDate = Date.from(nowInstant)
        val expirationInstant = nowInstant.plus(jwtConfigurationProperties.expireDuration)
        val expirationDate = Date.from(expirationInstant)

        return Jwts
            .builder()
            .setSubject(user.id.toString())
            .setIssuer(jwtConfigurationProperties.issuer)
            .setAudience(jwtConfigurationProperties.audience)
            .setIssuedAt(nowDate)
            .setNotBefore(nowDate)
            .setExpiration(expirationDate)
            .claim("username", user.username)
            .claim("authorities", user.authorities)
            .signWith(jwtConfigurationProperties.secretKey)
            .compact()
    }

    fun parseJwt(jwt: String): Jws<Claims> {
        return Jwts
            .parserBuilder()
            .requireIssuer(jwtConfigurationProperties.issuer)
            .requireAudience(jwtConfigurationProperties.audience)
            .setSigningKey(jwtConfigurationProperties.secretKey)
            .build()
            .parseClaimsJws(jwt)
    }

}
