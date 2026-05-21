package com.example.ui

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.OndemandVideo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.service.GeminiService
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranVideoApp() {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Navigation and state variables
    var currentScreen by remember { mutableStateOf("home") } // "home" -> "customize" -> "export" -> "exported"
    var selectedSurah by remember { mutableStateOf(QuranRepository.surahs[0]) }
    var currentAspectRatio by remember { mutableStateOf(AspectRatioType.PORTRAIT_9_16) }
    var selectedBackground by remember { mutableStateOf(BackgroundType.MOSQUE_STARRY) }
    var selectedFont by remember { mutableStateOf(ArabicFontType.UTHMANI) }
    var selectedOrnament by remember { mutableStateOf(OrnamentStyle.MIHRAB) }
    var selectedTransition by remember { mutableStateOf(TransitionEffect.SLOW_ZOOM) }
    var wordByWordHighlight by remember { mutableStateOf(true) }
    var isArabicFirstUi by remember { mutableStateOf(true) }

    // Custom coloring options
    var customTextColor by remember { mutableStateOf(Color(0xFFE5C158)) } // Islamic light gold
    var customTextSize by remember { mutableStateOf(26f) }
    var highlightColor by remember { mutableStateOf(Color(0xFFFFFFFF)) } // Highlighted word color

    // Custom audio links variables
    var customAudioUrl by remember { mutableStateOf("") }
    var customAudioTitle by remember { mutableStateOf("") }
    var isCustomAudioLoaded by remember { mutableStateOf(false) }

    // Media Player state
    var isPlaying by remember { mutableStateOf(false) }
    var playbackProgressMs by remember { mutableStateOf(0L) }
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    // AI recommendation state
    var isAiLoading by remember { mutableStateOf(false) }
    var aiReasonArabic by remember { mutableStateOf("") }
    var aiReasonEnglish by remember { mutableStateOf("") }
    var showAiToast by remember { mutableStateOf(false) }

    // Export variables
    var selectedQuality by remember { mutableStateOf("1080p") }
    var renderProgress by remember { mutableStateOf(0f) }
    var renderLog by remember { mutableStateOf("Initializing video renderer...") }

    // Initialize/release media player
    LaunchedEffect(selectedSurah, customAudioUrl, isCustomAudioLoaded) {
        // Stop current
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            Log.e("QuranVideo", "Error cleaning player", e)
        }
        isPlaying = false
        playbackProgressMs = 0L

        val url = if (isCustomAudioLoaded && customAudioUrl.isNotEmpty()) customAudioUrl else selectedSurah.audioUrl
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener {
                    // Ready to play if desired, but default to idle
                }
                setOnCompletionListener {
                    isPlaying = false
                    playbackProgressMs = 0L
                }
            }
        } catch (e: Exception) {
            Log.w("QuranVideo", "Could not load remote audio asset", e)
        }
    }

    // Direct local state simulation ticker
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isPlaying) {
                delay(100)
                try {
                    if (mediaPlayer?.isPlaying == true) {
                        playbackProgressMs = mediaPlayer!!.currentPosition.toLong()
                    } else {
                        // Safe simulated ticker in fallback or sandbox container background
                        playbackProgressMs += 100
                        val maxDur = if (isCustomAudioLoaded) 30000L else selectedSurah.durationMs
                        if (playbackProgressMs >= maxDur) {
                            playbackProgressMs = 0L
                            isPlaying = false
                        }
                    }
                } catch (e: Exception) {
                    playbackProgressMs += 100
                    val maxDur = if (isCustomAudioLoaded) 30000L else selectedSurah.durationMs
                    if (playbackProgressMs >= maxDur) {
                        playbackProgressMs = 0L
                        isPlaying = false
                    }
                }
            }
        }
    }

    // Clean up player upon dispose
    DisposableEffect(Unit) {
        onDispose {
            try {
                mediaPlayer?.stop()
                mediaPlayer?.release()
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Screen switching using elegant fade entries
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(200))
                },
                label = "ScreenTransition"
            ) { target ->
                when (target) {
                    "home" -> HomeScreen(
                        isArabicFirst = isArabicFirstUi,
                        onToggleLan = { isArabicFirstUi = !isArabicFirstUi },
                        onSelectSurah = { surah ->
                            selectedSurah = surah
                            isCustomAudioLoaded = false
                            currentScreen = "customize"
                        },
                        customAudioUrl = customAudioUrl,
                        onCustomAudioUrlChange = { customAudioUrl = it },
                        customAudioTitle = customAudioTitle,
                        onCustomAudioTitleChange = { customAudioTitle = it },
                        onLoadCustomAudio = {
                            if (customAudioUrl.isNotBlank()) {
                                isCustomAudioLoaded = true
                                currentScreen = "customize"
                            }
                        }
                    )

                    "customize" -> CustomizeScreen(
                        isArabicFirst = isArabicFirstUi,
                        selectedSurah = selectedSurah,
                        isCustomAudioLoaded = isCustomAudioLoaded,
                        customTitle = customAudioTitle,
                        currentAspectRatio = currentAspectRatio,
                        onSelectAspectRatio = { currentAspectRatio = it },
                        selectedBackground = selectedBackground,
                        onSelectBackground = { selectedBackground = it },
                        selectedFont = selectedFont,
                        onSelectFont = { selectedFont = it },
                        selectedOrnament = selectedOrnament,
                        onSelectOrnament = { selectedOrnament = it },
                        selectedTransition = selectedTransition,
                        onSelectTransition = { selectedTransition = it },
                        wordByWordHighlight = wordByWordHighlight,
                        onToggleWordByWord = { wordByWordHighlight = it },
                        customTextColor = customTextColor,
                        onSelectTextColor = { customTextColor = it },
                        customTextSize = customTextSize,
                        onTextSizeChange = { customTextSize = it },
                        isPlaying = isPlaying,
                        playbackProgressMs = playbackProgressMs,
                        onTogglePlayback = {
                            if (isPlaying) {
                                try { mediaPlayer?.pause() } catch(e:Exception){}
                                isPlaying = false
                            } else {
                                try { mediaPlayer?.start() } catch(e:Exception){}
                                isPlaying = true
                            }
                        },
                        onResetPlayback = {
                            try {
                                mediaPlayer?.seekTo(0)
                                mediaPlayer?.pause()
                            } catch(e:Exception){}
                            isPlaying = false
                            playbackProgressMs = 0L
                        },
                        isAiLoading = isAiLoading,
                        aiReasonArabic = aiReasonArabic,
                        aiReasonEnglish = aiReasonEnglish,
                        onTriggerAiAdvisor = {
                            coroutineScope.launch {
                                isAiLoading = true
                                val sampleText = selectedSurah.verses.firstOrNull()?.textUthmani ?: ""
                                val suggestion = GeminiService.fetchVisualSuggestions(
                                    selectedSurah.nameArabic,
                                    selectedSurah.nameEnglish,
                                    sampleText
                                )
                                // Apply suggestions
                                selectedBackground = BackgroundType.values().find { it.id == suggestion.backgroundId } ?: selectedBackground
                                selectedFont = ArabicFontType.values().find { it.fontName.equals(suggestion.fontName, true) } ?: selectedFont
                                selectedOrnament = OrnamentStyle.values().find { it.id == suggestion.ornamentStyle } ?: selectedOrnament
                                selectedTransition = TransitionEffect.values().find { it.id == suggestion.transitionEffect } ?: selectedTransition
                                aiReasonArabic = suggestion.reasonArabic
                                aiReasonEnglish = suggestion.reasonEnglish
                                isAiLoading = false
                                showAiToast = true
                            }
                        },
                        onNavigateBack = {
                            try { mediaPlayer?.stop() } catch(e:Exception){}
                            isPlaying = false
                            currentScreen = "home"
                        },
                        onProceedToExport = {
                            currentScreen = "export"
                        }
                    )

                    "export" -> ExportScreen(
                        isArabicFirst = isArabicFirstUi,
                        selectedQuality = selectedQuality,
                        onSelectQuality = { selectedQuality = it },
                        renderProgress = renderProgress,
                        renderLog = renderLog,
                        onStartRender = {
                            coroutineScope.launch {
                                renderProgress = 0.01f
                                renderLog = if (isArabicFirstUi) "بدء تهيئة مخرجات الفيديو..." else "Initializing output video buffers..."
                                delay(1200)

                                renderProgress = 0.15f
                                renderLog = if (isArabicFirstUi) "دمج مسار الصوت الرقمي..." else "Multiplexing continuous digital audio track..."
                                delay(1000)

                                renderProgress = 0.35f
                                renderLog = if (isArabicFirstUi) "تطبيق خط المصحف العثماني والزخارف الإسلامية..." else "Drawing high-contrast divine vector ornaments to buffer..."
                                delay(1200)

                                renderProgress = 0.55f
                                renderLog = if (isArabicFirstUi) "تصيير تدرج الخلفيات المتحركة بمعدل 60 إطار بالثانية..." else "Rendering 60 FPS spatial backdrop gradients..."
                                delay(1500)

                                renderProgress = 0.80f
                                renderLog = if (isArabicFirstUi) "دمج وعولجة التوقيت الدقيق للكلمات..." else "Syncing precise timeline verse triggers into meta blocks..."
                                delay(1000)

                                renderProgress = 0.95f
                                renderLog = if (isArabicFirstUi) "كتابة الحاوية النهائية MP4..." else "Writing final container sectors..."
                                delay(800)

                                renderProgress = 1.0f
                                renderLog = if (isArabicFirstUi) "اكتمل تصيير الفيديو!" else "Video export completely finalized!"
                                delay(400)
                                currentScreen = "exported"
                            }
                        },
                        onNavigateBack = {
                            currentScreen = "customize"
                        }
                    )

                    "exported" -> ExportedShareScreen(
                        isArabicFirst = isArabicFirstUi,
                        surahName = selectedSurah.nameArabic,
                        englishName = selectedSurah.nameEnglish,
                        aspectRatio = currentAspectRatio,
                        backgroundType = selectedBackground,
                        quality = selectedQuality,
                        onBackToEditor = {
                            currentScreen = "customize"
                        },
                        onBackToHome = {
                            currentScreen = "home"
                        }
                    )
                }
            }

            // Beautiful interactive AI recommendation toast bottom-sheet style popup
            if (showAiToast) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0C241B))
                        .border(1.dp, Color(0xFFD4AF37), RoundedCornerShape(16.dp))
                        .clickable { showAiToast = false }
                        .padding(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "AI",
                                    tint = Color(0xFFD4AF37),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isArabicFirstUi) "مستشار التصميم الذكي" else "AI Design Advisor Applied!",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE5C158)
                                )
                            }
                            IconButton(onClick = { showAiToast = false }, modifier = Modifier.size(24.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isArabicFirstUi) aiReasonArabic else aiReasonEnglish,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            textAlign = if (isArabicFirstUi) TextAlign.Right else TextAlign.Left,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isArabicFirstUi) "انقر للإغلاق وتعديل الإعدادات كما تحب" else "Tap to dismiss and modify your specifications freely",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFD4AF37).copy(alpha = 0.8f),
                            fontWeight = FontWeight.Light
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SCREEN 1: HOME PAGE
// -------------------------------------------------------------
@Composable
fun HomeScreen(
    isArabicFirst: Boolean,
    onToggleLan: () -> Unit,
    onSelectSurah: (Surah) -> Unit,
    customAudioUrl: String,
    onCustomAudioUrlChange: (String) -> Unit,
    customAudioTitle: String,
    onCustomAudioTitleChange: (String) -> Unit,
    onLoadCustomAudio: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PolishBodyBg)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Upper Professional Polish layout header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = PolishSurfaceBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, PolishWhite5)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo and Title info
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(PolishGold, PolishGoldGradient)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isArabicFirst) Icons.Default.Movie else Icons.Default.Videocam,
                                contentDescription = "Logo",
                                tint = PolishBodyBg,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = if (isArabicFirst) "قرآن فيديو" else "Quran Video",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "STUDIO EDITION",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PolishGold,
                                letterSpacing = 1.5.sp
                            )
                        }
                    }

                    // Configuration action bars
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Language Change Trigger
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(PolishWhite5, CircleShape)
                                .clickable { onToggleLan() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isArabicFirst) "EN" else "عربي",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PolishGold
                            )
                        }

                        // Status decoration settings icon
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(PolishWhite5, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sacred geometry title design
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "۩  قُـرآن فِـيدْيـو  ۩",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = PolishGold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isArabicFirst) {
                        "حوّل تلاوات القرآن الكريم إلى مقاطع فيديو احترافية في ثوانٍ معدودة"
                    } else {
                        "Convert sublime Quran recitations into professional videos for social sharing"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF8CADA2),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Section header for preloaded Surahs
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = PolishCardBg),
                border = BorderStroke(1.dp, PolishWhite10),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (isArabicFirst) "١. اختر تلاوة جاهزة لإنتاج فيديو فوري" else "1. Choose a pre-loaded recitation for instant videos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE5C158),
                        textAlign = if (isArabicFirst) TextAlign.Right else TextAlign.Left,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    QuranRepository.surahs.forEach { surah ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PolishSurfaceBg)
                                .border(1.dp, PolishWhite5, RoundedCornerShape(12.dp))
                                .clickable { onSelectSurah(surah) }
                                .padding(14.dp)
                                .testTag("surah_item_${surah.id}"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PolishBodyBg)
                                    .border(1.dp, PolishGold.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = surah.id.toString(),
                                    color = PolishGold,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = surah.nameArabic,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Left
                                )
                                Text(
                                    text = "${surah.nameEnglish} • ${surah.englishMeaning}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF8CADA2)
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Select",
                                tint = PolishGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        // Custom Link Import Option
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(containerColor = PolishCardBg),
                border = BorderStroke(1.dp, PolishWhite10),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (isArabicFirst) "٢. أو استورد تلاوات ورابط صوتي مخصص (MP3)" else "2. Or import custom recitation and audio link (MP3)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PolishLightGold,
                        textAlign = if (isArabicFirst) TextAlign.Right else TextAlign.Left,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isArabicFirst) {
                            "أدخل أي رابط مباشر لملف صوتي وسنقوم بمزامنته"
                        } else {
                            "Paste any direct web audio link or mock local file path to synchronize"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF8CADA2),
                        textAlign = if (isArabicFirst) TextAlign.Right else TextAlign.Left,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = customAudioTitle,
                        onValueChange = onCustomAudioTitleChange,
                        label = { Text(if (isArabicFirst) "اسم السورة / القارئ" else "Surah Name / Reciter", color = Color(0xFF8CADA2)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PolishGold,
                            unfocusedBorderColor = PolishWhite10,
                            focusedContainerColor = PolishSurfaceBg,
                            unfocusedContainerColor = PolishSurfaceBg
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = customAudioUrl,
                        onValueChange = onCustomAudioUrlChange,
                        label = { Text(if (isArabicFirst) "رابط ملف السيلكون MP3" else "Direct Web MP3 URL Link", color = Color(0xFF8CADA2)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PolishGold,
                            unfocusedBorderColor = PolishWhite10,
                            focusedContainerColor = PolishSurfaceBg,
                            unfocusedContainerColor = PolishSurfaceBg
                        ),
                        placeholder = { Text("https://example.com/audio.mp3", color = Color.Gray) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (customAudioUrl.isNotBlank() && customAudioTitle.isNotBlank()) {
                                onLoadCustomAudio()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PolishGold),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("import_audio_button"),
                        shape = RoundedCornerShape(16.dp),
                        enabled = customAudioUrl.isNotBlank() && customAudioTitle.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = "Import",
                            tint = PolishBodyBg
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabicFirst) "تحميل وتزامن الملف الصوتي" else "Load & Synchronize Audio",
                            color = PolishBodyBg,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Safe Simulator tip
                    Text(
                        text = if (isArabicFirst) {
                            "💡 تفضل باختيار رابط تجريبي مثل: https://server8.mp3quran.net/afs/112.mp3 لضمان أعلى دقة مزامنة فورية."
                        } else {
                            "💡 Tip: Try pasting a sample url like https://server8.mp3quran.net/afs/112.mp3 to see high fidelity waveform and lyric stream."
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = PolishGold.copy(alpha = 0.82f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SCREEN 2: CUSTOMIZE & PREVIEW LAYOUT
// -------------------------------------------------------------
@Composable
fun CustomizeScreen(
    isArabicFirst: Boolean,
    selectedSurah: Surah,
    isCustomAudioLoaded: Boolean,
    customTitle: String,
    currentAspectRatio: AspectRatioType,
    onSelectAspectRatio: (AspectRatioType) -> Unit,
    selectedBackground: BackgroundType,
    onSelectBackground: (BackgroundType) -> Unit,
    selectedFont: ArabicFontType,
    onSelectFont: (ArabicFontType) -> Unit,
    selectedOrnament: OrnamentStyle,
    onSelectOrnament: (OrnamentStyle) -> Unit,
    selectedTransition: TransitionEffect,
    onSelectTransition: (TransitionEffect) -> Unit,
    wordByWordHighlight: Boolean,
    onToggleWordByWord: (Boolean) -> Unit,
    customTextColor: Color,
    onSelectTextColor: (Color) -> Unit,
    customTextSize: Float,
    onTextSizeChange: (Float) -> Unit,
    isPlaying: Boolean,
    playbackProgressMs: Long,
    onTogglePlayback: () -> Unit,
    onResetPlayback: () -> Unit,
    isAiLoading: Boolean,
    aiReasonArabic: String,
    aiReasonEnglish: String,
    onTriggerAiAdvisor: () -> Unit,
    onNavigateBack: () -> Unit,
    onProceedToExport: () -> Unit
) {
    var activeTab by remember { mutableStateOf("canvas") } // "canvas" -> "bg" -> "font" -> "ornament"
    val highlightColor = Color.White

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PolishBodyBg)
    ) {
        val totalMs = if (isCustomAudioLoaded) 30000L else selectedSurah.durationMs
        
        // Helper time format
        val formatTime = remember<(Long) -> String> {
            { ms ->
                val totalSec = ms / 1000
                val min = totalSec / 60
                val sec = totalSec % 60
                val minStr = if (min < 10) "0$min" else "$min"
                val secStr = if (sec < 10) "0$sec" else "$sec"
                "$minStr:$secStr"
            }
        }

        // Live dot animation pulse
        val dotTransition = rememberInfiniteTransition(label = "RedPulse")
        val dotAlpha by dotTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "Alpha"
        )

        // Upper sleek header bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PolishSurfaceBg)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(PolishWhite5, CircleShape)
                    .clickable { onNavigateBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = if (isCustomAudioLoaded) customTitle else selectedSurah.nameArabic,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PolishLightGold
            )

            // Auto-AI Suggest button
            Box {
                if (isAiLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = PolishGold,
                        strokeWidth = 2.dp
                    )
                } else {
                    IconButton(
                        onClick = onTriggerAiAdvisor,
                        modifier = Modifier
                            .size(40.dp)
                            .background(PolishGold, CircleShape)
                            .testTag("ai_advisor_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Suggestions",
                            tint = PolishBodyBg,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(PolishWhite5)
        )

        // Finding the currently active verse based on time progress milliseconds
        val activeVerseIndex = remember(playbackProgressMs, selectedSurah) {
            val v = selectedSurah.verses.indexOfFirst { playbackProgressMs in it.startMs until it.endMs }
            if (v == -1) 0 else v
        }
        val activeVerse = selectedSurah.verses.getOrNull(activeVerseIndex) ?: selectedSurah.verses.first()

        // Big visual video canvas preview area
        Box(
            modifier = Modifier
                .weight(1.3f)
                .fillMaxWidth()
                .background(PolishBodyBg)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Simulated video output preview box responsive to target ratio
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(currentAspectRatio.ratio)
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.5.dp, PolishWhite10, RoundedCornerShape(24.dp))
                    .drawBehind {
                        // Multi color gradient brushes for the majestic background type selected
                        val brush = Brush.radialGradient(
                            colors = selectedBackground.gradientColors,
                            center = Offset(size.width / 2f, size.height / 2f),
                            radius = size.width * 1.2f
                        )
                        drawRect(brush)

                        // Ambient center glow representing the radial gradient blend from HTML
                        val spotBrush = Brush.radialGradient(
                            colors = listOf(PolishGold.copy(alpha = 0.18f), Color.Transparent),
                            center = Offset(size.width / 2f, size.height / 2f),
                            radius = size.width * 0.8f
                        )
                        drawRect(spotBrush)
                    },
                contentAlignment = Alignment.Center
            ) {
                // Background animated atmosphere overlay details
                AtmosphereEffects(selectedBackground = selectedBackground, isPlaying = isPlaying)

                // Selected Ornamental frame drawing overlay
                OrnamentFrame(
                    style = selectedOrnament,
                    arabicMarker = selectedOrnament.unicodeMarker,
                    isPortrait = currentAspectRatio == AspectRatioType.PORTRAIT_9_16
                )

                // Unified Layout Pane inside the preview container
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    
                    // 1. Premium pill badge header matching the HTML: "00:12 / 01:45"
                    Box(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                            .border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape)
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red.copy(alpha = dotAlpha))
                            )
                            Text(
                                text = "${formatTime(playbackProgressMs)} / ${formatTime(totalMs)}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    // 2. Center aligned actual Lyrics display
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Transition Effect wrapper visual simulation
                        AnimatedContent(
                            targetState = activeVerse,
                            transitionSpec = {
                                val curve = when (selectedTransition) {
                                    TransitionEffect.SLOW_ZOOM -> fadeIn(animationSpec = tween(500)) + scaleIn(initialScale = 0.95f, animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(300))
                                    TransitionEffect.RISE_UP -> slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)) togetherWith slideOutVertically(targetOffsetY = { -50 }, animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
                                    TransitionEffect.BLUR_REVEAL -> fadeIn(animationSpec = tween(600)) togetherWith fadeOut(animationSpec = tween(300))
                                    TransitionEffect.SOFT_FADE -> fadeIn(animationSpec = tween(450)) togetherWith fadeOut(animationSpec = tween(300))
                                }
                                curve
                            },
                            label = "VerseTransition"
                        ) { targetVerse ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .then(
                                        if (selectedTransition == TransitionEffect.BLUR_REVEAL && isPlaying) Modifier.blur(0.dp) else Modifier
                                    )
                            ) {
                                // Render target Arabic verse
                                if (wordByWordHighlight) {
                                    // Dynamic flow of custom words highlighted
                                    FlowRowLayout(
                                        modifier = Modifier.padding(horizontal = 8.dp),
                                        horizontalGap = 6.dp,
                                        verticalGap = 8.dp
                                    ) {
                                        targetVerse.words.forEach { word ->
                                            // Check if this word is currently illuminated based on timestamp
                                            val isWordActive = playbackProgressMs >= targetVerse.startMs + word.startMs && playbackProgressMs < targetVerse.startMs + word.endMs
                                            
                                            val wordScale by animateFloatAsState(
                                                targetValue = if (isWordActive) 1.25f else 1.0f,
                                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                                            )

                                            Text(
                                                text = word.arabic,
                                                fontSize = (customTextSize * wordScale).sp,
                                                color = if (isWordActive) highlightColor else customTextColor,
                                                fontWeight = if (isWordActive) FontWeight.Black else FontWeight.Medium,
                                                textAlign = TextAlign.Center,
                                                style = androidx.compose.ui.text.TextStyle(
                                                    fontFamily = when (selectedFont) {
                                                        ArabicFontType.UTHMANI -> FontFamily.Serif
                                                        ArabicFontType.AMIRI -> FontFamily.Default
                                                        ArabicFontType.KUFIC -> FontFamily.SansSerif
                                                    }
                                                ),
                                                modifier = Modifier.padding(horizontal = 2.dp)
                                            )
                                        }
                                    }
                                } else {
                                    // Static full verse highlight
                                    Text(
                                        text = targetVerse.textUthmani,
                                        color = customTextColor,
                                        fontSize = customTextSize.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        style = androidx.compose.ui.text.TextStyle(
                                            fontFamily = when (selectedFont) {
                                                ArabicFontType.UTHMANI -> FontFamily.Serif
                                                ArabicFontType.AMIRI -> FontFamily.Default
                                                ArabicFontType.KUFIC -> FontFamily.SansSerif
                                            },
                                            lineHeight = (customTextSize * 1.5).sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 12.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // English Translation Subtitles
                                Text(
                                    text = targetVerse.translation,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center,
                                    fontStyle = FontStyle.Italic,
                                    modifier = Modifier.padding(horizontal = 14.dp)
                                )
                            }
                        }
                    }

                    // 3. Bottom HUD descriptive pills matching the HTML format
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Capsule: Surah details
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "${selectedSurah.nameEnglish} • ${activeVerseIndex + 1}".uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White.copy(alpha = 0.7f),
                                letterSpacing = 1.sp
                            )
                        }

                        // Right Capsule: Gold 4K UHD badge
                        Box(
                            modifier = Modifier
                                .background(PolishGold.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                .border(1.dp, PolishGold.copy(alpha = 0.22f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "4K UHD",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PolishGold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        }

        // 1. Sleek linear progress line with gold to white gradient matching the design code
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(Color(0x0FFFFFFF))
        ) {
            val progressFraction = if (totalMs > 0) playbackProgressMs.toFloat() / totalMs else 0f
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progressFraction.coerceIn(0f, 1f))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(PolishGold, Color.White)
                        )
                    )
            )
        }

        // Live Realtime play track seeker controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PolishSurfaceBg)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onResetPlayback,
                    modifier = Modifier
                        .size(40.dp)
                        .background(PolishWhite5, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay, 
                        contentDescription = "Restart", 
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onTogglePlayback,
                    modifier = Modifier
                        .size(54.dp)
                        .background(PolishGold, CircleShape)
                        .testTag("play_pause_video_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Playback",
                        tint = PolishBodyBg,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Aspect ratio toggle
                Row {
                    AspectRatioType.values().forEach { ar ->
                        val isArActive = currentAspectRatio == ar
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isArActive) Color(0xFF14241B) else PolishWhite5)
                                .border(
                                    1.dp, 
                                    if (isArActive) PolishGold.copy(alpha = 0.4f) else Color.Transparent, 
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { onSelectAspectRatio(ar) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("aspect_ratio_${ar.name}")
                        ) {
                            Text(
                                text = ar.label,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isArActive) PolishGold else Color.White.copy(alpha = 0.5f),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Lower Settings customized tabs
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(PolishBodyBg)
        ) {
            // Category selectors row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PolishSurfaceBg)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TabHeaderItem(
                    title = if (isArabicFirst) "الخلفية" else "Backdrop",
                    icon = Icons.Default.Palette,
                    isActive = activeTab == "canvas",
                    onClick = { activeTab = "canvas" }
                )
                TabHeaderItem(
                    title = if (isArabicFirst) "الخطوط" else "Typography",
                    icon = Icons.Default.TextFields,
                    isActive = activeTab == "font",
                    onClick = { activeTab = "font" }
                )
                TabHeaderItem(
                    title = if (isArabicFirst) "الاطار" else "Frame",
                    icon = Icons.Default.BorderOuter,
                    isActive = activeTab == "ornament",
                    onClick = { activeTab = "ornament" }
                )
            }

            // Tab contents with scrollable sheets
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (activeTab) {
                    "canvas" -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item {
                                Text(
                                    text = if (isArabicFirst) "اختر خلفية ملهمة ومتحركة" else "Select visual spiritual backdrop",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = PolishGold,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                            items(BackgroundType.values()) { bg ->
                                val isBgActive = selectedBackground == bg
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isBgActive) Color(0xFF14241B) else PolishSurfaceBg)
                                        .border(
                                            1.dp, 
                                            if (isBgActive) PolishGold else PolishWhite5, 
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable { onSelectBackground(bg) }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(bg.gradientColors)
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = if (isArabicFirst) bg.arabicName else bg.displayName,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = bg.description,
                                            color = Color.LightGray.copy(alpha = 0.8f),
                                            style = MaterialTheme.typography.bodySmall,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    "font" -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item {
                                Text(
                                    text = if (isArabicFirst) "اختر نمط خط الكتابة المفضل" else "Choose Arabic scripture calligraphy",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = PolishGold,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                            item {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    ArabicFontType.values().forEach { ft ->
                                        val isFtActive = selectedFont == ft
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(horizontal = 4.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(if (isFtActive) Color(0xFF14241B) else PolishSurfaceBg)
                                                .border(
                                                    1.dp, 
                                                    if (isFtActive) PolishGold else PolishWhite5, 
                                                    RoundedCornerShape(12.dp)
                                                )
                                                .clickable { onSelectFont(ft) }
                                                .padding(10.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "بِسْمِ",
                                                color = Color.White,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                style = androidx.compose.ui.text.TextStyle(
                                                    fontFamily = when (ft) {
                                                        ArabicFontType.UTHMANI -> FontFamily.Serif
                                                        ArabicFontType.AMIRI -> FontFamily.Default
                                                        ArabicFontType.KUFIC -> FontFamily.SansSerif
                                                    }
                                                )
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = if (isArabicFirst) ft.displayName else ft.fontName,
                                                color = PolishGold,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // Highlight options toggler
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(PolishSurfaceBg)
                                        .border(1.dp, PolishWhite5, RoundedCornerShape(12.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isArabicFirst) "التزامن كلمة بكلمة" else "Word-by-word active highlight",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = if (isArabicFirst) "سيتم تلوين وتكبير كل كلمة حال تلاوتها تلقائياً" else "Glow each specific word in real-time sequence",
                                            color = Color.LightGray.copy(alpha = 0.8f),
                                            style = MaterialTheme.typography.bodySmall,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Switch(
                                        checked = wordByWordHighlight,
                                        onCheckedChange = { onToggleWordByWord(it) },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = PolishGold,
                                            checkedTrackColor = Color(0xFF14241B)
                                        )
                                    )
                                }
                            }

                            // Sizing Slider
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "${if (isArabicFirst) "حجم خط الكتابة" else "Arabic Text Size"}: ${customTextSize.toInt()}sp",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Slider(
                                    value = customTextSize,
                                    onValueChange = onTextSizeChange,
                                    valueRange = 18f..38f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = PolishGold,
                                        activeTrackColor = PolishGold
                                    )
                                )
                            }
                        }
                    }

                    "ornament" -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item {
                                Text(
                                    text = if (isArabicFirst) "اختر إطار الزخرفة المناسب" else "Islamic decorative frame borders",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = PolishGold,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                            items(OrnamentStyle.values()) { orn ->
                                val isOrnActive = selectedOrnament == orn
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isOrnActive) Color(0xFF14241B) else PolishSurfaceBg)
                                        .border(
                                            1.dp, 
                                            if (isOrnActive) PolishGold else PolishWhite5, 
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable { onSelectOrnament(orn) }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = orn.unicodeMarker,
                                        fontSize = 24.sp,
                                        color = PolishGold
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = orn.displayName,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            // Motion transition choice
                            item {
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = if (isArabicFirst) "تأثير الانتقال بين الآيات" else "Lyrics transition visual flow",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = PolishGold,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                            items(TransitionEffect.values()) { trans ->
                                val isTransActive = selectedTransition == trans
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isTransActive) Color(0xFF14241B) else PolishSurfaceBg)
                                        .border(
                                            1.dp, 
                                            if (isTransActive) PolishGold else PolishWhite5, 
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable { onSelectTransition(trans) }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.OndemandVideo,
                                        contentDescription = "Visual Transition",
                                        tint = PolishGold
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            text = trans.displayName,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = trans.description,
                                            color = Color.LightGray.copy(alpha = 0.8f),
                                            style = MaterialTheme.typography.bodySmall,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Compile Trigger FAB
        Button(
            onClick = onProceedToExport,
            colors = ButtonDefaults.buttonColors(containerColor = PolishGold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp)
                .testTag("compile_and_export_button"),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MovieFilter,
                contentDescription = "Compile",
                tint = PolishBodyBg
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isArabicFirst) "تصيير وإنتاج الفيديو الاجتماعي" else "Compile & Export Video",
                color = PolishBodyBg,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
            )
        }
    }
}

// -------------------------------------------------------------
// SCREEN 3: VIDEO COMPILATION / RENDER PROGESS
// -------------------------------------------------------------
@Composable
fun ExportScreen(
    isArabicFirst: Boolean,
    selectedQuality: String,
    onSelectQuality: (String) -> Unit,
    renderProgress: Float,
    renderLog: String,
    onStartRender: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PolishBodyBg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (renderProgress == 0f) {
            Text(
                text = "۩ خيارات التصدير والجودة ۩",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = PolishGold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isArabicFirst) {
                    "اختر دقة وضوح الفيديو المناسب لمنصات التواصل الاجتماعي المفضلة"
                } else {
                    "Select rendering resolution for optimum playback details"
                },
                color = Color.LightGray.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Quality Choice buttons
            listOf(
                Triple("720p", "High Definition (HD)", "Perfect for fast sharing on WhatsApp & Telegram"),
                Triple("1080p", "Full HD (FHD) • Recommended", "Optimized crisp encoding for TikTok, Reels, YouTube"),
                Triple("4K", "Ultra HD (4K Quality)", "Maximum premium detail for professional high-density TV displays")
            ).forEach { item ->
                val isSelected = selectedQuality == item.first
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFF14241B) else PolishSurfaceBg)
                        .border(
                            1.dp, 
                            if (isSelected) PolishGold else PolishWhite5, 
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onSelectQuality(item.first) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelectQuality(item.first) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = PolishGold,
                            unselectedColor = Color.White.copy(alpha = 0.3f)
                        )
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = item.second,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = item.third,
                            color = Color.LightGray.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = onStartRender,
                colors = ButtonDefaults.buttonColors(containerColor = PolishGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("start_render_button"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = "Export icon",
                    tint = PolishBodyBg
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isArabicFirst) "ابدأ التصدير والترميز" else "Start Encoding & Export",
                    color = PolishBodyBg,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = onNavigateBack) {
                Text(
                    text = if (isArabicFirst) "رجوع للمعدل" else "Cancel and Return",
                    color = PolishGold,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            // Actively compilation/rendering progressive sequence
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(150.dp)) {
                CircularProgressIndicator(
                    progress = renderProgress,
                    modifier = Modifier.size(140.dp),
                    color = PolishGold,
                    strokeWidth = 6.dp,
                    trackColor = PolishWhite5
                )

                Text(
                    text = "${(renderProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = if (isArabicFirst) "جاري إنتاج ومعالجة الفيديو..." else "Compiling your Quran video files...",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Simulated rendering logs
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                colors = CardDefaults.cardColors(containerColor = PolishSurfaceBg),
                border = BorderStroke(1.dp, PolishWhite5),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = renderLog,
                        style = MaterialTheme.typography.bodySmall,
                        color = PolishGold,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SCREEN 4: EXPORTED SUCCESS & SHARING PORTAL
// -------------------------------------------------------------
@Composable
fun ExportedShareScreen(
    isArabicFirst: Boolean,
    surahName: String,
    englishName: String,
    aspectRatio: AspectRatioType,
    backgroundType: BackgroundType,
    quality: String,
    onBackToEditor: () -> Unit,
    onBackToHome: () -> Unit
) {
    var shareToastMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PolishBodyBg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = PolishGold,
            modifier = Modifier.size(72.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = if (isArabicFirst) "تم تصدير مقطع الفيديو بنجاح! 🎉" else "Video Exported Successfully! 🎉",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isArabicFirst) {
                "مقطعك جاهز الآن للمشاركة المباشرة والانتشار بنقاء $quality وبنسبة أبعاد ${aspectRatio.label}."
            } else {
                "Your stunning video file is ready to share in high-quality $quality at ${aspectRatio.label}."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = Color.LightGray.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Direct Sharing Options
        Text(
            text = if (isArabicFirst) "شارك التلاوة المباركة فوراً" else "Share the blessed recitation now",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = PolishGold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ShareButton(
                label = "YouTube",
                icon = Icons.Default.PlayArrow,
                color = Color(0xFFFF0000),
                onClick = {
                    shareToastMessage = if (isArabicFirst) "جاري الرفع على YouTube Shorts..." else "Uploading to YouTube Shorts..."
                }
            )

            ShareButton(
                label = "TikTok",
                icon = Icons.Default.MusicNote,
                color = Color(0xFF111111),
                onClick = {
                    shareToastMessage = if (isArabicFirst) "جاري رفع وتهيئة الملف على TikTok..." else "Uploading to TikTok mobile feed..."
                }
            )

            ShareButton(
                label = "Instagram",
                icon = Icons.Default.CameraAlt,
                color = Color(0xFFE1306C),
                onClick = {
                    shareToastMessage = if (isArabicFirst) "جاري إرسال الإطارات لـ Instagram Reels..." else "Sending file to Instagram Reels..."
                }
            )

            ShareButton(
                label = "WhatsApp",
                icon = Icons.Default.Send,
                color = Color(0xFF25D366),
                onClick = {
                    shareToastMessage = if (isArabicFirst) "جاري الإرسال المباشر لـ WhatsApp..." else "Sending video file to WhatsApp Status..."
                }
            )
        }

        if (shareToastMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14241B)),
                border = BorderStroke(1.dp, PolishGold.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = shareToastMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
            LaunchedEffect(shareToastMessage) {
                delay(3000)
                shareToastMessage = ""
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = onBackToHome,
            colors = ButtonDefaults.buttonColors(containerColor = PolishSurfaceBg),
            border = BorderStroke(1.dp, PolishGold.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("back_to_home_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (isArabicFirst) "البدء بمشروع فيديو جديد" else "Create a New Project",
                color = PolishGold,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(onClick = onBackToEditor) {
            Text(
                text = if (isArabicFirst) "العودة للتعديل والمظهر" else "Back to Customize Editor", 
                color = Color.LightGray.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// -------------------------------------------------------------
// HELPER COMPOSABLE STYLED SHARING BUTTON
// -------------------------------------------------------------
@Composable
fun ShareButton(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
            .testTag("share_target_${label.lowercase()}")
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

// -------------------------------------------------------------
// CUSTOM FLOWROW FOR DYNAMIC REFLOW WORD HIGHLIGHTING
// -------------------------------------------------------------
@Composable
fun FlowRowLayout(
    modifier: Modifier = Modifier,
    horizontalGap: Dp = 4.dp,
    verticalGap: Dp = 4.dp,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.layout.Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }

        val rows = mutableListOf<MutableList<androidx.compose.ui.layout.Placeable>>()
        var currentRow = mutableListOf<androidx.compose.ui.layout.Placeable>()
        var currentRowWidth = 0

        placeables.forEach { placeable ->
            if (currentRowWidth + placeable.width + horizontalGap.roundToPx() > constraints.maxWidth && currentRow.isNotEmpty()) {
                rows.add(currentRow)
                currentRow = mutableListOf()
                currentRowWidth = 0
            }
            currentRow.add(placeable)
            currentRowWidth += placeable.width + horizontalGap.roundToPx()
        }
        if (currentRow.isNotEmpty()) {
            rows.add(currentRow)
        }

        // Calculate total measurements
        val totalHeight = rows.sumOf { row -> row.maxOf { it.height } } + (rows.size - 1) * verticalGap.roundToPx()
        val totalWidth = constraints.maxWidth

        layout(totalWidth, totalHeight) {
            var currentY = 0
            rows.forEach { row ->
                val rowHeight = row.maxOf { it.height }
                val rowWidth = row.sumOf { it.width } + (row.size - 1) * horizontalGap.roundToPx()
                // Mirror layout direction for Arabic-first look
                var currentX = (totalWidth - rowWidth) / 2 // Centered placement of lines

                row.forEach { placeable ->
                    placeable.place(currentX, currentY + (rowHeight - placeable.height) / 2)
                    currentX += placeable.width + horizontalGap.roundToPx()
                }
                currentY += rowHeight + verticalGap.roundToPx()
            }
        }
    }
}

// -------------------------------------------------------------
// ATMOSPHERE ANIMATED EFFECTS OVERLAY
// -------------------------------------------------------------
@Composable
fun AtmosphereEffects(selectedBackground: BackgroundType, isPlaying: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "AtmosphereInfinite")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AtmospherePulse"
    )

    val dustFloatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -30f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "AtmosphereDust"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(if (isPlaying) pulseScale else 0.95f)
    ) {
        if (selectedBackground == BackgroundType.MOSQUE_STARRY) {
            // Draw subtle glowing dust particles
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = if (isPlaying) dustFloatY.dp else 0.dp)
                    .drawBehind {
                        drawCircle(color = Color(0xFFD4AF37).copy(alpha = 0.25f), radius = 5f, center = Offset(size.width * 0.25f, size.height * 0.4f))
                        drawCircle(color = Color(0xFFD4AF37).copy(alpha = 0.35f), radius = 8f, center = Offset(size.width * 0.75f, size.height * 0.25f))
                        drawCircle(color = Color(0xFFFAF6EE).copy(alpha = 0.20f), radius = 4f, center = Offset(size.width * 0.50f, size.height * 0.75f))
                        drawCircle(color = Color(0xFFFAF6EE).copy(alpha = 0.40f), radius = 6f, center = Offset(size.width * 0.85f, size.height * 0.65f))
                    }
            )
        } else if (selectedBackground == BackgroundType.GOLDEN_ARABESQUE) {
            // Draw luxury intersecting background lines representing arabesque star
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        val stroke = Stroke(width = 1f)
                        val sizeRef = size.minDimension * 0.4f
                        val center = Offset(size.width / 2f, size.height / 2f)
                        // Star shape pattern line
                        val path = Path().apply {
                            for (i in 0 until 8) {
                                val angle = i * Math.PI / 4.0
                                val x = center.x + sizeRef * Math.cos(angle).toFloat()
                                val y = center.y + sizeRef * Math.sin(angle).toFloat()
                                if (i == 0) moveTo(x, y) else lineTo(x, y)
                            }
                            close()
                        }
                        drawPath(path, Color(0xFFD4AF37).copy(alpha = 0.15f), style = stroke)
                    }
            )
        }
    }
}

// -------------------------------------------------------------
// VISUAL FRAME OVERLAY DRAWER
// -------------------------------------------------------------
@Composable
fun OrnamentFrame(style: OrnamentStyle, arabicMarker: String, isPortrait: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        when (style) {
            OrnamentStyle.MIHRAB -> {
                // Draws thin arched Mihrab on the sides
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            val p = Path().apply {
                                moveTo(0f, size.height)
                                lineTo(0f, size.height * 0.25f)
                                cubicTo(
                                    size.width * 0.05f, size.height * 0.05f,
                                    size.width * 0.95f, size.height * 0.05f,
                                    size.width, size.height * 0.25f
                                )
                                lineTo(size.width, size.height)
                            }
                            drawPath(
                                path = p,
                                color = Color(0xFFD4AF37).copy(alpha = 0.25f),
                                style = Stroke(width = 3f)
                            )
                        }
                )
            }

            OrnamentStyle.ELEGANT_CORNERS -> {
                // Standard visual corners markup
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "⚜", color = Color(0xFFD4AF37).copy(alpha = 0.7f), fontSize = 24.sp)
                        Text(text = "⚜", color = Color(0xFFD4AF37).copy(alpha = 0.7f), fontSize = 24.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "⚜", color = Color(0xFFD4AF37).copy(alpha = 0.7f), fontSize = 24.sp)
                        Text(text = "⚜", color = Color(0xFFD4AF37).copy(alpha = 0.7f), fontSize = 24.sp)
                    }
                }
            }

            OrnamentStyle.ARABESQUE_BORDER -> {
                // Classic outer bounding card layout border line
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(1.dp, Color(0xFFD4AF37).copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                        .padding(4.dp)
                        .border(1.dp, Color(0xFFD4AF37).copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                )
            }

            OrnamentStyle.SIMPLE_MINIMAL -> {
                // Tiny elegant bottom accent dot
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Text(text = "• ۩ •", color = Color(0xFFD4AF37).copy(alpha = 0.5f), fontSize = 16.sp)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB ITEM ROW COMPOSABLE
// -------------------------------------------------------------
@Composable
fun RowScope.TabHeaderItem(
    title: String,
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .padding(vertical = 8.dp, horizontal = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isActive) Color(0xFF14241B) else Color(0x06FFFFFF))
            .border(
                1.dp,
                if (isActive) PolishGold.copy(alpha = 0.5f) else PolishWhite5,
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isActive) PolishGold else Color.White.copy(alpha = 0.4f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isActive) Color.White else Color.White.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }
    }
}
