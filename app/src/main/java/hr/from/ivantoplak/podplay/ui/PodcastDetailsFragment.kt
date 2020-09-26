package hr.from.ivantoplak.podplay.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import hr.from.ivantoplak.podplay.R
import hr.from.ivantoplak.podplay.adapter.EpisodeListAdapter
import hr.from.ivantoplak.podplay.model.EpisodeViewData
import hr.from.ivantoplak.podplay.ui.common.HiltFragment
import hr.from.ivantoplak.podplay.viewmodel.PodcastViewModel
import kotlinx.android.synthetic.main.fragment_podcast_details.*
import kotlinx.coroutines.launch

private const val SUBSCRIBE_ERROR_MESSAGE = "Error subscribing to feed"
private const val UNSUBSCRIBE_ERROR_MESSAGE = "Error unsubscribing to feed"

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
        lifecycle.coroutineScope.launch {
            runCatching { viewModel.saveActivePodcast() }.apply {
                onSuccess { updateToggleSubscriptionButton(true) }
                onFailure { exception ->
                    messageProvider.longPopup(getString(R.string.subscribe_error_message))
                    Log.e(TAG, SUBSCRIBE_ERROR_MESSAGE, exception)
                }
            }
        }
    }

    private fun unsubscribe() {
        lifecycle.coroutineScope.launch {
            runCatching { viewModel.deleteActivePodcast() }.apply {
                onSuccess { router.hidePodcastDetailsScreen() }
                onFailure { exception ->
                    messageProvider.longPopup(getString(R.string.unsubscribe_error_message))
                    Log.e(TAG, UNSUBSCRIBE_ERROR_MESSAGE, exception)
                }
            }
        }
    }
}



