package es.iesnervion.avazquez.askus.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.models.Login
import es.iesnervion.avazquez.askus.models.User
import es.iesnervion.avazquez.askus.ui.auth.viewmodel.AuthViewModel
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment(),
    View.OnClickListener {

    lateinit var viewModel: AuthViewModel

    companion object {

        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        viewModel = ViewModelProviders.of(activity!!)[AuthViewModel::class.java]

        val button = view.findViewById<Button>(R.id.btn_login)
        button?.setOnClickListener()
        {
            viewModel.login = Login(
                input_nickname.text.toString().trim({ it <= ' ' }),
                input_password.text.toString().trim({ it <= ' ' })
            )
            if (viewModel.login.nickname.isNotEmpty() && viewModel.login.password.isNotEmpty())
                viewModel.postAuth()
        }






        return view
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_login -> {
                var usuario: User = viewModel.usuarioActual
                usuario.nickname = input_nickname.text.toString().trim({ it <= ' ' })
                usuario.password = input_password.text.toString().trim({ it <= ' ' })
                viewModel.postAuth()
            }

        }
    }

}
