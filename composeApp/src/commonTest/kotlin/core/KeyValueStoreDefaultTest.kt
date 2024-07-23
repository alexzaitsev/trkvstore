package core

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class KeyValueStoreDefaultTest {

    private fun produceSut() = KeyValueStoreDefault()

    @Test
    fun sutSetSetsAValueAssociatedWithTheKey() = runTest {
        val sut = produceSut()

        sut.set("1", "1")

        assertEquals("1", sut.get("1"))
    }

    @Test
    fun sutGetReturnsKeyNotSetIfKeyIsNotPresent() = runTest {
        val sut = produceSut()

        sut.set("1", "1")

        assertEquals(MESSAGE_KEY_NOT_SET, sut.get("2"))
    }

    @Test
    fun sutDeleteRemovesKey() = runTest {
        val sut = produceSut()

        sut.set("1", "1")
        sut.delete("1")

        assertEquals(MESSAGE_KEY_NOT_SET, sut.get("1"))
    }

    @Test
    fun sutCountCalculatesEntryTimes() = runTest {
        val sut = produceSut()

        sut.set("1", "1")
        sut.set("2", "1")

        assertEquals(2, sut.count("1"))
    }

    @Test
    fun sutRollbackReturnsMessageIfThereIsNoTx() = runTest {
        val sut = produceSut()

        val result = sut.rollback()

        assertEquals(MESSAGE_NO_TX, result)
    }

    @Test
    fun sutCommitReturnsMessageIfThereIsNoTx() = runTest {
        val sut = produceSut()

        val result = sut.commit()

        assertEquals(MESSAGE_NO_TX, result)
    }

    @Test
    fun sutCommitUpdatesStorage() = runTest {
        val sut = produceSut()

        sut.set("1", "1")
        sut.begin()
        sut.set("1", "2")
        val result = sut.commit()

        assertEquals("", result)
        assertEquals("2", sut.get("1"))
    }

    @Test
    fun sutRollbackCancelsTx() = runTest {
        val sut = produceSut()

        sut.set("1", "1")
        sut.begin()
        sut.set("1", "2")
        val result = sut.rollback()

        assertEquals("", result)
        assertEquals("1", sut.get("1"))
    }

    @Test
    fun sutAllowsMultipleNestedTxs() = runTest {
        val sut = produceSut()

        sut.set("1", "1")
        sut.begin()
        sut.set("1", "2")
        sut.begin()
        sut.set("1", "3")
        sut.commit()
        val result = sut.commit()

        assertEquals("", result)
        assertEquals("3", sut.get("1"))
    }

    @Test
    fun sutRollbackCancelsLastNestedTx() = runTest {
        val sut = produceSut()

        sut.set("1", "1")
        sut.begin()
        sut.set("1", "2")
        sut.begin()
        sut.set("1", "3")
        sut.begin()
        sut.delete("1")
        sut.rollback()
        sut.commit()
        sut.set("1", "4")
        val result = sut.commit()

        assertEquals("", result)
        assertEquals("4", sut.get("1"))
    }
}
