# App Builder Go — Fondasi

Tahap 1 dari proyek "App Builder / Web-to-App Generator" (native Kotlin + Jetpack Compose).
Iterasi ini hanya menyiapkan fondasi agar **APK berhasil di-build lewat GitHub Actions**.

## Yang sudah ada
- Konfigurasi Gradle (Kotlin DSL) lengkap: `settings.gradle.kts`, `build.gradle.kts` root, `app/build.gradle.kts`.
- `AndroidManifest.xml` + tema + adaptive icon (vector only, tanpa binary PNG).
- `MainActivity.kt` minimal menampilkan splash "Fondasi siap".
- Tema Compose dark mode dengan aksen ungu `#5C5CFF` (Color.kt / Theme.kt / Type.kt).
- Workflow GitHub Actions `.github/workflows/build-apk.yml`.

## Yang BELUM ada (menyusul pada iterasi berikutnya)
- Wizard 5 langkah (Compose).
- File picker, logic Step 1–Step 5.
- Integrasi WebView untuk hasil generate.

## Cara build
Push ke branch `main`/`master`, lalu buka tab **Actions** di repo GitHub.
Setelah job selesai, unduh artifact `app-debug-apk`.

## Catatan teknis
- Tidak memakai `gradlew`/`gradle-wrapper.jar` karena wrapper binary tidak bisa dibuat lewat UI web GitHub.
  Workflow memakai `gradle/actions/setup-gradle@v3` yang meng-install Gradle sendiri.
- Package: `com.example.appbuilderGo`.
