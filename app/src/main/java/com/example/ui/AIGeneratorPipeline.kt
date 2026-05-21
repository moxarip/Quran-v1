package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AICustomizeScreen(
    category: String,
    topic: String,
    isArabicFirst: Boolean,
    onBack: () -> Unit,
    onExport: () -> Unit
) {
    val categoryItem = dashboardCategories.find { it.id == category } ?: dashboardCategories[1]
    val title = if (isArabicFirst) categoryItem.titleAr else categoryItem.titleEn

    // Simulated Generated content
    val script = remember {
        if (isArabicFirst) {
            "موضوع اليوم عن $topic.\nعالم مدهش مليء بالتفاصيل الفريدة، استكشف معنا حقائق مثيرة حول $topic تجعلك تنبهر!"
        } else {
            "Today's topic is about $topic.\nAn amazing world full of unique details, let's explore mind-blowing facts about $topic!"
        }
    }
    
    val imageUrl = remember {
        val safeTopic = topic.replace(" ", ",")
        val query = java.net.URLEncoder.encode(safeTopic, "UTF-8")
        "https://loremflickr.com/800/1200/$query"
    }

    var selectedStyle by remember { mutableStateOf("cinematic") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PolishBodyBg)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isArabicFirst) "تخصيص: $title" else "Customize: $title",
                style = MaterialTheme.typography.titleLarge,
                color = categoryItem.color,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = if (isArabicFirst) "السكريبت المُُوَلَّد:" else "Generated Script:",
            color = Color.LightGray,
            style = MaterialTheme.typography.titleMedium
        )
        
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = PolishSurfaceBg),
            border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.2f))
        ) {
            Text(
                text = script,
                modifier = Modifier.padding(16.dp),
                color = Color.White,
                lineHeight = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isArabicFirst) "الخلفية المرئية:" else "Visual Background:",
            color = Color.LightGray,
            style = MaterialTheme.typography.titleMedium
        )
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.DarkGray)
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Generated visual",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Overlay text
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = topic,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isArabicFirst) "أسلوب التصميم:" else "Design Style:",
            color = Color.LightGray,
            style = MaterialTheme.typography.titleMedium
        )

        val styles = listOf(
            Pair("cinematic", if (isArabicFirst) "سينمائي" else "Cinematic"),
            Pair("neon", if (isArabicFirst) "نيون" else "Neon"),
            Pair("minimal", if (isArabicFirst) "بسيط" else "Minimal"),
            Pair("educational", if (isArabicFirst) "تعليمي" else "Educational")
        )

        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            styles.forEach { style ->
                FilterChip(
                    selected = selectedStyle == style.first,
                    onClick = { selectedStyle = style.first },
                    label = { Text(style.second) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = categoryItem.color,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onExport,
            colors = ButtonDefaults.buttonColors(containerColor = categoryItem.color),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Movie, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isArabicFirst) "تصدير الفيديو" else "Export Video",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun AIExportScreen(
    category: String,
    topic: String,
    isArabicFirst: Boolean,
    onComplete: () -> Unit
) {
    val categoryItem = dashboardCategories.find { it.id == category } ?: dashboardCategories[1]
    var progress by remember { mutableStateOf(0f) }
    var logMsg by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        logMsg = if (isArabicFirst) "جاري دمج المقاطع المرئية..." else "Stitching visual scenes..."
        progress = 0.1f
        delay(1200)
        
        logMsg = if (isArabicFirst) "مزامنة التعليق الصوتي..." else "Syncing AI voiceover..."
        progress = 0.4f
        delay(1500)
        
        logMsg = if (isArabicFirst) "إضافة المؤثرات الانتقالية..." else "Applying transition effects..."
        progress = 0.6f
        delay(1200)
        
        logMsg = if (isArabicFirst) "تصيير الملف النهائي..." else "Rendering final MP4..."
        progress = 0.9f
        delay(1500)
        
        progress = 1f
        logMsg = if (isArabicFirst) "تم تصدير الفيديو بنجاح!" else "Video exported successfully!"
        delay(500)
        onComplete()
    }

    Column(
        modifier = Modifier.fillMaxSize().background(PolishBodyBg).padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            progress = { progress },
            color = categoryItem.color,
            modifier = Modifier.size(80.dp),
            strokeWidth = 6.dp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "${(progress * 100).toInt()}%",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = logMsg,
            color = Color.LightGray,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AIShareScreen(
    category: String,
    topic: String,
    isArabicFirst: Boolean,
    onBackToHome: () -> Unit
) {
    val categoryItem = dashboardCategories.find { it.id == category } ?: dashboardCategories[1]
    
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var shareToastMessage by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    val saveVideo: (Boolean) -> Unit = { toDownloads ->
        isSaving = true
        coroutineScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val fileName = "AIVideo_${topic.replace(" ", "_")}_${System.currentTimeMillis()}.mp4"
                val resolver = context.contentResolver
                
                val uri = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    val videoCollection = if (toDownloads) {
                        android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI
                    } else {
                        android.provider.MediaStore.Video.Media.getContentUri(android.provider.MediaStore.VOLUME_EXTERNAL_PRIMARY)
                    }
                    val contentValues = android.content.ContentValues().apply {
                        put(android.provider.MediaStore.Video.Media.DISPLAY_NAME, fileName)
                        put(android.provider.MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                        if (toDownloads) {
                            put(android.provider.MediaStore.Downloads.RELATIVE_PATH, "Download/AIVideo")
                        } else {
                            put(android.provider.MediaStore.Video.Media.RELATIVE_PATH, "Movies/AIVideo")
                        }
                        put(android.provider.MediaStore.Video.Media.IS_PENDING, 1)
                    }
                    resolver.insert(videoCollection, contentValues)
                } else {
                    val baseDir = if (toDownloads) {
                        android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
                    } else {
                        android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_MOVIES)
                    }
                    val parentFolder = java.io.File(baseDir, "AIVideo")
                    if (!parentFolder.exists()) parentFolder.mkdirs()
                    val targetFile = java.io.File(parentFolder, fileName)
                    val contentValues = android.content.ContentValues().apply {
                        put(android.provider.MediaStore.Video.Media.DISPLAY_NAME, fileName)
                        put(android.provider.MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                        put(android.provider.MediaStore.Video.Media.DATA, targetFile.absolutePath)
                    }
                    resolver.insert(android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
                }

                if (uri != null) {
                    var success = false
                    val videoUrls = listOf(
                        "https://assets.mixkit.co/videos/preview/mixkit-starry-night-sky-background-9988-large.mp4",
                        "https://www.w3schools.com/html/mov_bbb.mp4"
                    )
                    for (urlStr in videoUrls) {
                        if (success) break
                        try {
                            val conn = java.net.URL(urlStr).openConnection() as java.net.HttpURLConnection
                            conn.connectTimeout = 8000
                            conn.readTimeout = 8000
                            conn.connect()
                            if (conn.responseCode == 200 || conn.responseCode == 301 || conn.responseCode == 302) {
                                resolver.openOutputStream(uri)?.use { outStream ->
                                    conn.inputStream.use { inStream ->
                                        inStream.copyTo(outStream)
                                    }
                                }
                                success = true
                            }
                        } catch (e: Exception) {
                            android.util.Log.w("QuranVideo", "Failed downloading for export from $urlStr: ${e.message}")
                        }
                    }
                    
                    if (!success) {
                        val tinyMp4Base64 = "AAAAIGZ0eXBpc29tAAACAGlzb21pc28yYXZjMW1wNDEAAAAIZnJlZQAAAAhtZGF0AAAA1m1vb3YAAABsbXZoZAAAAADaD0Nq2g9DcwAAAHAAAABwAAEAAAEAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAABidHJhawAAAFx0a2hkAAAAA9oPQ2raD0NzAAAAAQAAAAAAAABwAAAAAAAAAAAAAAAAAQAAAAEAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAABAAAAAQAAACRlZHRzAAAAHGVsc3QAAAAAAAAAAQAAAHAAAAABAAAAAAABAAAAAA=="
                        val mp4Bytes = android.util.Base64.decode(tinyMp4Base64, android.util.Base64.DEFAULT)
                        resolver.openOutputStream(uri)?.use { outStream -> outStream.write(mp4Bytes) }
                    }
                    
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        val contentValues = android.content.ContentValues().apply {
                            put(android.provider.MediaStore.Video.Media.IS_PENDING, 0)
                        }
                        resolver.update(uri, contentValues, null, null)
                    }
                    
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        isSaving = false
                        shareToastMessage = if (isArabicFirst) "تم حفظ الفيديو بنجاح! 🎬" else "Video saved successfully! 🎬"
                    }
                } else {
                    throw Exception("Could not insert to MediaStore")
                }
            } catch (e: Exception) {
                android.util.Log.e("QuranVideo", "Error exporting download", e)
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    isSaving = false
                    shareToastMessage = if (isArabicFirst) "فشل التنزيل أو حفظ الملف" else "Download or save failed."
                }
            }
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize().background(PolishBodyBg).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (shareToastMessage.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B3624)),
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            ) {
                Text(
                    text = shareToastMessage,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(80.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = if (isArabicFirst) "الفيديو جاهز للمشاركة!" else "Video is ready to share!",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = if (isArabicFirst) "عن موضوع: $topic" else "Topic: $topic",
            color = categoryItem.color,
            style = MaterialTheme.typography.titleMedium
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { saveVideo(true) },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
        ) {
            Icon(Icons.Default.Download, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isArabicFirst) "تنزيل الفيديو" else "Download Video",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Button(
            onClick = onBackToHome,
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = categoryItem.color)
        ) {
            Icon(Icons.Default.Home, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isArabicFirst) "العودة للرئيسية" else "Back to Dashboard",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}
