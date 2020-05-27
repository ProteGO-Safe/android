package pl.gov.mc.protegosafe.domain.extension

import java.util.Date
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.LocalDateTime
import org.threeten.bp.OffsetDateTime

private const val EXPOSURE_VALIDITY_DAYS = 14L
private const val START_TIME_VALUE = 0
fun getExposureLastValidDate(): Date =
    LocalDateTime.now()
        .minusDays(EXPOSURE_VALIDITY_DAYS)
        .withHour(START_TIME_VALUE)
        .withMinute(START_TIME_VALUE)
        .withSecond(START_TIME_VALUE)
        .withNano(START_TIME_VALUE)
        .toInstant(OffsetDateTime.now().offset)
        .let { DateTimeUtils.toDate(it) }
