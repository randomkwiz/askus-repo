package es.iesnervion.avazquez.askus.ui.fragments

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.interfaces.AuthActivityInterface
import es.iesnervion.avazquez.askus.models.User
import es.iesnervion.avazquez.askus.ui.auth.viewmodel.AuthViewModel
import es.iesnervion.avazquez.askus.utils.AppConstants.CONFLICT
import es.iesnervion.avazquez.askus.utils.AppConstants.OK
import es.iesnervion.avazquez.askus.utils.AppConstants.PASSWORD_MIN_LENGHT
import kotlinx.android.synthetic.main.fragment_sign_up.*
import setVisibilityToGone
import setVisibilityToVisible

/**
 * A simple [Fragment] subclass.
 */
class SignUpFragment : Fragment()
    , View.OnClickListener {
    lateinit var viewModel: AuthViewModel
    lateinit var observerLoadingData: Observer<Boolean>
    lateinit var observerError: Observer<Boolean>
    lateinit var observerCredentials: Observer<List<Char>>
    var isCreateBtnClicked = false

    companion object {
        fun newInstance(): SignUpFragment {
            return SignUpFragment()
        }
    }

    override fun onStart() {
        super.onStart()
        lbl_error_login.setVisibilityToGone()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(AuthViewModel::class.java)
        btn_sign_up.setOnClickListener(this)
        link_login.setOnClickListener(this)
        initObservers()
    }

    private fun initObservers() {
        observerLoadingData = Observer { loading ->
            if (loading) {
                progress_bar.setVisibilityToVisible()
                lbl_error_login.setVisibilityToGone()
            } else {
                progress_bar.setVisibilityToGone()
            }
        }
        observerError = Observer { error ->
            if (error) {
                lbl_error_login.setVisibilityToVisible()
                lbl_error_login.text = resources.getText(R.string.error_internet)
            }
        }
        observerCredentials = Observer {
            if (it == listOf(OK)) {
                lbl_error_login.setVisibilityToGone()
                if (isCreateBtnClicked) {
                    if (context is AuthActivityInterface) {
                        (context as AuthActivityInterface).goToLogIn()
                    }
                }
                isCreateBtnClicked = false
            } else {
                if (it == listOf(CONFLICT)) {
                    lbl_error_login.text = resources.getText(R.string.error_create_user_conflict)
                } else {
                    lbl_error_login.text = resources.getText(R.string.error_create_user)
                }
                lbl_error_login.setVisibilityToVisible()
            }
        }
        viewModel.getUserCreatedInfo().observe(viewLifecycleOwner, observerCredentials)
        viewModel.loadingLiveData().observe(viewLifecycleOwner, observerLoadingData)
        viewModel.errorLiveData().observe(viewLifecycleOwner, observerError)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_sign_up -> {
                lbl_error_login.setVisibilityToGone()
                //if there are any empty field
                if (input_password.text.toString().trim({ it <= ' ' }).isEmpty()
                    ||
                    input_repeat_password.text.toString().trim({ it <= ' ' }).isEmpty()
                    ||
                    input_email.text.toString().trim({ it <= ' ' }).isEmpty()
                    ||
                    input_nickname.text.toString().trim({ it <= ' ' }).isEmpty()
                ) {
                    lbl_error_login.text = resources.getText(R.string.fillFields)
                    lbl_error_login.setVisibilityToVisible()
                } else
                //Passwords fields must match and password must be > X characters
                //Email pattern
                    if (
                        Patterns.EMAIL_ADDRESS.matcher
                            (input_email.text.toString().trim({ it <= ' ' })).matches()
                        &&
                        input_password.text.toString()
                            .trim({ it <= ' ' }).length >= PASSWORD_MIN_LENGHT
                        &&
                        input_password.text.toString().trim({ it <= ' ' })
                            .equals(input_repeat_password.text.toString().trim({ it <= ' ' }))
                    ) {
                        lbl_error_login.setVisibilityToGone()

                        viewModel.newUser =
                                User(0, input_nickname.text.toString().trim({ it <= ' ' }),
                                    input_email.text.toString().trim({ it <= ' ' }), false, false,
                                    "", 0, "", "", false,
                                    input_password.text.toString().trim({ it <= ' ' }))
                        isCreateBtnClicked = true
                        viewModel.createUser()
                    } else {
                        lbl_error_login.text = resources.getText(R.string.error_sign_up)
                        lbl_error_login.setVisibilityToVisible()
                    }
            }
            R.id.link_login -> {
                if (context is AuthActivityInterface) {
                    (context as AuthActivityInterface).goToLogIn()
                }
            }
        }
    }
}
