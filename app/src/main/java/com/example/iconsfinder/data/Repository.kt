package com.example.iconsfinder.data

import androidx.lifecycle.MutableLiveData
import com.example.iconsfinder.BuildConfig
import com.example.iconsfinder.model.ApiResponse
import com.example.iconsfinder.model.Icon
import com.example.iconsfinder.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.http.Headers

object Repository {

    private val coroutineScope = Dispatchers.IO
    private val iconsLiveData = MutableLiveData<List<Icon>>()

    fun getIcons(query: String, count: Int, index: Int): MutableLiveData<List<Icon>> {
        isLoading = true
        val job = CoroutineScope(coroutineScope).launch {
            val request: Response<ApiResponse> = retrofitClient.getIcons(params(query, count, index))

            if (request.isSuccessful) {
                isLoading = false
                iconsLiveData.postValue(request.body()?.icons)
            } else {
                isLoading = false
                //iconsLiveData.postValue(listOf())
            }
        }

        return iconsLiveData
    }

    @Headers("Authorization: Bearer $API_KEY")
    private fun params(query: String, count: Int, index: Int): Map<String, String> =

        mapOf(
            QUERY to query,
            COUNT to count.toString(),
            START_INDEX to index.toString(),
            CLIENT_ID to BuildConfig.CLIENT_ID,
            API_KEY to BuildConfig.API_KEY
        )
}