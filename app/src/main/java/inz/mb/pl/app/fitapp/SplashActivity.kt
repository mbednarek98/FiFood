package inz.mb.pl.app.fitapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import inz.mb.pl.app.fitapp.database.FirestoreClass
import inz.mb.pl.app.fitapp.database.AppDatabase
import inz.mb.pl.app.fitapp.database.dish.DishDTO
import inz.mb.pl.app.fitapp.database.exercise.ExerciseDTO
import inz.mb.pl.app.fitapp.database.saved_dish.SavedDishDTO
import inz.mb.pl.app.fitapp.databinding.ActivitySplashBinding
import inz.mb.pl.app.fitapp.enums.DBTypeEnum
import inz.mb.pl.app.fitapp.helper.Helper
import inz.mb.pl.app.fitapp.parent.HelperActivity
import inz.mb.pl.app.fitapp.interfaces.QueryInterface


@SuppressLint("CustomSplashScreen")
class SplashActivity : HelperActivity(),QueryInterface {
    private lateinit var auth : FirebaseAuth
    private val binding by lazy { ActivitySplashBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Helper.db = AppDatabase.open(applicationContext)
        auth = Firebase.auth
        screenSetUp(window)
        removeNavigationBar(window)
        goToNextActivity()
    }


    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_down)
    }

    private fun goToNextActivity(){
        Handler(Looper.getMainLooper()).postDelayed({
            when {
                auth.currentUser != null -> {
                    Helper.exerciseDTOList = selectData(type = DBTypeEnum.EXERCISE) as ArrayList<ExerciseDTO>
                    Helper.dishDTOList = selectData(type = DBTypeEnum.DISH) as ArrayList<DishDTO>
                    Helper.savedDishesList = selectData(type = DBTypeEnum.SAVEDDISH) as ArrayList<SavedDishDTO>
                    FirestoreClass().splashScreenAuth(this)
                }
                else -> {
                    changeActivityDownUp(this, Intent(this,StartActivity::class.java))
                }
            }
        }, 2000)
    }

}