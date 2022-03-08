package com.jmp.basicstructureproject.presentation.web

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.net.http.SslError
import android.net.http.SslError.SSL_IDMISMATCH
import android.os.Bundle
import android.os.Looper
import android.telephony.SmsManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.*
import android.widget.Toast
import com.jmp.basicstructureproject.BaseActivity
import com.jmp.basicstructureproject.BuildConfig
import com.jmp.basicstructureproject.R
import com.jmp.basicstructureproject.presentation.extension.hasPermissions
import org.json.JSONException
import org.json.JSONObject
import java.net.URISyntaxException
import java.net.URLDecoder


open class HybridWebViewClient : WebViewClient() {

    companion object {
        const val INTENT_PROTOCOL_START = "intent:"
        const val INTENT_PROTOCOL_INTENT = "#Intent;"
        const val INTENT_PROTOCOL_END = ";end;"
        const val GOOGLE_PLAY_STORE_PREFIX = "market://details?id="
        const val BDL_KEY_CLRHISTORY = "clrHistory"
        const val KEY_GLOBAL_WEB_VIEW_RELOAD = 1 + 9 shl 24
        const val KEY_GLOBAL_WEB_VIEW_RELOAD_URL = 1 + 11 shl 24
        const val KEY_LOAD_COMPLETE = 1 + 13 shl 24
        const val KEY_LOAD_ERROR = 1 + 15 shl 24
        const val WEB_VIEW_RELOAD_MAX_COUNT = 3

        const val APPBRIDGE_PLUGIN_ID = 0
        const val APPBRIDGE_PLUGIN_METHOD = 1
        const val APPBRIDGE_PLUGIN_PARAMS = 2
        const val APPBRIDGE_SPLIT = "?\$"


    }

    private var mOnPageStarted: OnPageStarted ?= null
    private var mOnPageFinished: OnPageFinished ?= null

    fun setOnPageFinishedListener(listener: OnPageFinished?) {
        mOnPageFinished = listener
    }

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {

        Log.d("jmp_web_plugin","shouldOverrideUrlLoading url : $url")

        val smallAndroidScriptScheme = "androidland:"
        val bigAndroidScriptScheme = "ANDROIDLAND:"
        val androidScriptScheme : String = when {
            url.contains(smallAndroidScriptScheme) -> {
                smallAndroidScriptScheme
            }
            url.contains(bigAndroidScriptScheme) -> {
                bigAndroidScriptScheme
            }
            else -> {
                ""
            }
        }

        if (url.isNotEmpty() && androidScriptScheme.isNotEmpty() &&url.startsWith(androidScriptScheme)) {

            Log.d("jmp_web_plugin","shouldOverrideUrlLoading plugin url : $url")

            val params = URLDecoder.decode(url, "UTF-8").replace(androidScriptScheme, "").split(APPBRIDGE_SPLIT)
            var jsonObject: JSONObject = JSONObject()

            try{
                jsonObject = when(params.size){
                    3 -> {
                        JSONObject(params[APPBRIDGE_PLUGIN_PARAMS])
                    }
                    else -> JSONObject()
                }
            }catch (e : JSONException){}

            if(params.size >= 2){

                android.os.Handler(Looper.getMainLooper()).post {
                    view.getHybridPlugInManager().executePlugIn(
                        params[APPBRIDGE_PLUGIN_ID]
                        , params[APPBRIDGE_PLUGIN_METHOD]
                        , jsonObject
                    )
                }
            }

            return true
        }

        if (url.isNotEmpty() && url.startsWith("jscall://callObjectiveC")) {
            return true
        }



        //웹뷰 내 표준창에서 외부앱(통신사 인증앱)을 호출하려면 intent:// URI를 별도로 처리해줘야 합니다.
        //다음 소스를 적용 해주세요.
        if (url.startsWith("intent://")) {
            var intent: Intent? = null
            try {
                intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                intent?.let { view.context.startActivity(it) }
            } catch (e: URISyntaxException) {
                //URI 문법 오류 시 처리 구간
            } catch (e: ActivityNotFoundException) {
                val packageName = intent!!.getPackage()
                if (packageName != "") {
                    // 앱이 설치되어 있지 않을 경우 구글마켓 이동
                    view.context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=$packageName")
                        )
                    )
                }
            }
            //return  값을 반드시 true로 해야 합니다.
            return true
        } else if (url.startsWith("https://play.google.com/store/apps/details?id=") || url.startsWith(
                "market://details?id="
            )
        ) {
            //표준창 내 앱설치하기 버튼 클릭 시 PlayStore 앱으로 연결하기 위한 로직
            val uri = Uri.parse(url)
            val packageName = uri.getQueryParameter("id")
            if (packageName != null && packageName != "") {
                // 구글마켓 이동
                view.context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")
                    )
                )
            }
            //return  값을 반드시 true로 해야 합니다.
            return true
        }

        if (url.startsWith(INTENT_PROTOCOL_START)) {
            val customUrlStartIndex = INTENT_PROTOCOL_START.length
            val customUrlEndIndex = url.indexOf(INTENT_PROTOCOL_INTENT)
            if (customUrlEndIndex < 0) {
                return false
            } else {
                val customUrl = url.substring(customUrlStartIndex, customUrlEndIndex)
                try {
                    view.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(customUrl)))
                } catch (e: ActivityNotFoundException) {
                    val packageStartIndex = customUrlEndIndex + INTENT_PROTOCOL_INTENT.length
                    val packageEndIndex = url.indexOf(INTENT_PROTOCOL_END)

                    val packageName =
                        url.substring(packageStartIndex, if (packageEndIndex < 0) url.length else packageEndIndex)
                    view.context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(GOOGLE_PLAY_STORE_PREFIX + packageName)
                        )
                    )
                }

                return true
            }
        } else {
            if(url.contains("FILE_NAME")) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                view.context.startActivity(intent)
                return true
            }
            return false
        }
    }

    /**
     * SSL 이슈를 사용자에게 공지 하지 않으면, 마켓 배포시 리젝사유.
     */
    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {

        Log.d("jmp_web","onReceivedSslError error : $error  , view.url : ${view.url}")

        if(error.primaryError == SSL_IDMISMATCH){
            return
        }

        try{
            val builder = AlertDialog.Builder(view.context)
            builder.setMessage(R.string.notification_error_ssl_cert_invalid)
            builder.setPositiveButton(view.context.getString(R.string.received_ssl_error_continue)) { _, _ -> handler.proceed() }
            builder.setNegativeButton(view.context.getString(R.string.received_ssl_error_cancel)) { _, _ -> handler.cancel() }
            val dialog = builder.create()
            dialog.show()
        }catch(e: WindowManager.BadTokenException){
            e.printStackTrace()
        }
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        mOnPageStarted?.onPageStarted(view, url)

        view?.setTag(KEY_LOAD_COMPLETE, false)
        view?.setTag(KEY_LOAD_ERROR, false)
        Log.d("jmp_web","onPageStarted url : $url")
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        mOnPageFinished?.onPageFinished(view, url)
        Log.d("jmp_web","onPageFinished url : $url")
        view?.setBackgroundColor(Color.TRANSPARENT)
        view?.visibility = View.VISIBLE

        if(url?.contains("mini")!!){
            Log.d("jmp_web","onPageFinished")
        }

        view?.setTag(KEY_LOAD_COMPLETE, true)

        view?.tag?.let {
            val tag = if(it is Bundle) it as Bundle else null

            if (tag != null && tag.getBoolean(BDL_KEY_CLRHISTORY)) {
                view.clearHistory()
                view.tag = null
            }
        }
    }

    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {

        Log.d("jmp_web","onReceivedError : ${view?.url}")
        Log.e("jmp_web","onReceivedError : $error")

        view?.setTag(KEY_LOAD_ERROR, true)
        error?.errorCode.let {

            Log.e("jmp_web","onReceivedError code : $it")
            when (it) {
                ERROR_AUTHENTICATION,
                ERROR_BAD_URL,
                ERROR_FAILED_SSL_HANDSHAKE,
                ERROR_FILE,
                ERROR_FILE_NOT_FOUND,
                ERROR_IO,
                ERROR_PROXY_AUTHENTICATION,
                ERROR_REDIRECT_LOOP,
                ERROR_TIMEOUT,
                ERROR_TOO_MANY_REQUESTS -> {

                    view?.let {
                        val reloadCount = view.getTag(KEY_GLOBAL_WEB_VIEW_RELOAD)
                        reloadCount?.let {count ->
                            val countInt = (count as Int)
                            if(countInt < WEB_VIEW_RELOAD_MAX_COUNT ){
                                actionReloadWebView( countInt, it )
                            }else{

                                view.setOnTouchListener { v, event ->
                                    val reloadUrl = view.getTag(KEY_GLOBAL_WEB_VIEW_RELOAD_URL) as String
                                    view.loadUrl(reloadUrl)

                                    view.setOnTouchListener(null)
                                    true
                                }
                                Log.d("jmp_web","view.url : ${view.url}")
                                view.loadDataWithBaseURL(

                                    null,

                                    "<div style=\"width:100%;height:100%;display:flex;align-items:center;justify-content: center;\">\n" +
                                            "        <img src=\"file:///android_res/drawable/reload.png\">\n" +
                                            "      </div>",

                                    "text/html",

                                    "UTF-8",

                                    null)
                            }
                        }?: actionReloadWebView(0, view)
                    }
                }
                else -> {}
            }
        }

        super.onReceivedError(view, request, error)
    }



    private fun actionReloadWebView(count: Int, webView: WebView){
        val plusCount = count + 1

        webView.reload()
        webView.setTag(KEY_GLOBAL_WEB_VIEW_RELOAD, plusCount)

        val url = webView?.url
        if(!url.isNullOrEmpty() && url != "about:blank"){
            webView.setTag(KEY_GLOBAL_WEB_VIEW_RELOAD_URL, url)
        }
    }

    private fun requestMessageCheckPermission(context: Context, url: String){
        try{
            val REQUEST_CODE_SEND_SMS = 200

            val activity = context as BaseActivity

            val permissionList: Array<String> = arrayOf(android.Manifest.permission.SEND_SMS)

            when (hasPermissions(activity, permissionList)) {
                true -> {
                    sendSMS(url)
                }
                else -> {
                    activity.requestPermissions(permissionList,REQUEST_CODE_SEND_SMS)
                    activity.addPermissionCallback(REQUEST_CODE_SEND_SMS, object :
                        BaseActivity.OnPermissionCallback {
                        override fun requestResult(
                            requestCode: Int,
                            permissions: Array<out String>,
                            grantResults: IntArray
                        ) {
                            when (requestCode) {
                                REQUEST_CODE_SEND_SMS -> {
                                    if ((grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                                        sendSMS(url)
                                    }
                                    return
                                }
                                else -> {}
                            }
                        }
                    })
                }
            }
        }catch(e: ArrayIndexOutOfBoundsException){
            e.printStackTrace()
        }
    }

    override fun onReceivedHttpAuthRequest(view : WebView,
                                           handler :HttpAuthHandler , host : String , realm : String ) {
        if(BuildConfig.DEBUG){
            handler.proceed("liivhub", "1234qwer")
        }
    }

    private fun sendSMS(context: Context, url : String){
        val data = url.split("sms:")
        val smsData = data[1].split("&body=")
        val phoneNumber = smsData[0]
        var message : String = ""

        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("sms:$phoneNumber"))

        if(smsData.size > 1){
            message = URLDecoder.decode(smsData[1], "UTF-8")
            intent.putExtra("sms_body", message)
        }
        context.startActivity(intent)
    }

    private fun sendSMS(url : String){
        val data = url.split("sms:")
        val smsData = data[1].split("&body=")
        val phoneNumber = smsData[0]
        var message : String = ""
        if(smsData.size > 1){
            message = URLDecoder.decode(smsData[1], "UTF-8")
        }

        val sms: SmsManager = SmsManager.getDefault()

        val partMessage = sms.divideMessage(message)
        when{
            (partMessage.size > 1) -> {
                sms.sendMultipartTextMessage(phoneNumber, null, partMessage, null, null)
            }
            else -> {
                sms.sendTextMessage(phoneNumber, null, message, null, null)
            }
        }
    }

    interface OnPageStarted {
        fun onPageStarted(view: WebView?, url: String?)
    }

    interface OnPageFinished {
        fun onPageFinished(view: WebView?, url: String?)
    }
}
