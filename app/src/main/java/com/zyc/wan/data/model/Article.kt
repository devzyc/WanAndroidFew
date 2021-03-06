package com.zyc.wan.data.model

data class Article(val shareDate: Long = 0,
                   val projectLink: String = "",
                   val prefix: String = "",
                   val canEdit: Boolean = false,
                   val origin: String = "",
                   val link: String = "",
                   val title: String = "",
                   val type: Int = 0,
                   val selfVisible: Int = 0,
                   val apkLink: String = "",
                   val envelopePic: String = "",
                   val audit: Int = 0,
                   val chapterId: Int = 0,
                   val host: String = "",
                   val realSuperChapterId: Int = 0,
                   val id: Int = 0,
                   val courseId: Int = 0,
                   val superChapterName: String = "",
                   val descMd: String = "",
                   val publishTime: Long = 0,
                   val niceShareDate: String = "",
                   val visible: Int = 0,
                   val niceDate: String = "",
                   val author: String = "",
                   val zan: Int = 0,
                   val chapterName: String = "",
                   val userId: Int = 0,
                   val superChapterId: Int = 0,
                   val fresh: Boolean = false,
                   var collect: Boolean = false,
                   val shareUser: String = "",
                   val desc: String = "")