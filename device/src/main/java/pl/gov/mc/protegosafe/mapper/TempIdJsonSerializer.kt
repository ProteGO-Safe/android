package pl.gov.mc.protegosafe.mapper

import com.google.gson.Gson
import pl.gov.mc.protegosafe.model.BtServicesStatus
import pl.gov.mc.protegosafe.model.BtServicesStatusRoot

class TempIdJsonSerializer {

    fun toJson(tempId: String): String =
        Gson().toJson(
            BtServicesStatusRoot(
                btServiceStatus = BtServicesStatus(
                    tempId = tempId
                )
            )
        )
}