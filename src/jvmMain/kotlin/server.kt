import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.ObjectOutputStream
import java.net.ServerSocket
import java.net.Socket

class Server(private val scope: CoroutineScope, private val rsa: RSAEncryptor) {
    private lateinit var socket: Socket
    private val _state = MutableStateFlow(Message())
    val state = _state.asStateFlow()
    init {
        Thread {
            val server = ServerSocket(1112)
            socket = server.accept()
            println(socket)
            sendKeys()
            receiveMessage()
        }.start()
    }

    private fun sendKeys() {
        val oos = ObjectOutputStream(socket.getOutputStream())
        oos.writeObject(rsa.publicKey)
        oos.writeObject(rsa.privateKey)
    }

    private fun receiveMessage() {
        while (true) {
            val strBuilder = StringBuilder()
            val length = socket.getInputStream().read()
            for (i in 1..length) {
                strBuilder.append(socket.getInputStream().read().toChar())
            }
            val decrypted = rsa.decryptMessage(strBuilder.toString())
            scope.launch {
                _state.emit(
                    Message(
                        message = decrypted,
                        messageType = MessageType.SERVER
                    )
                )
            }
        }
    }


    fun sendMessage(text: String) {
        if (::socket.isInitialized) {
            socket.getOutputStream().write(text.length)
            socket.getOutputStream().write(text.toByteArray())
        }
    }
}
