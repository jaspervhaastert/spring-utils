package nl.jvhaastert.springutils.security.configurers

import com.fasterxml.jackson.databind.ObjectMapper
import nl.jvhaastert.springutils.security.extensions.getConfigurer
import nl.jvhaastert.springutils.security.extensions.getSharedObject
import nl.jvhaastert.springutils.security.Http401AuthenticationEntryPoint
import nl.jvhaastert.springutils.security.filters.JwtAuthenticationFilter
import nl.jvhaastert.springutils.security.handlers.JsonAuthenticationFailureHandler
import nl.jvhaastert.springutils.security.handlers.NoOpAuthenticationSuccessHandler
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.HttpSecurityBuilder
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.accept.ContentNegotiationStrategy
import org.springframework.web.accept.HeaderContentNegotiationStrategy

class JwtAuthenticationConfigurer<H : HttpSecurityBuilder<H>> :
    AbstractHttpConfigurer<JsonAuthenticationConfigurer<H>, H>() {

    var initialized = false
        private set

    private var successHandler: AuthenticationSuccessHandler? = null
    private var failureHandler: AuthenticationFailureHandler? = null
    private var continueFilterChain = true

    override fun init(http: H) {
        initialized = true

        val jsonAuthenticationConfigurer = http.getConfigurer<H, JsonAuthenticationConfigurer<H>>()
        if (jsonAuthenticationConfigurer?.initialized == true) return

        val exceptionHandling = http.getConfigurer<H, ExceptionHandlingConfigurer<H>>()
        exceptionHandling?.defaultAuthenticationEntryPointFor(
            Http401AuthenticationEntryPoint(),
            getAuthenticationEntryPointMatcher(http)
        )

        val sessionManagement = http.getConfigurer<H, SessionManagementConfigurer<H>>()
        sessionManagement?.sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        if (http.getSharedObject<ObjectMapper>() == null) {
            val applicationContext = http.getSharedObject(ApplicationContext::class.java)
            val objectMapper = applicationContext.getBean(ObjectMapper::class.java)

            http.setSharedObject(ObjectMapper::class.java, objectMapper)
        }
    }

    override fun configure(http: H) {
        val authenticationManager = http.getSharedObject(AuthenticationManager::class.java)
        val objectMapper = http.getSharedObject(ObjectMapper::class.java)

        val authenticationFilter = JwtAuthenticationFilter(authenticationManager, continueFilterChain)
        authenticationFilter.setAuthenticationSuccessHandler(
            successHandler ?: NoOpAuthenticationSuccessHandler()
        )
        authenticationFilter.setAuthenticationFailureHandler(
            failureHandler ?: JsonAuthenticationFailureHandler(objectMapper)
        )

        http.addFilterAfter(authenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
    }

    fun setSuccessHandler(successHandler: AuthenticationSuccessHandler): JwtAuthenticationConfigurer<H> {
        this.successHandler = successHandler
        return this
    }

    fun setFailureHandler(failureHandler: AuthenticationFailureHandler): JwtAuthenticationConfigurer<H> {
        this.failureHandler = failureHandler
        return this
    }

    fun setContinueFilterChain(continueFilterChain: Boolean): JwtAuthenticationConfigurer<H> {
        this.continueFilterChain = continueFilterChain
        return this
    }

    private fun getAuthenticationEntryPointMatcher(http: H): RequestMatcher {
        val contentNegotiationStrategy = http.getSharedObject(ContentNegotiationStrategy::class.java)
            ?: HeaderContentNegotiationStrategy()

        return MediaTypeRequestMatcher(contentNegotiationStrategy, MediaType.APPLICATION_JSON).apply {
            setIgnoredMediaTypes(setOf(MediaType.ALL))
        }
    }

}
