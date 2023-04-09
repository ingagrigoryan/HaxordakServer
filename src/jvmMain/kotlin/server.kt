import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import kotlin.jvm.Throws

@Throws(IOException::class)
internal fun startServer() {

}

class Server(private val scope: CoroutineScope) {
    private lateinit var socket: Socket
    private val _state = MutableStateFlow(Message())
    val state = _state.asStateFlow()
    init {
        Thread {
            val server = ServerSocket(1112)
            socket = server.accept()
            println(socket)
            receiveMessage()
        }.start()
    }

    private fun receiveMessage() {
        while (true) {
            val strBuilder = StringBuilder()
            val length = socket.getInputStream().read()
            for (i in 1..length) {
                strBuilder.append(socket.getInputStream().read().toChar())
            }
            scope.launch {
                _state.emit(
                    Message(
                        message = strBuilder.toString(),
                        messageType = MessageType.SERVER
                    )
                )
                println("received")
            }
        }
    }


    fun sendMessage(text: String) {
        if (::socket.isInitialized) {
            socket.getOutputStream().write(text.length)
            socket.getOutputStream().write(text.toByteArray())
            println(text)
        }
    }
}
