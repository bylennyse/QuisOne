package se.bylenny.quiz.views.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import se.bylenny.quiz.databinding.StartFragmentBinding

@AndroidEntryPoint
class StartFragment : Fragment() {

    companion object {
        private const val TAG = "StartFragment"
        fun newInstance() = StartFragment()
    }

    private val viewModel: StartViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = StartFragmentBinding.inflate(inflater, container, false).also {
        it.lifecycleOwner = viewLifecycleOwner
        it.viewModel = viewModel
    }.root
}
