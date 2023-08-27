package inz.mb.pl.app.fitapp.parent

import android.content.Intent
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import inz.mb.pl.app.fitapp.MainActivity
import inz.mb.pl.app.fitapp.R
import inz.mb.pl.app.fitapp.SettingsActivity
import inz.mb.pl.app.fitapp.StartActivity
import java.io.*


open class BasicFragment : Fragment() {
    lateinit var auth : FirebaseAuth
    fun toolBarSetup(toolbar: androidx.appcompat.widget.Toolbar){
            with(toolbar) {
                title = resources.getString(R.string.application_name)
                setTitleTextAppearance(context, R.style.toolbarStyle)
                inflateMenu(R.menu.overflow_menu)
                setOnMenuItemClickListener(this@BasicFragment::onItemClickMenu)
            }
    }

    private fun onItemClickMenu(item : MenuItem) : Boolean {
        when (item.itemId) {
            R.id.settings -> {
                val activity =  (activity as MainActivity)
                val intent = Intent(context,SettingsActivity::class.java)
                intent.putExtra("user_data", activity.user)
                startActivityForResult(intent, AppCompatActivity.RESULT_OK)
                activity.slideLeftToRight(activity)
            }
            R.id.sign_out -> {
                auth.signOut()
                startActivity(Intent(this.context, StartActivity::class.java))
                this.requireActivity().overridePendingTransition(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                this.requireActivity().finish()
            }
        }
        return false
    }

}