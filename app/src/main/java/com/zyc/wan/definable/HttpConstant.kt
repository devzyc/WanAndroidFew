package com.zyc.wan.definable

import com.zyc.wan.reusable.Preference

/**
 * Created by chenxz on 2018/6/9.
 */
object HttpConstant {

    const val SAVE_USER_LOGIN_KEY = "user/login"
    const val SAVE_USER_REGISTER_KEY = "user/register"

    const val COLLECTIONS_WEBSITE = "lg/collect"
    const val UNCOLLECTIONS_WEBSITE = "lg/uncollect"
    const val ARTICLE_WEBSITE = "article"
    const val TODO_WEBSITE = "lg/todo"
    const val COIN_WEBSITE = "lg/coin"

    const val SET_COOKIE_KEY = "set-cookie"
    const val COOKIE_NAME = "Cookie"

    fun encodeCookie(cookies: List<String>): String {
        val sb = StringBuilder()
        val set = HashSet<String>()
        cookies
                .map { cookie ->
                    cookie.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                }
                .forEach {
                    it.filterNot { set.contains(it) }.forEach { set.add(it) }
                }
        val ite = set.iterator()
        while (ite.hasNext()) {
            val cookie = ite.next()
            sb.append(cookie).append(";")
        }
        val last = sb.lastIndexOf(";")
        if (sb.length - 1 == last) {
            sb.deleteCharAt(last)
        }
        return sb.toString()
    }

    fun saveCookie(url: String?, domain: String?, cookies: String) {
        url ?: return
        var spUrl: String by Preference(url, cookies)
        @Suppress("UNUSED_VALUE")
        spUrl = cookies
        domain ?: return
        var spDomain: String by Preference(domain, cookies)
        @Suppress("UNUSED_VALUE")
        spDomain = cookies
    }

}