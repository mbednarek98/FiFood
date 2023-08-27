package inz.mb.pl.app.fitapp.interfaces

import android.app.AlertDialog
import android.content.Context


interface DialogInterface {
    fun createNewAlertDialog(titleName : String? = null, description : String? = null): AlertDialog.Builder {
        val dialog = AlertDialog.Builder(getDialogContext())
        dialog.setTitle(titleName)
        dialog.setMessage(description)
        return dialog
    }


    fun getDialogContext() : Context

}