package com.example.model

import androidx.compose.ui.graphics.Color

data class QuranWord(
    val arabic: String,
    val startMs: Long,
    val endMs: Long
)

data class QuranVerse(
    val number: Int,
    val textUthmani: String,
    val textModern: String,
    val translation: String,
    val words: List<QuranWord>,
    val startMs: Long,
    val endMs: Long
)

data class Surah(
    val id: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val englishMeaning: String,
    val verses: List<QuranVerse>,
    val durationMs: Long,
    val audioUrl: String,
    val backgroundSuggestIdea: String
)

enum class AspectRatioType(val ratio: Float, val label: String, val desc: String) {
    PORTRAIT_9_16(9f / 16f, "9:16", "Reels, TikTok, stories"),
    LANDSCAPE_16_9(16f / 9f, "16:9", "YouTube, Facebook feed"),
    SQUARE_1_1(1f, "1:1", "Instagram post, feed")
}

enum class ArabicFontType(val fontName: String, val displayName: String, val description: String) {
    UTHMANI("Uthmani", "مصحف عثماني", "Traditional Mushaf style scripture"),
    AMIRI("Amiri", "الخط الأميري", "Elegant classic Naskh calligraphy"),
    KUFIC("Kufic", "الخط الكوفي", "Modern geometric rounded script")
}

enum class BackgroundType(
    val id: String,
    val displayName: String,
    val arabicName: String,
    val description: String,
    val gradientColors: List<Color>,
    val decorType: String
) {
    KAABA_GOLD(
        "kaaba_gold",
        "Kaaba Golden Hour",
        "أستار الكعبة",
        "Muted dark gold with beautiful silk textures",
        listOf(Color(0xFF0F1412), Color(0xFF1E2824), Color(0xFF322307)),
        "geometric"
    ),
    MOSQUE_STARRY(
        "mosque_starry",
        "Emerald Mosque Night",
        "محراب الليل",
        "Starry serene night behind a majestic green minheur",
        listOf(Color(0xFF051D14), Color(0xFF0D3E2F), Color(0xFF1C5745)),
        "mihrab"
    ),
    GOLDEN_ARABESQUE(
        "golden_arabesque",
        "Royal Arabesque",
        "نقوش أندلسية",
        "Complex intricate Islamic patterns etched in glowing light",
        listOf(Color(0xFF12140D), Color(0xFF232B1B), Color(0xFF3F3512)),
        "arabesque"
    ),
    DESERT_SUNSET(
        "desert_sunset",
        "Spiritual Oasis",
        "غروب الروح",
        "Calming orange gradients reflecting desert silence",
        listOf(Color(0xFF1D0F0A), Color(0xFF3D1C0F), Color(0xFF1F2F1B)),
        "minimal"
    ),
    EMERALD_AURA(
        "emerald_aura",
        "Mystic Dawn",
        "الفجر الصادق",
        "Deep minimalist rich shades of deep spiritual emerald",
        listOf(Color(0xFF020E0A), Color(0xFF09291E), Color(0xFF124233)),
        "floral"
    )
}

enum class OrnamentStyle(val id: String, val displayName: String, val unicodeMarker: String) {
    MIHRAB("mihrab", "Mihrab Arch Frame", "۩"),
    ELEGANT_CORNERS("corners", "Islamic Corner Flourishes", "⚜"),
    ARABESQUE_BORDER("border", "Kufic Border Outline", "✿"),
    SIMPLE_MINIMAL("minimal", "Strict Minimal Accent", "•")
}

enum class TransitionEffect(val id: String, val displayName: String, val description: String) {
    SOFT_FADE("fade", "Whisper Fade", "Slow spiritual opacity fading"),
    RISE_UP("rise", "Divine Rise", "Gentle ascending verse sequence"),
    SLOW_ZOOM("zoom", "Cosmic Focus", "Cinematic scale zoom to text focus"),
    BLUR_REVEAL("blur", "Light Mist", "Dreamy soft blur resolution effect")
}

object QuranRepository {
    val surahs = listOf(
        Surah(
            id = 1,
            nameArabic = "سورة الفاتحة",
            nameEnglish = "Surah Al-Fatihah",
            englishMeaning = "The Opening",
            durationMs = 15000L,
            audioUrl = "https://server8.mp3quran.net/afs/001.mp3",
            backgroundSuggestIdea = "Represents light and guidance. Recommended: Emerald Mosque Night with warm golden Uthmani fonts and soft zoom effect to enhance deep focus.",
            verses = listOf(
                QuranVerse(
                    number = 1,
                    textUthmani = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                    textModern = "بسم الله الرحمن الرحيم",
                    translation = "In the name of Allah, the Entirely Merciful, the Especially Merciful.",
                    startMs = 0,
                    endMs = 3500,
                    words = listOf(
                        QuranWord("بِسْمِ", 0, 800),
                        QuranWord("اللَّهِ", 800, 1500),
                        QuranWord("الرَّحْمَٰنِ", 1500, 2400),
                        QuranWord("الرَّحِيمِ", 2400, 3500)
                    )
                ),
                QuranVerse(
                    number = 2,
                    textUthmani = "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
                    textModern = "الحمد لله رب العالمين",
                    translation = "[All] praise is [due] to Allah, Lord of the worlds -",
                    startMs = 3500,
                    endMs = 7000,
                    words = listOf(
                        QuranWord("الْحَمْدُ", 3500, 4200),
                        QuranWord("لِلَّهِ", 4200, 5000),
                        QuranWord("رَبِّ", 5000, 5800),
                        QuranWord("الْعَالَمِينَ", 5800, 7000)
                    )
                ),
                QuranVerse(
                    number = 3,
                    textUthmani = "الرَّحْمَٰنِ الرَّحِيمِ",
                    textModern = "الرحمن الرحيم",
                    translation = "The Entirely Merciful, the Especially Merciful,",
                    startMs = 7000,
                    endMs = 10000,
                    words = listOf(
                        QuranWord("الرَّحْمَٰنِ", 7000, 8500),
                        QuranWord("الرَّحِيمِ", 8500, 10000)
                    )
                ),
                QuranVerse(
                    number = 4,
                    textUthmani = "مَالِكِ يَوْمِ الدِّينِ",
                    textModern = "مالك يوم الدين",
                    translation = "Sovereign of the Day of Recompense.",
                    startMs = 10000,
                    endMs = 13500,
                    words = listOf(
                        QuranWord("مَالِكِ", 10000, 11000),
                        QuranWord("يَوْمِ", 11000, 12000),
                        QuranWord("الدِّينِ", 12000, 13500)
                    )
                )
            )
        ),
        Surah(
            id = 108,
            nameArabic = "سورة الكوثر",
            nameEnglish = "Surah Al-Kawthar",
            englishMeaning = "The Abundance",
            durationMs = 12000L,
            audioUrl = "https://server8.mp3quran.net/afs/108.mp3",
            backgroundSuggestIdea = "Depicts celestial abundance and rivers in Paradise. Recommended: Mystic Dawn with rich emerald shadows and classic white Amiri fonts with slow rising transition.",
            verses = listOf(
                QuranVerse(
                    number = 1,
                    textUthmani = "إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ",
                    textModern = "إنا أعطيناك الكوثر",
                    translation = "Indeed, We have granted you, [O Muhammad], al-Kawthar.",
                    startMs = 0,
                    endMs = 4000,
                    words = listOf(
                        QuranWord("إِنَّا", 0, 1000),
                        QuranWord("أَعْطَيْنَاكَ", 1000, 2500),
                        QuranWord("الْكَوْثَرَ", 2500, 4000)
                    )
                ),
                QuranVerse(
                    number = 2,
                    textUthmani = "فَصَلِّ لِرَبِّكَ وَانْحَرْ",
                    textModern = "فصل لربك وانحر",
                    translation = "So pray to your Lord and sacrifice [to Him alone].",
                    startMs = 4000,
                    endMs = 8000,
                    words = listOf(
                        QuranWord("فَصَلِّ", 4000, 5200),
                        QuranWord("لِرَبِّكَ", 5200, 6800),
                        QuranWord("وَانْحَرْ", 6800, 8000)
                    )
                ),
                QuranVerse(
                    number = 3,
                    textUthmani = "إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ",
                    textModern = "إن شانئك هو الأبتر",
                    translation = "Indeed, your enemy is the one cut off.",
                    startMs = 8000,
                    endMs = 12000,
                    words = listOf(
                        QuranWord("إِنَّ", 8000, 9000),
                        QuranWord("شَانِئَكَ", 9000, 10200),
                        QuranWord("هُوَ", 10200, 11000),
                        QuranWord("الْأَبْتَرُ", 11000, 12000)
                    )
                )
            )
        ),
        Surah(
            id = 112,
            nameArabic = "سورة الإخلاص",
            nameEnglish = "Surah Al-Ikhlas",
            englishMeaning = "The Sincerity",
            durationMs = 14000L,
            audioUrl = "https://server8.mp3quran.net/afs/112.mp3",
            backgroundSuggestIdea = "Speaks of Monotheism (Tawhid). Recommended: Sovereign Kaaba Gold background, signifying the sanctuary of Islam, matched with beautiful gilded text outlines.",
            verses = listOf(
                QuranVerse(
                    number = 1,
                    textUthmani = "قُلْ هُوَ اللَّهُ أَحَدٌ",
                    textModern = "قل هو الله أحد",
                    translation = "Say, 'He is Allah, [who is] One,",
                    startMs = 0,
                    endMs = 3500,
                    words = listOf(
                        QuranWord("قُلْ", 0, 800),
                        QuranWord("هُوَ", 800, 1500),
                        QuranWord("اللَّهُ", 1500, 2400),
                        QuranWord("أَحَدٌ", 2400, 3500)
                    )
                ),
                QuranVerse(
                    number = 2,
                    textUthmani = "اللَّهُ الصَّمَدُ",
                    textModern = "الله الصمد",
                    translation = "Allah, the Eternal Refuge.",
                    startMs = 3500,
                    endMs = 6800,
                    words = listOf(
                        QuranWord("اللَّهُ", 3500, 4800),
                        QuranWord("الصَّمَدُ", 4800, 6800)
                    )
                ),
                QuranVerse(
                    number = 3,
                    textUthmani = "لَمْ يَلِدْ وَلَمْ يُولَدْ",
                    textModern = "لم يلد ولم يولد",
                    translation = "He neither begets nor is born,",
                    startMs = 6800,
                    endMs = 10500,
                    words = listOf(
                        QuranWord("لَمْ", 6800, 7500),
                        QuranWord("يَلِدْ", 7500, 8600),
                        QuranWord("وَلَمْ", 8600, 9400),
                        QuranWord("يُولَدْ", 9400, 10500)
                    )
                ),
                QuranVerse(
                    number = 4,
                    textUthmani = "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ",
                    textModern = "ولم يكن له كفوا أحد",
                    translation = "And there is none co-equal or comparable to Him.'",
                    startMs = 10500,
                    endMs = 14000,
                    words = listOf(
                        QuranWord("وَلَمْ", 10500, 11200),
                        QuranWord("يَكُن", 11200, 11900),
                        QuranWord("لَّهُ", 11900, 12600),
                        QuranWord("كُفُوًا", 12600, 13200),
                        QuranWord("أَحَدٌ", 13200, 14000)
                    )
                )
            )
        )
    )
}
