package store.pengu.server

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

fun googleTranslateAPI(string: String): String {
    val client = OkHttpClient()

    val mediaType = "application/x-www-form-urlencoded".toMediaType()
    val body = "q=$string&format=text&target=pt&source=en".toRequestBody(mediaType)
    val request = Request.Builder()
        .url("https://google-translate1.p.rapidapi.com/language/translate/v2")
        .post(body)
        .addHeader("content-type", "application/x-www-form-urlencoded")
        .addHeader("accept-encoding", "application/gzip")
        .addHeader("x-rapidapi-key", "dea39728dcmshd8238214b220c2dp1cf0edjsn13d6e429506a")
        .addHeader("x-rapidapi-host", "google-translate1.p.rapidapi.com")
        .build()

    //return client.newCall(request).execute().networkResponse!!.message //data.translations[0].translatedText
    return "teste"
}