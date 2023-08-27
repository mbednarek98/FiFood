package inz.mb.pl.app.fitapp

import android.content.Intent
import android.os.Bundle
import inz.mb.pl.app.fitapp.databinding.ActivityStartBinding
import inz.mb.pl.app.fitapp.parent.HelperActivity
import inz.mb.pl.app.fitapp.sign_activities.SignInActivity
import inz.mb.pl.app.fitapp.sign_activities.SignUpActivity

class StartActivity : HelperActivity() {
    private val binding by lazy { ActivityStartBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        screenSetUp(window)

        binding.startSignIn.setOnClickListener {
            changeActivity(this,Intent(this, SignInActivity::class.java))
        }
        binding.startSignUp.setOnClickListener {
            changeActivity(this, Intent(this, SignUpActivity::class.java))
        }

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
    }



}