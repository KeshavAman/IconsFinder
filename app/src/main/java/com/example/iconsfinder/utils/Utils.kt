package com.example.iconsfinder.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build


private val interceptor = HttpLoggingInterceptor()

private const val REQUEST_CODE = 2

var isLoading = false

val retrofitClient: IconFinderService by lazy {
    interceptor.level = HttpLoggingInterceptor.Level.BODY



    val client = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder().addInterceptor { chain ->
            val request = chain.request().newBuilder().addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer ${API_KEY}").build()
            chain.proceed(request)
        }.build())
        .build()
        .create(IconFinderService::class.java)
    client
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun askForPermission(activity: Activity) {
    ActivityCompat.requestPermissions(
        activity,
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
        REQUEST_CODE
    )
}

fun isPermissionGranted(context: Context): Boolean {
    return (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED)
}

fun downloadImage(context: Context, downloadUrl: String) {
    val intent = Intent(context, DownloadService::class.java)
    intent.putExtra("url", downloadUrl)
    context.startService(intent)
}

fun isNetworkConnected(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT < 23) {
        val info = cm.activeNetworkInfo

        if (info != null) {
            return info.isConnected &&
                    (info.type == ConnectivityManager.TYPE_WIFI ||
                            info.type == ConnectivityManager.TYPE_MOBILE)
        }
    } else {
        val network = cm.activeNetwork
        if (network != null) {
            val capabilities = cm.getNetworkCapabilities(network)
            return capabilities!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        }
    }

    return false
}