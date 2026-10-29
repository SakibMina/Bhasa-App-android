package com.bhasha.dictionary

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { BhashaApp() }
    }
}


@Composable
fun BhashaApp() {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    var direction by remember { mutableStateOf("English → বাংলা") }
    var result by remember { mutableStateOf<DictionaryEntry?>(null) }
    var popupMessage by remember { mutableStateOf("") }
    val teal = Color(0xFF146C5A)

    MaterialTheme(colorScheme = lightColorScheme(primary = teal, secondary = Color(0xFFEBB65A))) {
        Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF5F8F6)) {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
                Text("Bhasha", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = teal)
                Text("Bangla · English Dictionary", color = Color(0xFF5F6D68), fontSize = 14.sp)
                Spacer(Modifier.height(22.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("English → বাংলা", "বাংলা → English").forEach { option ->
                        FilterChip(selected = direction == option, onClick = { direction = option; result = null }, label = { Text(option) })
                    }
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it; result = null },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search a word… / শব্দ লিখুন") },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp)
                )
                Spacer(Modifier.height(10.dp))
                Button(onClick = { result = Dictionary.lookup(query, direction) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Find meaning  ·  অর্থ খুঁজুন")
                }
                Spacer(Modifier.height(20.dp))
                result?.let { entry ->
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                        Column(Modifier.padding(18.dp)) {
                            Text(entry.word, fontSize = 25.sp, fontWeight = FontWeight.Bold, color = teal)
                            Text(entry.partOfSpeech, color = Color.Gray, fontSize = 13.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(entry.meaning, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                            Spacer(Modifier.height(10.dp))
                            Text("Example", fontWeight = FontWeight.Bold)
                            Text(entry.example)
                            Text(entry.exampleBangla, color = teal)
                        }
                    }
                }
                if (result == null && query.isNotBlank()) {
                    Text("Search to see available dictionary entries.", color = Color.Gray)
                }
                Spacer(Modifier.height(22.dp))
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE5F2EC))) {
                    Column(Modifier.padding(18.dp)) {
                        Text("Floating Dictionary", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = teal)
                        Spacer(Modifier.height(6.dp))
                        Text("Copy a word in another app, then tap the Bhasha floating bubble to look it up.")
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = {
                            if (!Settings.canDrawOverlays(context)) {
                                context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}")))
                            } else {
                                context.startService(Intent(context, FloatingDictionaryService::class.java))
                                popupMessage = "Floating bubble started. Copy text in another app, then tap the bubble."
                            }
                        }) { Text("Enable floating bubble") }
                        if (popupMessage.isNotBlank()) Text(popupMessage, color = teal, fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.height(18.dp))
                Text("MVP · Offline sample dictionary", fontSize = 12.sp, color = Color.Gray)
                Text("Sentence translation and a larger dictionary dataset can be connected in the next build.", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

data class DictionaryEntry(val word: String, val partOfSpeech: String, val meaning: String, val example: String, val exampleBangla: String)

object Dictionary {
    private val entries = listOf(
        DictionaryEntry("abundant", "adjective", "প্রচুর; পর্যাপ্ত", "The region has abundant rainfall.", "এ অঞ্চলে প্রচুর বৃষ্টিপাত হয়।"),
        DictionaryEntry("account", "noun / verb", "হিসাব; বিবরণ; বিবেচনা করা", "Take the cost into account.", "খরচটি বিবেচনায় নাও।"),
        DictionaryEntry("achieve", "verb", "অর্জন করা; সফল হওয়া", "She worked hard to achieve her goal.", "সে তার লক্ষ্য অর্জনের জন্য কঠোর পরিশ্রম করেছে।"),
        DictionaryEntry("beautiful", "adjective", "সুন্দর; মনোরম", "It is a beautiful day.", "আজকের দিনটি সুন্দর।"),
        DictionaryEntry("book", "noun / verb", "বই; বুক করা", "I read a book every day.", "আমি প্রতিদিন একটি বই পড়ি।"),
        DictionaryEntry("brave", "adjective", "সাহসী; নির্ভীক", "The brave child helped the dog.", "সাহসী শিশুটি কুকুরটিকে সাহায্য করেছিল।"),
        DictionaryEntry("consider", "verb", "বিবেচনা করা; মনে করা", "Please consider my suggestion.", "দয়া করে আমার পরামর্শটি বিবেচনা করুন।"),
        DictionaryEntry("difficult", "adjective", "কঠিন; দুরূহ", "This question is difficult.", "এই প্রশ্নটি কঠিন।"),
        DictionaryEntry("eventually", "adverb", "অবশেষে; শেষ পর্যন্ত", "She eventually found the answer.", "সে অবশেষে উত্তরটি খুঁজে পেল।"),
        DictionaryEntry("freedom", "noun", "স্বাধীনতা; মুক্তি", "Everyone deserves freedom.", "প্রত্যেকের স্বাধীনতা পাওয়ার অধিকার আছে।"),
        DictionaryEntry("improve", "verb", "উন্নতি করা; উন্নত করা", "Reading can improve your English.", "পড়াশোনা আপনার ইংরেজির উন্নতি করতে পারে।"),
        DictionaryEntry("overcome", "verb", "জয় করা; কাটিয়ে ওঠা", "He overcame his fear.", "সে তার ভয় কাটিয়ে উঠেছিল।"),
        DictionaryEntry("run", "verb / noun", "দৌড়ানো; চালানো; পরিচালনা করা", "She runs a small business.", "সে একটি ছোট ব্যবসা পরিচালনা করে।"),
        DictionaryEntry("take into account", "phrase", "বিবেচনায় নেওয়া", "Take the weather into account.", "আবহাওয়াটি বিবেচনায় নাও।"),
        DictionaryEntry("উন্নতি", "noun", "improvement; progress", "She made good progress.", "সে ভালো উন্নতি করেছে।"),
        DictionaryEntry("সাহসী", "adjective", "brave; courageous", "He is a brave person.", "সে একজন সাহসী মানুষ।")
    )
    fun lookup(query: String, direction: String): DictionaryEntry? {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return null
        return entries.firstOrNull {
            if (direction.startsWith("English")) it.word.lowercase() == q
            else it.meaning.lowercase().split(";", ",").any { meaning -> meaning.trim() == q }
                || it.word.lowercase() == q
        } ?: entries.firstOrNull { it.word.lowercase().startsWith(q) }
    }
}
