package inz.mb.pl.app.fitapp.parent

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import inz.mb.pl.app.fitapp.R
import inz.mb.pl.app.fitapp.database.water_change.WaterChangeDTO
import inz.mb.pl.app.fitapp.database.weight_change.WeightChangeDTO
import inz.mb.pl.app.fitapp.databinding.RightTitleComponentBinding
import inz.mb.pl.app.fitapp.interfaces.ErrorInterface
import inz.mb.pl.app.fitapp.models.User

open class HelperActivity : AppCompatActivity(), ErrorInterface {
    private lateinit var progressDialog: Dialog

    fun screenSetUp(window: Window) = with(window){
        setFullScreen(this)
        stayScreenAwake(this)
    }

    @Suppress("DEPRECATION")
    private fun setFullScreen(window: Window)  = with(window) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                insetsController?.hide(WindowInsets.Type.statusBars())
            }
            else -> {
                setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
            }
        }
    }


    private fun stayScreenAwake(window: Window) = window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    @Suppress("DEPRECATION")
    fun removeNavigationBar(window: Window) = with(window){
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                setDecorFitsSystemWindows(false)
                insetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                insetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
            else -> {
                decorView.apply {
                    systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
                }
            }
        }
    }

    fun settingUpRightTitleComponent(binding : RightTitleComponentBinding, titleText : String, descriptionText : String){
        with(binding) {
            rightTitleLabel.text = titleText
            rightDescriptionLabel.text = descriptionText
        }
    }

    fun showProgressDialog(dialogText: String) = Dialog(this).also {
        progressDialog = it
        with(progressDialog) {
            setContentView(R.layout.progress_dialog)
            findViewById<TextView>(R.id.progress_text).text = dialogText
            show()
        }
    }


    fun slideRightToLeft(activity: Activity) = activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

    fun slideLeftToRight(activity: Activity) = activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

    fun slideUpToDown(activity: Activity) = activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)

    fun slideDownToUp(activity: Activity) = activity.overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_up)

    fun changeActivityDownUp(activity: Activity, intent: Intent, user: User? = null, weightChange : WeightChangeDTO? = null, waterChange: WaterChangeDTO? = null) = with(activity){
        putExtraIntent(intent,user,weightChange,waterChange)
        startActivity(intent)
        slideDownToUp(this)
        finish()
    }

    fun changeActivity(activity: Activity, intent: Intent, user: User? = null, weightChange : WeightChangeDTO? = null, waterChange : WaterChangeDTO? = null) = with(activity) {
        putExtraIntent(intent,user,weightChange, waterChange)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }

    fun printLog(code : String,text : String, err : Throwable? = null){
        Log.d(code,text,err)
    }

    private fun putExtraIntent(intent: Intent, user: User?, weightChange : WeightChangeDTO?, waterChange: WaterChangeDTO?){
        if(user != null) intent.putExtra("user_details", user)
        if(weightChange != null) intent.putExtra("weight_change", weightChange)
        if(waterChange != null) intent.putExtra("water_change", waterChange)
    }


    fun hideProgressDialog() = progressDialog.dismiss()
    override fun getRequiredContext(): Context = this
    override fun getRequiredActivity(): Activity = this


}