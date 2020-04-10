package pl.gov.mc.protego.ui.main.fragments.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.gov.mc.protego.R
import pl.gov.mc.protego.ui.base.BaseFragment

class DashboardMainFragment : BaseFragment() {

    override val viewModel: DashboardMainViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }
}

