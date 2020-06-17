package es.iesnervion.avazquez.askus.ui.splash

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.ui.auth.view.AuthActivity
import es.iesnervion.avazquez.askus.utils.AppConstants
import kotlinx.android.synthetic.main.activity_splash.*

class Splash : AppCompatActivity() {
    lateinit var sharedPreference: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        sharedPreference = getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        val isDarkModeOn = sharedPreference.getBoolean("isDarkModeEnabled", false)
        //Para que al iniciar se ponga correctamente
        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        val anim: Animation = AnimationUtils.loadAnimation(this, R.anim.splash_anim)
        splash_img.startAnimation(anim)
        //lottie_anim_splash.startAnimation(anim)
        credits_txt.startAnimation(anim)
        Handler().postDelayed({
            val i = Intent(this, AuthActivity::class.java)
            startActivity(i)
            finish()
        }, 3000)
    }
}
