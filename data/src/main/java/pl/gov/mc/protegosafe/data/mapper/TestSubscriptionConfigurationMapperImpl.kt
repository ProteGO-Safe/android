package pl.gov.mc.protegosafe.data.mapper

import com.google.gson.Gson
import pl.gov.mc.protegosafe.data.model.covidtest.TestSubscriptionConfigurationData
import pl.gov.mc.protegosafe.domain.model.TestSubscriptionConfigurationItem
import pl.gov.mc.protegosafe.domain.model.TestSubscriptionConfigurationMapper

class TestSubscriptionConfigurationMapperImpl : TestSubscriptionConfigurationMapper {
    override fun toEntity(testSubscriptionJson: String): TestSubscriptionConfigurationItem {
        return Gson().fromJson(testSubscriptionJson, TestSubscriptionConfigurationData::class.java)
            .toEntity()
    }
}
