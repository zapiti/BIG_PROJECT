package br.com.nathan.app.login_gmail_facebook_firebase

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import br.com.nathan.app.login_gmail_facebook_firebase.util.ConstantsUtil
import br.com.nathan.app.login_gmail_facebook_firebase.util.ConstantsUtil.CODUSU
import br.com.nathan.app.login_gmail_facebook_firebase.util.ConstantsUtil.EMAIL
import br.com.nathan.app.login_gmail_facebook_firebase.util.ConstantsUtil.NAMEUSU
import br.com.nathan.app.login_gmail_facebook_firebase.util.ConstantsUtil.USU_TABLE
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.item_load_view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.okButton
import java.util.*


class LoginActivity : AppCompatActivity() {
    private var mCallbackManager: CallbackManager? = null
    private var mAuth: FirebaseAuth? = null
    private var mgoogleApiClient: GoogleApiClient? = null
    private val RC_SIGN_IN = 9001
    private val FB_SIGN_IN = 64206
    private var mfirebase: FirebaseApp? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mfirebase = FirebaseApp.initializeApp(this)

        initiData()
        initiComponente()
        initAnimation()

    }

    private fun initAnimation() {
        lottie_animation.setAnimation(ConstantsUtil.ANIMATIONLOAD)
    }

    private fun startLoad() {
        lottie_animation.playAnimation()
        load_login.visibility = View.VISIBLE
    }

    private fun stopLoad() {
        lottie_animation.pauseAnimation()
        load_login.visibility = View.GONE
    }

    private fun initiData() {
        initFirebaseAndFacebook()
        mAuth?.signOut()
        verifyStatusUser()
    }

    private fun verifyStatusUser() {
        if (mAuth?.currentUser != null) {
            Log.wtf("logado", "logado")
            goToHome()
            return
        } else {
            Log.wtf("deslogado", "deslogado")
        }
    }

    private fun initFirebaseAndFacebook() {
        mAuth = FirebaseAuth.getInstance(mfirebase!!)
        mCallbackManager = CallbackManager.Factory.create()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(ConstantsUtil.GOOGLE_API)
            .requestEmail()
            .build()
        mgoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this) {

                //   snackbar(login_button_facebook, getString(R.string.login_activity_login_falure))
            }
            .addApi<GoogleSignInOptions>(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
    }

    private fun initiComponente() {

        initBeMember()
        initFacebook()
        initGoogle()

    }

    private fun initGoogle() {
        registers_google.setOnClickListener {
            registreGoogle()
        }

    }

    private fun initFacebook() {
        LoginManager.getInstance().registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                val credential = FacebookAuthProvider.getCredential(loginResult.accessToken.token)
                signInCredential(credential)
            }

            override fun onCancel() {
                // O user cancelou o login enquanto carregava
                stopLoad()
                alert("falha ao logar") { okButton { } }.show()
            }

            override fun onError(error: FacebookException) {
                //Ocorreu um erro ao tentar fazer logi
                error.printStackTrace()
                stopLoad()
                alert("falha ao logar $error") { okButton { } }.show()
            }
        })

        registers_facebook.setOnClickListener {
            registreFacebook()
        }

    }


    private fun initBeMember() {
        login_be_member.setOnClickListener {
            callLogin()
        }

    }


    private fun registreFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(
            this@LoginActivity,
            Arrays.asList("public_profile", "email")
        )
    }

    private fun registreGoogle() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mgoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun callLogin() {

    }

    //region <!Verificando credencial facebook!>
    private fun signInCredential(credential: AuthCredential) {
        mAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    val user = mAuth?.currentUser
                    updateUI(user)
                } else {
                    snackbar(this@LoginActivity.login_line, getString(R.string.login_activity_login_falure))
                }
            }
    }

    //endregion
    //region <!Controle de usuario!>
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            veriFyUserExistis(user)
        } else {
            snackbar(this@LoginActivity.login_line, getString(R.string.login_activity_login_falure))
        }
    }
    //endregion

    /**
     * Verificacao que usuario existe
     * @param user usuario que vem do firebase
     */
    private fun veriFyUserExistis(user: FirebaseUser) {
        startLoad()
        val rootRef = FirebaseFirestore.getInstance()
        val allUsersRef = rootRef.collection(USU_TABLE).whereEqualTo(CODUSU, user.uid)
        allUsersRef.limit(1).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val isEmpty = task.result?.isEmpty ?: false
                    //region <!Verifica se existe usuario!>
                    if (isEmpty) {
                        //region <!Criacao de usuario!>
                        val addUsu = HashMap<String, Any>()
                        addUsu[CODUSU] = user.uid
                        if (user.displayName != null) {
                            addUsu[NAMEUSU] = user.displayName!!
                        }
                        addUsu[EMAIL] = user.email.toString()
                        // adicionando novo usuario pelo user id codusu
                        rootRef.collection(USU_TABLE)
                            .add(addUsu)
                            .addOnSuccessListener {
                                goToHome()
                            }
                            .addOnFailureListener {
                                snackbar(
                                    this@LoginActivity.login_line,
                                    getString(R.string.login_activity_user_register_falure)
                                )
                            }
                        //endregion
                    } else {
                        goToHome()
                    }
                    //endregion
                } else {
                    stopLoad()
                    snackbar(this@LoginActivity.login_line, getString(R.string.login_activity_login_falure))
                }
            }
    }

    private fun goToHome() {
        stopLoad()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(
            intent,
            ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
        )

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == FB_SIGN_IN) {
            mCallbackManager?.onActivityResult(requestCode, resultCode, data)
            startLoad()
        }

        if (requestCode == RC_SIGN_IN) {
            startLoad()
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                val acc = result.signInAccount
                val credential = GoogleAuthProvider.getCredential(acc?.idToken, null)
                if (credential != null) {
                    signInCredential(credential)
                }
            } else {
                stopLoad()
                alert("falha ao logar") { okButton { } }.show()
            }
        }
    }

}
