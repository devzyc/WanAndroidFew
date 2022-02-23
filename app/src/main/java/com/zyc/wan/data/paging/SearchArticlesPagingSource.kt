package com.zyc.wan.data.paging

import com.zyc.wan.data.repo.SearchRepo

class SearchArticlesPagingSource(
    private val repo: SearchRepo,
    private val key: String
) : ArticlePagingSource() {

    override suspend fun getPagedArticles(nextPage: Int) = repo.searchArticles(nextPage, key)
}