package ui

import core.KeyValueStore

/**
 * @return command execution result or Success otherwise
 */
suspend fun KeyValueStore.runCommand(fc: FullCommand): String =
    when (fc.command) {
        Command.SET -> wrap { set(fc.args[0], fc.args[1]) }
        Command.GET -> get(fc.args[0])
        Command.DELETE -> wrap { delete(fc.args[0]) }
        Command.COUNT -> count(fc.args[0]).toString()
        Command.BEGIN -> wrap { begin() }
        Command.COMMIT -> commit().ifEmpty { "Success" }
        Command.ROLLBACK -> rollback().ifEmpty { "Success" }
    }

private suspend fun wrap(block: suspend () -> Unit): String {
    block()
    return "Success"
}
