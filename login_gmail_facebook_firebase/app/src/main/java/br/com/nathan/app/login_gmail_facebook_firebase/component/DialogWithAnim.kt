package br.com.nathan.app.login_gmail_facebook_firebase.component

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.Window
import br.com.nathan.app.login_gmail_facebook_firebase.R
import com.airbnb.lottie.LottieAnimationView
import kotlinx.android.synthetic.main.dialog_with_animation.*


class DialogWithAnim
/**
 * Construtor
 * @param context
 */
    (context: Context) : Dialog(context) {


    var animationLottie: String? = null
        set(value) {
            circleAnimation.setAnimation(
                value ?: "network_not_fould.json",
                LottieAnimationView.DEFAULT_CACHE_STRATEGY
            )
            circleAnimation.playAnimation()
            field = value
        }


    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_with_animation)

        setCancelable(false)
        circleAnimation.setAnimation("network_not_fould.json", LottieAnimationView.DEFAULT_CACHE_STRATEGY)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        this@DialogWithAnim.animationLottie = null
        onBtnCancel()
    }

    fun hidePositive() {
        btn_positive.visibility = View.GONE
    }

    fun hideNegative() {
        btn_close.visibility = View.GONE
    }

    fun onBtnCancel() {
        btn_close.setOnClickListener { dismiss() }
    }

    fun negativeButton(textNegative: String? = null, call: () -> Unit) {
        if (textNegative != null) {
            btn_close.text = textNegative
        }

        btn_close.setOnClickListener {
            call.invoke()
        }
    }

    fun positiveButton(textPositive: String? = null, call: () -> Unit) {
        if (textPositive != null) {
            btn_positive.text = textPositive
        }

        btn_positive.setOnClickListener {
            call.invoke()
        }
    }

    var title: String? = null
        get() = if (btn_gmail.text.isNullOrEmpty()) null else btn_gmail.text.toString()
        set(value) {
            if (!value.isNullOrEmpty()) {
                btn_gmail.text = value
                btn_gmail.visibility = View.VISIBLE
            } else {
                btn_gmail.visibility = View.GONE
            }
            field = value
        }


    var description: String? = null
        get() = if (btn_facebook.text.isNullOrEmpty()) null else btn_facebook.text.toString()
        set(value) {
            if (!value.isNullOrEmpty()) {
                btn_facebook.text = value
                btn_facebook.visibility = View.VISIBLE
            } else {
                btn_facebook.visibility = View.GONE
            }
            field = value
        }


}
