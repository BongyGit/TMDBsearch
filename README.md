# TMDBsearch

An Android Kotlin application for searching movies using The Movie Database (TMDB) API.

## Features

- Search movies by title
- Optional year filter
- Display movie details including:
  - Poster image
  - Title
  - Release year (extracted from release date)
  - Genres
  - Plot overview
  - Vote average (rating)
  - TMDB ID
  - IMDB ID

## Setup

1. Clone the repository
2. Open in Android Studio
3. Build and run on an Android device or emulator (API level 24+)

## API Configuration

The app uses The Movie Database (TMDB) API. The API key is configured in `TMDBApiClient.kt`.

## Architecture

- **MainActivity.kt**: Main activity with search functionality
- **MovieAdapter.kt**: RecyclerView adapter for displaying movies
- **TMDBService.kt**: Retrofit service interface for API calls
- **TMDBApiClient.kt**: Retrofit client initialization and API key interceptor
- **Movie.kt**: Data classes for API responses

## Dependencies

- Retrofit 2.9.0 - HTTP client
- Glide 4.16.0 - Image loading
- OkHttp - HTTP logging
- Coroutines - Asynchronous operations
