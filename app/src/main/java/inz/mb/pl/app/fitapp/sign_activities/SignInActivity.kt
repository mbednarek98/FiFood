package inz.mb.pl.app.fitapp.sign_activities

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import inz.mb.pl.app.fitapp.BaseActivity
import inz.mb.pl.app.fitapp.R
import inz.mb.pl.app.fitapp.database.FirestoreClass
import inz.mb.pl.app.fitapp.databinding.ActivitySignInBinding


class SignInActivity : BaseActivity() {
    private val binding by lazy { ActivitySignInBinding.inflate(layoutInflater) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        screenSetUp(window)
        settingUpRightTitleComponent(
            binding.titleComponent,
            resources.getString(R.string.welcome),
            resources.getString(R.string.welcome_desc_sign_in)
        )
    }


    override fun buttonSetUp() = with(binding) {
            signIn.setOnClickListener {
                clientSideSignInValidation() }
            googleButton.setOnClickListener {
                googleSignIn() }
            facebookButton.setOnClickListener {
                facebookLogIn() }
            signUp.setOnClickListener {
                changeActivity(
                    this@SignInActivity,
                    Intent(this@SignInActivity, SignUpActivity::class.java)
                )
            }
        }



    private fun clientSideSignInValidation() =
        when {
            checkIfNotEmpty(binding.emailText,binding.emailEditText) && checkIfNotEmpty(binding.passwordText,binding.passwordEditText) -> {
                with(binding) {
                    signIn(trimText(emailEditText), trimText(passwordEditText))
                }
            }
            else -> {
                printLog(this.localClassName, "Problem with Client Validation")
            }
        }


    private fun checkIfNotEmpty(textView: TextView, editText: EditText): Boolean =
        when {
            editText.text.toString().isEmpty() -> {
                markErrorField(textView, editText)
                showErrorSnackBar(resources.getString(R.string.error_field_empty,resources.getResourceEntryName(editText.id)))
            false
        }
        else -> {
            unMarkErrorField(textView, editText)
            true
        }
    }



    private fun trimText(editText: EditText): String = editText.text.toString().trim { it <= ' ' }


    private fun signIn(email: String, password: String) {
        showProgressDialog(resources.getString(R.string.please_wait))
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                hideProgressDialog()
                when {
                    task.isSuccessful -> {
                        FirestoreClass().loginUser(this)
                    }
                    else -> {
                        showErrorSnackBar(task.exception!!.message.toString())
                    }
                }
            }
    }


}