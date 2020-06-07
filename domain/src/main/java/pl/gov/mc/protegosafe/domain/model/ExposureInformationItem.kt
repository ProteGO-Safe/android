package pl.gov.mc.protegosafe.domain.model

class ExposureInformationItem(
    var dateMillisSinceEpoch: Long,
    var durationMinutes: Int,
    var attenuationValue: Int,
    var transmissionRiskLevel: Int,
    var totalRiskScore: Int,
    var attenuationDurations: IntArray
)
