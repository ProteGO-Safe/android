package pl.gov.anna.ui.registration

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.gov.anna.ui.validator.MsisdnValidator

class RegistrationViewModel(
    private val msisdnValidator: MsisdnValidator
)  : ViewModel() {

    val msisdnError = MutableLiveData<String?>()

    fun onNewMsisdn(msisdn: String) {
        if (!msisdnValidator.validate(msisdn)) {
            msisdnError.value = "Niepoprawny numer telefonu"
        } else {
            msisdnError.value = null
        }
    }
}