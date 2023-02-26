package sectasy.rocket

import com.macasaet.fernet.Key
import com.macasaet.fernet.StringValidator
import com.macasaet.fernet.Token
import com.macasaet.fernet.Validator


class RCrypto(private val secretKey: Key) {

    fun encrypt(packet: RPacket): ByteArray {
        return Token.generate(secretKey, packet.toSocket()).serialise().encodeToByteArray()
    }

    fun decrypt(payload: String): String {
        val token: Token = Token.fromString(payload)
        val validator: Validator<String> = object : StringValidator { }
        return token.validateAndDecrypt(secretKey, validator)
    }
}