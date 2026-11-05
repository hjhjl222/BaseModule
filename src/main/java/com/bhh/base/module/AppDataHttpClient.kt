package com.bhh.base.module

import com.bhh.tools.http.HttpClient
import okhttp3.Request

class AppDataHttpClient: HttpClient(60,60,60) {

    override fun headers(request: Request): java.util.HashMap<String, String> {
        return HashMap<String, String>().apply {
            put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0YXJnZXQiOiJnb2xkcnVzaCIsInVzZXIiOiJpbm5lcl9uZXcifQ.ksomJSMMy2fODBZ_oHSPYL5lNL9_GmToRpDLabKvVg4")
            put("Content-Type", "application/json")
        }
    }

    fun post(url: String, data: String) : String? {
        return startPostRequest<String,String>(url, data)
    }
}