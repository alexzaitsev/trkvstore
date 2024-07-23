import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import core.KeyValueStoreDefault
import org.jetbrains.compose.ui.tooling.preview.Preview
import ui.Command
import ui.FullCommand
import ui.parseCommand
import ui.runCommand

// In case of further extension, only this single-line change is required to substitute implementation
private val kvStore = KeyValueStoreDefault()

private val commandShortcuts = listOf(Command.BEGIN, Command.COMMIT, Command.ROLLBACK)
private val commandConfirmationRequired = listOf(Command.DELETE, Command.COMMIT, Command.ROLLBACK)

@Composable
@Preview
fun App() {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var messageHistory by remember { mutableStateOf(listOf<String>()) }
    var waitingForConfirmation by remember { mutableStateOf<FullCommand?>(null) }

    suspend fun showMessage(message: String) {
        messageHistory = messageHistory + message
        listState.animateScrollToItem(messageHistory.size - 1)
    }

    suspend fun execCommand(fc: FullCommand) {
        showMessage(fc.toString())
        val execResult = try {
            "> ${kvStore.runCommand(fc)}"
        } catch (e: Exception) {
            e.printStackTrace()
            "> Command run exception happened: ${e.message}"
        }
        showMessage(execResult)
    }

    MaterialTheme {
        MainScreen(
            listState = listState,
            messages = messageHistory,
            onSend = { input ->
                scope.launch {
                    try {
                        val waitingCommand = waitingForConfirmation
                        if (waitingCommand == null) {
                            // regular mode
                            val fc = parseCommand(input)
                            if (fc.command in commandConfirmationRequired) {
                                showMessage("> Please, confirm command ${fc.command} by entering YES")
                                waitingForConfirmation = fc
                            } else {
                                execCommand(fc)
                            }
                        } else if (input.trim().lowercase() == "yes") {
                            // confirmation mode - confirmed
                            execCommand(waitingCommand)
                            waitingForConfirmation = null
                        } else {
                            // confirmation mode - discarded
                            showMessage("> Discarded")
                            waitingForConfirmation = null
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showMessage("> Input contains incorrect command: ${e.message}")
                    }
                }
            }
        )
    }
}

@Composable
fun MainScreen(
    listState: LazyListState,
    messages: List<String>,
    onSend: (String) -> Unit,
) {
    var input by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(horizontal = 5.dp, vertical = 10.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            state = listState
        ) {
            items(items = messages) { item ->
                Text(
                    modifier = Modifier.padding(bottom = 5.dp),
                    text = item
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            commandShortcuts.forEachIndexed { index, command ->
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        input = ""
                        onSend(command.command)
                    }) {
                    Text(command.command)
                }
                if (index != commandShortcuts.lastIndex) {
                    DefaultSpacer()
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                modifier = Modifier.weight(1f),
                value = input,
                onValueChange = { input = it }
            )
            DefaultSpacer()
            Button(onClick = {
                onSend(input)
                input = ""
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send"
                )
            }
        }
    }
}

@Composable
fun DefaultSpacer() = Spacer(modifier = Modifier.size(5.dp))
