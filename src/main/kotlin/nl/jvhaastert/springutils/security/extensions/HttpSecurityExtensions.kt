package nl.jvhaastert.springutils.security.extensions

import nl.jvhaastert.springutils.security.configurers.JsonAuthenticationConfigurer
import nl.jvhaastert.springutils.security.configurers.JwtAuthenticationConfigurer
import org.springframework.security.config.annotation.SecurityConfigurer
import org.springframework.security.config.annotation.web.HttpSecurityBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain

fun HttpSecurity.jsonAuthentication(): JsonAuthenticationConfigurer<HttpSecurity> =
    apply(JsonAuthenticationConfigurer())
fun HttpSecurity.jwtAuthentication(): JwtAuthenticationConfigurer<HttpSecurity> = apply(JwtAuthenticationConfigurer())

inline fun <reified C> HttpSecurityBuilder<*>.getSharedObject(): C? = getSharedObject(C::class.java)
inline fun <H, reified C> HttpSecurityBuilder<H>.getConfigurer(): C? where H : HttpSecurityBuilder<H>, C : SecurityConfigurer<DefaultSecurityFilterChain, H> =
    getConfigurer(C::class.java)
