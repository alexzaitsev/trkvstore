package core

interface KeyValueStore {
    /**
     * Stores the value associated with the key.
     */
    suspend fun set(key: String, value: String)

    /**
     * Retrieves the current value for key.
     *
     * @return current value if the key is present or [MESSAGE_KEY_NOT_SET] otherwise
     */
    suspend fun get(key: String): String

    /**
     * Removes the entry for key.
     */
    suspend fun delete(key: String)

    /**
     * Retrieves the number of keys that have the given value.
     *
     * @return number of keys
     */
    suspend fun count(value: String): Int

    /**
     * Starts a new transaction.
     */
    suspend fun begin()

    /**
     * Completes the current transaction.
     *
     * @return empty string in case of success or [MESSAGE_NO_TX] otherwise
     */
    suspend fun commit(): String

    /**
     * Reverts to state prior to [begin] call.
     *
     * @return empty string in case of success or [MESSAGE_NO_TX] otherwise
     */
    suspend fun rollback(): String
}

const val MESSAGE_KEY_NOT_SET = "key not set"
const val MESSAGE_NO_TX = "no transaction"
