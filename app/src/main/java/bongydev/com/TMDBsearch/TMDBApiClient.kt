package bongydev.com.TMDBsearch

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TMDBApiClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    private const val API_KEY = "e04f7d260ec670160a9d2aaa7f9a3bef"

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(ApiKeyInterceptor())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val tmdbService: TMDBService = retrofit.create(TMDBService::class.java)

    private class ApiKeyInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val url = originalRequest.url.newBuilder()
                .addQueryParameter("api_key", API_KEY)
                .build()
            val newRequest = originalRequest.newBuilder()
                .url(url)
                .build()
            return chain.proceed(newRequest)
        }
    }
}
