package com.zyc.wan.data.network.response

data class LoginResult(val password: String = "",
                       val publicName: String = "",
                       val icon: String = "",
                       val nickname: String = "",
                       val admin: Boolean = false,
                       val id: Int = 0,
                       val type: Int = 0,
                       val email: String = "",
                       val coinCount: Int = 0,
                       val token: String = "",
                       val username: String = "")