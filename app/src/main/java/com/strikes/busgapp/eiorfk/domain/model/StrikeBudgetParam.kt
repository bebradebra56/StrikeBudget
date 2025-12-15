package com.strikes.busgapp.eiorfk.domain.model

import com.google.gson.annotations.SerializedName


private const val STRIKE_BUDGET_A = "com.strikes.busgapp"
private const val STRIKE_BUDGET_B = "strikebudget"
data class StrikeBudgetParam (
    @SerializedName("af_id")
    val strikeBudgetAfId: String,
    @SerializedName("bundle_id")
    val strikeBudgetBundleId: String = STRIKE_BUDGET_A,
    @SerializedName("os")
    val strikeBudgetOs: String = "Android",
    @SerializedName("store_id")
    val strikeBudgetStoreId: String = STRIKE_BUDGET_A,
    @SerializedName("locale")
    val strikeBudgetLocale: String,
    @SerializedName("push_token")
    val strikeBudgetPushToken: String,
    @SerializedName("firebase_project_id")
    val strikeBudgetFirebaseProjectId: String = STRIKE_BUDGET_B,

    )