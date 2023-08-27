package inz.mb.pl.app.fitapp.sign_activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.google.firebase.auth.FirebaseAuth
import inz.mb.pl.app.fitapp.BaseActivity
import inz.mb.pl.app.fitapp.R
import inz.mb.pl.app.fitapp.database.FirestoreClass
import inz.mb.pl.app.fitapp.databinding.ActivitySignUpBinding
import inz.mb.pl.app.fitapp.models.TextLabelField
import inz.mb.pl.app.fitapp.models.User


class SignUpActivity : BaseActivity() {
    companion object {
        private const val PASSWORD_PATTERN: String =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$"
    }

    private val binding by lazy { ActivitySignUpBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        screenSetUp(window)
        settingUpRightTitleComponent(
            binding.titleComponent,
            resources.getString(R.string.welcome_new_acc),
            resources.getString(R.string.welcome_desc_sign_up)
        )
    }

    override fun buttonSetUp() = with(binding) {
        signUp.setOnClickListener {
            setupButtonValidation() }
        googleButton.setOnClickListener {
            googleSignIn() }
        facebookButton.setOnClickListener {
            facebookLogIn() }
        signIn.setOnClickListener {
            changeActivity(
                this@SignUpActivity,
                Intent(this@SignUpActivity, SignInActivity::class.java)
            )
        }
    }


    private fun registerUser() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
            binding.emailEditText.text.toString(),
            binding.passwordEditText.text.toString()
        ).addOnCompleteListener { task ->
            when {
                task.isSuccessful -> {
                    printLog(SIGNTAG, "signInWithEmail:success")
                    FirestoreClass().registerUser(this,  User(task.result!!.user!!.uid, binding.emailEditText.text.toString()))
                }
                else -> {
                    printLog(SIGNTAG, "signInWithEmail:failure", task.exception)
                    showErrorSnackBar("Authentication failed.")
                }
            }
        }
    }

    private fun setupButtonValidation() = with(binding) {
        when {
            clientSideEmailValidation(emailEditText.text.toString()) -> {
                    unMarkErrorField(emailText, emailEditText)
                    when {
                        clientSidePasswordValidation(
                            passwordEditText.text.toString(),
                            passwordAgainEditText.text.toString()
                        ) -> {
                            unMarkErrorFields(listOf(TextLabelField(passwordText,passwordEditText),TextLabelField(passwordAgainText, passwordAgainEditText)))
                            registerUser()
                        }
                        else -> markErrorFields(listOf(TextLabelField(passwordText,passwordEditText),TextLabelField(passwordAgainText, passwordAgainEditText)))
                    }

            }
            else -> {
                showErrorSnackBar(resources.getString(R.string.error_email_empty))
                markErrorField(emailText, emailEditText)
            }
        }
    }

    private fun clientSidePasswordValidation(firstPassword: String, secondPassword: String): Boolean = when (firstPassword) {
        secondPassword -> {
            when {
                firstPassword.matches(PASSWORD_PATTERN.toRegex()) and secondPassword.matches(
                    PASSWORD_PATTERN.toRegex()
                ) -> {
                    true
                }
                else -> {
                    showErrorSnackBar(resources.getString(R.string.error_pass_8_char))
                    false
                }
            }
        }
        else -> {
            showErrorSnackBar(resources.getString(R.string.error_pass_not_equal))
            false
        }
    }

    private fun clientSideEmailValidation(email: String): Boolean = !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()


}