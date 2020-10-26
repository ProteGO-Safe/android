package pl.gov.mc.protegosafe.domain.model

interface TestSubscriptionConfigurationMapper {
    fun toEntity(testSubscriptionJson: String): TestSubscriptionConfigurationItem
}
