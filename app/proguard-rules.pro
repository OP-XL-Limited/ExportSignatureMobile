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


##############################################
# Production R8 / ProGuard Rules
##############################################

##############################################
# General Optimization
##############################################

-optimizationpasses 5
-allowaccessmodification
-repackageclasses ''
-dontpreverify
-verbose

##############################################
# Remove Logs in Release
##############################################

-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** println(...);
}

##############################################
# Preserve Source Information for Crashlytics
##############################################

-keepattributes SourceFile,LineNumberTable

##############################################
# Kotlin Metadata
##############################################

-keep class kotlin.Metadata { *; }

##############################################
# Annotations
##############################################

-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

##############################################
# Parcelable
##############################################

-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

##############################################
# Serializable
##############################################

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

##############################################
# Keep Custom Views
##############################################

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public <init>(android.content.Context, android.util.AttributeSet, int, int);
}

##############################################
# Activities, Services, Receivers
##############################################

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.Application

##############################################
# Retrofit
##############################################

-keepattributes Signature
-keepattributes Exceptions

-keepclassmembers interface * {
    @retrofit2.http.* <methods>;
}



##############################################
# Room
##############################################

-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

##############################################
# Lifecycle
##############################################

-keep class androidx.lifecycle.DefaultLifecycleObserver

##############################################
# Coroutines
##############################################

-dontwarn kotlinx.coroutines.**

##############################################
# Hilt / Dagger
##############################################

-dontwarn dagger.**
-dontwarn javax.inject.**

##############################################
# Firebase
##############################################

-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

##############################################
# Play Services
##############################################

-dontwarn com.google.android.gms.**

##############################################


##############################################
# Coil
##############################################

-dontwarn coil.**

##############################################
# Compose
##############################################

-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

##############################################
# Enum Values
##############################################

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

##############################################
# JNI
##############################################

-keepclasseswithmembernames class * {
    native <methods>;
}

##############################################
# Keep Runtime Visible Annotations
##############################################

-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

##############################################
# Ignore harmless warnings
##############################################

-dontwarn org.intellij.lang.annotations.**
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn kotlin.jvm.**
-dontwarn org.jetbrains.annotations.**

##############################################
# Remove Unused Classes
##############################################

-printmapping mapping.txt
-printusage unused.txt