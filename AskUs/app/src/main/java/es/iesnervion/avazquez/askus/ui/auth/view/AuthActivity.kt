package es.iesnervion.avazquez.askus.ui.auth.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.interfaces.AuthActivityInterface
import es.iesnervion.avazquez.askus.ui.auth.viewmodel.AuthViewModel
import es.iesnervion.avazquez.askus.ui.fragments.LoginFragment
import es.iesnervion.avazquez.askus.ui.fragments.SignUpFragment
import es.iesnervion.avazquez.askus.ui.home.view.HomeActivity
import es.iesnervion.avazquez.askus.utils.AppConstants
import es.iesnervion.avazquez.askus.utils.AppConstants.TOKEN_LENGHT

class AuthActivity : AppCompatActivity()
    , AuthActivityInterface {
    val loginFragment: Fragment =
        LoginFragment.newInstance()
    val signUpFragment: Fragment =
        SignUpFragment.newInstance()
    lateinit var tokenObserver: Observer<List<Char>>
    lateinit var userIDObserver: Observer<List<Int>>
    lateinit var viewModel: AuthViewModel
    lateinit var sharedPreference: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreference = getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        editor = sharedPreference.edit()
        viewModel = ViewModelProviders.of(this)[AuthViewModel::class.java]
        //Pongo el fragment del login
        if (savedInstanceState == null) {
            loadFragmentLoader(loginFragment)
        }
        initObservers()
        AppCenter.start(application, "e5856b93-d2a9-4b5d-983a-6512e3b8190d",
            Analytics::class.java, Crashes::class.java)
    }

    private fun initObservers() {
        // Create the observer which updates the UI.
        tokenObserver = Observer<List<Char>> {
            if (it.size > TOKEN_LENGHT) {
                val token = it.joinToString("")
                editor.putString("token", token)
                editor.putString("user_nickname", viewModel.login.nickname)
                editor.commit()
                viewModel.loadUserIDByNickname(nickname = viewModel.login.nickname, token = token)
            }
        }
        viewModel.getToken()
            .observe(this, tokenObserver)
        userIDObserver = Observer<List<Int>> {
            if (it.isNotEmpty()) {
                if (it.size == 1 && it[0] > 0) {
                    editor.putInt("user_id", it.first().toInt())
                    editor.commit()
                    startActivity(Intent(this, HomeActivity::class.java).putExtra("type", "auth")
                        //.putExtra("user_id", it.first().toInt())
                    )
                    finish()
                }
            }
        }
        viewModel.getIDUserByNickname().observe(this, userIDObserver)
    }

    /**
     * This will load the fragment
     */
    private fun loadFragmentLoader(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment, fragment)
        //transaction.addToBackStack(null);
        transaction.commit()
    }

    override fun goToSignUp() {
        loadFragmentLoader(signUpFragment)
    }

    override fun goToLogIn() {
        loadFragmentLoader(loginFragment)
    }
}
