package hr.from.ivantoplak.podplay.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.from.ivantoplak.podplay.R
import hr.from.ivantoplak.podplay.extensions.htmlToSpannable
import hr.from.ivantoplak.podplay.extensions.inflate
import hr.from.ivantoplak.podplay.extensions.toHourMinSec
import hr.from.ivantoplak.podplay.model.EpisodeViewData
import kotlinx.android.synthetic.main.episode_item.view.*

class EpisodeListAdapter(
    private val episodeViewList: MutableList<EpisodeViewData>,
    private val episodeListAdapterListener: EpisodeListAdapterListener
) : RecyclerView.Adapter<EpisodeListAdapter.ViewHolder>() {

    interface EpisodeListAdapterListener {
        fun onSelectedEpisode(episodeViewData: EpisodeViewData)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener {
                episodeListAdapterListener.onSelectedEpisode(episodeViewData)
            }
        }

        lateinit var episodeViewData: EpisodeViewData
        val titleTextView: TextView = view.titleView
        val descTextView: TextView = view.descView
        val durationTextView: TextView = view.durationView
        val releaseDateTextView: TextView =
            view.releaseDateView
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EpisodeListAdapter.ViewHolder = ViewHolder(parent.inflate(R.layout.episode_item))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val episodeView = episodeViewList[position]
        holder.episodeViewData = episodeView
        holder.titleTextView.text = episodeView.title
        holder.descTextView.text = episodeView.description.htmlToSpannable()
        holder.durationTextView.text = episodeView.duration.toHourMinSec()
        holder.releaseDateTextView.text = episodeView.releaseDate
    }

    override fun getItemCount(): Int = episodeViewList.size

    fun setEpisodes(episodes: MutableList<EpisodeViewData>) {
        episodeViewList.clear()
        episodeViewList.addAll(episodes)
        notifyDataSetChanged()
    }
}