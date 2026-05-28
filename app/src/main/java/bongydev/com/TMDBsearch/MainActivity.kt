package bongydev.com.TMDBsearch

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var movieTitleInput: EditText
    private lateinit var movieYearInput: EditText
    private lateinit var searchButton: Button
    private lateinit var moviesRecyclerView: RecyclerView
    private lateinit var movieAdapter: MovieAdapter
    private val moviesList = mutableListOf<Movie>()
    private val tmdbApi = TMDBApiClient.tmdbService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        movieTitleInput = findViewById(R.id.etMovieTitle)
        movieYearInput = findViewById(R.id.etMovieYear)
        searchButton = findViewById(R.id.btnSearch)
        moviesRecyclerView = findViewById(R.id.rvMovies)

        // Set up RecyclerView
        movieAdapter = MovieAdapter(moviesList)
        moviesRecyclerView.adapter = movieAdapter
        moviesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Search button click listener
        searchButton.setOnClickListener {
            val title = movieTitleInput.text.toString().trim()
            val year = movieYearInput.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a movie title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            searchMovies(title, year)
        }
    }

    private fun searchMovies(title: String, year: String) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = if (year.isNotEmpty()) {
                    tmdbApi.searchMovies(title, year)
                } else {
                    tmdbApi.searchMovies(title, null)
                }

                if (response.isSuccessful && response.body() != null) {
                    val results = response.body()!!.results

                    if (results.isEmpty()) {
                        Toast.makeText(
                            this@MainActivity,
                            "no movie found, try again",
                            Toast.LENGTH_SHORT
                        ).show()
                        movieTitleInput.text.clear()
                        movieYearInput.text.clear()
                        moviesList.clear()
                        movieAdapter.notifyDataSetChanged()
                    } else {
                        // Fetch IMDB IDs for each movie
                        val moviesWithImdbId = results.map { movie ->
                            val detailsResponse = tmdbApi.getMovieDetails(movie.id)
                            val imdbId = if (detailsResponse.isSuccessful) {
                                detailsResponse.body()?.imdb_id ?: "N/A"
                            } else {
                                "N/A"
                            }
                            movie.copy(imdb_id = imdbId)
                        }

                        moviesList.clear()
                        moviesList.addAll(moviesWithImdbId)
                        movieAdapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Error: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
