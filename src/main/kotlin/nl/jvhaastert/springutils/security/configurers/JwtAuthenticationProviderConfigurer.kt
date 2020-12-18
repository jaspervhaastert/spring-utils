package nl.jvhaastert.springutils.security.configurers

import nl.jvhaastert.springutils.security.JwtAuthenticationProvider
import nl.jvhaastert.springutils.security.JwtService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder

class JwtAuthenticationProviderConfigurer<A : AuthenticationManagerBuilder>(
    private val jwtService: JwtService
) : SecurityConfigurerAdapter<AuthenticationManager, A>() {

    override fun configure(auth: A) {
        val authenticationProvider = JwtAuthenticationProvider(jwtService)
        auth.authenticationProvider(authenticationProvider)
    }

}
