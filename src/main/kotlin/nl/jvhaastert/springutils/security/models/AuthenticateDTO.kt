package nl.jvhaastert.springutils.security.models

data class AuthenticateDTO(
    val username: String,
    val password: String
)
