# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/dido/dev/sdk/android-sdk-linux/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

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
#Main config
#-keep class com.omisoft.hsracer.** { *; }
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontoptimize
-verbose
#EventBus
-keepattributes *Annotation*,EnclosingMethod,Signature
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
#Jackson
-keepnames class com.fasterxml.jackson.** { *; }
 -dontwarn com.fasterxml.jackson.databind.**
 -keep class org.codehaus.** { *; }
 -keepclassmembers public final enum org.codehaus.jackson.annotate.JsonAutoDetect$Visibility {
 public static final org.codehaus.jackson.annotate.JsonAutoDetect$Visibility *; }
-keep public class your.class.** {
  public void set*(***);
  public *** get*();
}
#Okhttp
# OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
#OSMDroid
-keep class microsoft.mappoint.** {*;}
-keep class org.osmdroid.** {*;}
-keep class org.metalev.multitouch.controller.** {*;}
# https://github.com/osmdroid/osmdroid/issues/633 SDK version 26
-dontwarn org.osmdroid.tileprovider.modules.NetworkAvailabliltyCheck
#WebRTC
-keep class org.webrtc.** { *; }
# Room Persistence
# Retain declared checked exceptions for use by a Proxy instance.
-keep class android.arch.** {*;}
-keep class android.arch.persistence.room.paging.LimitOffsetDataSource
# WebSocket - neovisionaries
-keep class com.neovisionaries.** {*;}
# Firebase
# Basic ProGuard rules for Firebase Android SDK 2.0.0+
-keep class com.google.firebase.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-dontwarn org.w3c.dom.**
-dontwarn org.joda.time.**
-dontwarn org.shaded.apache.**
-dontwarn org.ietf.jgss.**
-dontwarn com.google.firebase.**
-dontnote com.google.firebase.client.core.GaePlatform