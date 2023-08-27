package inz.mb.pl.app.fitapp.ui.get_started

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import inz.mb.pl.app.fitapp.R
import inz.mb.pl.app.fitapp.databinding.FragmentGetStartedFirstBinding
import inz.mb.pl.app.fitapp.interfaces.SpinnerInterface


class GetStartedFragmentOne : GetStartedFragment(), SpinnerInterface {

    private var _binding: FragmentGetStartedFirstBinding? = null
    val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGetStartedFirstBinding.inflate(inflater, container, false)

        assignNewSpinnerAdapter(binding.spinner, R.array.gender_array, R.array.gender_image_array)
        binding.nextButtonFirstFragment.setOnClickListener {
            getSActivity()?.viewPager2?.setCurrentItem(1, true)
        }

        return binding.root
    }

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"
        @JvmStatic
        fun newInstance(sectionNumber: Int): GetStartedFragmentOne {
            return GetStartedFragmentOne().apply {
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