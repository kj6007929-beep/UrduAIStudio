package com.example.urdiaistudio

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.effect.Presentation
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.EditedMediaItemSequence
import androidx.media3.transformer.Effects
import androidx.media3.transformer.Transformer
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.ExportException
import androidx.media3.common.util.UnstableApi
import java.io.File

object MediaExporter {
 @OptIn(UnstableApi::class)
 fun export(
  context: Context,
  videoUris: List<Uri>,
  audioUri: Uri?,
  output: File,
  onComplete: (String) -> Unit,
  onError: (String) -> Unit
 ) {
  if (videoUris.isEmpty()) { onError("کم از کم ایک ویڈیو منتخب کریں"); return }
  val mainHandler = Handler(Looper.getMainLooper())
  try {
   val videoItems = videoUris.map { uri ->
    val effects = Effects(emptyList(), listOf(Presentation.createForHeight(720)))
    EditedMediaItem.Builder(MediaItem.fromUri(uri)).setEffects(effects).build()
   }
   val videoSequence = EditedMediaItemSequence.withAudioAndVideoFrom(videoItems)
   val sequences = mutableListOf(videoSequence)
   if (audioUri != null) {
    val music = EditedMediaItem.Builder(MediaItem.fromUri(audioUri)).build()
    // Use the stable factory method here; audio sequence looping APIs vary by
    // Media3 version. This keeps compilation independent of Builder-only APIs.
    val audioSequence = EditedMediaItemSequence.withAudioFrom(listOf(music))
    sequences.add(audioSequence)
   }
   val composition = Composition.Builder(sequences).build()
   val transformer = Transformer.Builder(context)
    .setVideoMimeType(MimeTypes.VIDEO_H264)
    .setAudioMimeType(MimeTypes.AUDIO_AAC)
    .addListener(object : Transformer.Listener {
     override fun onCompleted(composition: Composition, exportResult: ExportResult) {
      mainHandler.post { onComplete(output.absolutePath) }
     }
     override fun onError(composition: Composition, exportResult: ExportResult, exportException: ExportException) {
      mainHandler.post { onError(exportException.message ?: "نامعلوم رینڈرنگ خرابی") }
     }
    }).build()
   output.parentFile?.mkdirs()
   transformer.start(composition, output.absolutePath)
  } catch (e: Exception) {
   onError(e.message ?: "ایکسپورٹ شروع نہیں ہو سکی")
  }
 }
}
