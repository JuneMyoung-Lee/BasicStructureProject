package com.jmp.basicstructureproject.presentation.web.plugin

import com.jmp.basicstructureproject.presentation.web.HybridWebViewConstants
import org.json.JSONException
import org.json.JSONObject
import java.net.URLEncoder

open class AppPlugin : HybridAppPlugin {

    enum class AppPluginKey(val key: String){
        CALLBACK_KEY_CODE("resultCode"), CALLBACK_KEY_MESSAGE("resultMsg")
    }
    enum class AppPluginCode(val code: String, val mesage: String){
        DEFAULT_SUCCESS("0000", "정상"), DEFAULT_FAIL("9999", "오류가 발생하였습니다")
    }

    /**
     * 성공코드나 성공메세지가 없을경우 Default값을 설정 합니다.
     * @param json
     * @return
     */
    private fun getSuccessJSON(value: String): JSONObject {

        val json = JSONObject()

        try {
            json.put("value", value)

            if (json.isNull(AppPluginKey.CALLBACK_KEY_CODE.key)) {
                json.put(AppPluginKey.CALLBACK_KEY_CODE.key, AppPluginCode.DEFAULT_SUCCESS.code)
            }
            if (json.isNull(AppPluginKey.CALLBACK_KEY_MESSAGE.key)) {
                json.put(AppPluginKey.CALLBACK_KEY_MESSAGE.key, AppPluginCode.DEFAULT_SUCCESS.mesage)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return json
    }
    /**
     * 성공코드나 성공메세지가 없을경우 Default값을 설정 합니다.
     * @param json
     * @return
     */
    private fun getSuccessJSON(json: JSONObject?): JSONObject {
        var json = json

        if (json == null) {
            json = JSONObject()
        }

        try {

            if (json.isNull(AppPluginKey.CALLBACK_KEY_CODE.key)) {
                json.put(AppPluginKey.CALLBACK_KEY_CODE.key, AppPluginCode.DEFAULT_SUCCESS.code)
            }
            if (json.isNull(AppPluginKey.CALLBACK_KEY_MESSAGE.key)) {
                json.put(AppPluginKey.CALLBACK_KEY_MESSAGE.key, AppPluginCode.DEFAULT_SUCCESS.mesage)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return json
    }

    /**
     * 실패코드나 실패메세지가 없을경우 Default값을 설정 합니다.
     * @param json
     * @return
     */
    private fun getFailJSON(json: JSONObject?): JSONObject {
        var json = json
        if (json == null) {
            json = JSONObject()
        }

        try {
            if (json.isNull(AppPluginKey.CALLBACK_KEY_CODE.key)) {
                json.put(AppPluginKey.CALLBACK_KEY_CODE.key, AppPluginCode.DEFAULT_FAIL.code)
            }
            if (json.isNull(AppPluginKey.CALLBACK_KEY_MESSAGE.key)) {
                json.put(AppPluginKey.CALLBACK_KEY_MESSAGE.key, AppPluginCode.DEFAULT_FAIL.mesage)
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return json
    }

    fun getWebSuccessCallbackString(interfaceMethodName: String, data: String): String {
        val data = getSuccessJSON(data)

        return String.format(
            HybridWebViewConstants.CALL_JS, true, javaClass.simpleName, interfaceMethodName,
            URLEncoder.encode(data.toString(), HybridWebViewConstants.ENCODE_ENC)
        )
    }
    fun getWebSuccessCallbackString(interfaceMethodName: String, data: JSONObject?): String {
        var data = data
        data = getSuccessJSON(data)

        return String.format(
            HybridWebViewConstants.CALL_JS, true, javaClass.simpleName, interfaceMethodName,
            URLEncoder.encode(data.toString(), HybridWebViewConstants.ENCODE_ENC)
        )
    }
    fun getWebSuccessCallbackString(pluginId : String, interfaceMethodName: String, data: JSONObject?): String {
        var data = data
        data = getSuccessJSON(data)

        return String.format(
            HybridWebViewConstants.CALL_JS, true, pluginId, interfaceMethodName,
            URLEncoder.encode(data.toString(), HybridWebViewConstants.ENCODE_ENC)
        )
    }

    fun getWebFailCallbackString(interfaceMethodName: String, data: JSONObject): String {
        var data = data
        data = getFailJSON(data)

        return String.format(
            HybridWebViewConstants.CALL_JS, false, javaClass.simpleName, interfaceMethodName,
            URLEncoder.encode(data.toString(), HybridWebViewConstants.ENCODE_ENC)
        )
    }
}