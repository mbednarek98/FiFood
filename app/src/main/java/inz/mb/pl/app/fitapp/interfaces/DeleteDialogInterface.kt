package inz.mb.pl.app.fitapp.interfaces

import android.app.AlertDialog
import android.content.Context


interface DeleteDialogInterface {
    fun createDeleteDialog(context : Context, title : String, description : String){
        val builder = AlertDialog.Builder(context)
        with(builder) {
            setTitle(title)
            setMessage(description)
            setPositiveButton(android.R.string.ok) { _, _ -> positiveButtonFun() }
            setNegativeButton(android.R.string.cancel) { _, _ ->  }

        }.create().show()
    }

    fun positiveButtonFun()
}