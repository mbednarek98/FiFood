package inz.mb.pl.app.fitapp.ui.get_started

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import inz.mb.pl.app.fitapp.databinding.FragmentGetStartedSecondBinding
import inz.mb.pl.app.fitapp.GetStartedActivity


class GetStartedFragmentTwo : GetStartedFragment() {

    private var _binding: FragmentGetStartedSecondBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGetStartedSecondBinding.inflate(inflater, container, false)
        binding.nextButtonSecondFragment.setOnClickListener {
            (activity as GetStartedActivity?)?.setCurrentItem(2, true)
        }
        return binding.root
    }

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(sectionNumber: Int): GetStartedFragmentTwo {
            return GetStartedFragmentTwo().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}