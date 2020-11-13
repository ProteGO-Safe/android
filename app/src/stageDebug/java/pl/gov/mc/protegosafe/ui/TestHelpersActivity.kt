package pl.gov.mc.protegosafe.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.KoinComponent
import pl.gov.mc.protegosafe.R
import pl.gov.mc.protegosafe.databinding.ActivityTestHelpersBinding

class TestHelpersActivity() : AppCompatActivity(), KoinComponent {

    private val viewModel: TestHelpersViewModel by viewModel()
    private lateinit var binding: ActivityTestHelpersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_test_helpers)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        initObservers()
    }

    private fun initObservers() {
        viewModel.successfulEvent.observe(
            this,
            {
                Toast.makeText(this, "$it set", Toast.LENGTH_LONG).show()
                finish()
            }
        )
        viewModel.failedEvent.observe(
            this,
            {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        )
    }
}
