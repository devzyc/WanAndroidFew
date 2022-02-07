package com.zyc.wan.reusable

import com.google.gson.annotations.SerializedName

data class Paged<T>(
    @SerializedName("datas") val list: List<T>,
    @SerializedName("total") val pageCount: Int = 0,
    @SerializedName("over") val isLastPage: Boolean = false,
)