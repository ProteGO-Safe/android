package pl.gov.mc.protego.ui.registration.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.gov.mc.protego.R

class OnboardingViewModel : ViewModel() {

    private val pages = listOf(
        PageInfo(
            illustration = R.drawable.onboarding_01,
            title = R.string.onboarding_hello_title,
            description = R.string.onboarding_hello_description,
            backButtonVisible = false,
            nextButtonVisible = true
        ),
        PageInfo(
            illustration = R.drawable.onboarding_02,
            header = R.string.onboarding_status_title,
            description = R.string.onboarding_status_description,
            backButtonVisible = true,
            nextButtonVisible = true
        ),
        PageInfo(
            illustration = R.drawable.onboarding_03,
            header = R.string.onboarding_bluetooth_title,
            description = R.string.onboarding_bluetooth_description,
            backButtonVisible = true,
            nextButtonVisible = true
        ),
        PageInfo(
            illustration = R.drawable.onboarding_04,
            header = R.string.onboarding_sharing_title,
            description = R.string.onboarding_sharing_description,
            backButtonVisible = true,
            nextButtonVisible = true
        )
    )

    private var currentPageIndex = 0
        set(value) {
            field = value
            page.value = pages[currentPageIndex]
        }

    val page = MutableLiveData<PageInfo>()
    val navigateToRegistration = MutableLiveData<Unit>()

    init {
        page.value = pages[currentPageIndex]
    }

    fun onNextClicked() {
        if (currentPageIndex < pages.lastIndex) {
            currentPageIndex++
        } else {
            navigateToRegistration.value = Unit
        }
    }

    fun onBackClicked() {
        if (currentPageIndex > 0) {
            currentPageIndex--
        }
    }
}

data class PageInfo(
    @DrawableRes val illustration: Int,
    @StringRes val title: Int? = null,
    @StringRes val header: Int? = null,
    @StringRes val description: Int,
    val backButtonVisible: Boolean,
    val nextButtonVisible: Boolean
)