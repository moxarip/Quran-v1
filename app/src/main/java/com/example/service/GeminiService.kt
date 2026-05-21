package com.example.service

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AISuggestion(
    val backgroundId: String,
    val fontName: String,
    val ornamentStyle: String,
    val transitionEffect: String,
    val reasonArabic: String,
    val reasonEnglish: String
)

object GeminiService {
    private const val TAG = "GeminiService"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun fetchVisualSuggestions(surahName: String, englishMeaning: String, verseSample: String): AISuggestion = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "Gemini API Key is placeholder or empty.")
            return@withContext getFallbackSuggestion(surahName)
        }

        val prompt = """
            You are an expert Islamic art consultant and video designer. 
            Analyze the following Surah details to recommend the perfect visual backdrop, typography, framing ornaments, and transitions for a short-form video.
            
            Surah Details:
            - Name: $surahName
            - English Meaning: $englishMeaning
            - Sample verses: "$verseSample"
            
            Choose exactly from these available design options in the application:
            - Background ID (choose one from: "kaaba_gold", "mosque_starry", "golden_arabesque", "desert_sunset", "emerald_aura")
            - Font Name (choose one from: "Uthmani", "Amiri", "Kufic")
            - Ornament Style (choose one from: "mihrab", "corners", "border", "minimal")
            - Transition Effect (choose one from: "fade", "rise", "zoom", "blur")
            
            Respond strictly with a JSON object. Do not include markdown wraps or anything other than the raw JSON body:
            {
              "backgroundId": "...",
              "fontName": "...",
              "ornamentStyle": "...",
              "transitionEffect": "...",
              "reasonArabic": "مثال: هذه السورة تتناول آيات النور والهداية ولذلك نقترح خلفية محراب الليل الخضراء الهادئة...",
              "reasonEnglish": "Example: This Surah speaks of spiritual clarity and monotheism, so we recommend the Kaaba Gold with traditional Uthmani script..."
            }
        """.trimIndent()

        // Build request body according to API standards
        val jsonRequest = JSONObject().apply {
            put("contents", org.json.JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", org.json.JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.3)
            })
        }

        val requestBody = jsonRequest.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val url = "$BASE_URL?key=$apiKey"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .header("Content-Type", "application/json")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: ""
                Log.d(TAG, "Response: $body")
                if (!response.isSuccessful) {
                    Log.e(TAG, "HTTP Error: ${response.code} - $body")
                    return@withContext getFallbackSuggestion(surahName)
                }

                val jsonResponse = JSONObject(body)
                val candidates = jsonResponse.getJSONArray("candidates")
                val textResponse = candidates.getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")

                val parsed = JSONObject(textResponse)
                AISuggestion(
                    backgroundId = parsed.optString("backgroundId", "mosque_starry"),
                    fontName = parsed.optString("fontName", "Uthmani"),
                    ornamentStyle = parsed.optString("ornamentStyle", "mihrab"),
                    transitionEffect = parsed.optString("transitionEffect", "zoom"),
                    reasonArabic = parsed.optString("reasonArabic", "تم اختيار هذه التنسيقات بناءً على روحانية السورة الكريمة."),
                    reasonEnglish = parsed.optString("reasonEnglish", "Selected matching spiritual visual accents aligned with the serene verse content.")
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error invoking Gemini API", e)
            getFallbackSuggestion(surahName)
        }
    }

    private fun getFallbackSuggestion(surahName: String): AISuggestion {
        // Safe, beautiful localized values in case of API issues
        return when {
            surahName.contains("الفاتحة") -> AISuggestion(
                backgroundId = "mosque_starry",
                fontName = "Uthmani",
                ornamentStyle = "mihrab",
                transitionEffect = "zoom",
                reasonArabic = "سورة الفاتحة هي الشافية الكافية وبوابة القرآن؛ نقترح لها زينة المحراب الأندلسي مع حركة زووم هادئة لتحفيز الخشوع.",
                reasonEnglish = "Al-Fatihah is the foundational opening of the Quran. We paired it with the Emerald Mosque Mihrab and elegant Uthmani script to create a serene prayer sanctuary vibe."
            )
            surahName.contains("الكوثر") -> AISuggestion(
                backgroundId = "emerald_aura",
                fontName = "Amiri",
                ornamentStyle = "corners",
                transitionEffect = "rise",
                reasonArabic = "الكوثر رمز الفضل والنعيم والوفرة الربانية؛ تليق بها درجات الفجر الصادق الخضراء مع خط أميري فخم ونقوش زاوية أنيقة.",
                reasonEnglish = "Al-Kawthar highlights celestial rivers and eternal abundance. Pair it with the golden-rimmed Mystic Emerald Aura background and rising lines for an ethereal transition."
            )
            else -> AISuggestion(
                backgroundId = "kaaba_gold",
                fontName = "Uthmani",
                ornamentStyle = "border",
                transitionEffect = "fade",
                reasonArabic = "آيات التوحيد وجلال الخالق؛ تلاؤمها أستار الكعبة المحاطة بنور ذهبي مهيب مع إطارات هندسية فخمة وخط المصحف التقليدي.",
                reasonEnglish = "These verses express themes of divine sovereignty. We recommend the Kaaba Gold background combined with traditional Mushaf text and soft crossfades."
            )
        }
    }
}
