# Urdu AI Studio — hybrid Android source MVP

یہ Android Studio/Kotlin پروجیکٹ ہے (APK اس پیکج میں شامل نہیں، کیونکہ اس ماحول میں Android SDK/Gradle اور build download network دستیاب نہیں تھا؛ اس لیے compile یا device test کی تصدیق نہیں کی جا سکی)۔ اس میں آف لائن اردو/انگریزی اسکرین پلے خاکہ، OpenAI-compatible text API adapter، مقامی آڈیو انتخاب، فون TTS preview، اور AndroidX Media3 Transformer کے ذریعے منتخب ویڈیو کلپس کو ترتیب سے جوڑ کر MP4 ایکسپورٹ کرنے کی بنیادی سہولت شامل ہے۔ ویڈیو ایکسپورٹ میں 720p ہدف resolution effect اور optional looping background audio sequence شامل ہے۔

**یہ ابھی مکمل خودکار AI فلم ساز نہیں ہے۔** AI سے نئے ویڈیو مناظر/کردار بنانے کے لیے کوئی مخصوص ویڈیو فراہم کنندہ/API ابھی مربوط نہیں۔ یہ ایپ آپ کی دی ہوئی ویڈیو کلپس کو جوڑ سکتی ہے؛ یہ خود سے AI فوٹیج تخلیق نہیں کرتی۔ حقیقی ڈیوائس پر build اور export کی آزمائش اس ماحول میں نہیں ہوئی۔

## اصلاحات اس ورژن میں
- `MainActivity.kt` کی خراب import لائن درست کی گئی۔
- TextToSpeech ایک ہی instance کے طور پر Compose lifecycle سے منسلک ہے اور screen ختم ہونے پر shutdown ہوتا ہے۔
- منتخب ویڈیو/آڈیو URIs کے لیے persistable read permission لینے کی کوشش کی جاتی ہے۔
- Android کے platform `org.json` کو استعمال کیا جاتا ہے؛ غیر ضروری external dependency ہٹا دی گئی۔
- Media3 audio looping کو موجودہ API کے مطابق رکھا گیا ہے؛ یہ sequence غیر looping video sequence کے اختتام پر ختم ہوتی ہے۔
- HTTPS API calls کے لیے cleartext HTTP بند ہے۔ اگر جان بوجھ کر مقامی HTTP LLM server استعمال کرنا ہو تو مخصوص network security config کے ساتھ اسے الگ سے فعال کریں۔

## Build requirements
- Android Studio (موزوں موجودہ ورژن)، JDK 17، Android SDK platform 35.
- Android 8.0+ (API 26). OnePlus/OxygenOS کی مطابقت متوقع ہے مگر ہر ڈیوائس پر جانچ ضروری ہے.
- Internet only for online text AI. API استعمال کے اخراجات ہوسکتے ہیں.
- AndroidX Media3 Transformer 1.11.1.

## APK بنانے کا طریقہ
1. ZIP extract کریں اور Android Studio میں `UrduAIStudio` فولڈر کھولیں۔
2. Gradle sync کریں اور SDK 35 انسٹال کریں اگر پوچھا جائے۔
3. **Build > Build Bundle(s) / APK(s) > Build APK(s)** منتخب کریں۔
4. Debug APK عام طور پر `app/build/outputs/apk/debug/app-debug.apk` میں ہوگی۔
5. اس ZIP میں Gradle wrapper شامل نہیں؛ Android Studio کا Gradle استعمال کریں یا compatible Gradle installation سے `gradle wrapper` چلائیں۔
6. APK انسٹال کرنے کے لیے فون میں فائل کھولیں یا Android platform-tools سے `adb install -r app/build/outputs/apk/debug/app-debug.apk` چلائیں۔

## Online text AI setup
- Default base URL: `https://api.openai.com/v1`
- Default model: `gpt-4o-mini` (اپنے اکاؤنٹ میں دستیاب model منتخب کریں)
- اپنی API key ایپ میں درج کریں۔ یہ موجودہ app session کی memory میں رہتی ہے، ڈسک پر محفوظ نہیں ہوتی۔
- عوامی طور پر جاری ہونے والی ایپ میں API secret شامل نہ کریں؛ backend proxy، user auth، rate limiting اور abuse protection بنائیں۔ قیمت، دستیابی اور علاقائی رسائی provider کے مطابق مختلف ہوسکتی ہے۔

## فیچرز
- آف لائن: اردو/انگریزی بنیادی screenplay scaffold، دورانیہ 1–30 منٹ، مقامی ویڈیو/آڈیو فائل انتخاب۔
- آن لائن: حقیقی text generation API adapter۔
- آڈیو: Android TTS preview (اردو voice فون کے speech engine پر منحصر)، background audio sequence۔
- ایڈیٹنگ: منتخب ویڈیو کلپس کی ترتیب وار composition اور MP4 export؛ 720p target scaling effect۔
- شامل نہیں: خودکار AI video/image generation، AI voice API، viral/trending music catalogue، مکمل timeline UI، trim/reorder UI، transitions/crossfade، persistent projects، background render job recovery۔ Media3 compositions میں crossfade کی موجودہ پابندیاں ہیں۔

## حقیقی AI video generation مکمل کرنے کے لیے
ایک مخصوص video provider منتخب کرنا ہوگا جس کے پاس موجودہ documented API، پاکستان میں قابل رسائی سروس، واضح قیمت، اور تجارتی حقوق ہوں۔ اس کے بعد API key کو backend پر محفوظ رکھ کر job creation/status polling/result download کا adapter بنائیں۔ اس archive میں کوئی vendor-specific video generation endpoint شامل نہیں، کیونکہ ہر provider کی authentication اور job API مختلف ہوتی ہے اور کوئی API key فراہم نہیں کی گئی۔

## Music / copyright
Trending social media tracks اکثر کاپی رائٹ کے تحت ہوتے ہیں۔ ایپ میں دستیاب audio file منتخب کی جا سکتی ہے؛ غیر مجاز scraping یا redistribution شامل نہیں۔ صرف اجازت یافتہ یا لائسنس شدہ audio استعمال کریں۔

## Privacy
Online prompt آپ کے منتخب API provider کو بھیجا جاتا ہے۔ Provider کی data retention اور privacy policy دیکھیں۔ Production API key کو public APK میں نہ رکھیں۔


## APK build status
اس سورس آرکائیو کے ساتھ کوئی APK شامل نہیں ہے، کیونکہ اس execution environment میں Android SDK/Gradle موجود نہیں اور بیرونی build downloads دستیاب نہیں تھے۔ اس لیے APK کی compile، install، یا device-test کامیابی کا دعویٰ نہیں کیا جا رہا۔ Build کے بعد متوقع debug APK path: `app/build/outputs/apk/debug/app-debug.apk`.


## Build APK in GitHub Actions
1. Push this project into a repository you own on GitHub.
2. Ensure `.github/workflows/android-apk.yml` is in the default branch.
3. In GitHub, open **Actions → Build Urdu AI Studio APK → Run workflow** (or push to `main`/`master`).
4. Open the completed run and download the `UrduAIStudio-debug-apk` artifact.

This workflow is included, but no Android SDK/Gradle build has been run in this environment. The provided project is a starter: screenplay generation, online chat-completions integration, phone TTS preview, and basic video clip composition are present; generated AI video/images and a music trend catalogue are not.
