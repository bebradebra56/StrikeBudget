# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

############ APPSFLYER ############
-keep class com.appsflyer.** { *; }
-keep class com.appsflyer.internal.** { *; }
-dontwarn com.appsflyer.**

-keep class com.android.installreferrer.** { *; }
-dontwarn com.android.installreferrer.**

############ FIREBASE ############
-keep class com.google.firebase.installations.** { *; }
-keep class com.google.firebase.analytics.** { *; }
-keep class com.google.firebase.** { *; }
-keep class com.google.firebase.messaging.** { *; }
-keepclassmembers class com.google.firebase.iid.** { *; }

-keep class com.google.android.gms.ads.identifier.** { *; }
-dontwarn com.google.android.gms.ads.identifier.**

-dontwarn com.google.firebase.analytics.**
-dontwarn com.google.firebase.messaging.**
-dontwarn com.google.firebase.iid.**
-dontwarn com.google.firebase.installations.**

############ KOTLIN ############
-keep class kotlin.jvm.internal.** { *; }

############ KOIN ############
# Сохранить Koin Core
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# Сохранить твои классы, которые создаются через Koin (важно!)
-keep class * implements org.koin.core.component.KoinComponent { *; }

-keep class org.koin.android.** { *; }
-dontwarn org.koin.android.**

############ ROOM ############
# Хранить аннотации
-keepattributes *Annotation*

# Сохранить DAO и сущности
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# Сохранить модели с @Entity, чтобы поля не обфусцировались
-keep @androidx.room.Entity class * { *; }

# Сохранить абстрактный класс Database
-keep class * extends androidx.room.RoomDatabase { *; }

-keepclassmembers class * {
    @androidx.room.TypeConverter *;
}

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature