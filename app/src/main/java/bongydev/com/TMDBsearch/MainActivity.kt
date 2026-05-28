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
    private lateinit var imdbIdInput: EditText
    private lateinit var searchImdbIdButton: Button
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
        imdbIdInput = findViewById(R.id.etImdbId)
        searchImdbIdButton = findViewById(R.id.btnSearchImdbId)
        moviesRecyclerView = findViewById(R.id.rvMovies)

        // Set up RecyclerView
        movieAdapter = MovieAdapter(moviesList)
        moviesRecyclerView.adapter = movieAdapter
        moviesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Search by Name button click listener
        searchButton.setOnClickListener {
            val title = movieTitleInput.text.toString().trim()
            val year = movieYearInput.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a movie title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            searchMovies(title, year)
        }

        // Search by IMDB ID button click listener
        searchImdbIdButton.setOnClickListener {
            val imdbId = imdbIdInput.text.toString().trim()

            if (imdbId.isEmpty()) {
                Toast.makeText(this, "Please enter an IMDB ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            searchMovieByImdbId(imdbId)
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

    private fun searchMovieByImdbId(imdbId: String) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = tmdbApi.findByImdbId(imdbId)

                if (response.isSuccessful && response.body() != null) {
                    val movieResults = response.body()!!.movie_results

                    if (movieResults.isEmpty()) {
                        Toast.makeText(
                            this@MainActivity,
                            "no movie found, try again",
                            Toast.LENGTH_SHORT
                        ).show()
                        imdbIdInput.text.clear()
                        moviesList.clear()
                        movieAdapter.notifyDataSetChanged()
                    } else {
                        // Get the first movie result and fetch full details
                        val movie = movieResults[0]
                        val detailsResponse = tmdbApi.getMovieDetails(movie.id)

                        if (detailsResponse.isSuccessful && detailsResponse.body() != null) {
                            val movieDetails = detailsResponse.body()!!
                            val movieWithDetails = Movie(
                                id = movieDetails.id,
                                title = movieDetails.title,
                                overview = movieDetails.overview,
                                poster_path = movieDetails.poster_path,
                                release_date = movieDetails.release_date,
                                vote_average = movieDetails.vote_average,
                                imdb_id = movieDetails.imdb_id
                            )

                            moviesList.clear()
                            moviesList.add(movieWithDetails)
                            movieAdapter.notifyDataSetChanged()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Error fetching movie details",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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
