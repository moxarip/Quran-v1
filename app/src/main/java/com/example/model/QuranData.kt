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
    KUFIC("Kufic", "الخط الكوفي", "Modern geometric rounded script"),
    CAIRO("Cairo", "خط القاهرة الحديث", "Modern geometric Sans-serif style"),
    REEM_KUFI("ReemKufi", "خط ريم الكوفي", "Decorative heavy block calligraphy"),
    TAJAWAL("Tajawal", "خط تجول الأنيق", "Sleek and contemporary flowing lines"),
    LATEEF("Lateef", "خط لطيف المرن", "Warm traditional flowing script")
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
        "أستار الكعبة الغراء",
        "Muted dark gold with beautiful silk textures",
        listOf(Color(0xFF0F1412), Color(0xFF1E2824), Color(0xFF322307)),
        "geometric"
    ),
    MOSQUE_STARRY(
        "mosque_starry",
        "Emerald Mosque Night",
        "محراب الليل والنجوم",
        "Starry serene night behind a majestic green minheur",
        listOf(Color(0xFF051D14), Color(0xFF0D3E2F), Color(0xFF1C5745)),
        "mihrab"
    ),
    GOLDEN_ARABESQUE(
        "golden_arabesque",
        "Royal Arabesque",
        "نقوش أندلسية ذهبية",
        "Complex intricate Islamic patterns etched in glowing light",
        listOf(Color(0xFF12140D), Color(0xFF232B1B), Color(0xFF3F3512)),
        "arabesque"
    ),
    DESERT_SUNSET(
        "desert_sunset",
        "Spiritual Oasis",
        "غروب الروح الصحراوي",
        "Calming orange gradients reflecting desert silence",
        listOf(Color(0xFF1D0F0A), Color(0xFF3D1C0F), Color(0xFF1F2F1B)),
        "minimal"
    ),
    EMERALD_AURA(
        "emerald_aura",
        "Mystic Dawn",
        "الفجر الصادق الزمردي",
        "Deep minimalist rich shades of deep spiritual emerald",
        listOf(Color(0xFF020E0A), Color(0xFF09291E), Color(0xFF124233)),
        "floral"
    ),
    SACRED_LIGHTS(
        "sacred_lights",
        "Sacred Floating Lights",
        "أشعة الأنوار السابحة",
        "Divine floating light rays over pure dark turquoise gradient",
        listOf(Color(0xFF031416), Color(0xFF082D33), Color(0xFF1C5D66)),
        "mihrab"
    ),
    MADINAH_GREEN(
        "madinah_green",
        "Prophet's Pure Green",
        "الروضة الشريفة الخضراء",
        "Serene Madinah green dome vibes with gold patterns",
        listOf(Color(0xFF02170D), Color(0xFF063A22), Color(0xFF1E523A)),
        "geometric"
    ),
    PALESTINE_CROWN(
        "palestine_crown",
        "Palestine Golden Dusk",
        "بيت المقدس ومسرى الأنبياء",
        "Golden Dome of the Rock silhouette in dusk violet blue",
        listOf(Color(0xFF0E0B1A), Color(0xFF1F1A3A), Color(0xFF4C3015)),
        "arabesque"
    ),
    AURORA_PRAYER(
        "aurora_prayer",
        "Aurora Spiritual Prayer",
        "شفق مناجاة السماوي",
        "Vibrant dynamic green/purple northern aurora skies",
        listOf(Color(0xFF010A14), Color(0xFF05253A), Color(0xFF3E1E5E)),
        "minimal"
    ),
    ROYAL_NAVY(
        "royal_navy",
        "Tranquil Royal Navy",
        "زخارف البحر الساكن",
        "Deep navy blue geometry representing ultimate peace",
        listOf(Color(0xFF020914), Color(0xFF05172E), Color(0xFF10284D)),
        "geometric"
    ),
    PEARL_DAWN(
        "pearl_dawn",
        "Pearl Dawn Mist",
        "الندى واللؤلؤ الإيماني",
        "Soft premium platinum steel and gold dawn layout",
        listOf(Color(0xFF0A0F0D), Color(0xFF1C2220), Color(0xFF3F3821)),
        "floral"
    ),
    COSMIC_MIRACLE(
        "cosmic_miracle",
        "Cosmic Planetary Miracle",
        "الإعجاز الكوني العظيم",
        "Deep nebula space textures with spinning geometric circles",
        listOf(Color(0xFF05000C), Color(0xFF120324), Color(0xFF2C0A4B)),
        "geometric"
    ),
    QURAN_PAGES(
        "quran_pages",
        "Glow of Mushaf Parchment",
        "نور الصحائف والهدى",
        "Comfortable vintage parchment glow with fine border lines",
        listOf(Color(0xFF14120A), Color(0xFF2E2A1C), Color(0xFF261D03)),
        "arabesque"
    ),
    GOLDEN_PORTAL(
        "golden_portal",
        "Portal of Paradise",
        "بوابة الفردوس النورانية",
        "Golden arches leading to high contrast ambient glow",
        listOf(Color(0xFF110C02), Color(0xFF291B03), Color(0xFF472803)),
        "mihrab"
    ),
    MINDFUL_PEACE(
        "mindful_peace",
        "Calming Void of Peace",
        "سكينة النفس الصامتة",
        "Ultra-minimalistic dark slate with breathing halo circle",
        listOf(Color(0xFF0C0E0F), Color(0xFF181C1E), Color(0xFF232A2D)),
        "minimal"
    ),
    KAABA_NIGHT(
        "kaaba_night",
        "Kaaba Midnight Silver",
        "البيت العتيق الفضي",
        "Serene deep night Kaaba vibes with silver embroidery tones",
        listOf(Color(0xFF020408), Color(0xFF0F1524), Color(0xFF263348)),
        "geometric"
    ),
    AL_AQSA_BLUE(
        "al_aqsa_blue",
        "Al-Aqsa Turquoise Dawn",
        "الأقصى مسرى الأنبياء",
        "Prussian sky silhouette of the Al-Aqsa Dome with emerald dust",
        listOf(Color(0xFF080217), Color(0xFF0C1935), Color(0xFF0E4554)),
        "mihrab"
    ),
    NATURE_SACRED(
        "nature_sacred",
        "Sacred River Oasis",
        "الروضة والينابيع العذبة",
        "Deep calming forest greens layered with organic dawn sky hints",
        listOf(Color(0xFF02120C), Color(0xFF082C1D), Color(0xFF244F2E)),
        "floral"
    ),
    ANIMATED_HEAVEN(
        "animated_heaven",
        "Celestial Gates",
        "السماوات العلى والشهب",
        "Ethereal shimmering stars in a deep cosmic lilac dawn sky",
        listOf(Color(0xFF0F021A), Color(0xFF220538), Color(0xFF4C085C)),
        "arabesque"
    )
}

data class SurahMeta(
    val id: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val englishMeaning: String,
    val versesCount: Int,
    val classification: String
)

enum class ReciterType(val id: String, val displayName: String, val arabicName: String, val serverPrefixMap: Map<String, String>) {
    ALAFASY(
        "alafasy", "Mishary Alafasy", "مشاري العفاسي",
        mapOf(
            "mp3quran" to "https://server8.mp3quran.net/afs/",
            "quranicaudio" to "https://download.quranicaudio.com/quran/mishari_rashid_al_afasy/",
            "everydayquran" to "https://everydayquran.org/mp3/Alafasy/"
        )
    ),
    ABDULBASIT(
        "basit", "Abdul Basit (Mujawwad)", "عبد الباسط عبد الصمد",
        mapOf(
            "mp3quran" to "https://server11.mp3quran.net/basit_mtwd/",
            "quranicaudio" to "https://download.quranicaudio.com/quran/abdul_basit_mujawwad/",
            "everydayquran" to "https://everydayquran.org/mp3/AbdulBasit/"
        )
    ),
    MAHER(
        "maher", "Maher Al-Muaiqly", "ماهر المعيقلي",
        mapOf(
            "mp3quran" to "https://server12.mp3quran.net/maher/",
            "quranicaudio" to "https://download.quranicaudio.com/quran/maher_al_muaiqly/",
            "everydayquran" to "https://everydayquran.org/mp3/Maher%20Al%20Muaiqly/"
        )
    ),
    GHAMDI(
        "ghamdi", "Saad Al-Ghamdi", "سعد الغامدي",
        mapOf(
            "mp3quran" to "https://server7.mp3quran.net/s_gmd/",
            "quranicaudio" to "https://download.quranicaudio.com/quran/sa3d_al_ghamdi/complete/",
            "everydayquran" to "https://everydayquran.org/mp3/Saad%20Al%20Ghamdi/"
        )
    ),
    SHATRI(
        "shatri", "Abu Bakr Al-Shatri", "أبو بكر الشاطري",
        mapOf(
            "mp3quran" to "https://server11.mp3quran.net/shatri/",
            "quranicaudio" to "https://download.quranicaudio.com/quran/abu_bakr_al_shatri/",
            "everydayquran" to "https://everydayquran.org/mp3/Abu%20Bakr%20Al%20Shatri/"
        )
    ),
    SHURAIM(
        "shuraim", "Saud Al-Shuraim", "سعود الشريم",
        mapOf(
            "mp3quran" to "https://server7.mp3quran.net/shur/",
            "quranicaudio" to "https://download.quranicaudio.com/quran/sa3ood_ash_shuraym/",
            "everydayquran" to "https://everydayquran.org/mp3/Saud%20Al-Shuraim/"
        )
    )
}

enum class ServerType(val id: String, val displayName: String, val arabicName: String) {
    MP3QURAN("mp3quran", "MP3Quran Realtime CDN", "سيرفر البث الرئيسي (سرعة فائقة)"),
    QURANICAUDIO("quranicaudio", "Islamic Audio Archive", "الخادم الاحتياطي (الأرشيف الإسلامي)"),
    EVERYDAYQURAN("everydayquran", "Global Quran Cloud", "خادم التوزيع السحابي الاحترافي")
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

    val completeSurahs = listOf(
        SurahMeta(1, "الفاتحة", "Al-Fatihah", "The Opening", 7, "Meccan"),
        SurahMeta(2, "البقرة", "Al-Baqarah", "The Cow", 286, "Medinan"),
        SurahMeta(3, "آل عمران", "Ali 'Imran", "Family of Imran", 200, "Medinan"),
        SurahMeta(4, "النساء", "An-Nisa", "The Women", 176, "Medinan"),
        SurahMeta(5, "المائدة", "Al-Ma'idah", "The Table Spread", 120, "Medinan"),
        SurahMeta(6, "الأنعام", "Al-An'am", "The Cattle", 165, "Meccan"),
        SurahMeta(7, "الأعراف", "Al-Ar'af", "The Heights", 206, "Meccan"),
        SurahMeta(8, "الأنفال", "Al-Anfal", "The Spoils of War", 75, "Medinan"),
        SurahMeta(9, "التوبة", "At-Tawbah", "The Repentance", 129, "Medinan"),
        SurahMeta(10, "يونس", "Yunus", "Jonah", 109, "Meccan"),
        SurahMeta(11, "هود", "Hud", "Hud", 123, "Meccan"),
        SurahMeta(12, "يوسف", "Yusuf", "Joseph", 111, "Meccan"),
        SurahMeta(13, "الرعد", "Ar-Ra'd", "The Thunder", 43, "Medinan"),
        SurahMeta(14, "إبراهيم", "Ibrahim", "Abrahim", 52, "Meccan"),
        SurahMeta(15, "الحجر", "Al-Hijr", "The Rocky Tract", 99, "Meccan"),
        SurahMeta(16, "النحل", "An-Nahl", "The Bee", 128, "Meccan"),
        SurahMeta(17, "الإسراء", "Al-Isra", "The Night Journey", 111, "Meccan"),
        SurahMeta(18, "الكهف", "Al-Kahf", "The Cave", 110, "Meccan"),
        SurahMeta(19, "مريم", "Maryam", "Mary", 98, "Meccan"),
        SurahMeta(20, "طه", "Taha", "Ta-Ha", 135, "Meccan"),
        SurahMeta(21, "الأنبياء", "Al-Anbiya", "The Prophets", 112, "Meccan"),
        SurahMeta(22, "الحج", "Al-Hajj", "The Pilgrimage", 78, "Medinan"),
        SurahMeta(23, "المؤمنون", "Al-Mu'minun", "The Believers", 118, "Meccan"),
        SurahMeta(24, "النور", "An-Nur", "The Light", 64, "Medinan"),
        SurahMeta(25, "الفرقان", "Al-Furqan", "The Criterion", 77, "Meccan"),
        SurahMeta(26, "الشعراء", "Ash-Shu'ara", "The Poets", 227, "Meccan"),
        SurahMeta(27, "النمل", "An-Naml", "The Ant", 93, "Meccan"),
        SurahMeta(28, "القصص", "Al-Qasas", "The Stories", 88, "Meccan"),
        SurahMeta(29, "العنكبوت", "Al-'Ankabut", "The Spider", 69, "Meccan"),
        SurahMeta(30, "الروم", "Ar-Rum", "The Romans", 60, "Meccan"),
        SurahMeta(31, "لقمان", "Luqman", "Luqman", 34, "Meccan"),
        SurahMeta(32, "السجدة", "As-Sajdah", "The Prostration", 30, "Meccan"),
        SurahMeta(33, "الأحزاب", "Al-Ahzab", "The Combined Forces", 73, "Medinan"),
        SurahMeta(34, "سبأ", "Saba", "Sheba", 54, "Meccan"),
        SurahMeta(35, "فاطر", "Fatir", "Originator", 45, "Meccan"),
        SurahMeta(36, "يس", "Ya-Sin", "Ya Seen", 83, "Meccan"),
        SurahMeta(37, "الصافات", "As-Saffat", "Those who set the Ranks", 182, "Meccan"),
        SurahMeta(38, "ص", "Sad", "The Letter 'Saad'", 88, "Meccan"),
        SurahMeta(39, "الزمر", "Az-Zumar", "The Troops", 75, "Meccan"),
        SurahMeta(40, "غافر", "Ghafir", "The Forgiver", 85, "Meccan"),
        SurahMeta(41, "فصلت", "Fussilat", "Explained in Detail", 54, "Meccan"),
        SurahMeta(42, "الشورى", "Ash-Shura", "The Consultation", 53, "Meccan"),
        SurahMeta(43, "الزخرف", "Az-Zukhruf", "The Ornaments of Gold", 89, "Meccan"),
        SurahMeta(44, "الدخان", "Ad-Dukhan", "The Smoke", 59, "Meccan"),
        SurahMeta(45, "الجاثية", "Al-Jathiyah", "The Crouching", 37, "Meccan"),
        SurahMeta(46, "الأحقاف", "Al-Ahqaf", "The Wind-Curved Sandhills", 35, "Meccan"),
        SurahMeta(47, "محمد", "Muhammad", "Muhammad", 38, "Medinan"),
        SurahMeta(48, "الفتح", "Al-Fath", "The Victory", 29, "Medinan"),
        SurahMeta(49, "الحجرات", "Al-Hujurat", "The Dwellings", 18, "Medinan"),
        SurahMeta(50, "ق", "Qaf", "The Letter 'Qaf'", 45, "Meccan"),
        SurahMeta(51, "الذاريات", "Adh-Dhariyat", "The Winnowing Winds", 60, "Meccan"),
        SurahMeta(52, "الطور", "At-Tur", "The Mount", 49, "Meccan"),
        SurahMeta(53, "النجم", "An-Najm", "The Star", 62, "Meccan"),
        SurahMeta(54, "القمر", "Al-Qamar", "The Moon", 55, "Meccan"),
        SurahMeta(55, "الرحمن", "Ar-Rahman", "The Beneficent", 78, "Medinan"),
        SurahMeta(56, "الواقعة", "Al-Waqi'ah", "The Inevitable", 96, "Meccan"),
        SurahMeta(57, "الحديد", "Al-Hadid", "The Iron", 29, "Medinan"),
        SurahMeta(58, "المجادلة", "Al-Mujadilah", "The Pleading Woman", 22, "Medinan"),
        SurahMeta(59, "الحشر", "Al-Hashr", "The Exile", 24, "Medinan"),
        SurahMeta(60, "الممتحنة", "Al-Mumtahanah", "She that is to be examined", 13, "Medinan"),
        SurahMeta(61, "الصف", "As-Saff", "The Ranks", 14, "Medinan"),
        SurahMeta(62, "الجمعة", "Al-Jumu'ah", "The Congregation, Friday", 11, "Medinan"),
        SurahMeta(63, "المنافقون", "Al-Munafiqun", "The Hypocrites", 11, "Medinan"),
        SurahMeta(64, "التغابن", "At-Taghabun", "The Mutual Disillusion", 18, "Medinan"),
        SurahMeta(65, "الطلاق", "At-Talaq", "The Divorce", 12, "Medinan"),
        SurahMeta(66, "التحريم", "At-Tahrim", "The Prohibition", 12, "Medinan"),
        SurahMeta(67, "الملك", "Al-Mulk", "The Sovereignty", 30, "Meccan"),
        SurahMeta(68, "القلم", "Al-Qalam", "The Pen", 52, "Meccan"),
        SurahMeta(69, "الحاقة", "Al-Haqqah", "The Reality", 52, "Meccan"),
        SurahMeta(70, "المعارج", "Al-Ma'arij", "The Ascending Stairways", 44, "Meccan"),
        SurahMeta(71, "نوح", "Nuh", "Noah", 28, "Meccan"),
        SurahMeta(72, "الجن", "Al-Jinn", "The Jinn", 28, "Meccan"),
        SurahMeta(73, "المزمل", "Al-Muzzammil", "The Enshrouded One", 20, "Meccan"),
        SurahMeta(74, "المدثر", "Al-Muddaththir", "The Cloaked One", 56, "Meccan"),
        SurahMeta(75, "القيامة", "Al-Qiyamah", "The Resurrection", 40, "Meccan"),
        SurahMeta(76, "الإنسان", "Al-Insan", "The Man", 31, "Medinan"),
        SurahMeta(77, "المرسلات", "Al-Mursalat", "The Emissaries", 50, "Meccan"),
        SurahMeta(78, "النبأ", "An-Naba", "The Tidings", 40, "Meccan"),
        SurahMeta(79, "النازعات", "An-Nazi'at", "Those who drag forth", 46, "Meccan"),
        SurahMeta(80, "عبس", "Abasa", "He Frowned", 42, "Meccan"),
        SurahMeta(81, "التكوير", "At-Takwir", "The Overthrowing", 29, "Meccan"),
        SurahMeta(82, "الانفطار", "Al-Infitar", "The Cleaving", 19, "Meccan"),
        SurahMeta(83, "المطففين", "Al-Mutaffifin", "The Defrauders", 36, "Meccan"),
        SurahMeta(84, "الانشقاق", "Al-Inshiqaq", "The Sundering", 25, "Meccan"),
        SurahMeta(85, "البروج", "Al-Buruj", "The Mansions of the Stars", 22, "Meccan"),
        SurahMeta(86, "الطارق", "At-Tariq", "The Morning Star", 17, "Meccan"),
        SurahMeta(87, "الأعلى", "Al-A'la", "The Most High", 19, "Meccan"),
        SurahMeta(88, "الغاشية", "Al-Ghashiyah", "The Overwhelming", 26, "Meccan"),
        SurahMeta(89, "الفجر", "Al-Fajr", "The Dawn", 30, "Meccan"),
        SurahMeta(90, "البلد", "Al-Balad", "The City", 20, "Meccan"),
        SurahMeta(91, "الشمس", "Ash-Shams", "The Sun", 15, "Meccan"),
        SurahMeta(92, "الليل", "Al-Layl", "The Night", 21, "Meccan"),
        SurahMeta(93, "الضحى", "Ad-Duha", "The Morning Hours", 11, "Meccan"),
        SurahMeta(94, "الشرح", "Ash-Sharh", "The Consolation", 8, "Meccan"),
        SurahMeta(95, "التين", "At-Tin", "The Fig", 8, "Meccan"),
        SurahMeta(96, "العلق", "Al-'Alaq", "The Clot", 19, "Meccan"),
        SurahMeta(97, "القدر", "Al-Qadr", "The Power", 5, "Meccan"),
        SurahMeta(98, "البينة", "Al-Bayyinah", "The Clear Proof", 8, "Medinan"),
        SurahMeta(99, "الزلزلة", "Az-Zalzalah", "The Earthquake", 8, "Medinan"),
        SurahMeta(100, "العاديات", "Al-'Adiyat", "The Courser", 11, "Meccan"),
        SurahMeta(101, "القارعة", "Al-Qari'ah", "The Calamity", 11, "Meccan"),
        SurahMeta(102, "التكاثر", "At-Takathur", "The Rivalry in World Increase", 8, "Meccan"),
        SurahMeta(103, "العصر", "Al-'Asr", "The Declining Day", 3, "Meccan"),
        SurahMeta(104, "الهمزة", "Al-Humazah", "The Traducer", 9, "Meccan"),
        SurahMeta(105, "الفيل", "Al-Fil", "The Elephant", 5, "Meccan"),
        SurahMeta(106, "قريش", "Quraysh", "Quraysh", 4, "Meccan"),
        SurahMeta(107, "الماعون", "Al-Ma'un", "The Small Kindnesses", 7, "Meccan"),
        SurahMeta(108, "الكوثر", "Al-Kawthar", "The Abundance", 3, "Meccan"),
        SurahMeta(109, "الكافرون", "Al-Kafirun", "The Disbelievers", 6, "Meccan"),
        SurahMeta(110, "النصر", "An-Nasr", "The Divine Support", 3, "Medinan"),
        SurahMeta(111, "المسد", "Al-Masad", "The Palm Fiber", 5, "Meccan"),
        SurahMeta(112, "الإخلاص", "Al-Ikhlas", "The Sincerity", 4, "Meccan"),
        SurahMeta(113, "الفلق", "Al-Falaq", "The Daybreak", 5, "Meccan"),
        SurahMeta(114, "الناس", "An-Nas", "The Mankind", 6, "Meccan")
    )
}
