package com.strikes.busgapp.eiorfk.domain.model

import com.google.gson.annotations.SerializedName


data class StrikeBudgetEntity (
    @SerializedName("ok")
    val strikeBudgetOk: String,
    @SerializedName("url")
    val strikeBudgetUrl: String,
    @SerializedName("expires")
    val strikeBudgetExpires: Long,
)