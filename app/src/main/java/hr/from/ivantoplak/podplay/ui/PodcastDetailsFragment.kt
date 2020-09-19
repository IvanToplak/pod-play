package hr.from.ivantoplak.podplay.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import hr.from.ivantoplak.podplay.R
import hr.from.ivantoplak.podplay.adapter.EpisodeListAdapter
import hr.from.ivantoplak.podplay.model.EpisodeViewData
import hr.from.ivantoplak.podplay.ui.common.HiltFragment
import hr.from.ivantoplak.podplay.viewmodel.PodcastViewModel
import kotlinx.android.synthetic.main.fragment_podcast_details.*

class PodcastDetailsFragment : HiltFragment(), EpisodeListAdapter.EpisodeListAdapterListener {

    companion object {
        const val TAG = "PodcastDetailsFragment"
        fun newInstance(): PodcastDetailsFragment = PodcastDetailsFragment()
    }

    private val viewModel: PodcastViewModel by activityViewModels()
    private lateinit var episodeListAdapter: EpisodeListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_podcast_details, container, false)

    override fun doOnViewCreated(view: View, savedInstanceState: Bundle?) {
        setScreenTitleVisibility(false)
        setupControls()
        setupRecyclerView()
        updateHeader()
    }

    override fun doOnDestroyView() {
        setScreenTitleVisibility(true)
    }

    override fun onSelectedEpisode(episodeViewData: EpisodeViewData) {
        viewModel.activeEpisodeViewData = episodeViewData
        router.showEpisodeDetailsScreen()
    }

    fun updateScreen() {
        updateHeader()
        viewModel.activePodcastViewData?.episodes?.let {
            episodeListAdapter.setEpisodes(it)
        }
    }

    private fun setupControls() {
        toggleSubscriptionButton.setOnClickListener {
            viewModel.activePodcastViewData?.let { viewData ->
                if (viewData.subscribed) {
                    unsubscribe()
                } else {
                    subscribe()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        episodeRecyclerView.layoutManager = layoutManager
        val dividerItemDecoration =
            DividerItemDecoration(episodeRecyclerView.context, layoutManager.orientation)
        episodeRecyclerView.addItemDecoration(dividerItemDecoration)
        episodeListAdapter =
            EpisodeListAdapter(
                viewModel.activePodcastViewData?.episodes ?: mutableListOf(), this
            )
        episodeRecyclerView.adapter = episodeListAdapter
    }

    private fun updateHeader() {
        viewModel.activePodcastViewData?.let { viewData ->
            feedTitleTextView.text = viewData.feedTitle
            feedCategoryTextView.text = viewData.category
            feedDescTextView.text = viewData.feedDesc
            updateToggleSubscriptionButton(viewData.subscribed)
            Glide.with(this).load(viewData.imageUrl).into(feedImageView)
        }
    }

    private fun updateToggleSubscriptionButton(subscribed: Boolean) {
        toggleSubscriptionButton.text =
            if (subscribed) getString(R.string.unsubscribe) else getString(R.string.subscribe)
    }

    private fun subscribe() {
        viewModel.saveActivePodcast()
        updateToggleSubscriptionButton(true)
    }

    private fun unsubscribe() {
        viewModel.deleteActivePodcast()
        router.hidePodcastDetailsScreen()
    }
}



