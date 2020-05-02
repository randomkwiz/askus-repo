package es.iesnervion.avazquez.askus.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.interfaces.AuthActivityInterface
import es.iesnervion.avazquez.askus.models.Login
import es.iesnervion.avazquez.askus.ui.auth.viewmodel.AuthViewModel
import es.iesnervion.avazquez.askus.utils.AppConstants.PREFERENCE_NAME
import es.iesnervion.avazquez.askus.utils.AppConstants.UNAUTHORIZED
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment(),
    View.OnClickListener {
    lateinit var authActivityInterface: AuthActivityInterface
    lateinit var viewModel: AuthViewModel
    lateinit var observerLoadingData: Observer<Boolean>
    lateinit var observerError: Observer<Boolean>
    lateinit var observerCredentials: Observer<List<Char>>

    companion object {
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(AuthViewModel::class.java)
        btn_login.setOnClickListener(this)
        link_signup.setOnClickListener(this)
        rememberPassword.setOnClickListener(this)
        initObservers()
    }

    private fun initObservers() {
        observerLoadingData = Observer { loading ->
            if (loading) {
                progress_bar.visibility = View.VISIBLE
                lbl_error_login.visibility = View.GONE
            } else {
                progress_bar.visibility = View.GONE
            }
        }
        observerError = Observer { error ->
            if (error) {
                lbl_error_login.visibility = View.VISIBLE
                lbl_error_login.text = resources.getText(R.string.error_internet)
            }
        }
        observerCredentials = Observer {
            if (it.size == 1) {
                if (it == listOf(UNAUTHORIZED)) {
                    lbl_error_login.text = resources.getText(R.string.invalid_credentials)
                } else {
                    lbl_error_login.text = resources.getText(R.string.error_login)
                }
                lbl_error_login.visibility = View.VISIBLE
            } else {
                lbl_error_login.visibility = View.GONE
            }
        }
        viewModel.getToken().observe(viewLifecycleOwner, observerCredentials)
        viewModel.loadingLiveData().observe(viewLifecycleOwner, observerLoadingData)
        viewModel.errorLiveData().observe(viewLifecycleOwner, observerError)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_login -> {
                viewModel.login = Login(
                    input_nickname.text.toString().trim({ it <= ' ' }),
                    input_password.text.toString().trim({ it <= ' ' })
                )
                if (viewModel.login.nickname.isNotEmpty() && viewModel.login.password.isNotEmpty()) {
                    lbl_error_login.visibility = View.GONE
                    viewModel.checkLogin()
                } else {
                    lbl_error_login.text = resources.getText(R.string.fillFields)
                    lbl_error_login.visibility = View.VISIBLE
                }
            }
            R.id.link_signup -> {
                authActivityInterface.goToSignUp()
            }
            R.id.rememberPassword -> {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Password issues")
                builder.setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                })
                builder.setMessage("If you have forgotten your password, contact the support team at: avazquez.developer@gmail.com")
                //TODO("Mejorar esto del email con esto de stackoverflow -> https://stackoverflow.com/questions/40386812/open-email-client-in-alertdialog")
                builder.show()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AuthActivityInterface)
            this.authActivityInterface = context
    }
}
