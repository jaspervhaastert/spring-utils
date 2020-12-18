package nl.jvhaastert.springutils.security.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import nl.jvhaastert.springutils.security.JwtService
import nl.jvhaastert.springutils.security.models.User
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationSuccessHandler(
    private val jwtService: JwtService,
    private val objectMapper: ObjectMapper
) : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val user = authentication.principal as User
        val jwt = jwtService.generateJwtFromUser(user)

        response.contentType = "application/json"
        objectMapper.writeValue(response.outputStream, mapOf("jwt" to jwt))
    }

}
