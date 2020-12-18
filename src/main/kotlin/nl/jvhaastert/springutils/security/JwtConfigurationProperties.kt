package nl.jvhaastert.springutils.security

import org.hibernate.validator.constraints.Length
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.validation.annotation.Validated
import java.time.Duration
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Validated
@ConfigurationProperties("jwt")
class JwtConfigurationProperties(
    applicationContext: ApplicationContext
) {

    @NotBlank
    var issuer: String? = if (applicationContext.id != "application") applicationContext.id else null

    var audience: String? = null

    @NotNull
    var expireDuration: Duration? = Duration.ofMinutes(30)

    @NotBlank
    @Length(min = 32, max = 32)
    var key: String? = null

    val secretKey: SecretKey get() = SecretKeySpec(key!!.encodeToByteArray(), "HmacSHA256")

}
