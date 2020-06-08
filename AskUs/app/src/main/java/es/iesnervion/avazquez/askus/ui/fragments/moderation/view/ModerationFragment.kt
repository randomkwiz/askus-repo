package es.iesnervion.avazquez.askus.ui.fragments.moderation.view

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import com.github.florent37.tutoshowcase.TutoShowcase
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.SwipeableMethod
import es.iesnervion.avazquez.askus.DTOs.VotoModeracionDTO
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.adapters.ModerationPostAdapter
import es.iesnervion.avazquez.askus.ui.fragments.moderation.viewmodel.ModerationViewModel
import es.iesnervion.avazquez.askus.utils.AppConstants
import es.iesnervion.avazquez.askus.utils.AppConstants.NO_CONTENT
import es.iesnervion.avazquez.askus.utils.PaginationScrollListener
import es.iesnervion.avazquez.askus.utils.UtilClass.Companion.getFormattedCurrentDatetime
import kotlinx.android.synthetic.main.fragment_moderation.*
import setVisibilityToGone
import setVisibilityToVisible

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
    lateinit var viewModel: ModerationViewModel
    lateinit var sharedPreference: SharedPreferences
    lateinit var token: String
    var idCurrentUser = 0
    private lateinit var layoutManager: CardStackLayoutManager

    //Pagination
    private var currentPage: Int = PaginationScrollListener.PAGE_START
    private var totalPage = 2
    lateinit var voto: VotoModeracionDTO
    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_moderation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ModerationViewModel::class.java)
        sharedPreference =
                activity!!.getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        token = sharedPreference.getString("token", "").toString()
        idCurrentUser = sharedPreference.getInt("user_id", 0)
        initAdapter()
        doApiCall()
        setObservers()

        TutoShowcase.from(activity!!).setContentView(R.layout.moderation_tutorial_layout)
            .on(R.id.stack_view).displaySwipableRight().showOnce("MODERATION_TUTORIAL")
    }

    private fun setObservers() {
        viewModel.areValuesReady().observe(viewLifecycleOwner, Observer(::onDataReceived))
        viewModel.responseCodeVotoModeracionSent()
            .observe(viewLifecycleOwner, Observer(::onRequestCodeReceived))
    }

    private fun onDataReceived(areValuesReady: Boolean) {
        if (areValuesReady) {
            if (currentPage == viewModel.currentPaginHeader.currentPage) {
                adapter.addItems(viewModel.postsList.toMutableList())
                this.totalPage = viewModel.currentPaginHeader.totalPages
            }
            if (viewModel.postsList.isEmpty()) {
                adapter.clearShowingCards()
                moderation__label_no_more_posts.setVisibilityToVisible()
            } else {
                moderation__label_no_more_posts.setVisibilityToGone()
            }
        }
    }

    private fun onRequestCodeReceived(code: Int) {
        when (code) {
            NO_CONTENT -> {
                //                Toast.makeText(context, resources.getText(R.string.processed_vote),
                //                    Toast.LENGTH_SHORT).show()
            }
            else       -> {
                //                Toast.makeText(context, resources.getText(R.string.there_was_an_error),
                //                    Toast.LENGTH_SHORT).show()
                //  Toast.makeText(context, "Error $code", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun doApiCall() {
        viewModel.loadModerationPosts(token = token, pageNumber = currentPage,
            pageSize = PaginationScrollListener.PAGE_SIZE, idUsuarioLogeado = idCurrentUser)
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
        if (position == adapter.getLastPosition() && currentPage < viewModel.currentPaginHeader.totalPages && adapter.itemCount < viewModel.currentPaginHeader.totalCount) {
            currentPage++
            doApiCall()
        }
        if (position == adapter.getLastPosition()) {
            moderation__label_no_more_posts.setVisibilityToVisible()
        }
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
        when (direction) {
            Direction.Left  -> {
                moderation__label_placeholder.setTextColor(Color.RED)
                moderation__label_placeholder.text = getString(R.string.reject)
            }
            Direction.Right -> {
                moderation__label_placeholder.setTextColor(Color.GREEN)
                moderation__label_placeholder.text = getString(R.string.approve)
            }
            else            -> {
                moderation__label_placeholder.text = ""
            }
        }
        moderation__label_placeholder.setVisibilityToVisible()
    }

    override fun onCardSwiped(direction: Direction?) {
        moderation__label_placeholder.setVisibilityToGone()
        val valoracion: Boolean
        when (direction) {
            Direction.Left  -> {
                valoracion = false
            }
            Direction.Right -> {
                valoracion = true
            }
            else            -> {
                valoracion = false
            }
        }
        voto = VotoModeracionDTO(idUsuario = idCurrentUser,
            idPublicacion = adapter.getItem(viewModel.currentPostPosition).idPublicacion,
            valoracion = valoracion, fechaHoraEmisionVoto = getFormattedCurrentDatetime())

        viewModel.insertVotoModeracion(token = token, votoModeracion = voto)
    }

    override fun onCardCanceled() {
        moderation__label_placeholder.setVisibilityToGone()
    }

    override fun onCardAppeared(view: View?, position: Int) {
        viewModel.currentPostPosition = position
        moderation__label_placeholder.setVisibilityToGone()
    }

    override fun onCardRewound() {
        //Toast.makeText(context, "On card rewound ", Toast.LENGTH_SHORT).show()
    }
}
