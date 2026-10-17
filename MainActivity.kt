package com.example.urdiaistudio

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.core.content.FileProvider
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

class MainActivity : ComponentActivity() {
 private var videoUris by mutableStateOf<List<Uri>>(emptyList())
 private var audioUri by mutableStateOf<Uri?>(null)
 private val videoPicker = registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
  uris.forEach { uri ->
   try { contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: Exception) { }
  }
  videoUris = uris
 }
 private val audioPicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
  if (uri != null) {
   try { contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: Exception) { }
  }
  audioUri = uri
 }
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  setContent { StudioScreen(videoUris, audioUri, { videoPicker.launch(arrayOf("video/*")) }, { audioPicker.launch(arrayOf("audio/*")) }) }
 }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioScreen(videoUris: List<Uri>, audioUri: Uri?, pickVideos: () -> Unit, pickAudio: () -> Unit) {
 val context = LocalContext.current
 val scope = rememberCoroutineScope()
 var prompt by remember { mutableStateOf("A brave village girl discovers a hidden city beneath Lahore.") }
 var language by remember { mutableStateOf("English") }
 var minutes by remember { mutableFloatStateOf(5f) }
 var base by remember { mutableStateOf("https://api.openai.com/v1") }
 var key by remember { mutableStateOf("") }
 var model by remember { mutableStateOf("gpt-4o-mini") }
 var output by remember { mutableStateOf("") }
 var status by remember { mutableStateOf("آف لائن کہانی کا نظام تیار ہے") }
 var busy by remember { mutableStateOf(false) }
 var exportPath by remember { mutableStateOf("") }
 var ttsInstance by remember { mutableStateOf<TextToSpeech?>(null) }
 var ttsReady by remember { mutableStateOf(false) }

 DisposableEffect(context) {
  var disposed = false
  val engine = TextToSpeech(context) { result ->
   if (!disposed) ttsReady = result == TextToSpeech.SUCCESS
  }
  ttsInstance = engine
  onDispose {
   disposed = true
   engine.stop()
   engine.shutdown()
   ttsInstance = null
   ttsReady = false
  }
 }

 Scaffold(topBar={ TopAppBar(title={ Text("Urdu AI Studio") }) }) { padding ->
  Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement=Arrangement.spacedBy(10.dp)) {
   Text("کہانی سے فلم تک",style=MaterialTheme.typography.headlineSmall)
   Text("آن لائن + آف لائن • ہدف معیار 720p",style=MaterialTheme.typography.bodyMedium)
   OutlinedTextField(prompt,{prompt=it},label={Text("کہانی کا خیال / Story prompt")},modifier=Modifier.fillMaxWidth(),minLines=3)
   Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
    FilterChip(selected=language=="English",onClick={language="English"},label={Text("English")})
    FilterChip(selected=language=="Urdu",onClick={language="Urdu"},label={Text("اردو")})
   }
   Text("مطلوبہ دورانیہ: ${minutes.toInt()} منٹ")
   Slider(value=minutes,onValueChange={minutes=it},valueRange=1f..30f,steps=28)
   Button(onClick={output=StoryEngine.generate(prompt,language,minutes.toInt());status="آف لائن اسکرپٹ تیار ہے"}) { Text("آف لائن کہانی اور مناظر بنائیں") }
   HorizontalDivider()
   Text("آن لائن AI اسکرپٹ",style=MaterialTheme.typography.titleMedium)
   OutlinedTextField(base,{base=it},label={Text("API base URL")},modifier=Modifier.fillMaxWidth(),singleLine=true)
   OutlinedTextField(model,{model=it},label={Text("ماڈل کا نام")},modifier=Modifier.fillMaxWidth(),singleLine=true)
   OutlinedTextField(key,{key=it},label={Text("API key (صرف موجودہ سیشن)")},modifier=Modifier.fillMaxWidth(),singleLine=true)
   Button(enabled=key.isNotBlank()&&!busy,onClick={
    busy=true; status="آن لائن AI سے رابطہ ہو رہا ہے…"
    scope.launch { try { output=AiClient.generate(base,key,model,prompt,language,minutes.toInt());status="آن لائن اسکرپٹ تیار ہے" } catch(e:Exception) {status="درخواست ناکام: ${e.message}"};busy=false }
   }) { Text(if(busy) "کام جاری ہے…" else "حقیقی AI سے اسکرپٹ بنائیں") }
   HorizontalDivider()
   Text("ویڈیو ایڈیٹنگ اور MP4 ایکسپورٹ",style=MaterialTheme.typography.titleMedium)
   OutlinedButton(onClick=pickVideos,modifier=Modifier.fillMaxWidth()) { Text("ایک یا زیادہ ویڈیو کلپس منتخب کریں (${videoUris.size})") }
   OutlinedButton(onClick=pickAudio,modifier=Modifier.fillMaxWidth()) { Text(if(audioUri==null) "پس منظر کی آڈیو منتخب کریں" else "آڈیو منتخب ہے") }
   Button(enabled=videoUris.isNotEmpty()&&!busy,onClick={
    busy=true; status="720p MP4 تیار ہو رہی ہے…"; exportPath=""
    val outFile=File(context.cacheDir,"urdu_ai_film_${System.currentTimeMillis()}.mp4")
    MediaExporter.export(context,videoUris,audioUri,outFile,
     onComplete={ path -> exportPath=path;status="MP4 ایکسپورٹ مکمل";busy=false },
     onError={ err -> status="ایکسپورٹ ناکام: $err";busy=false })
   },modifier=Modifier.fillMaxWidth()) { Text(if(busy) "ایکسپورٹ جاری ہے…" else "کلپس جوڑیں اور MP4 بنائیں (720p ہدف)") }
   if(exportPath.isNotBlank()) {
    Text("فائل: $exportPath",style=MaterialTheme.typography.bodySmall)
    Button(onClick={ val share=Intent(Intent.ACTION_SEND).setType("video/mp4").putExtra(Intent.EXTRA_STREAM,FileProvider.getUriForFile(context, context.packageName+".fileprovider",File(exportPath))).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); context.startActivity(Intent.createChooser(share,"MP4 شیئر کریں")) }) { Text("ویڈیو شیئر کریں") }
   }
   Button(enabled=ttsReady,onClick={
    val text = output.ifBlank { if (language == "Urdu") "یہ آپ کی کہانی کا آغاز ہے۔" else "This is the beginning of your story." }.take(3000)
    val locale = if (language == "Urdu") Locale("ur", "PK") else Locale.US
    ttsInstance?.let { engine ->
     val languageResult = engine.setLanguage(locale)
     if (languageResult == TextToSpeech.LANG_MISSING_DATA || languageResult == TextToSpeech.LANG_NOT_SUPPORTED) {
      status = "اس فون کے TTS انجن میں منتخب زبان دستیاب نہیں ہے"
     } else {
      engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "story-preview")
      status = "فون کی آواز چل رہی ہے"
     }
    }
   }) { Text("فون پر وائس پری ویو") }
   Text(status,color=MaterialTheme.colorScheme.primary)
   if(output.isNotBlank()) {
    OutlinedButton(onClick={ context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT,output),"اسکرپٹ شیئر کریں")) }) { Text("اسکرپٹ شیئر کریں") }
    Surface(color=MaterialTheme.colorScheme.surfaceVariant,shape=MaterialTheme.shapes.medium) { Text(output,Modifier.fillMaxWidth().padding(12.dp)) }
   }
   Text("نوٹ: کلپس جوڑنے اور MP4 ایکسپورٹ کے لیے مقامی Media3 رینڈرنگ شامل ہے۔ خودکار AI ویڈیو کلپس بنانے کے لیے الگ ویڈیو جنریشن فراہم کنندہ/API ابھی درکار ہے۔",style=MaterialTheme.typography.bodySmall)
  }
 }
}
