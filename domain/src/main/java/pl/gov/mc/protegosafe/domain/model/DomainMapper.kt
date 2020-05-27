package pl.gov.mc.protegosafe.domain.model

fun ExposureInformationItem.toExposureItem() = ExposureItem(
    date = dateMillisSinceEpoch,
    durationInMinutes = durationMinutes,
    riskScore = totalRiskScore
)
