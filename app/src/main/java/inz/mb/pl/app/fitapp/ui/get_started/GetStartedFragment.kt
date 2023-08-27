package inz.mb.pl.app.fitapp.ui.get_started

import android.app.Activity
import android.content.Context

import inz.mb.pl.app.fitapp.parent.BasicFragment
import inz.mb.pl.app.fitapp.GetStartedActivity
import inz.mb.pl.app.fitapp.interfaces.ErrorInterface

open class GetStartedFragment : BasicFragment(), ErrorInterface{
    fun getSActivity() : GetStartedActivity? = (activity as GetStartedActivity?)
    override fun getRequiredContext(): Context = this.requireContext()
    override fun getRequiredActivity(): Activity = this.requireActivity()


}