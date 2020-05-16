package es.iesnervion.avazquez.askus.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.ui.auth.view.AuthActivity
import kotlinx.android.synthetic.main.activity_splash.*

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val anim: Animation = AnimationUtils.loadAnimation(this, R.anim.splash_anim)
        splash_img.startAnimation(anim)
        splash_progressBar.startAnimation(anim)
        credits_txt.startAnimation(anim)
        Handler().postDelayed(Runnable {
            val i = Intent(this, AuthActivity::class.java)
            startActivity(i)
            finish()
        }, 3000)
    }
}
