package inz.mb.pl.app.fitapp.interfaces

import android.app.Activity
import android.content.Context
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import inz.mb.pl.app.fitapp.R
import inz.mb.pl.app.fitapp.models.TextLabelField

interface ErrorInterface {
    fun markErrorField(text : TextView, field : EditText, context : Context = getRequiredContext()){
        text.setTextColor(ContextCompat.getColor(context, R.color.red))
        field.setBackgroundResource(R.drawable.error_edit_text_drawable)
    }

    fun markErrorFields(listOfTextLabelField : List<TextLabelField>, context : Context = getRequiredContext()){
        for(textLabelField in listOfTextLabelField){
            markErrorField(textLabelField.textView,textLabelField.editText,context)
        }
    }

    fun unMarkErrorField(text : TextView, field : EditText, context : Context = getRequiredContext()){
        text.setTextColor(ContextCompat.getColor(context, R.color.black))
        field.setBackgroundResource(R.drawable.edit_text_drawable)
    }

    fun unMarkErrorFields(listOfTextLabelField : List<TextLabelField>, context : Context = getRequiredContext()){
        for(textLabelField in listOfTextLabelField){
            unMarkErrorField(textLabelField.textView,textLabelField.editText,context)
        }
    }

    fun showErrorSnackBar(message: String, activity : Activity = getRequiredActivity(), context: Context = getRequiredContext()) {
        with(Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)){
            val snackBarView = view
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.design_default_color_error
                )
            )
            show()
        }
    }

    fun getRequiredContext() : Context
    fun getRequiredActivity() : Activity


}