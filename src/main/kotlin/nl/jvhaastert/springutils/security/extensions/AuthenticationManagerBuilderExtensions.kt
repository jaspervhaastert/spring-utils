package nl.jvhaastert.springutils.security.extensions

import nl.jvhaastert.springutils.security.JwtService
import nl.jvhaastert.springutils.security.configurers.JwtAuthenticationProviderConfigurer
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder

fun AuthenticationManagerBuilder.jwtAuthentication(jwtService: JwtService)
        : JwtAuthenticationProviderConfigurer<AuthenticationManagerBuilder> =
    apply(JwtAuthenticationProviderConfigurer(jwtService))
