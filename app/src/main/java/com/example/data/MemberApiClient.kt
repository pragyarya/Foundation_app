package com.example.data

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

private class AppCookieJar : CookieJar {

    private val cookies = mutableListOf<Cookie>()

    override fun saveFromResponse(
        url: HttpUrl,
        newCookies: List<Cookie>
    ) {
        synchronized(cookies) {
            cookies.removeAll { old ->
                newCookies.any { newCookie ->
                    old.name == newCookie.name &&
                    old.domain == newCookie.domain &&
                    old.path == newCookie.path
                }
            }

            cookies.addAll(newCookies)
        }
    }

    override fun loadForRequest(
        url: HttpUrl
    ): List<Cookie> {
        synchronized(cookies) {
            return cookies.filter { it.matches(url) }
        }
    }

    fun clear() {
        synchronized(cookies) {
            cookies.clear()
        }
    }
}

object MemberApiClient {

    private const val BASE_URL = "https://www.afsap.in/"

    private val cookieJar = AppCookieJar()

    private val okHttpClient = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val api: MemberApi by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                MoshiConverterFactory.create()
            )
            .build()
            .create(MemberApi::class.java)
    }

    fun clearSession() {
        cookieJar.clear()
    }
}
