package es.iesnervion.avazquez.askus.ui.auth.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
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
        // Create the observer which updates the UI.
        tokenObserver = Observer<List<Char>> {
            if (it.size == TOKEN_LENGHT) {
                editor.putString("token", it.joinToString(""))
                editor.commit()
                startActivity(Intent(this, HomeActivity::class.java))
            }
        }
        viewModel.getToken()
            .observe(this, tokenObserver)
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
