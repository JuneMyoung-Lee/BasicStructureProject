package com.jmp.basicstructureproject.presentation.web

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.jmp.basicstructureproject.BaseActivity
import com.jmp.basicstructureproject.R
import com.jmp.basicstructureproject.databinding.ActivityWebBinding
import com.jmp.basicstructureproject.presentation.extension.rxClickListener
import com.jmp.basicstructureproject.presentation.web.plugin.HybridWebViewPluginListener
import org.json.JSONObject


class HybridWebViewActivity : BaseActivity(){

    companion object {
        const val KEY_URL = "KEY_URL"
        const val KEY_CATEGORY = "KEY_CATEGORY"
        const val KEY_IS_FULL_SCREEN = "KEY_IS_FULL_SCREEN"
        const val KEY_BACKGROUND_WHITE = "KEY_BACKGROUND_WHITE"
        const val KEY_STATUS_BAR_WHITE = "KEY_STATUS_BAR_WHITE"

        const val KEY_BACK_PRESSED_TAG = 1 + 6 shl 24
    }

    private var isActivityLoaded = false
    private var lastBackPressedTime = 0L

    private lateinit var binding : ActivityWebBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        isActivityLoaded = false
        super.onCreate(savedInstanceState)

        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.run {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            statusBarColor = 0x00000000  // transparent
        }
        if(intent.getBooleanExtra(KEY_STATUS_BAR_WHITE, false)) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        }

        initWebView()
    }

    private val onPageFinishedListener = object: HybridWebViewClient.OnPageFinished {
        override fun onPageFinished(view: WebView?, url: String?) {

        }
    }

    private val hybridWebViewPluginListener = object : HybridWebViewPluginListener {
        override fun execute(
            pluginID: String,
            method: String,
            jsonObj: JSONObject
        ): Boolean {

            return when(pluginID){
                else -> false
            }
        }
    }

    /**
     * 이전화면에서 전달해준 URL를 웹뷰로 띄운다.
     */
    private fun initWebView(){
        with(binding){

            val webViewUrl = intent.getStringExtra(KEY_URL)
            val category = intent.getStringExtra(KEY_CATEGORY)

            webViewUrl?.let {
                webView.loadWithHybridInitialize(url=it, actionCmd=null, activityContext = this@HybridWebViewActivity, onPageFinished = onPageFinishedListener)
            }?: finish()

            with(webView) {
                addHybridPlugInListener(this@HybridWebViewActivity, hybridWebViewPluginListener)
            }

            fourWebBack.rxClickListener {
                if(webView.canGoBack()) {
                    webView.goBack()
                } else {
                    super.onBackPressed()
                }
            }
            fourWebForward.rxClickListener {
                if(webView.canGoForward()) {
                    webView.goForward()
                }
            }
            fourWebRefresh.rxClickListener {
                webView.reload()
            }
            fourWebClose.rxClickListener {
                super.onBackPressed()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.webView.onResume()
        isActivityLoaded = true
    }

    override fun onPause() {
        super.onPause()
        binding.webView.onPause()
    }

    override fun onBackPressed() {
        //웹뷰에 히스토리가 남아 있을경우, 웹뷰 히스토리 백을 수행 합니다.
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else{
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        binding.webView.setTag(KEY_BACK_PRESSED_TAG ,null)
        binding.webView.clearCache(true)

        super.onDestroy()
    }
}
