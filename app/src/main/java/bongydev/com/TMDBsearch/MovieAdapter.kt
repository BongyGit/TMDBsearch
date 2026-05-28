package bongydev.com.TMDBsearch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MovieAdapter(private val moviesList: List<Movie>) :
    RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val posterImage: ImageView = itemView.findViewById(R.id.ivPoster)
        private val titleText: TextView = itemView.findViewById(R.id.tvTitle)
        private val yearText: TextView = itemView.findViewById(R.id.tvYear)
        private val genreText: TextView = itemView.findViewById(R.id.tvGenre)
        private val plotText: TextView = itemView.findViewById(R.id.tvPlot)
        private val ratingText: TextView = itemView.findViewById(R.id.tvRating)
        private val tmdbIdText: TextView = itemView.findViewById(R.id.tvTMDBId)
        private val imdbIdText: TextView = itemView.findViewById(R.id.tvIMDBId)

        fun bind(movie: Movie) {
            titleText.text = movie.title
            yearText.text = "Year: ${movie.release_date.take(4)}"
            genreText.text = "Genres: ${GenreManager.getGenreNames(movie.genre_ids)}"
            plotText.text = movie.overview
            ratingText.text = "Rating: ${movie.vote_average}"
            tmdbIdText.text = "TMDB ID: ${movie.id}"
            imdbIdText.text = "IMDB ID: ${movie.imdb_id}"

            // Load poster image using Glide
            if (!movie.poster_path.isNullOrEmpty()) {
                val imageUrl = "https://image.tmdb.org/t/p/w500${movie.poster_path}"
                Glide.with(itemView.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .into(posterImage)
            } else {
                posterImage.setImageResource(R.drawable.ic_placeholder)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(moviesList[position])
    }

    override fun getItemCount(): Int = moviesList.size
}
