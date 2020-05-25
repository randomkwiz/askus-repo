package es.iesnervion.avazquez.askus.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.SwipeableMethod
import es.iesnervion.avazquez.askus.DTOs.PostCompletoParaMostrarDTO
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.adapters.ModerationPostAdapter
import es.iesnervion.avazquez.askus.ui.fragments.tabs.viewmodel.MainViewModel
import es.iesnervion.avazquez.askus.utils.AppConstants
import kotlinx.android.synthetic.main.fragment_moderation.*

/**
 * A simple [Fragment] subclass.
 */
class ModerationFragment : Fragment(), CardStackListener {
    companion object {
        fun newInstance(): ModerationFragment {
            return ModerationFragment()
        }
    }

    val adapter = ModerationPostAdapter()
    lateinit var viewModel: MainViewModel
    lateinit var sharedPreference: SharedPreferences
    lateinit var token: String
    var idCurrentUser = 0
    private lateinit var layoutManager: CardStackLayoutManager
    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_moderation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        sharedPreference =
                activity!!.getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        token = sharedPreference.getString("token", "").toString()
        idCurrentUser = sharedPreference.getInt("user_id", 0)
        initAdapter()
        doApiCall()
        setObservers()
    }

    private fun setObservers() {
        viewModel.allVisiblePostsByTag().observe(viewLifecycleOwner, Observer(::onDataReceived))
    }

    private fun onDataReceived(list: List<PostCompletoParaMostrarDTO>) {
        if (list.isNotEmpty()) {
            adapter.addItems(list.toMutableList())
        }
    }

    private fun doApiCall() {
        viewModel.loadPostsByTag(token, 0, 1, 5, idUsuarioLogeado = idCurrentUser)
    }

    private fun initAdapter() {
        layoutManager = CardStackLayoutManager(context, this).apply {
            setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
            setOverlayInterpolator(LinearInterpolator())
        }
        stack_view.layoutManager = layoutManager
        stack_view.adapter = adapter
        stack_view.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        val lastPosition = adapter.itemCount - 3
        if (position == lastPosition) {
            doApiCall()
        }
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
    }

    override fun onCardSwiped(direction: Direction?) {
        if (direction != null) {
            Toast.makeText(context, "On card swiped " + direction.name, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCardCanceled() {
    }

    override fun onCardAppeared(view: View?, position: Int) {
    }

    override fun onCardRewound() {
        Toast.makeText(context, "On card rewound ", Toast.LENGTH_SHORT).show()
    }
}
