package pl.gov.mc.protegosafe.ui.common

import androidx.fragment.app.Fragment
import io.reactivex.rxjava3.disposables.CompositeDisposable

abstract class BaseFragment : Fragment() {

    protected val disposables = CompositeDisposable()

    override fun onDetach() {
        super.onDetach()
        disposables.clear()
    }
}
