import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.util.Base64

@Composable
fun App(server: Server, rsaEncryptor: RSAEncryptor) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    val lazyListState by remember { mutableStateOf(LazyListState()) }
    val messages = remember {
        mutableStateListOf<Message>()
    }
    val receivedMessage = server.state.collectAsState()
    if (receivedMessage.value.message.isNotBlank()) {
        messages.add(receivedMessage.value)
    }
    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Server",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Button(onClick = {
                if (textFieldValue.text.isBlank()) {
                    return@Button
                }

                val encodedMessage = rsaEncryptor.encryptMessage(textFieldValue.text)
                server.sendMessage(encodedMessage)
                messages.add(
                    Message(
                        message = textFieldValue.text,
                        messageType = MessageType.USER
                    )
                )
                textFieldValue = TextFieldValue()
            }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Send")
            }
            TextField(textFieldValue, {
                textFieldValue = it

            }, placeholder = { Text("Write message here...") }, modifier = Modifier.fillMaxWidth())

            LazyColumn(modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                state = lazyListState) {

                itemsIndexed(messages) {index, message ->
                    when (message.messageType) {
                        MessageType.SERVER -> getText(messages[index].message)
                        MessageType.USER -> getTextFrom(messages[index].message)
                    }
                }
            }
        }
    }


}


fun main() = application {
    val rsa = RSAEncryptor()
    val server = Server(rememberCoroutineScope(), rsa)
    Window(onCloseRequest = ::exitApplication, title = "Haxordak Server") {
        App(server, rsa)
    }
}

