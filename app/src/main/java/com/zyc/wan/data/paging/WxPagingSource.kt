package com.zyc.wan.data.paging

import com.zyc.wan.data.repo.WxListsRepo

class WxPagingSource(
    private val repo: WxListsRepo,
    private val channelId: Int
) : ArticlePagingSource() {

    override suspend fun getPagedArticles(nextPage: Int) = repo.getArticles(nextPage, channelId)
}