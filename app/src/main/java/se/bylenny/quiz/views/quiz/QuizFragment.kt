package se.bylenny.quiz.views.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.quiz_fragment.*
import se.bylenny.quiz.databinding.QuizFragmentBinding
import se.bylenny.quiz.extensions.observe

@AndroidEntryPoint
class QuizFragment : Fragment() {

    companion object {
        fun newInstance() = QuizFragment()
    }

    private val viewModel: QuizViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = QuizFragmentBinding.inflate(inflater, container, false).also {
        it.lifecycleOwner = viewLifecycleOwner
        it.viewModel = viewModel
    }.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        pager.apply {
            isUserInputEnabled = false
            offscreenPageLimit = 1
        }

        observe(viewModel.currentPage) { page ->
            pager.currentItem = page
        }
    }

}
