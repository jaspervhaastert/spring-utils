package nl.jvhaastert.springutils.security.models

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class User(
    val id: Long,
    username: String,
    password: String,
    authorities: Collection<GrantedAuthority>
) : User(username, password, authorities)
