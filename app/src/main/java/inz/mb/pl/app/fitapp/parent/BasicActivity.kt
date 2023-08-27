package inz.mb.pl.app.fitapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import inz.mb.pl.app.fitapp.database.FirestoreClass
import inz.mb.pl.app.fitapp.database.water_change.WaterChangeDTO
import inz.mb.pl.app.fitapp.database.weight_change.WeightChangeDTO
import inz.mb.pl.app.fitapp.parent.HelperActivity
import inz.mb.pl.app.fitapp.models.User

abstract class BaseActivity : HelperActivity() {

    companion object{
        const val SIGNTAG = "SignActivity"
    }
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var callbackManager: CallbackManager
    lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpConnections()
        buttonSetUp()
    }

    private fun setUpConnections(){
        setUpGoogleConnection()
        setUpFacebookConnection()
        auth = Firebase.auth
    }

    override fun finish() {
        super.finish()
        slideRightToLeft(this)
    }

    abstract fun buttonSetUp()

    private val googleResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if(task.isSuccessful){
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    with(account){
                        printLog(SIGNTAG, "firebaseAuthWithGoogle:$id")
                        firebaseAuthWithGoogle(idToken!!)
                    }
                } catch (e: ApiException) {
                    printLog(SIGNTAG, "Google sign in failed", e)
                }

            } else {
                printLog(SIGNTAG,"Problem with loading Google Service" ,task.exception)
                showErrorSnackBar("There was some problem with connecting to Google")
            }
    }




    fun googleSignIn() {
        showProgressDialog(resources.getString(R.string.please_wait))
        googleResult.launch(googleSignInClient.signInIntent)
    }


     private fun setUpGoogleConnection(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

     private fun setUpFacebookConnection(){
         callbackManager = CallbackManager.Factory.create()
         LoginManager.getInstance().registerCallback(callbackManager, object :
             FacebookCallback<LoginResult> {
             override fun onSuccess(result: LoginResult) {
                 printLog(SIGNTAG, "facebook:onSuccess:$result")
                 handleFacebookAccessToken(result.accessToken)
             }

             override fun onCancel() {
                 printLog(SIGNTAG, "facebook:onCancel")
             }

             override fun onError(error: FacebookException) {
                 printLog(SIGNTAG, "facebook:onError", error)
             }
         })
    }

     fun facebookLogIn(){
         showProgressDialog(resources.getString(R.string.please_wait))
         LoginManager.getInstance().logInWithReadPermissions(
             this,
             callbackManager,
             listOf("public_profile", "email")
         )
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        printLog(SIGNTAG, "handleGoogleAccessToken:$idToken")
        handleFirebaseAuth(GoogleAuthProvider.getCredential(idToken, null))
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        printLog(SIGNTAG, "handleFacebookAccessToken:$token")
        handleFirebaseAuth(FacebookAuthProvider.getCredential(token.token))
    }
    
    private fun handleFirebaseAuth(credential : AuthCredential) =
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) {
                when {
                    it.isSuccessful -> {
                        printLog(SIGNTAG, "signInWithCredential:success")
                        val user = it.result!!.user!!
                        FirestoreClass().checkIfUserExist(this,  User(user.uid,user.email!!))
                    }
                    else -> {
                        printLog(SIGNTAG, "signInWithCredential:failure", it.exception)
                        showErrorSnackBar("Authentication failed.")
                    }
                }
        }

    fun userSuccess(user : User, weightChange : WeightChangeDTO? = null, waterChange : WaterChangeDTO? = null){
        hideProgressDialog()
        changeActivity(
            this,
            Intent(this, MainActivity::class.java),
            user,
            weightChange,
            waterChange
        )
    }

    fun userFirstSuccess(){
        hideProgressDialog()
        changeActivity(
            this,
            Intent(this, GetStartedActivity::class.java)
        )
    }


}