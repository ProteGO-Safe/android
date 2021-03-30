package pl.gov.mc.protegosafe.domain.extension

import org.threeten.bp.LocalDateTime
import org.threeten.bp.OffsetDateTime

fun getCurrentTimeInSeconds(): Long {
    return LocalDateTime.now().toInstant(OffsetDateTime.now().offset).epochSecond
}
