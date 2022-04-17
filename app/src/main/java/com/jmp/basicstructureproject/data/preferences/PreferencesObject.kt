package com.jmp.basicstructureproject.data.preferences

import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesObject @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    /**
     * Boolean 타입 데이터 저장
     * @param key
     * @param value 밸류
     */
    fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    /**
     * Float 타입 데이터 저장
     * @param key 키
     * @param value 밸류
     */
    fun putFloat(key: String, value: Float) {
        sharedPreferences.edit().putFloat(key, value).apply()
    }

    /**
     * Int 타입 데이터 저장
     * @param key 키
     * @param value 밸류
     */
    fun putInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    /**
     * Long 타입 데이터 저장
     * @param key 키
     * @param value 밸류
     */
    fun putLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }

    /**
     * Double 타입 데이터 저장
     * @param key 키
     * @param value 밸류
     */
    fun putDouble(key: String, value: Double) {
        sharedPreferences.edit().putString(key, value.toString()).apply()
    }

    /**
     * String 타입 데이터 저장
     * @param key 키
     * @param value 밸류
     */
    fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    /**
     * Set<String> 타입 데이터 저장
     * @param key 키
     * @param value 밸류
     */
    fun putStringSet(key: String, value: Set<String>) {
        sharedPreferences.edit().putStringSet(key, value).apply()
    }

    /**
     * ArrayList 타입 데이터 저장
     * @param key 키
     * @param values 밸류
     * @param <E> 데이터 타입
     */
    fun <E> putArrayList(key: String, values: ArrayList<E>, type: Type) {

//        val type : Type? = object : TypeToken<ArrayList<E>>(){}.type
        var str = Gson().toJson(values, type)

        if(values.isNotEmpty()) {
            sharedPreferences.edit().putString(key, str.toString()).apply()

        } else {
            sharedPreferences.edit().putString(key, null).apply()
        }
    }

    /**
     * Object 타입 데이터 저장
     * @param key 키
     * @param value 밸류 T 데이터 타입
     */
    fun <T> putObject(key: String, value: T) {
        val json = Gson().toJson(value)
        sharedPreferences.edit().putString(key, json).apply()
    }

    /**
     * Boolean 타입 데이터 로드. 키에 해당하는 밸류가 없는 경우 Default 밸류 리턴
     * @param key 키
     * @param defValue Default 밸류
     * @return 밸류
     */
    fun getBoolean(key: String, defValue: Boolean) : Boolean {
        return sharedPreferences.getBoolean(key, defValue)
    }

    /**
     * Boolean 타입 데이터 로드. 키에 해당하는 밸류가 없는 경우 false 리턴
     * @param key 키
     * @return 밸류
     */
    fun getBoolean(key: String) : Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    /**
     * Float 타입 데이터 로드. 키에 해당하는 밸류가 없는 경우 Default 밸류 리턴
     * @param key 키
     * @param defValue Default 밸류
     * @return 밸류
     */
    fun getFloat(key: String, defValue: Float) : Float {
        return sharedPreferences.getFloat(key, defValue)
    }

    /**
     * Float 타입 데이터 로드. 키에 해당하는 밸류가 없는 경우 0F 리턴
     * @param key 키
     * @return 밸류
     */
    fun getFloat(key: String) : Float {
        return sharedPreferences.getFloat(key, 0F)
    }

    /**
     * Int 타입 데이터 로드. 키에 해당하는 밸류가 없는 경우 Default 밸류 리턴
     * @param key 키
     * @param defValue Default 밸류
     * @return 밸류
     */
    fun getInt(key: String, defValue: Int) : Int {
        return sharedPreferences.getInt(key, defValue)
    }

    /**
     * Int 타입 데이터 로드. 키에 해당하는 밸류가 없는 경우 0 리턴
     * @param key 키
     * @return 밸류
     */
    fun getInt(key: String) : Int {
        return sharedPreferences.getInt(key, 0)
    }

    /**
     * Long 타입 데이터 로드. 키에 해당하는 밸류가 없는 경우 Default 밸류 리턴
     * @param key 키
     * @param defValue Default 밸류
     * @return 밸류
     */
    fun getLong(key: String, defValue: Long) : Long {
        return sharedPreferences.getLong(key, defValue)
    }

    /**
     * Long 타입 데이터 로드. 키에 해당하는 밸류가 없는 경우 0L 리턴
     * @param key 키
     * @return 밸류
     */
    fun getLong(key: String) : Long {
        return sharedPreferences.getLong(key, 0L)
    }

    /**
     * Double 타입 데이터 저장
     * @param key 키
     * @param value 밸류
     */
    fun getDouble(key: String, defValue: Double) : Double {
        return sharedPreferences.getString(key, defValue.toString())?.toDouble() ?: defValue
    }

    /**
     * Double 타입 데이터 저장
     * @param key 키
     * @param value 밸류
     */
    fun getDouble(key: String) : Double {
        return sharedPreferences.getString(key, 0.0.toString())?.toDouble() ?: 0.0
    }

    /**
     * String 타입 데이터 로드. 키에 해당하는 밸류가 없는 경우 Default 밸류 리턴
     * @param key 키
     * @param defValue Default 밸류
     * @return 밸류
     */
    fun getString(key: String, defValue: String) : String {
        return sharedPreferences.getString(key, defValue) ?: defValue
    }

    /**
     * String 타입 데이터 로드. 키에 해당하는 밸류가 없는 경우 "" 리턴
     * @param key 키
     * @return 밸류
     */
    fun getString(key: String) : String {
        return sharedPreferences.getString(key, "") ?: ""
    }

    /**
     * Set<String> 타입 데이터 로드. 키에 해당하는 밸류가 없는 경우 Default 밸류 리턴
     * @param key 키
     * @param defValue Default 밸류
     * @return 밸류
     */
    fun getStringSet(key: String, defValue: Set<String>) : Set<String> {
        return sharedPreferences.getStringSet(key, defValue) ?: defValue
    }

    /**
     * Set<String> 타입 데이터 로드. 키에 해당하는 밸류가 없는 경우 null 리턴
     * @param key 키
     * @return 밸류
     */
    fun getStringSet(key: String) : Set<String>? {
        return sharedPreferences.getStringSet(key, null)
    }

    /**
     * ArrayList 타입 데이터 로드. 키에 해당하는 밸류가 없는 경우 Default 밸류 리턴
     * @param key 키
     * @param defValue Default 밸류
     * @param <E> 데이터 타입
     * @return 밸류
     */
    fun <E> getArrayList(key: String, defValue: ArrayList<E>, type: Type) : ArrayList<E> {
        val json = sharedPreferences.getString(key, null)
        var arrayList = ArrayList<E>()

        if(json != null) {

            arrayList = Gson().fromJson(json, type)

            return arrayList

        } else {
            return defValue
        }

    }

    /**
     * ArrayList 타입 데이터 로드. 키에 해당하는 밸류가 없는 경우 null 리턴
     * @param key 키
     * @param <E> 데이터 타입
     * @return 밸류
     */
    fun <E> getArrayList(key: String) : ArrayList<E>? {
        val json = sharedPreferences.getString(key, null)
        val arrayList = ArrayList<E>()

        if(json != null) {
            val jsonArray = JSONArray(json)

            for(i in 0..jsonArray.length()) {
                arrayList.add(jsonArray.opt(i) as E)
            }

            return arrayList

        } else {
            return null
        }
    }

    /**
     * Object 타입 데이터 로드. 키에 해당하는 밸류가 없는 경우 null 리턴
     * @param key 키
     * @param Class<T> 데이터 타입 class
     * @return 밸류
     */
    fun <T> getObject(key: String, obj: Class<T>): T? {
        val json = sharedPreferences.getString(key, null)

        return if(json != null) {
            Gson().fromJson(json, obj)
        } else {
            null
        }
    }

    /**
     * SharedPreferences에 저장된 데이터 전체 삭제
     */
    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    /**
     * 키에 해당하는 데이터 삭제
     * @param key 키
     */
    fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    /**
     * 키에 해당하는 데이터가 존재하는지의 여부 리턴
     * @param key 키
     * @return 존재여부 (true: 존재, false: 미존재)
     */
    fun contains(key: String) : Boolean {
        return sharedPreferences.contains(key)
    }

    /**
     * SharedPreferences에 저장된 데이터 전체 리턴
     * @return 데이터 전체 (Map 형식)
     */
    fun getAll() : Map<String, *> {
        return sharedPreferences.all
    }
}