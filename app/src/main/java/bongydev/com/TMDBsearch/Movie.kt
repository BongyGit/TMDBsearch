package bongydev.com.TMDBsearch

data class Movie(
    val id: Int,
    val title: String,
    val poster_path: String?,
    val release_date: String,
    val overview: String,
    val vote_average: Double,
    val genre_ids: List<Int>,
    var imdb_id: String = "N/A"
)

data class MovieSearchResponse(
    val results: List<Movie>
)

data class MovieDetails(
    val id: Int,
    val imdb_id: String?
)

data class Genre(
    val id: Int,
    val name: String
)

object GenreManager {
    private val genres = mapOf(
        28 to "Action",
        12 to "Adventure",
        16 to "Animation",
        35 to "Comedy",
        80 to "Crime",
        99 to "Documentary",
        18 to "Drama",
        10751 to "Family",
        14 to "Fantasy",
        36 to "History",
        27 to "Horror",
        10402 to "Music",
        9648 to "Mystery",
        10749 to "Romance",
        878 to "Science Fiction",
        10770 to "TV Movie",
        53 to "Thriller",
        10752 to "War",
        37 to "Western"
    )

    fun getGenreNames(genreIds: List<Int>): String {
        return genreIds.mapNotNull { genres[it] }.joinToString(", ")
    }
}
