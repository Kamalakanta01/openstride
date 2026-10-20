package dev.openstride.local

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

class ConvertersTest {
    private val converters = Converters()

    @Test
    fun `instant survives a round trip`() {
        val value = Instant.ofEpochMilli(1_700_000_000_123)
        assertEquals(value, converters.longToInstant(converters.instantToLong(value)))
    }

    @Test
    fun `local date survives a round trip`() {
        val value = LocalDate.of(2026, 3, 23)
        assertEquals(value, converters.longToLocalDate(converters.localDateToLong(value)))
    }

    @Test
    fun `null stays null`() {
        assertNull(converters.longToInstant(converters.instantToLong(null)))
        assertNull(converters.longToLocalDate(converters.localDateToLong(null)))
    }
}
