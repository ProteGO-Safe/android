package pl.gov.mc.protegosafe.domain.extension

import java.util.Date
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.LocalDateTime
import org.threeten.bp.OffsetDateTime
import pl.gov.mc.protegosafe.domain.model.TemporaryExposureKeyItem

private const val EXPOSURE_VALIDITY_DAYS = 14L
private const val START_TIME_VALUE = 0
private const val MINUTES_IN_HOUR = 60L
private const val EXPOSURE_TEK_PERIOD = 10L
private const val MILLIS_IN_SEC = 1000L

fun getExposureLastValidDate(): Date =
    LocalDateTime.now()
        .minusDays(EXPOSURE_VALIDITY_DAYS)
        .withHour(START_TIME_VALUE)
        .withMinute(START_TIME_VALUE)
        .withSecond(START_TIME_VALUE)
        .withNano(START_TIME_VALUE)
        .toInstant(OffsetDateTime.now().offset)
        .let { DateTimeUtils.toDate(it) }

fun TemporaryExposureKeyItem.getDayStartRollingNumber(): Long {
    val instant = DateTimeUtils.toInstant(
        Date(this.rollingPeriod * MINUTES_IN_HOUR * EXPOSURE_TEK_PERIOD * MILLIS_IN_SEC)
    )

    return LocalDateTime.ofInstant(instant, OffsetDateTime.now().offset)
        .withHour(START_TIME_VALUE)
        .withMinute(START_TIME_VALUE)
        .withSecond(START_TIME_VALUE)
        .withNano(START_TIME_VALUE)
        .toInstant(OffsetDateTime.now().offset)
        .let { DateTimeUtils.toDate(it) }
        .let { it.time / MINUTES_IN_HOUR / EXPOSURE_TEK_PERIOD / MILLIS_IN_SEC }
}
