# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\Programs\android-studio\sdk/tools/proguard/proguard-android.txt
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

# Custom views referenced only from XML layouts
-keep public class * extends android.view.View

# Annotations
-keepattributes **
-keepattributes SourceFile,LineNumberTable, Signature, *Annotation*
-keep class javax.annotation.Nullable

# Android Annotations
-dontwarn org.androidannotations.**

# Keep DTOs classes
-keep class com.spiraclestudios.autoskola.**Dto {
    <init>(...);
     *;
}

# Leave fragment/injected class constructors
-keepclassmembers class ** {
    public <init>();
}

# Keep enums
-keepnames enum * {
    **[] $VALUES;
    public *;
}
-dontwarn android.hardware.**

-dontwarn android.support.v4.**

# ButterKnife 7

-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# Crashlytics

-keepattributes SourceFile,LineNumberTable,*Annotation*
-keep public class * extends java.lang.Exception
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

# Configuration for Guava 18.0
#
# disagrees with instructions provided by Guava project: https://code.google.com/p/guava-libraries/wiki/UsingProGuardWithGuava

-keep class com.google.common.io.Resources {
    public static <methods>;
}
-keep class com.google.common.collect.Lists {
    public static ** reverse(**);
}
-keep class com.google.common.base.Charsets {
    public static <fields>;
}

-keep class com.google.common.base.Joiner {
    public static com.google.common.base.Joiner on(java.lang.String);
    public ** join(...);
}

-keep class com.google.common.collect.MapMakerInternalMap$ReferenceEntry
-keep class com.google.common.cache.LocalCache$ReferenceEntry

# http://stackoverflow.com/questions/9120338/proguard-configuration-for-guava-with-obfuscation-and-optimization
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe

# Guava 19.0
-dontwarn java.lang.ClassValue
-dontwarn com.google.j2objc.annotations.Weak
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# About Libraries
-keep class .R
-keep class **.R$* {
    <fields>;
}