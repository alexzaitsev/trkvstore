package ui

fun parseCommand(input: String): FullCommand {
    val words = input.trim().split(" ")
    val comm = words[0]
    val argsNum = words.size - 1
    val command = Command.parse(comm, argsNum) ?: throw IllegalArgumentException("cannot parse \"${input.take(20)}\"")
    val args = words.subList(1, words.size)
    return FullCommand(command, args)
}
