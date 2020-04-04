package pl.gov.mc.protego.ui.registration.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.gov.mc.protego.R

class OnboardingViewModel : ViewModel() {

    private val pages = listOf(
        PageInfo(
            illustration = R.drawable.onboarding_01,
            title = R.string.onboarding_hello_title,
            description = R.string.onboarding_hello_description,
            statusLegendVisible = false,
            backButtonVisible = false,
            nextButtonVisible = true
        ),
        PageInfo(
            illustration = R.drawable.onboarding_02,
            header = R.string.onboarding_status_title,
            description = R.string.onboarding_status_description,
            statusLegendVisible = true,
            backButtonVisible = true,
            nextButtonVisible = true
        ),
        PageInfo(
            illustration = R.drawable.onboarding_03,
            header = R.string.onboarding_bluetooth_title,
            description = R.string.onboarding_bluetooth_description,
            statusLegendVisible = false,
            backButtonVisible = true,
            nextButtonVisible = true
        ),
        PageInfo(
            illustration = R.drawable.onboarding_04,
            header = R.string.onboarding_sharing_title,
            description = R.string.onboarding_sharing_description,
            statusLegendVisible = false,
            backButtonVisible = true,
            nextButtonVisible = true
        )
    )

    private var currentPageIndex = 0
        set(value) {
            field = value
            _page.value = pages[currentPageIndex]
        }

    val _page = MutableLiveData<PageInfo>()
    val page: LiveData<PageInfo>
        get() = _page
    val navigateToRegistration: LiveData<Unit>
        get() = _navigateToRegistration
    val _navigateToRegistration = MutableLiveData<Unit>()
    val _finishApplication = MutableLiveData<Unit>()
    val finishApplication: LiveData<Unit>
        get() = _finishApplication

    init {
        _page.value = pages[currentPageIndex]
    }

    fun onNextClicked() {
        if (currentPageIndex < pages.lastIndex) {
            currentPageIndex++
        } else {
            _navigateToRegistration.value = Unit
        }
    }

    fun onBackClicked() {
        if (currentPageIndex > 0) {
            currentPageIndex--
        } else {
            _finishApplication.value = Unit
        }
    }
}

data class PageInfo(
    @DrawableRes val illustration: Int,
    @StringRes val title: Int? = null,
    @StringRes val header: Int? = null,
    @StringRes val description: Int,
    val statusLegendVisible: Boolean,
    val backButtonVisible: Boolean,
    val nextButtonVisible: Boolean
)