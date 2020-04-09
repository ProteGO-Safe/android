package pl.gov.mc.protego.ui.registration.onboarding

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_onboarding.*
import kotlinx.android.synthetic.main.onboarding_page_view.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.gov.mc.protego.R
import pl.gov.mc.protego.ui.base.BaseActivity
import pl.gov.mc.protego.ui.observeLiveData
import pl.gov.mc.protego.ui.registration.RegistrationActivity

class OnboardingActivity : BaseActivity<Unit>() {

    override val viewModel: OnboardingViewModel by viewModel()

    private var pageAnimatorSet = AnimatorSet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        with(viewModel) {
            observeLiveData(page, ::showPage)
            observeLiveData(navigateToRegistration) { navigateToRegistrationEvent ->
                if (navigateToRegistrationEvent.getContentIfNotHandled() != null) {
                    navigateToRegistration()
                }
            }
            observeLiveData(finishApplication) { finishEvent ->
                if (finishEvent.getContentIfNotHandled() != null) {
                    finish()
                }
            }
        }
        back_button.setOnClickListener {
            if (!pageAnimatorSet.isRunning) { // ignore click if animation is in progress
                viewModel.onBackClicked()
            }
        }
        next_button.setOnClickListener {
            if (!pageAnimatorSet.isRunning) { // ignore click if animation is in progress
                viewModel.onNextClicked()
            }
        }
    }

    override fun onBackPressed() {
        viewModel.onBackClicked()
    }

    private fun showPage(pageChange: PageChange) {
        val nextPage = layoutInflater.inflate(
            R.layout.onboarding_page_view,
            page_container,
            false
        )

        setupPage(nextPage, pageChange.pageInfo)

        if (page_container.childCount == 0) {
            // no pages displayed yet, so add it without animation
            page_container.addView(nextPage)
        } else {
            animatePageChange(
                page_container,
                page_container.getChildAt(0),
                nextPage,
                pageChange.forward
            )
        }
    }

    private fun setupPage(page: View, pageInfo: PageInfo) {
        page.run {
            illustration.setImageResource(pageInfo.illustration)
            page_title.displayIfNotNull(pageInfo.title)
            header.displayIfNotNull(pageInfo.header)
            description.setText(pageInfo.description)
            back_button.isVisible = pageInfo.backButtonVisible
            next_button.isVisible = pageInfo.nextButtonVisible
            status_legend.isVisible = pageInfo.statusLegendVisible
        }
    }

    private fun animatePageChange(
        containerView: ViewGroup,
        oldPage: View,
        newPage: View,
        forward: Boolean
    ) {
        // slide out old page, slide in new page
        // and remove old page from hierarchy when the animation is finished
        val newPageInitialTranslationX = if (forward) {
            containerView.width
        } else {
            -containerView.width
        }

        val oldPageFinalTranslationX = if (forward) {
            -containerView.width
        } else {
            containerView.width
        }

        newPage.x += newPageInitialTranslationX
        containerView.addView(newPage)

        val oldPageTranslation =
            ObjectAnimator.ofFloat(oldPage, View.TRANSLATION_X, oldPageFinalTranslationX.toFloat())
                .apply {
                    interpolator = AccelerateDecelerateInterpolator()
                    doOnEnd {
                        containerView.removeView(oldPage)
                    }
                }

        val newPageTranslation = ObjectAnimator.ofFloat(newPage, View.TRANSLATION_X, 0f)
            .apply {
                interpolator = AccelerateDecelerateInterpolator()
            }

        pageAnimatorSet.apply {
            doOnEnd { pageAnimatorSet = AnimatorSet() }
            playTogether(oldPageTranslation, newPageTranslation)
        }.start()
    }

    override fun observeIsInProgress() = Unit

    private fun navigateToRegistration() =
        startActivity(Intent(this, RegistrationActivity::class.java))
}

private fun TextView.displayIfNotNull(@StringRes textId: Int?) {
    if (textId == null) {
        visibility = View.GONE
    } else {
        visibility = View.VISIBLE
        setText(textId)
    }
}
