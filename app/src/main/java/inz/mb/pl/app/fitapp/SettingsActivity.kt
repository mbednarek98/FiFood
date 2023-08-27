package inz.mb.pl.app.fitapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import inz.mb.pl.app.fitapp.adapter.SettingsAdapter
import inz.mb.pl.app.fitapp.databinding.ActivitySettingsBinding
import inz.mb.pl.app.fitapp.models.User
import inz.mb.pl.app.fitapp.parent.HelperActivity

import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import inz.mb.pl.app.fitapp.database.FirestoreClass
import inz.mb.pl.app.fitapp.helper.Helper
import inz.mb.pl.app.fitapp.interfaces.DialogInterface
import inz.mb.pl.app.fitapp.interfaces.ErrorInterface
import inz.mb.pl.app.fitapp.interfaces.SpinnerInterface


class SettingsActivity : HelperActivity() , DialogInterface, ErrorInterface, SpinnerInterface {
    private lateinit var auth : FirebaseAuth
    private val binding by lazy { ActivitySettingsBinding.inflate(layoutInflater)}
    private lateinit var user : User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        user = getUserFromIntent()
        setContentView(binding.root)
        screenSetUp(window)
        binding.settingsToolbar.setNavigationOnClickListener {
            FirestoreClass().updateUser(user, this)
        }
        setUpListView()
    }

    fun finishSettingsActivity(){
        val intent = Intent()
        intent.putExtra("new_user", user)
        println(user.height)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun getUserFromIntent(flag : String = "user_data") = when {
        intent.hasExtra(flag) -> intent.extras?.getParcelable(flag)!!
        else -> User()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }

    private fun setUpListView(){
        val (list, map) = setUpData()
        val adapter = SettingsAdapter(list, map)
        with(binding) {
            settingsExpandableListView.setAdapter(adapter)
            for (i in 0 until adapter.groupCount) settingsExpandableListView.expandGroup(i)
        }
    }

    private fun setUpData() : Pair<List<String>, Map<String, List<Triple<String, String, Runnable>>>> {
        val titles : List<String> = resources.getStringArray(R.array.settings_titles).toList()
        val listOfCredentials : List<Triple<String, String, Runnable>> = listOf(
            Triple(resources.getString(R.string.age),user.age.toString(),setAgeDialog()),
            Triple(resources.getString(R.string.gender),resources.getStringArray(R.array.gender_array)[user.gender], setGenderDialog()),
            Triple(resources.getString(R.string.height),resources.getString(R.string.data_cm,user.height.toString()),setHeightDialog())
        )
        val listOfActivity : List<Triple<String,String,Runnable>> = listOf(
            Triple(resources.getString(R.string.activity), resources.getStringArray(R.array.activity_array)[user.activity],setActivityDialog()),
            Triple(resources.getString(R.string.weight_progress), resources.getStringArray(R.array.progress_array)[user.progress],setProgressDialog())
        )

        val listOfMeal : List<Triple<String,String,Runnable>> = listOf(
            Triple(resources.getString(R.string.meal_count),user.dishesCount.toString(),setMealCountDialog()),
            Triple(resources.getString(R.string.meal_type), resources.getStringArray(R.array.food_type_of_diet)[user.dishesType],setMealTypeDialog())
        )
        return Pair(titles,mutableMapOf( titles[0] to listOfCredentials, titles[1] to listOfActivity, titles[2] to listOfMeal))
    }

    private fun setAgeDialog() : Runnable = Runnable {
        val textViewString = resources.getString(R.string.age)
        val (dialog, holder) = createEditTextDialog(resources.getString(R.string.change_age),textViewString,user.age.toString())
        with(dialog){
            getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                user.age = setValueToUser(holder, this, Helper.AGE_RANGE, textViewString, Helper.AGE_LOWER_RANGE, Helper.AGE_UPPER_RANGE, user.age)
                setUpListView()
            }
        }
    }

    private fun setHeightDialog() : Runnable = Runnable {
        val textViewString = resources.getString(R.string.height)
        val (dialog, holder) = createEditTextDialog(resources.getString(R.string.change_height),textViewString,user.height.toString())
        with(dialog){
            getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                user.height = setValueToUser(holder, this, Helper.HEIGHT_RANGE, textViewString, Helper.HEIGHT_LOWER_RANGE, Helper.HEIGHT_UPPER_RANGE, user.height)
                setUpListView()
            }
        }
    }


    private fun setMealCountDialog() : Runnable = Runnable {
        val textViewString = resources.getString(R.string.meal_count)
        val (dialog, holder) = createEditTextDialog(resources.getString(R.string.change_dish),textViewString,user.dishesCount.toString())
        with(dialog){
            getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                user.dishesCount = setValueToUser(holder, this, Helper.DISHES_RANGE, textViewString, Helper.DISHES_LOWER_RANGE, Helper.DISHES_UPPER_RANGE, user.dishesCount)
                setUpListView()
            }
        }
    }


    private fun setGenderDialog() : Runnable = Runnable {
        val builder = createNewAlertDialog(resources.getString(R.string.change_gender))
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.change_spinner_dialog, null)
        val holder = OneSpinnerViewHolder(dialogView)
        with(holder){
            textView.text = resources.getString(R.string.gender)
            assignNewSpinnerAdapter(spinner, R.array.gender_array, R.array.gender_image_array)
            spinner.setSelection(user.gender)
        }
        with(builder){
            setView(dialogView)
            setPositiveButton(getDialogContext().getString(R.string.done)) {_, _ ->
                user.gender = holder.spinner.selectedItemPosition
                setUpListView()
            }
            setNegativeButton(getDialogContext().getString(R.string.cancel)) { _,_ ->}
        }.create().show()
    }

    private fun setActivityDialog() : Runnable = Runnable {
        val builder = createNewAlertDialog(resources.getString(R.string.change_activity))
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.change_spinner_dialog, null)
        val holder = OneSpinnerViewHolder(dialogView)
        with(holder){
            textView.text = resources.getString(R.string.activity)
            assignNewSpinnerAdapter(spinner, R.array.activity_array, R.array.activity_description_array,user.activity)
        }
        with(builder){
            setView(dialogView)
            setPositiveButton(getDialogContext().getString(R.string.done)) {_, _ ->
                user.activity = holder.spinner.selectedItemPosition
                setUpListView()
            }
            setNegativeButton(getDialogContext().getString(R.string.cancel)) { _,_ ->}
        }.create().show()
    }

    private fun setProgressDialog() : Runnable = Runnable {
        val builder = createNewAlertDialog(resources.getString(R.string.change_progress))
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.change_spinner_dialog, null)
        val holder = OneSpinnerViewHolder(dialogView)
        with(holder){
            textView.text = resources.getString(R.string.weight_progress)
            assignNewSpinnerAdapter(spinner, R.array.progress_array, R.array.progress_description_array,user.progress)
        }
        with(builder){
            setView(dialogView)
            setPositiveButton(getDialogContext().getString(R.string.done)) {_, _ ->
                user.progress = holder.spinner.selectedItemPosition
                setUpListView()
            }
            setNegativeButton(getDialogContext().getString(R.string.cancel)) { _,_ ->}
        }.create().show()
    }


    private fun setMealTypeDialog() : Runnable = Runnable {
        val builder = createNewAlertDialog(resources.getString(R.string.change_type))
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.change_spinner_dialog, null)
        val holder = OneSpinnerViewHolder(dialogView)
        with(holder){
            textView.text = resources.getString(R.string.meal_type)
            assignTextSpinnerAdapter(spinner, R.array.food_type_of_diet)
            spinner.setSelection(user.dishesType)
        }
        with(builder){
            setView(dialogView)
            setPositiveButton(getDialogContext().getString(R.string.done)) {_, _ ->
                user.dishesType = holder.spinner.selectedItemPosition
                setUpListView()
            }
            setNegativeButton(getDialogContext().getString(R.string.cancel)) { _,_ ->}
        }.create().show()
    }


    override fun getDialogContext(): Context = this

    inner class OneBoxViewHolder(dialogView: View){
        val textView : TextView = dialogView.findViewById(R.id.dialog_one_text_view)
        val editText : EditText = dialogView.findViewById(R.id.dialog_one_edit_text)
    }
    inner class OneSpinnerViewHolder(dialogView: View){
        val textView : TextView = dialogView.findViewById(R.id.dialog_spinner_text_view)
        val spinner : AppCompatSpinner = dialogView.findViewById(R.id.dialog_spinner)
    }

    private fun createEditTextDialog(title: String, textViewString : String, editTextString : String): Pair<AlertDialog, OneBoxViewHolder> {
        val builder = createNewAlertDialog(title)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.change_one_int_dialog, null)
        val holder = OneBoxViewHolder(dialogView)
        with(holder) {
            textView.text = textViewString
            editText.setText(editTextString)
        }
        val dialog = with(builder){
            setView(dialogView)
            setPositiveButton(getDialogContext().getString(R.string.done), null)
            setNegativeButton(getDialogContext().getString(R.string.cancel)) { _,_ ->}
        }.create()
        dialog.show()
        return Pair(dialog, holder)
    }

    private fun setValueToUser(
        holder: OneBoxViewHolder,
        dialog: AlertDialog,
        range: IntRange,
        textViewString: String,
        lowerRange: Int,
        higherRange: Int,
        integerNumber : Int
    ) : Int {
        var old = integerNumber
        val ageTextView = holder.textView
        val ageEditText = holder.editText
        when (val height = ageEditText.text.toString().toInt()) {
            in range -> {
                unMarkErrorField(ageTextView, ageEditText)
                old = height
                dialog.dismiss()
            }
            else -> {
                errorField(ageTextView,
                    ageEditText,
                    textViewString,
                    lowerRange,
                    higherRange)
            }
        }
        return old
    }


    private fun errorField(textView : TextView, editText: EditText, name : String, lowerRange : Int, upperRange : Int){
        markErrorField(textView,editText)
        showErrorSnackBar(resources.getString(R.string.error_overall,name, lowerRange, upperRange))
    }

}