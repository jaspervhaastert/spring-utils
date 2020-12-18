package nl.jvhaastert.springutils.security.filters

import nl.jvhaastert.springutils.security.JwtAuthenticationToken
import nl.jvhaastert.springutils.security.MissingCredentialsException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationFilter(
    authenticationManager: AuthenticationManager,
    private val continueFilterChain: Boolean
) : AbstractAuthenticationProcessingFilter("/**", authenticationManager) {

    private val authorizationHeaderRegex = Regex("Bearer ([A-Za-z0-9+/=_-]+\\.[A-Za-z0-9+/=_-]+\\.[A-Za-z0-9+/=_-]+)")

    override fun requiresAuthentication(request: HttpServletRequest, response: HttpServletResponse): Boolean =
        super.requiresAuthentication(request, response) && request.getHeader("Authorization") != null

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val authorizationHeader = request.getHeader("Authorization")
            ?: throw MissingCredentialsException("Missing authorization header")

        val authorizationHeaderMatchResult = authorizationHeaderRegex.matchEntire(authorizationHeader)
            ?: throw MissingCredentialsException("Missing JWT")

        val jwt = authorizationHeaderMatchResult.groupValues[1]
        val authentication = JwtAuthenticationToken(jwt)

        return authenticationManager.authenticate(authentication)
    }

    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication
    ) {
        super.successfulAuthentication(request, response, chain, authResult)
        if (continueFilterChain) chain.doFilter(request, response)
    }

}
