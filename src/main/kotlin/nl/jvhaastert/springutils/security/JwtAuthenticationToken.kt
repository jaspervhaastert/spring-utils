package nl.jvhaastert.springutils.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

class JwtAuthenticationToken : Authentication {

    private val jwt: String
    private val jws: Jws<Claims>?

    constructor(jwt: String) {
        this.jwt = jwt
        this.jws = null
    }

    constructor(jwt: String, jws: Jws<Claims>) {
        this.jwt = jwt
        this.jws = jws
    }

    override fun getName() = jws?.body?.get("username", String::class.java)
    override fun getCredentials() = jwt
    override fun getDetails() = null
    override fun getPrincipal() = jws?.body?.subject?.toLong()
    override fun isAuthenticated() = jws != null

    override fun getAuthorities(): Collection<GrantedAuthority>? = jws?.body
        ?.get("authorities", Collection::class.java)
        ?.map { obj -> SimpleGrantedAuthority(obj as String) }

    override fun setAuthenticated(isAuthenticated: Boolean) =
        throw UnsupportedOperationException("Cannot set this token to trusted - use constructor which takes a Jws instead")

}