package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

val PolishBodyBg = Color(0xFF0F172A) // Tailwind Slate 900
val PolishSurfaceBg = Color(0xFF1E293B) // Tailwind Slate 800
val PolishGold = Color(0xFFEAB308) // Distinct Gold

data class CategoryItem(val id: String, val titleAr: String, val titleEn: String, val icon: ImageVector, val color: Color, val descAr: String, val descEn: String)

val dashboardCategories = listOf(
    CategoryItem("quran", "القرآن الكريم", "Quran Recitations", Icons.Default.MenuBook, Color(0xFFD4AF37), "تصميم فيديوهات تلاوة", "Synchronized verse videos"),
    CategoryItem("documentary", "وثائقيات", "Documentaries", Icons.Default.Public, Color(0xFF4CAF50), "وثائقيات بالذكاء الاصطناعي", "AI generated short doc videos"),
    CategoryItem("wisdom", "حِكم ومواعظ", "Wisdom & Quotes", Icons.Default.FormatQuote, Color(0xFF9C27B0), "نصائح وإلهامات", "Inspirational quote videos"),
    CategoryItem("facts", "هل تعلم؟", "Amazing Facts", Icons.Default.TipsAndUpdates, Color(0xFFFF9800), "حقائق علمية وتاريخية", "Fun facts for social media"),
    CategoryItem("learning", "تعلم كل يوم", "Learn Something", Icons.Default.School, Color(0xFF2196F3), "دروس سريعة مفيدة", "Daily mini lesson videos")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(
    isArabicFirst: Boolean,
    onToggleLan: () -> Unit,
    onSelectCategory: (String) -> Unit
) {
    var animateEntrance by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        animateEntrance = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PolishBodyBg)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isArabicFirst) "استوديو الإبداع" else "Creation Studio",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onToggleLan) {
                Icon(
                    imageVector = Icons.Default.Translate,
                    contentDescription = "Translate",
                    tint = PolishGold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isArabicFirst) "ماذا تريد أن تصنع اليوم؟" else "What would you like to create today?",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.LightGray
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            items(dashboardCategories) { cat ->
                val scale by animateFloatAsState(
                    targetValue = if (animateEntrance) 1f else 0.8f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                    label = "scale"
                )
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(scale)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onSelectCategory(cat.id) },
                    colors = CardDefaults.cardColors(containerColor = PolishSurfaceBg),
                    border = BorderStroke(1.dp, cat.color.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(cat.color.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = cat.icon,
                                contentDescription = null,
                                tint = cat.color,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isArabicFirst) cat.titleAr else cat.titleEn,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isArabicFirst) cat.descAr else cat.descEn,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.LightGray
                            )
                        }
                        
                        Icon(
                            imageVector = if (isArabicFirst) Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.ArrowForward,
                            contentDescription = "Proceed",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIGeneratorScreen(
    category: String,
    isArabicFirst: Boolean,
    onBack: () -> Unit,
    onGenerate: (String) -> Unit
) {
    val categoryItem = dashboardCategories.find { it.id == category } ?: dashboardCategories[1]
    var topicInput by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PolishBodyBg)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isArabicFirst) categoryItem.titleAr else categoryItem.titleEn,
                style = MaterialTheme.typography.titleLarge,
                color = categoryItem.color,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = if (isArabicFirst) "أدخل موضوع الفيديو" else "Enter the video topic",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = topicInput,
            onValueChange = { topicInput = it },
            placeholder = { 
                Text(
                    text = if (isArabicFirst) "مثال: الفضاء الخارجي، النمل، الصبر..." else "e.g. Outer space, Ants, Patience...",
                    color = Color.Gray
                )
            },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = categoryItem.color,
                unfocusedBorderColor = categoryItem.color.copy(alpha = 0.5f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = PolishSurfaceBg,
                unfocusedContainerColor = PolishSurfaceBg
            ),
            shape = RoundedCornerShape(12.dp),
            textStyle = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            colors = CardDefaults.cardColors(containerColor = categoryItem.color.copy(alpha = 0.1f)),
            border = BorderStroke(1.dp, categoryItem.color.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = categoryItem.color)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isArabicFirst) 
                        "سيقوم الذكاء الاصطناعي بكتابة السكريبت، وتوليد الصوت، واختيار الصور المناسبة تلقائياً." 
                    else 
                        "AI will automatically write the script, generate voiceover, and match suitable images.",
                    color = Color.LightGray,
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = { 
                val topic = if (topicInput.isNotBlank()) topicInput else (if (isArabicFirst) categoryItem.titleAr else categoryItem.titleEn)
                onGenerate(topic) 
            },
            colors = ButtonDefaults.buttonColors(containerColor = categoryItem.color),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (isArabicFirst) "توليد الفيديو بالذكاء الاصطناعي" else "Generate Video with AI",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}
