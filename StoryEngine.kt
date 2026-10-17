package com.example.urdiaistudio

object StoryEngine {
 fun generate(prompt: String, language: String, minutes: Int): String {
  val count = (minutes.coerceIn(1,30) * 3).coerceIn(3,90)
  val urdu = language == "Urdu"
  return buildString {
   appendLine(if (urdu) "عنوان: آپ کی کہانی" else "TITLE: Your Story")
   appendLine((if (urdu) "خیال: " else "Premise: ") + prompt)
   appendLine((if (urdu) "دورانیہ: " else "Target runtime: ") + "$minutes minutes")
   appendLine((if (urdu) "مناظر: " else "Scenes: ") + count)
   for (i in 1..count) {
    appendLine("\n${if (urdu) "منظر" else "SCENE"} $i")
    appendLine(if (urdu) "مقام: مرکزی مقام، شام" else "Location: Primary location, evening")
    appendLine(if (urdu) "ایکشن: کردار اپنے مقصد کے قریب پہنچتا ہے، ایک نئی رکاوٹ سامنے آتی ہے۔" else "Action: The protagonist moves toward the goal, but a new obstacle appears.")
    appendLine(if (urdu) "مکالمہ: ہمیں آگے بڑھنا ہوگا، چاہے کچھ بھی ہو۔" else "Dialogue: We have to keep going, whatever happens.")
    appendLine(if (urdu) "آواز: ہلکی موسیقی اور ماحول کی آوازیں" else "Audio: Ambient score and location sound")
   }
   appendLine(if (urdu) "\nیہ آف لائن خاکہ ہے؛ مکمل ویڈیو کے لیے AI میڈیا اور رینڈرنگ سروس درکار ہے۔" else "\nOffline screenplay scaffold. Actual video generation requires configured media services and rendering.")
  }
 }
}
