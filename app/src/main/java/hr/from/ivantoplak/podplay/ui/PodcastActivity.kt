package hr.from.ivantoplak.podplay.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import hr.from.ivantoplak.podplay.R
import hr.from.ivantoplak.podplay.adapter.PodcastListAdapter
import hr.from.ivantoplak.podplay.adapter.PodcastListAdapter.PodcastListAdapterListener
import hr.from.ivantoplak.podplay.extensions.fadeIn
import hr.from.ivantoplak.podplay.extensions.fadeOut
import hr.from.ivantoplak.podplay.extensions.hide
import hr.from.ivantoplak.podplay.extensions.show
import hr.from.ivantoplak.podplay.intent.EXTRA_FEED_URL
import hr.from.ivantoplak.podplay.model.PodcastSummaryViewData
import hr.from.ivantoplak.podplay.ui.common.HiltActivity
import hr.from.ivantoplak.podplay.viewmodel.PodcastViewModel
import hr.from.ivantoplak.podplay.viewmodel.PodcastViewState
import hr.from.ivantoplak.podplay.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.activity_podcast.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "PodcastActivity"
private const val SEARCH_PODCASTS_ERROR_MESSAGE = "Error searching podcast on remote API"
private const val GET_PODCAST_ERROR_MESSAGE = "Error loading feed"
private const val GET_PODCASTS_ERROR_MESSAGE = "Error loading feeds"
private const val SET_ACTIVE_PODCAST_ERROR_MESSAGE =
    "Error setting selected podcast from notification as active"

class PodcastActivity : HiltActivity(), PodcastListAdapterListener {

    private val searchViewModel: SearchViewModel by viewModels()
    private val podcastViewModel: PodcastViewModel by viewModels()

    private lateinit var podcastListAdapter: PodcastListAdapter
    private lateinit var searchMenuItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        // Switch from splash screen theme to main app theme with delay of 400 ms to avoid UI flashing
        if (savedInstanceState == null) {
            Thread.sleep(400)
        }
        setTheme(R.style.PodPlay)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_podcast)
        setupToolbar()
        setupScreenTitleProvider()
        setupRecyclerView()
        setupPodcastListView()
        addBackStackListener()
        if (savedInstanceState == null) {
            scheduleJobs()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        searchMenuItem = menu.findItem(R.id.search_item)
        val searchView = searchMenuItem.actionView as SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        if (supportFragmentManager.backStackEntryCount > 0) {
            podcastRecyclerView.hide()
            searchMenuItem.isVisible = false
        }
        updateTitle()
        return true
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == Intent.ACTION_SEARCH) {
            setIntent(intent)
            handleSearchIntent(intent)
        } else {
            handleNotificationIntent(intent)
        }
    }

    override fun onShowDetails(podcastSummaryViewData: PodcastSummaryViewData) {
        loadingPodcastProgressBar.show()
        searchMenuItem.collapseActionView()
        lifecycle.coroutineScope.launch {
            runCatching { podcastViewModel.getPodcast(podcastSummaryViewData) }.apply {
                onSuccess {
                    loadingPodcastProgressBar.hide()
                    showPodcastDetailsScreen()
                }
                onFailure { exception ->
                    loadingPodcastProgressBar.hide()
                    messageProvider.longPopup(getString(R.string.loading_feed_error_message))
                    Log.e(
                        TAG,
                        "$GET_PODCAST_ERROR_MESSAGE: podcastSummaryViewData.feedUrl",
                        exception
                    )
                }
            }
        }
    }

    override fun onBackPressed() {
        when {
            supportFragmentManager.backStackEntryCount > 0 -> super.onBackPressed()
            podcastViewModel.podcastViewState == PodcastViewState.SUBSCRIPTION -> super.onBackPressed()
            podcastViewModel.podcastViewState == PodcastViewState.SEARCH -> {
                podcastViewModel.podcastViewState = PodcastViewState.SUBSCRIPTION
                showSubscribedPodcasts()
            }
        }
    }

    private fun setupToolbar() = setSupportActionBar(toolbar)

    private fun setupScreenTitleProvider() {
        screenTitleProvider.registerSetTitleFunction { title ->
            toolbar.title = title
        }

        screenTitleProvider.registerSetTitleVisibilityFunction { show ->
            if (show) {
                toolbar.show()
            } else {
                toolbar.hide()
            }
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        podcastRecyclerView.layoutManager = layoutManager
        val dividerItemDecoration =
            DividerItemDecoration(podcastRecyclerView.context, layoutManager.orientation)
        podcastRecyclerView.addItemDecoration(dividerItemDecoration)
        podcastListAdapter = PodcastListAdapter(mutableListOf(), this, this)
        podcastRecyclerView.adapter = podcastListAdapter
    }

    private fun setupPodcastListView() {
        lifecycle.coroutineScope.launch {
            podcastViewModel.podcasts.catch { exception ->
                messageProvider.longPopup(getString(R.string.get_podcasts_error_message))
                Log.e(TAG, GET_PODCASTS_ERROR_MESSAGE, exception)
            }.collect { podcasts ->
                podcastViewModel.subscribedPodcasts = podcasts
                when (podcastViewModel.podcastViewState) {
                    PodcastViewState.SEARCH ->
                        if (supportFragmentManager.backStackEntryCount == 0) {
                            handleSearchIntent(intent)
                        }
                    PodcastViewState.SUBSCRIPTION -> showSubscribedPodcasts()
                }
            }
        }
    }

    private fun addBackStackListener() {
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                podcastRecyclerView.fadeIn()
                searchMenuItem.isVisible = true
            }
        }
    }

    private fun handleNotificationIntent(intent: Intent) {
        val podcastFeedUrl = intent.getStringExtra(EXTRA_FEED_URL)
        if (podcastFeedUrl != null) {
            lifecycle.coroutineScope.launch {
                runCatching { podcastViewModel.setActivePodcast(podcastFeedUrl) }.apply {
                    onSuccess { podcast ->
                        onShowDetails(podcast)
                    }
                    onFailure { exception ->
                        messageProvider.longPopup(getString(R.string.set_active_podcast_error_message))
                        Log.e(TAG, SET_ACTIVE_PODCAST_ERROR_MESSAGE, exception)
                    }
                }
            }
        }
    }

    private fun handleSearchIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_SEARCH) {
            val term = intent.getStringExtra(SearchManager.QUERY) ?: return
            podcastViewModel.podcastViewState = PodcastViewState.SEARCH
            performSearch(term)
        }
    }

    private fun performSearch(term: String) {
        updateUIonSearchStarted()
        lifecycle.coroutineScope.launch {
            runCatching { searchViewModel.searchPodcasts(term) }.apply {
                onSuccess { podcasts ->
                    updateUIonSearchFinished()
                    podcastListAdapter.setPodcasts(podcasts)
                }
                onFailure { exception ->
                    updateUIonSearchFinished()
                    podcastListAdapter.setPodcasts(emptyList())
                    messageProvider.longPopup(getString(R.string.search_podcasts_error_message))
                    Log.e(TAG, SEARCH_PODCASTS_ERROR_MESSAGE, exception)
                }
            }
        }
    }

    private fun updateUIonSearchStarted() {
        podcastRecyclerView.fadeOut()
        loadingPodcastProgressBar.show()
    }

    private fun updateUIonSearchFinished() {
        podcastRecyclerView.fadeIn()
        loadingPodcastProgressBar.hide()
        updateTitle()
    }

    private fun showPodcastDetailsScreen() {
        router.showPodcastDetailsScreen()
        podcastRecyclerView.hide()
        searchMenuItem.isVisible = false
    }

    private fun showSubscribedPodcasts() {
        podcastRecyclerView.fadeOut()
        updateTitle()
        podcastListAdapter.setPodcasts(podcastViewModel.subscribedPodcasts)
        podcastRecyclerView.fadeIn()
    }

    private fun updateTitle() {
        toolbar.title = when (podcastViewModel.podcastViewState) {
            PodcastViewState.SEARCH -> searchViewModel.searchQuery
            PodcastViewState.SUBSCRIPTION -> getString(R.string.subscribed_podcasts)
        }
    }

    private fun scheduleJobs() = podcastViewModel.scheduleEpisodeUpdateJob()
}
