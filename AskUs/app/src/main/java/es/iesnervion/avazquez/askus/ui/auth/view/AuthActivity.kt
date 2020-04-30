package es.iesnervion.avazquez.askus.ui.auth.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.ui.fragments.LoginFragment
import es.iesnervion.avazquez.askus.ui.fragments.SignUpFragment
import es.iesnervion.avazquez.askus.ui.fragments.UserListFragment
import es.iesnervion.avazquez.askus.ui.auth.viewmodel.AuthViewModel

class AuthActivity : AppCompatActivity() {

    val loginFragment: Fragment =
        LoginFragment()
    val signUpFragment: Fragment =
        SignUpFragment()
    val userListFragment: Fragment =
        UserListFragment()
    lateinit var viewModel: AuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel: AuthViewModel = ViewModelProviders.of(this)[AuthViewModel::class.java]

        //Pongo el fragment del login
        if (savedInstanceState == null) {
            // 2
            supportFragmentManager
                // 3
                .beginTransaction()
                // 4
                .replace(R.id.fragment, loginFragment)
                // 5
                .commit()
        }


        // Create the observer which updates the UI.
        val nameObserver = Observer<String> {
            if (it.isNotEmpty()) {
                supportFragmentManager
                    // 3
                    .beginTransaction()
                    // 4
                    .replace(R.id.fragment, userListFragment)
                    // 5
                    .commit()
            }
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.token.observe(this, nameObserver)

    }
}
