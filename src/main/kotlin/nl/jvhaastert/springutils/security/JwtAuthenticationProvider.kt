package nl.jvhaastert.springutils.security

import io.jsonwebtoken.JwtException
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication

class JwtAuthenticationProvider(
    private val jwtService: JwtService
) : AuthenticationProvider {

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == JwtAuthenticationToken::class.java
    }

    override fun authenticate(authentication: Authentication?): Authentication {
        val jwtAuthenticationToken = authentication as JwtAuthenticationToken
        val jwt = jwtAuthenticationToken.credentials

        return try {
            val jws = jwtService.parseJwt(jwt)
            JwtAuthenticationToken(jwt, jws)
        } catch (e: JwtException) {
            throw BadCredentialsException(e.localizedMessage, e)
        }
    }

}
