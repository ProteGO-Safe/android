package pl.gov.mc.protego.ui.registration.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.gov.mc.protego.R
import pl.gov.mc.protego.ui.Event

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
            val forward = value > field
            field = value
            _page.value = PageChange(pages[currentPageIndex], forward)
        }

    val page: LiveData<PageChange>
        get() = _page
    private val _page = MutableLiveData<PageChange>()

    val navigateToRegistration: LiveData<Event<Unit>>
        get() = _navigateToRegistration
    private val _navigateToRegistration = MutableLiveData<Event<Unit>>()

    val finishApplication: LiveData<Event<Unit>>
        get() = _finishApplication
    private val _finishApplication = MutableLiveData<Event<Unit>>()

    init {
        _page.value = PageChange(pages[currentPageIndex], true)
    }

    fun onNextClicked() {
        if (currentPageIndex < pages.lastIndex) {
            currentPageIndex++
        } else {
            _navigateToRegistration.value = Event(Unit)
        }
    }

    fun onBackClicked() {
        if (currentPageIndex > 0) {
            currentPageIndex--
        } else {
            _finishApplication.value = Event(Unit)
        }
    }
}

data class PageChange(val pageInfo: PageInfo, val forward: Boolean)

data class PageInfo(
    @DrawableRes val illustration: Int,
    @StringRes val title: Int? = null,
    @StringRes val header: Int? = null,
    @StringRes val description: Int,
    val statusLegendVisible: Boolean,
    val backButtonVisible: Boolean,
    val nextButtonVisible: Boolean
)