package nl.jvhaastert.springutils.security.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JsonAuthenticationFailureHandler(
    private val objectMapper: ObjectMapper
) : AuthenticationFailureHandler {

    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        val responseBody = mapOf(
            "timestamp" to Date(),
            "status" to 401,
            "error" to "Unauthorized",
            "message" to "Unauthenticated",
            "path" to request.servletPath
        )

        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json"
        objectMapper.writeValue(response.outputStream, responseBody)
    }

}
