package com.zyc.wan.biz.detail

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.*
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.ramcosta.composedestinations.annotation.Destination
import com.zyc.wan.ui.DefaultTransitions
import com.zyc.wan.biz.destinations.ArticleDetailScreenDestination
import com.zyc.wan.ui.theme.cyan500
import kotlinx.coroutines.launch

/**
 * @author devzyc
 */
@ExperimentalComposeUiApi
@Destination(style = DefaultTransitions::class)
@Composable
fun ArticleDetailScreen(
    url: String,
    onBack: () -> Unit,
) {
    var progress by remember { mutableStateOf(-1) }
    val activity = LocalContext.current as Activity
    Box {
        CustomWebView(
            url,
            initSettings = { settings ->
                settings?.apply {
                    javaScriptEnabled = true
                    //是否支持通过JS打开新窗口
                    javaScriptCanOpenWindowsAutomatically = true
                    //将图片调整到适合webView的大小
                    useWideViewPort = true
                    //缩放至屏幕的大小
                    loadWithOverviewMode = true
                    //缩放操作
                    setSupportZoom(true)
                    builtInZoomControls = true
                    displayZoomControls = true
                    //不加载缓存内容
                    cacheMode = WebSettings.LOAD_NO_CACHE
                    domStorageEnabled = true
                    loadsImagesAutomatically = true
                    layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }
            },
            onProgressChange = { progress = it },
            onBack = { webView ->
                if (webView?.canGoBack() == true) {
                    webView.goBack()
                } else {
                    activity.finish()
                }
            }
        )
        LinearProgressIndicator(
            progress = progress * 1f / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (progress == 100) 0.dp else 5.dp),
            color = cyan500
        )
    }

    BackHandler {
        onBack()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CustomWebView(
    url: String,
    modifier: Modifier = Modifier.fillMaxSize(),
    initSettings: (webSettings: WebSettings?) -> Unit = {},
    onProgressChange: (progress: Int) -> Unit = {},
    onBack: (webView: WebView?) -> Unit,
    onReceivedError: (error: WebResourceError?) -> Unit = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d(ArticleDetailScreenDestination.route, "CustomWebView encountered an error: ${it?.description}")
        }
    }
) {
    val chromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            //回调网页内容加载进度
            onProgressChange(newProgress)
            super.onProgressChanged(view, newProgress)
        }
    }
    val viewClient = object : WebViewClient() {
        override fun onPageStarted(
            view: WebView?,
            url: String?,
            favicon: Bitmap?
        ) {
            super.onPageStarted(view, url, favicon)
            onProgressChange(-1)
        }

        override fun onPageFinished(
            view: WebView?,
            url: String?
        ) {
            super.onPageFinished(view, url)
            onProgressChange(100)
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            if (request?.url == null) return false
            val requestUrl = request.url.toString()
            try {
                if (!requestUrl.startsWith("http://")
                    && !requestUrl.startsWith("https://")
                ) {
                    //处理非http和https开头的链接地址
                    Intent(Intent.ACTION_VIEW, Uri.parse(requestUrl)).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        view?.context?.applicationContext?.startActivity(this)
                    }
                    return true
                }
            } catch (e: Exception) {
                Log.e(ArticleDetailScreenDestination.route, "shouldOverrideUrlLoading: $e")
                return true
            }
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            onReceivedError(error)
        }
    }
    var webView: WebView? = null
    val coroutineScope = rememberCoroutineScope()
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                webViewClient = viewClient
                webChromeClient = chromeClient
                overScrollMode = WebView.OVER_SCROLL_NEVER
                initSettings(settings)
                webView = this
                loadUrl(url)
            }
        }
    )
    BackHandler {
        coroutineScope.launch { onBack(webView) }
    }
}