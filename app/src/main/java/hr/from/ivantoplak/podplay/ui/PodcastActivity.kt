package hr.from.ivantoplak.podplay.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import hr.from.ivantoplak.podplay.R
import hr.from.ivantoplak.podplay.adapter.PodcastListAdapter
import hr.from.ivantoplak.podplay.adapter.PodcastListAdapter.PodcastListAdapterListener
import hr.from.ivantoplak.podplay.extensions.hide
import hr.from.ivantoplak.podplay.extensions.show
import hr.from.ivantoplak.podplay.intent.EXTRA_FEED_URL
import hr.from.ivantoplak.podplay.model.PodcastSummaryViewData
import hr.from.ivantoplak.podplay.ui.common.HiltActivity
import hr.from.ivantoplak.podplay.viewmodel.PodcastViewModel
import hr.from.ivantoplak.podplay.viewmodel.PodcastViewState
import hr.from.ivantoplak.podplay.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.activity_podcast.*

class PodcastActivity : HiltActivity(), PodcastListAdapterListener {

    private val searchViewModel: SearchViewModel by viewModels()
    private val podcastViewModel: PodcastViewModel by viewModels()

    private lateinit var podcastListAdapter: PodcastListAdapter
    private lateinit var searchMenuItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
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
        podcastViewModel.getPodcast(podcastSummaryViewData) {
            loadingPodcastProgressBar.hide()
            if (it.isValid()) {
                showPodcastDetailsScreen()
            } else {
                showError(getString(R.string.error_loading_feed, podcastSummaryViewData.feedUrl))
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
        podcastViewModel.getPodcasts()?.observe(this, Observer { podcasts ->
            podcasts?.let { showPodcasts() }
        })
    }

    private fun addBackStackListener() {
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                podcastRecyclerView.show()
                searchMenuItem.isVisible = true
            }
        }
    }

    private fun showPodcasts() {
        when (podcastViewModel.podcastViewState) {
            PodcastViewState.SEARCH -> handleSearchIntent(intent)
            PodcastViewState.SUBSCRIPTION -> showSubscribedPodcasts()
        }
    }

    private fun handleNotificationIntent(intent: Intent) {
        val podcastFeedUrl = intent.getStringExtra(EXTRA_FEED_URL)
        if (podcastFeedUrl != null) {
            podcastViewModel.setActivePodcast(podcastFeedUrl) {
                onShowDetails(it)
            }
        }
    }

    private fun handleSearchIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_SEARCH) {
            searchViewModel.searchQuery = intent.getStringExtra(SearchManager.QUERY) ?: return
            podcastViewModel.podcastViewState = PodcastViewState.SEARCH
            performSearch(searchViewModel.searchQuery)
        }
    }

    private fun performSearch(term: String) {
        loadingPodcastProgressBar.show()
        searchViewModel.searchPodcasts(term) { results ->
            loadingPodcastProgressBar.hide()
            updateTitle()
            podcastListAdapter.setSearchData(results)
        }
    }

    private fun showPodcastDetailsScreen() {
        router.showPodcastDetailsScreen()
        podcastRecyclerView.hide()
        searchMenuItem.isVisible = false
    }

    private fun showError(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok_button), null)
            .create()
            .show()
    }

    private fun showSubscribedPodcasts() {
        val podcasts = podcastViewModel.getPodcasts()?.value
        podcasts?.let {
            updateTitle()
            podcastListAdapter.setSearchData(it)
        }
    }

    private fun updateTitle() {
        toolbar.title = when (podcastViewModel.podcastViewState) {
            PodcastViewState.SEARCH -> searchViewModel.searchQuery
            PodcastViewState.SUBSCRIPTION -> getString(R.string.subscribed_podcasts)
        }
    }

    private fun scheduleJobs() = podcastViewModel.scheduleEpisodeUpdateJob()
}
