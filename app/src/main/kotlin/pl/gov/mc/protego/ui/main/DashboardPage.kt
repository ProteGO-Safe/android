package pl.gov.mc.protego.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import pl.gov.mc.protego.R
import pl.gov.mc.protego.ui.main.fragments.dashboard.DashboardMainFragment
import pl.gov.mc.protego.ui.main.fragments.history.HistoryFragment

sealed class DashboardPage(
    val pageFragmentTag: String
) {

    abstract fun createFragment(): Fragment
    abstract fun showFragment(fragmentManager: FragmentManager, fragmentToShow: Fragment)

    class MainPage : DashboardPage(DashboardMainFragment::class.java.simpleName) {

        override fun createFragment() = DashboardMainFragment()

        override fun showFragment(fragmentManager: FragmentManager, fragmentToShow: Fragment) {
            val currentFragment = fragmentManager.fragments.lastOrNull()

            when {
                currentFragment?.tag == pageFragmentTag -> return
                currentFragment != null -> {
                    fragmentManager.beginTransaction()
                        .setCustomAnimations(NO_ANIMATION_RES_ID, R.anim.slide_to_right)
                        .remove(currentFragment)
                        .commit()
                }
                else -> {
                    fragmentManager.beginTransaction()
                        .add(R.id.container, fragmentToShow, pageFragmentTag)
                        .commit()
                }
            }
        }
    }

    class HistoryPage : DashboardPage(HistoryFragment::class.java.simpleName) {

        override fun createFragment() = HistoryFragment()

        override fun showFragment(fragmentManager: FragmentManager, fragmentToShow: Fragment) {
            when {
                fragmentToShow.tag == pageFragmentTag -> return
                else -> {
                    fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_from_right, NO_ANIMATION_RES_ID)
                        .add(R.id.container, fragmentToShow, pageFragmentTag)
                        .commit()
                }
            }
        }
    }

    companion object {
        private const val NO_ANIMATION_RES_ID = -1
    }
}