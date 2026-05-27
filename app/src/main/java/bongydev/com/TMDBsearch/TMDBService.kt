package bongydev.com.TMDBsearch

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBService {
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("primary_release_year") year: String?
    ): Response<MovieSearchResponse>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int
    ): Response<MovieDetails>
}
