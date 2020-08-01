package hr.from.ivantoplak.podplay.adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import hr.from.ivantoplak.podplay.R
import hr.from.ivantoplak.podplay.extensions.inflate
import hr.from.ivantoplak.podplay.model.PodcastSummaryViewData
import kotlinx.android.synthetic.main.search_item.view.*

class PodcastListAdapter(
    private val podcastSummaryViewList: MutableList<PodcastSummaryViewData>,
    private val podcastListAdapterListener: PodcastListAdapterListener,
    private val parentActivity: Activity
) : RecyclerView.Adapter<PodcastListAdapter.ViewHolder>() {

    interface PodcastListAdapterListener {
        fun onShowDetails(podcastSummaryViewData: PodcastSummaryViewData)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var podcastSummaryViewData: PodcastSummaryViewData
        val nameTextView: TextView = view.podcastNameTextView
        val genreTextView: TextView = view.podcastGenreTextView
        val podcastImageView: ImageView = view.podcastImage

        init {
            view.setOnClickListener {
                podcastListAdapterListener.onShowDetails(podcastSummaryViewData)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PodcastListAdapter.ViewHolder =
        ViewHolder(parent.inflate(R.layout.search_item))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchView = podcastSummaryViewList[position]
        holder.podcastSummaryViewData = searchView
        holder.nameTextView.text = searchView.name
        holder.genreTextView.text = searchView.primaryGenreName
        Glide.with(parentActivity)
            .load(searchView.imageUrl)
            .into(holder.podcastImageView)
    }

    override fun getItemCount(): Int = podcastSummaryViewList.size

    fun setSearchData(podcastSummaryViewData: List<PodcastSummaryViewData>) {
        podcastSummaryViewList.clear()
        podcastSummaryViewList.addAll(podcastSummaryViewData)
        notifyDataSetChanged()
    }
}