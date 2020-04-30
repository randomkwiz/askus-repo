package es.iesnervion.avazquez.askus.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.ui.auth.viewmodel.AuthViewModel

/**
 * A simple [Fragment] subclass.
 */
class UserListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val viewModel: AuthViewModel = ViewModelProviders.of(activity!!)[AuthViewModel::class.java]

// 1
        //  viewModel.loadUsers()


        /*El observer*/
        //        val areUsersLoadedObserver =
        //            Observer<List<User>> {
        //                if (it.isNotEmpty()) {
        //                    val listUsers = arrayOfNulls<String>(viewModel.userList.value!!.size)
        //                    for (i in listUsers.indices) {
        //
        //                        listUsers[i] = viewModel.userList.value!![i].toString()
        //                    }
        //                    val adapter =
        //                        ArrayAdapter(context!!, android.R.layout.simple_list_item_1, listUsers)
        //                    listViewUsers.adapter = adapter
        //                }
        //            }
        //Observo
        // viewModel.userList.observe(viewLifecycleOwner, areUsersLoadedObserver)







        return inflater.inflate(R.layout.fragment_user_list, container, false)
    }

}
