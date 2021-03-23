package pl.gov.mc.protegosafe.domain.utils

import pl.gov.mc.protegosafe.domain.extension.getCurrentTimeInSeconds

fun isTimestampBeforeNow(timestamp: Long): Boolean = timestamp < getCurrentTimeInSeconds()
