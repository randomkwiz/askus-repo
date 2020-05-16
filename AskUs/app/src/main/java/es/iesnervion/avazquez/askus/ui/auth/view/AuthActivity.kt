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
import es.iesnervion.avazquez.askus.utils.AppConstants.LOG_OUT

class AuthActivity : AppCompatActivity(), AuthActivityInterface {
    val loginFragment: Fragment = LoginFragment.newInstance()
    val signUpFragment: Fragment = SignUpFragment.newInstance()
    lateinit var tokenObserver: Observer<List<Char>>
    lateinit var userIDObserver: Observer<List<Int>>
    lateinit var viewModel: AuthViewModel
    lateinit var sharedPreference: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var passwordSaved: String
    var isRememberPasswordActivated = false
    lateinit var nicknameSaved: String
    lateinit var tokenSaved: String
    var idSaved = 0
    var userHasLoggedOut = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreference = getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        editor = sharedPreference.edit()
        viewModel = ViewModelProviders.of(this)[AuthViewModel::class.java]
        setDataFromSharedPref()
        userHasLoggedOut = intent.getBooleanExtra(LOG_OUT, false)
        if (isDataSaved()) {
            startActivity(Intent(this, HomeActivity::class.java).putExtra("type", "auth"))
            finish()
        }
        //Pongo el fragment del login
        if (savedInstanceState == null) {
            loadFragmentLoader(loginFragment)
        }
        initObservers()
        AppCenter.start(application, "e5856b93-d2a9-4b5d-983a-6512e3b8190d", Analytics::class.java,
            Crashes::class.java)
    }

    private fun initObservers() {
        // Create the observer which updates the UI.
        tokenObserver = Observer<List<Char>> {
            if (!it.toString().contains("ERROR") && !userHasLoggedOut) {
                val token = it.joinToString("")
                editor.putString("token", token)
                editor.putString("user_nickname", viewModel.login.nickname)
                editor.commit()
                viewModel.loadUserIDByNickname(nickname = viewModel.login.nickname, token = token)
            }
        }
        viewModel.getToken().observe(this, tokenObserver)
        userIDObserver = Observer<List<Int>> {
            if (!it.isNullOrEmpty() && !userHasLoggedOut) {
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

    override fun userIsTryingToLogIn() {
        this.userHasLoggedOut = false
    }

    private fun setDataFromSharedPref() {
        this.nicknameSaved = sharedPreference.getString("nicknameToSave", "").toString()
        this.passwordSaved = sharedPreference.getString("passwordToSave", "").toString()
        this.tokenSaved = sharedPreference.getString("token", "").toString()
        this.isRememberPasswordActivated = sharedPreference.getBoolean("remember_password", false)
        this.idSaved = sharedPreference.getInt("user_id", 0)
    }

    private fun isDataSaved(): Boolean {
        return nicknameSaved.isNotEmpty() && passwordSaved.isNotEmpty() && tokenSaved.isNotEmpty() && isRememberPasswordActivated && idSaved > 0
    }
}
