package core

/**
 * Default in-memory implementation of [KeyValueStore].
 * Designed to be used in a single-thread environment.
 */
class KeyValueStoreDefault : KeyValueStore {
    private val txs = ArrayDeque<Tx>().apply {
        add(Tx(mutableMapOf()))
    }
    private val currentKv: KeyValue
        get() = txs.last().kv

    override suspend fun set(key: String, value: String) {
        currentKv[key] = value
    }

    override suspend fun get(key: String): String =
        currentKv[key] ?: MESSAGE_KEY_NOT_SET

    override suspend fun delete(key: String) {
        currentKv.remove(key)
    }

    override suspend fun count(value: String): Int =
        currentKv.count { entry -> entry.value == value }

    override suspend fun begin() {
        txs.addLast(Tx(currentKv))
    }

    override suspend fun commit(): String =
        if (txs.size > 1) {
            val commitedTx = txs.removeLast()
            txs.last().kv = commitedTx.kv
            ""
        } else {
            MESSAGE_NO_TX
        }

    override suspend fun rollback(): String =
        if (txs.size > 1) {
            txs.removeLast()
            ""
        } else {
            MESSAGE_NO_TX
        }
}

private typealias KeyValue = MutableMap<String, String>

private class Tx(initialKv: KeyValue) {
    var kv: KeyValue = initialKv.toMutableMap() // copies the initial KV
}
