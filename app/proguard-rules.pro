# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Room entities and DAOs
-keep class com.onlinechessgame.app.chess.data.local.** { *; }
-keep class androidx.room.** { *; }

# Chess models used by Firebase and Room
-keep class com.onlinechessgame.app.chess.model.** { *; }
-keep class com.onlinechessgame.app.chess.online.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Kotlin / coroutines
-dontwarn kotlinx.coroutines.**

# Keep line numbers for crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
