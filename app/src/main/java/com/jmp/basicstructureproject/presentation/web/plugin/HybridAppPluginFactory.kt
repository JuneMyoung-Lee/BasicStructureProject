package com.jmp.basicstructureproject.presentation.web.plugin

import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.jmp.basicstructureproject.presentation.web.HybridWebViewConstants
import com.jmp.basicstructureproject.presentation.web.SetHybridPlugIn
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.Field
import java.net.URLEncoder
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 *
 * Hybrid 플러그인을 제공하기위한 관리 클래스
 * 플러그인 모듈을 실행 시키기 위한 클래스
 */
class HybridAppPluginFactory(private val webView: WebView) {

    private var plugins: HashMap<String, KClass<out Any>>? = null
    private var hybridWebViewListeners: MutableList<HybridWebViewPluginListener> = mutableListOf()

    @JavascriptInterface
    fun executePlugIn(pluginID: String, method: String, jsonObj: JSONObject) {

        Log.d("jmp_web_plugin","executePlugIn pluginID : $pluginID   , method : $method     , jsonObj : $jsonObj")

        for(listener in hybridWebViewListeners){
            val eventStop = listener.execute(pluginID, method, jsonObj)
            if(eventStop){
                return
            }
        }

        val plugIn: Any? = getPlugIn(pluginID)

        //해당하는 플러그인이 존재하지 않습니다.
        if (plugIn == null) {
            callBack(false, pluginID, method, "")
            return
        }

        //플러그인 메소드가 존재하지 않습니다.
        if (method.isEmpty()) {
            callBack(false, pluginID, method, "")
            return
        }

        val methods = plugIn.javaClass.methods
        for (m in methods) {
            if (m.name == method) { // 메소드를 찾았다.
                try {
                    when (m.returnType) {
                        String::class.java -> {
                            val retData = m.invoke(plugIn, webView, jsonObj) as String
                            callBack(true, pluginID, method, retData)
                        }
                        JSONObject::class.java -> {
                            val retData = m.invoke(plugIn, webView, jsonObj) as JSONObject
                            callBack(true, pluginID, method, retData.toString())
                        }
                        JSONArray::class.java -> {
                            val retData = m.invoke(plugIn, webView, jsonObj) as JSONArray
                            callBack(true, pluginID, method, retData.toString())
                        }
                        else -> {
                            m.invoke(plugIn, webView, jsonObj)

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    callBack(false, pluginID, method, "")
                }
                break
            }
        }
    }

    @JavascriptInterface
    fun callBack( isOK: Boolean, plugInID: String?, method: String?, data: String ) {
        val executeScript = String.format(
            HybridWebViewConstants.CALL_JS,
            isOK,
            plugInID,
            method,
            URLEncoder.encode(notNullString(data), HybridWebViewConstants.ENCODE_ENC)
        )

        webView.loadUrl(executeScript)
    }

    /**
     * cmd에 해당하는 플러그인을 찾는다.
     * @param cmd
     * @return
     */
    private fun getPlugIn(cmd: String): HybridAppPlugin? {
        //플러그인이 존재하지 않습니다.
        if (plugins == null) {
            return null
        }

        //플러그인이 존재하지 않으면 리턴 null
        val cls = plugins!![cmd] ?: return null

        var plugin: HybridAppPlugin? = null
        try {
            plugin = cls.createInstance() as HybridAppPlugin
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        return plugin
    }

    fun addHybridListener(listenerHybridWeb: HybridWebViewPluginListener){
        hybridWebViewListeners.add(listenerHybridWeb)
    }

    fun removeHybridListener(listenerHybridWeb: HybridWebViewPluginListener){
        hybridWebViewListeners.remove(listenerHybridWeb)
    }
    /**
     * 해당 Config 클래스에서 플러그인을 읽어 들인다.
     * @param configClass
     */
    fun addHybridPlugIn(configClass: Class<*>) {

        val fields = configClass.declaredFields
        if (plugins == null) {
            plugins = HashMap()
        }

        for (f in fields) {
            if (f.isAnnotationPresent(SetHybridPlugIn::class.java)) {
                val ann = f.getAnnotation(SetHybridPlugIn::class.java)
                val pluginId: String = ann.pluginId

                //필드 값을 가져온다
                val testClass = getFieldData(f, configClass)
                testClass?.let {
                    plugins!![pluginId] = it
                }
            }
        }
    }

    /**
     * 필드이 값을 가져온다.
     * @param f
     * @param obj
     * @return
     */
    private fun getFieldData(f: Field, obj: Any): KClass<out Any>? {
        try {
            f.declaringClass
            return f.get(obj)::class
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 문자를 null로 리턴되지 않도록 한다.
     *
     * @param str
     * @return
     */
    private fun notNullString(str: String?): String {
        return str ?: ""
    }
}
