import javax.crypto.Cipher
import java.security.*
import java.util.*


class RSAEncryptor {
    var privateKey: PrivateKey
        private set
    var publicKey: PublicKey
        private set

    init {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(1024)
        val pair = keyGen.generateKeyPair()
        privateKey = pair.private
        publicKey = pair.public
    }

    @Throws(Exception::class)
    fun encryptMessage(plainText: String): String {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return Base64.getEncoder()
            .encodeToString(
                cipher.doFinal(plainText.toByteArray())
            )
    }

    @Throws(Exception::class)
    fun decryptMessage(encryptedText: String?):
            String {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return String(cipher.doFinal(Base64.getDecoder().decode(encryptedText)))
    }
}