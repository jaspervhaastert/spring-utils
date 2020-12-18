package nl.jvhaastert.springutils.security

import org.springframework.security.core.AuthenticationException

class MissingCredentialsException : AuthenticationException {

    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)

}
