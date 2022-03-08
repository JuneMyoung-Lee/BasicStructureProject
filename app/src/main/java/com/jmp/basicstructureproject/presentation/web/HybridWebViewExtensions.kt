package com.jmp.basicstructureproject.presentation.web

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Picture
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.jmp.basicstructureproject.AppApplication.CONST.cookieManager
import com.jmp.basicstructureproject.BaseActivity
import com.jmp.basicstructureproject.BuildConfig
import com.jmp.basicstructureproject.presentation.extension.addObserver
import com.jmp.basicstructureproject.presentation.extension.getActivityContext
import com.jmp.basicstructureproject.presentation.web.plugin.AppPluginConfig
import com.jmp.basicstructureproject.presentation.web.plugin.HybridAppPluginFactory
import com.jmp.basicstructureproject.presentation.web.plugin.HybridWebViewPluginListener
import net.gotev.cookiestore.syncToWebKitCookieManager
import org.json.JSONObject
import java.io.FileOutputStream
import java.net.URLDecoder


@SuppressLint("SetJavaScriptEnabled")
@UiThread
fun WebView.loadWithHybridInitialize(
    url: String? = null,
    actionCmd: String? = null,
    activityContext: Context? = null,
    onProgressChanged: HybridWebViewChromeClient.OnProgressChanged? = null,
    onPageStarted: HybridWebViewClient.OnPageStarted? = null,
    onPageFinished: HybridWebViewClient.OnPageFinished? = null
) {
    val thisWebview = this
    scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
    isScrollbarFadingEnabled = true

    setInitialScale(1)//수정하는 방법 : error : Unable to create layer for WebView, size 1008x9024 exceeds max size 8192

    addHybridPlugIn(AppPluginConfig::class.java)

    downloadFileListener()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        //평균적으로 킷캣 이상에서는 하드웨어 가속이 성능이 좋음.
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
    }

    with(settings) {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            setEnableSmoothTransition(true)
            layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            setRenderPriority(WebSettings.RenderPriority.HIGH)
        }

        javaScriptEnabled = true
        setSupportZoom(true)
        builtInZoomControls = true
        databaseEnabled = true
        domStorageEnabled = true
        setGeolocationEnabled(true)
        loadWithOverviewMode = true
        useWideViewPort = true

        allowFileAccess = true
        allowContentAccess = true

        displayZoomControls = false

        scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        isLongClickable = false

        isHapticFeedbackEnabled = false
        isScrollbarFadingEnabled = true
        isVerticalScrollBarEnabled = true
        isHorizontalFadingEdgeEnabled = false
        isHorizontalScrollBarEnabled = false

        javaScriptCanOpenWindowsAutomatically = true
        setSupportMultipleWindows(true)

        textZoom = 100

        cacheMode = WebSettings.LOAD_NO_CACHE

        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(thisWebview, true)

        userAgentString = "$userAgentString APP_BASE_STRUCTURE"

    }

    WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)

    addJavascriptInterface(HybridJavaScript(thisWebview), HybridWebViewConstants.WEB_NATIVE_CALL_SCRIPT_ID)

    val hybridWebViewClient = HybridWebViewClient()
    hybridWebViewClient.setOnPageFinishedListener(onPageFinished)
    webViewClient = hybridWebViewClient

    webChromeClient = HybridWebViewChromeClient(activityContext ?: context, actionCmd, onProgressChanged)

    cookieManager.cookieStore.syncToWebKitCookieManager()

    url?.let {
        loadUrl(it)

        setBackgroundColor(Color.TRANSPARENT)
    }
}

fun WebView.downloadFileListener(){
    this.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->

        val activity = context.getActivityContext() as BaseActivity

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            actionDownload(url, userAgent, contentDisposition, mimetype, contentLength, context)

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ) {
                actionDownloadPermission(url, userAgent, contentDisposition, mimetype, contentLength, context)
            }
        }else{
            actionDownloadPermission(url, userAgent, contentDisposition, mimetype, contentLength, context)
        }
    }
}

fun actionDownloadPermission(url: String , userAgent: String,
                             contentDisposition: String, mimetype: String, contentLength : Long,
                             context: Context){

    val REQUEST_CODE_EXTERNAL_STORAGE = 1004

    val activity = context.getActivityContext() as BaseActivity

    activity.addPermissionCallback(REQUEST_CODE_EXTERNAL_STORAGE, object: BaseActivity.OnPermissionCallback{
        override fun requestResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            when (requestCode) {
                REQUEST_CODE_EXTERNAL_STORAGE -> {
                    if ((grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                        actionDownload(url, userAgent, contentDisposition, mimetype, contentLength, context)
                        return
                    }else{
                        // TODO Dialog 만들기
//                        activity.supportFragmentManager.showChoiceDialog(
//                            title = "",
//                            desc = activity.getString(R.string.failed_permission_move_system_settings),
//                            negButtonTitle = "아니요",
//                            posButtonTitle = "예",
//                            negCallback = {},
//                            posCallback = {
//                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}"))
//                                intent.addCategory(Intent.CATEGORY_DEFAULT)
//                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                                context.startActivity(intent)
//                            }
//                        )
                    }
                }
            }
        }
    })

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        actionDownload(url, userAgent, contentDisposition, mimetype, contentLength, context)
    else {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_CODE_EXTERNAL_STORAGE
        )
    }

}

fun actionDownload(url: String , userAgent: String,
                   contentDisposition: String, mimetype: String, contentLength : Long, context: Context){
    val activity = context.getActivityContext() as BaseActivity

    val request = DownloadManager.Request(Uri.parse(url))
    val dm = activity.getSystemService(DOWNLOAD_SERVICE) as DownloadManager

    val contentDisposition = URLDecoder.decode(contentDisposition,"UTF-8"); //디코딩
    val FileName = contentDisposition.replace("attachment; filename=", "") //attachment; filename*=UTF-8''뒤에 파일명이있는데 파일명만 추출하기위해 앞에 attachment; filename*=UTF-8''제거

    val fileName = FileName //위에서 디코딩하고 앞에 내용을 자른 최종 파일명
    request.setMimeType(mimetype)
    request.addRequestHeader("User-Agent", userAgent)
    request.setDescription("Downloading File")
    request.setAllowedOverMetered(true)
    request.setAllowedOverRoaming(true)
    request.setTitle(fileName)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        request.setRequiresCharging(false)
    }

    request.allowScanningByMediaScanner()
    request.setAllowedOverMetered(true)
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
    dm.enqueue(request)

    Toast.makeText(context, "파일이 다운로드됩니다.", Toast.LENGTH_LONG).show()
}

fun WebView.addHybridPlugIn(configClass : Class<*>){
    getHybridPlugInManager().addHybridPlugIn(configClass)
}

fun WebView.addHybridPlugInListener(lifecycleOwner: LifecycleOwner, listenerHybridWeb : HybridWebViewPluginListener){
    lifecycleOwner.addObserver(
        onStartCallback = {
            getHybridPlugInManager().removeHybridListener(listenerHybridWeb)
            getHybridPlugInManager().addHybridListener(listenerHybridWeb)
        },
        onDestroyCallback = {
            getHybridPlugInManager().removeHybridListener(listenerHybridWeb)
        }
    )
}

fun WebView.callback( isOK: Boolean, plugInID: String, method: String, data: JSONObject ){
    getHybridPlugInManager().callBack( isOK, plugInID, method, data.toString() )
}

fun WebView.getHybridPlugInManager() : HybridAppPluginFactory {
    if(tag == null){
        tag = HybridAppPluginFactory(this)
    }

    return tag as HybridAppPluginFactory
}

fun WebView.screenshot(){

    val picture: Picture = capturePicture()
    val width = picture.width
    val height = picture.height
    if (width > 0 && height > 0) {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        picture.draw(canvas)
        try {
            val fileName = "/sdcard/download/webview_capture1.jpg"
            val fos = FileOutputStream(fileName)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fos)
            fos.close()
            Toast.makeText(context, "스크린샷 저장됨", Toast.LENGTH_LONG).show()
            bitmap.recycle()
        } catch (e: java.lang.Exception) {
            Log.e("jmp_web", e.message.toString())
        }
    }
}

fun WebView.clear(){
    clearHistory()
    clearCache(true)
    loadUrl("about:blank")
}