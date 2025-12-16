package com.strikes.busgapp.eiorfk.presentation.app

import android.app.Application
import android.util.Log
import android.view.WindowManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.strikes.busgapp.eiorfk.presentation.di.strikeBudgetModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


sealed interface StrikeBudgetAppsFlyerState {
    data object StrikeBudgetDefault : StrikeBudgetAppsFlyerState
    data class StrikeBudgetSuccess(val strikeBudgetData: MutableMap<String, Any>?) :
        StrikeBudgetAppsFlyerState

    data object StrikeBudgetError : StrikeBudgetAppsFlyerState
}

interface StrikeBudgetAppsApi {
    @Headers("Content-Type: application/json")
    @GET(STRIKE_BUDGET_LIN)
    fun strikeBudgetGetClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}

private const val STRIKE_BUDGET_APP_DEV = "3KTK9NCDByogS8StccPUrZ"
private const val STRIKE_BUDGET_LIN = "com.strikes.busgapp"

class StrikeBudgetApplication : Application() {

    private var strikeBudgetIsResumed = false
//    private var strikeBudgetConversionTimeoutJob: Job? = null
    private var strikeBudgetDeepLinkData: MutableMap<String, Any>? = null

    override fun onCreate() {
        super.onCreate()

        val appsflyer = AppsFlyerLib.getInstance()
        strikeBudgetSetDebufLogger(appsflyer)
        strikeBudgetMinTimeBetween(appsflyer)

        AppsFlyerLib.getInstance().subscribeForDeepLink(object : DeepLinkListener {
            override fun onDeepLinking(p0: DeepLinkResult) {
                when (p0.status) {
                    DeepLinkResult.Status.FOUND -> {
                        strikeBudgetExtractDeepMap(p0.deepLink)
                        Log.d(STRIKE_BUDGET_MAIN_TAG, "onDeepLinking found: ${p0.deepLink}")

                    }

                    DeepLinkResult.Status.NOT_FOUND -> {
                        Log.d(STRIKE_BUDGET_MAIN_TAG, "onDeepLinking not found: ${p0.deepLink}")
                    }

                    DeepLinkResult.Status.ERROR -> {
                        Log.d(STRIKE_BUDGET_MAIN_TAG, "onDeepLinking error: ${p0.error}")
                    }
                }
            }

        })


        appsflyer.init(
            STRIKE_BUDGET_APP_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
//                    strikeBudgetConversionTimeoutJob?.cancel()
                    Log.d(STRIKE_BUDGET_MAIN_TAG, "onConversionDataSuccess: $p0")
                    val afStatus = p0?.get("af_status")?.toString() ?: "null"
                    if (afStatus == "Organic") {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(5000)
                                val api = strikeBudgetGetApi(
                                    "https://gcdsdk.appsflyer.com/install_data/v4.0/",
                                    null
                                )
                                val response = api.strikeBudgetGetClient(
                                    devkey = STRIKE_BUDGET_APP_DEV,
                                    deviceId = strikeBudgetGetAppsflyerId()
                                ).awaitResponse()

                                val resp = response.body()
                                Log.d(STRIKE_BUDGET_MAIN_TAG, "After 5s: $resp")
                                if (resp?.get("af_status") == "Organic" || resp?.get("af_status") == null) {
                                    strikeBudgetResume(StrikeBudgetAppsFlyerState.StrikeBudgetError)
                                } else {
                                    strikeBudgetResume(
                                        StrikeBudgetAppsFlyerState.StrikeBudgetSuccess(resp)
                                    )
                                }
                            } catch (d: Exception) {
                                Log.d(STRIKE_BUDGET_MAIN_TAG, "Error: ${d.message}")
                                strikeBudgetResume(StrikeBudgetAppsFlyerState.StrikeBudgetError)
                            }
                        }
                    } else {
                        strikeBudgetResume(StrikeBudgetAppsFlyerState.StrikeBudgetSuccess(p0))
                    }
                }

                override fun onConversionDataFail(p0: String?) {
//                    strikeBudgetConversionTimeoutJob?.cancel()
                    Log.d(STRIKE_BUDGET_MAIN_TAG, "onConversionDataFail: $p0")
                    strikeBudgetResume(StrikeBudgetAppsFlyerState.StrikeBudgetError)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(STRIKE_BUDGET_MAIN_TAG, "onAppOpenAttribution")
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(STRIKE_BUDGET_MAIN_TAG, "onAttributionFailure: $p0")
                }
            },
            this
        )

        appsflyer.start(this, STRIKE_BUDGET_APP_DEV, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(STRIKE_BUDGET_MAIN_TAG, "AppsFlyer started")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(STRIKE_BUDGET_MAIN_TAG, "AppsFlyer start error: $p0 - $p1")
            }
        })
//        strikeBudgetStartConversionTimeout()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@StrikeBudgetApplication)
            modules(
                listOf(
                    strikeBudgetModule
                )
            )
        }
    }

    private fun strikeBudgetExtractDeepMap(dl: DeepLink) {
        val map = mutableMapOf<String, Any>()
        dl.deepLinkValue?.let { map["deep_link_value"] = it }
        dl.mediaSource?.let { map["media_source"] = it }
        dl.campaign?.let { map["campaign"] = it }
        dl.campaignId?.let { map["campaign_id"] = it }
        dl.afSub1?.let { map["af_sub1"] = it }
        dl.afSub2?.let { map["af_sub2"] = it }
        dl.afSub3?.let { map["af_sub3"] = it }
        dl.afSub4?.let { map["af_sub4"] = it }
        dl.afSub5?.let { map["af_sub5"] = it }
        dl.matchType?.let { map["match_type"] = it }
        dl.clickHttpReferrer?.let { map["click_http_referrer"] = it }
        dl.getStringValue("timestamp")?.let { map["timestamp"] = it }
        dl.isDeferred?.let { map["is_deferred"] = it }
        for (i in 1..10) {
            val key = "deep_link_sub$i"
            dl.getStringValue(key)?.let {
                if (!map.containsKey(key)) {
                    map[key] = it
                }
            }
        }
        Log.d(STRIKE_BUDGET_MAIN_TAG, "Extracted DeepLink data: $map")
        strikeBudgetDeepLinkData = map
    }

//    private fun strikeBudgetStartConversionTimeout() {
//        strikeBudgetConversionTimeoutJob = CoroutineScope(Dispatchers.Main).launch {
//            delay(30000)
//            if (!strikeBudgetIsResumed) {
//                Log.d(STRIKE_BUDGET_MAIN_TAG, "TIMEOUT: No conversion data received in 30s")
//                strikeBudgetResume(StrikeBudgetAppsFlyerState.StrikeBudgetError)
//            }
//        }
//    }

    private fun strikeBudgetResume(state: StrikeBudgetAppsFlyerState) {
//        strikeBudgetConversionTimeoutJob?.cancel()
        if (state is StrikeBudgetAppsFlyerState.StrikeBudgetSuccess) {
            val convData = state.strikeBudgetData ?: mutableMapOf()
            val deepData = strikeBudgetDeepLinkData ?: mutableMapOf()
            val merged = mutableMapOf<String, Any>().apply {
                putAll(convData)
                for ((key, value) in deepData) {
                    if (!containsKey(key)) {
                        put(key, value)
                    }
                }
            }
            if (!strikeBudgetIsResumed) {
                strikeBudgetIsResumed = true
                strikeBudgetConversionFlow.value =
                    StrikeBudgetAppsFlyerState.StrikeBudgetSuccess(merged)
            }
        } else {
            if (!strikeBudgetIsResumed) {
                strikeBudgetIsResumed = true
                strikeBudgetConversionFlow.value = state
            }
        }
    }

    private fun strikeBudgetGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(this) ?: ""
        Log.d(STRIKE_BUDGET_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    private fun strikeBudgetSetDebufLogger(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun strikeBudgetMinTimeBetween(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }

    private fun strikeBudgetGetApi(url: String, client: OkHttpClient?): StrikeBudgetAppsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }

    companion object {

        var strikeBudgetInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val strikeBudgetConversionFlow: MutableStateFlow<StrikeBudgetAppsFlyerState> = MutableStateFlow(
            StrikeBudgetAppsFlyerState.StrikeBudgetDefault
        )
        var STRIKE_BUDGET_FB_LI: String? = null
        const val STRIKE_BUDGET_MAIN_TAG = "StrikeBudgetMainTag"
    }
}