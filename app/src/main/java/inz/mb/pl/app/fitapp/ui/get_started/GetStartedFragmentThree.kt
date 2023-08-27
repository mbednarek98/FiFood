package inz.mb.pl.app.fitapp.ui.get_started

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import inz.mb.pl.app.fitapp.MainActivity
import inz.mb.pl.app.fitapp.R
import inz.mb.pl.app.fitapp.database.FirestoreClass
import inz.mb.pl.app.fitapp.databinding.FragmentGetStartedFirstBinding
import inz.mb.pl.app.fitapp.databinding.FragmentGetStartedSecondBinding
import inz.mb.pl.app.fitapp.databinding.FragmentGetStartedThirdBinding
import inz.mb.pl.app.fitapp.helper.Helper
import inz.mb.pl.app.fitapp.helper.Helper.AGE_LOWER_RANGE
import inz.mb.pl.app.fitapp.helper.Helper.AGE_RANGE
import inz.mb.pl.app.fitapp.helper.Helper.AGE_UPPER_RANGE
import inz.mb.pl.app.fitapp.helper.Helper.DISHES_LOWER_RANGE
import inz.mb.pl.app.fitapp.helper.Helper.DISHES_RANGE
import inz.mb.pl.app.fitapp.helper.Helper.DISHES_UPPER_RANGE
import inz.mb.pl.app.fitapp.helper.Helper.HEIGHT_LOWER_RANGE
import inz.mb.pl.app.fitapp.helper.Helper.HEIGHT_RANGE
import inz.mb.pl.app.fitapp.helper.Helper.HEIGHT_UPPER_RANGE
import inz.mb.pl.app.fitapp.helper.Helper.WEIGHT_LOWER_RANGE
import inz.mb.pl.app.fitapp.helper.Helper.WEIGHT_RANGE
import inz.mb.pl.app.fitapp.helper.Helper.WEIGHT_UPPER_RANGE
import inz.mb.pl.app.fitapp.interfaces.SpinnerInterface
import inz.mb.pl.app.fitapp.models.TextLabelField
import inz.mb.pl.app.fitapp.models.User

class GetStartedFragmentThree : GetStartedFragment(), SpinnerInterface {
    private lateinit var user : User
    private var _binding: FragmentGetStartedThirdBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGetStartedThirdBinding.inflate(inflater, container, false)
        assignSpinners()
        FirestoreClass().getCurrentUser(this)
        binding.finishButton.setOnClickListener {
            val bindingFirst = Helper.FragmentOne!!.binding
            val bindingSecond = Helper.FragmentTwo!!.binding
            if(validateFirstFragment(bindingFirst) && validateSecondFragment(bindingSecond) && validateThirdFragment(binding)) {
                saveDataToUser(bindingFirst,bindingSecond)
                FirestoreClass().setUserDetails(this,user)
            }
        }
        return binding.root
    }

    fun assignUser(user : User) {
        this.user = user
    }



    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(sectionNumber: Int): GetStartedFragmentThree {
            return GetStartedFragmentThree().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    fun changeToMainActivity(user : User){
        val intent = Intent(this.context, MainActivity::class.java)
        intent.putExtra("user_details", user)
        startActivity(intent)
        requireActivity().overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun assignSpinners() =
        with(binding) {
            assignNewSpinnerAdapter(
                spinnerActivity,
                R.array.activity_array,
                R.array.activity_description_array,
                0
            )
            assignNewSpinnerAdapter(
                spinnerWeight,
                R.array.progress_array,
                R.array.progress_description_array,
                3
            )
        }


    private fun validateFirstFragment(binding : FragmentGetStartedFirstBinding) : Boolean =
        with(binding){
            val age : String = ageEditText.text.toString()
            return when {
                ifNotEmpty(age,AGE_RANGE) -> {
                    unMarkErrorField(ageTextView,ageEditText)
                    true
                }
                else -> {
                    errorField(ageTextView,ageEditText,resources.getString(R.string.age),AGE_LOWER_RANGE, AGE_UPPER_RANGE,0)
                    false
                }
            }

        }


    private fun validateThirdFragment(binding: FragmentGetStartedThirdBinding) : Boolean =
        with(binding) {
            val dishesCount: String = editTextMeals.text.toString()
            return when {
                ifNotEmpty(dishesCount, DISHES_RANGE) -> {
                    unMarkErrorField(textMeals, editTextMeals)
                    true
                }
                else -> {
                    errorField(textMeals,editTextMeals,resources.getString(R.string.dishes),DISHES_LOWER_RANGE, DISHES_UPPER_RANGE,2)
                    false
                }
            }
        }


    private fun validateSecondFragment(binding : FragmentGetStartedSecondBinding) : Boolean =
        with(binding){
            val height : String = heightEditText.text.toString()
            val weight : String = weightEditText.text.toString()
            return when {
                ifNotEmpty(height,HEIGHT_RANGE) -> {
                    unMarkErrorField(heightTextView,heightEditText)
                    when {
                        ifNotEmpty(weight,WEIGHT_RANGE) -> {
                            unMarkErrorField(weightTextView,weightEditText)
                            true
                        }
                        else -> {
                            errorField(weightTextView,weightEditText,resources.getString(R.string.weight),WEIGHT_LOWER_RANGE, WEIGHT_UPPER_RANGE,1)
                            false
                        }
                    }
                }
                ifNotEmpty(weight,WEIGHT_RANGE) -> {
                    unMarkErrorField(weightTextView,weightEditText)
                    when {
                        ifNotEmpty(height, HEIGHT_RANGE) -> {
                            unMarkErrorField(heightTextView,heightEditText)
                            true
                        }
                        else -> {
                            errorField(heightTextView,heightEditText,resources.getString(R.string.height),HEIGHT_LOWER_RANGE, HEIGHT_UPPER_RANGE,1)
                            false
                        }
                    }
                }
                else -> {
                    markErrorFields(listOf(TextLabelField(heightTextView,heightEditText), TextLabelField(weightTextView,heightEditText)))
                    showErrorSnackBar(resources.getString(R.string.error_height_weight_overall))
                    getSActivity()?.setCurrentItem(1, true)
                    false
                }
            }

        }


    private fun errorField(textView : TextView, editText: EditText, name : String, lowerRange : Int, upperRange : Int, fragmentNumber : Int){
        markErrorField(textView,editText)
        showErrorSnackBar(resources.getString(R.string.error_overall,name, lowerRange, upperRange))
        getSActivity()?.setCurrentItem(fragmentNumber, true)
    }
    private fun ifNotEmpty(number : String, range : IntRange) : Boolean = number.isNotEmpty() && range.contains(number.toDouble().toInt())
    private fun saveDataToUser(bindingFirst : FragmentGetStartedFirstBinding,bindingSecond : FragmentGetStartedSecondBinding){
        with(user){
            age  = bindingFirst.ageEditText.text.toString().toInt()
            gender = bindingFirst.spinner.selectedItemPosition
            height = bindingSecond.heightEditText.text.toString().toInt()
            weight = bindingSecond.weightEditText.text.toString().toDouble()
            activity =  binding.spinnerActivity.selectedItemPosition
            progress =  binding.spinnerWeight.selectedItemPosition
            dishesCount = binding.editTextMeals.text.toString().toInt()
        }
    }


}