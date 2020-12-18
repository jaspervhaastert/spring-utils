package nl.jvhaastert.springutils.security.filters

import com.fasterxml.jackson.databind.ObjectMapper
import nl.jvhaastert.springutils.security.MissingCredentialsException
import nl.jvhaastert.springutils.security.models.AuthenticateDTO
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JsonAuthenticationFilter(
    authenticationManager: AuthenticationManager,
    private val objectMapper: ObjectMapper
) : UsernamePasswordAuthenticationFilter(authenticationManager) {

    init {
        setFilterProcessesUrl("/authenticate")
    }

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val authenticateDTO = try {
            objectMapper.readValue(request.inputStream, AuthenticateDTO::class.java)
        } catch (e: IOException) {
            throw MissingCredentialsException(e.localizedMessage, e)
        }

        val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(
            authenticateDTO.username,
            authenticateDTO.password
        )
        return authenticationManager.authenticate(usernamePasswordAuthenticationToken)
    }

}
