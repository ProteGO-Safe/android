package pl.gov.mc.protegosafe.domain.model

sealed class DistrictsUpdatedNotificationType {
    object EmptySubscribedDistrictsList : DistrictsUpdatedNotificationType()
    object NoDistrictsUpdated : DistrictsUpdatedNotificationType()
    class DistrictsUpdated(val districts: List<DistrictItem>) : DistrictsUpdatedNotificationType()
}
