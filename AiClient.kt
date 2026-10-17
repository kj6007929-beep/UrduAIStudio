package com.example.urdiaistudio

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object AiClient {
 private val http = OkHttpClient.Builder().connectTimeout(25,TimeUnit.SECONDS).readTimeout(180,TimeUnit.SECONDS).build()
 suspend fun generate(base: String, key: String, model: String, prompt: String, language: String, minutes: Int): String = withContext(Dispatchers.IO) {
  val system = "You are a screenplay planner. Write in $language. Create a title, logline, consistent cast, $minutes-minute scene-by-scene screenplay, visual prompts, dialogue, voice direction, music and sound effects. Do not claim media files have been rendered."
  val body = JSONObject().put("model",model).put("messages",JSONArray()
   .put(JSONObject().put("role","system").put("content",system))
   .put(JSONObject().put("role","user").put("content",prompt))).toString().toRequestBody("application/json; charset=utf-8".toMediaType())
  val request = Request.Builder().url(base.trimEnd('/')+"/chat/completions").post(body)
   .header("Authorization","Bearer $key").header("Content-Type","application/json").build()
  http.newCall(request).execute().use { r ->
   val text = r.body?.string().orEmpty()
   if (!r.isSuccessful) throw IllegalStateException("HTTP ${r.code}: ${text.take(300)}")
   JSONObject(text).getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content")
  }
 }
}
