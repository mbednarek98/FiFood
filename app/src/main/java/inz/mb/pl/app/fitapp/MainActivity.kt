package inz.mb.pl.app.fitapp


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import inz.mb.pl.app.fitapp.database.water_change.WaterChangeDTO
import inz.mb.pl.app.fitapp.databinding.ActivityMainBinding
import inz.mb.pl.app.fitapp.parent.HelperActivity
import inz.mb.pl.app.fitapp.models.User
import inz.mb.pl.app.fitapp.database.weight_change.WeightChangeDTO
import inz.mb.pl.app.fitapp.helper.Helper

class MainActivity : HelperActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    lateinit var user : User
    var weightChangeDTO: WeightChangeDTO? = null
    var waterChangeDTO: WaterChangeDTO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = getUserFromIntent()
        weightChangeDTO = getWeightChangeFromIntent()
        waterChangeDTO = getWaterChangeFromIntent()
        Helper.mainActivity = this
        setContentView(binding.root)
        screenSetUp(window)

        val navView: BottomNavigationView = binding.mainNavBar
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard, R.id.navigation_workout, R.id.navigation_diet,
            )
        )

        navView.setupWithNavController(navController)
    }

    private fun getUserFromIntent(flag : String = "user_details") = when {
        intent.hasExtra(flag) -> intent.extras?.getParcelable(flag)!!
        else -> User()
    }
    private fun getWeightChangeFromIntent(flag : String = "weight_change"): WeightChangeDTO? = when {
        intent.hasExtra(flag) -> intent.extras?.getParcelable(flag)!!
        else -> null
    }
    private fun getWaterChangeFromIntent(flag: String = "water_change"): WaterChangeDTO? = when {
        intent.hasExtra(flag) -> intent.extras?.getParcelable(flag)!!
        else -> null
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            user = data?.extras?.getParcelable("new_user") ?: User()
            println(user)
        }
        println(user.height)
    }

}