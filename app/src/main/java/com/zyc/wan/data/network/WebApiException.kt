package com.zyc.wan.data.network

class WebApiException(message: String?) : RuntimeException(message) {

    companion object {
        private const val serialVersionUID = -8588129487495084493L
    }
}