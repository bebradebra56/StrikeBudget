package com.strikes.busgapp.eiorfk.data.repo

import android.util.Log
import com.strikes.busgapp.eiorfk.domain.model.StrikeBudgetEntity
import com.strikes.busgapp.eiorfk.domain.model.StrikeBudgetParam
import com.strikes.busgapp.eiorfk.presentation.app.StrikeBudgetApplication.Companion.STRIKE_BUDGET_MAIN_TAG
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface StrikeBudgetApi {
    @Headers("Content-Type: application/json")
    @POST("config.php")
    fun strikeBudgetGetClient(
        @Body jsonString: JsonObject,
    ): Call<StrikeBudgetEntity>
}


private const val STRIKE_BUDGET_MAIN = "https://strikebudget.com/"
class StrikeBudgetRepository {

    suspend fun strikeBudgetGetClient(
        strikeBudgetParam: StrikeBudgetParam,
        strikeBudgetConversion: MutableMap<String, Any>?
    ): StrikeBudgetEntity? {
        val gson = Gson()
        val api = strikeBudgetGetApi(STRIKE_BUDGET_MAIN, null)

        val strikeBudgetJsonObject = gson.toJsonTree(strikeBudgetParam).asJsonObject
        strikeBudgetConversion?.forEach { (key, value) ->
            val element: JsonElement = gson.toJsonTree(value)
            strikeBudgetJsonObject.add(key, element)
        }
        return try {
            val strikeBudgetRequest: Call<StrikeBudgetEntity> = api.strikeBudgetGetClient(
                jsonString = strikeBudgetJsonObject,
            )
            val strikeBudgetResult = strikeBudgetRequest.awaitResponse()
            Log.d(STRIKE_BUDGET_MAIN_TAG, "Retrofit: Result code: ${strikeBudgetResult.code()}")
            if (strikeBudgetResult.code() == 200) {
                Log.d(STRIKE_BUDGET_MAIN_TAG, "Retrofit: Get request success")
                Log.d(STRIKE_BUDGET_MAIN_TAG, "Retrofit: Code = ${strikeBudgetResult.code()}")
                Log.d(STRIKE_BUDGET_MAIN_TAG, "Retrofit: ${strikeBudgetResult.body()}")
                strikeBudgetResult.body()
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            Log.d(STRIKE_BUDGET_MAIN_TAG, "Retrofit: Get request failed")
            Log.d(STRIKE_BUDGET_MAIN_TAG, "Retrofit: ${e.message}")
            null
        }
    }


    private fun strikeBudgetGetApi(url: String, client: OkHttpClient?) : StrikeBudgetApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }


}
