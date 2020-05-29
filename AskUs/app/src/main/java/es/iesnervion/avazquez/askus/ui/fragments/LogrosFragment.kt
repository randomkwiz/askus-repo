package es.iesnervion.avazquez.askus.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.iesnervion.avazquez.askus.R

class LogrosFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logros, container, false)
    }

    companion object {
        fun newInstance() = LogrosFragment()
    }
}