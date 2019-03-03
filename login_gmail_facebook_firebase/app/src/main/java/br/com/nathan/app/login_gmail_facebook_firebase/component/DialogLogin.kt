package br.com.nathan.app.login_gmail_facebook_firebase.component

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.view.Window
import br.com.nathan.app.login_gmail_facebook_firebase.R
import com.airbnb.lottie.LottieAnimationView
import kotlinx.android.synthetic.main.dialog_login.*


class DialogLogin
/**
 * Construtor
 * @param context
 */
    (context: Context) : Dialog(context) {


    var animationLottie: String? = null
        set(value) {
            circleAnimation.setAnimation(
                value ?: "login.json",
                LottieAnimationView.DEFAULT_CACHE_STRATEGY
            )
            circleAnimation.playAnimation()
            field = value
        }


    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_login)

        setCancelable(false)
        circleAnimation.setAnimation("login.json", LottieAnimationView.DEFAULT_CACHE_STRATEGY)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        this@DialogLogin.animationLottie = null
        onBtnclose()
    }


    fun onBtnclose() {
        btn_close.setOnClickListener { dismiss() }
    }

    fun closeButton(call: () -> Unit) {

        btn_close.setOnClickListener {
            call.invoke()
        }
    }

    fun gmailButton(call: () -> Unit) {
        btn_gmail.setOnClickListener {
            call.invoke()
        }
    }

    fun facebookButton(call: () -> Unit) {
        btn_facebook.setOnClickListener {
            call.invoke()
        }
    }


}
