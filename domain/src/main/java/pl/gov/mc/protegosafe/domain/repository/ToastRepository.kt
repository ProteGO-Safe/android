package pl.gov.mc.protegosafe.domain.repository

interface ToastRepository {
    fun showIsBtServiceEnabledInfo(isEnabled: Boolean)
}