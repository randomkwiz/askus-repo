package es.iesnervion.avazquez.askus.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.interfaces.HomeActivityCallback
import es.iesnervion.avazquez.askus.utils.AppConstants
import kotlinx.android.synthetic.main.fragment_settings.*

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment(), View.OnClickListener {
    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }

    lateinit var sharedPreference: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreference =
                activity!!.getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        editor = sharedPreference.edit()
        btn_logout.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_logout -> {
                deleteAllRememberedData()
                if (context is HomeActivityCallback) {
                    (context as HomeActivityCallback).logOut()
                }
            }
        }
    }

    private fun deleteAllRememberedData() {
        editor.putString("nicknameToSave", "")
        editor.putString("passwordToSave", "")
        editor.putString("token", "")
        editor.putBoolean("remember_password", false)
        editor.putInt("user_id", 0)
        editor.commit()
    }
}
