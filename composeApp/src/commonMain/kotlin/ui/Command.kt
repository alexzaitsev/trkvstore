package ui

enum class Command(val command: String, val argsNum: Int) {
    SET("SET", 2),
    GET("GET", 1),
    DELETE("DELETE", 1),
    COUNT("COUNT", 1),
    BEGIN("BEGIN", 0),
    COMMIT("COMMIT", 0),
    ROLLBACK("ROLLBACK", 0);

    companion object {
        fun parse(command: String, argsNum: Int): Command? =
            entries.find { it.command.lowercase() == command.lowercase() && it.argsNum == argsNum }
    }
}

data class FullCommand(
    val command: Command,
    val args: List<String>
) {
    override fun toString(): String {
        val argsAsStr = args.fold("") { r, n -> "$r$n " }.trim()
        return "${command.command} $argsAsStr"
    }
}
