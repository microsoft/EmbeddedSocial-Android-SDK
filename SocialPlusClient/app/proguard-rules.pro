# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\android-sdk/tools/proguard/proguard-android.txt
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

-keep class com.microsoft.embeddedsocial.** { *; }
# for java 8 lambdas
-dontwarn java.lang.invoke**

# annotations
-dontwarn javax.annotation.**

-dontwarn sun.misc.Unsafe

# for joda DateTime
-dontwarn org.joda.convert.**
-dontwarn org.joda.time.**
-keep class org.joda.time.** { *; }
-keep interface org.joda.time.** { *;}

# for picasso
-dontwarn com.squareup.okhttp.**

-dontwarn okhttp3.**
-dontwarn okio.**

-dontwarn retrofit2.**

-dontwarn uk.co.senab.photoview.**

-keep interface java.lang.Method { *; }
-keep class org.codehaus.mojo.** { *; }


# For Jackson, using for JSON data binding
-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry
-keepattributes *Annotation*, EnclosingMethod, Signature
-keepnames class com.fasterxml.jackson.** { *; }
-dontwarn com.fasterxml.jackson.databind.**
-keep class org.codehaus.** { *; }
-keep class com.fasterxml.jackson.annotation.** { *; }
-keepclassmembers public final enum org.codehaus.jackson.annotate.JsonAutoDetect$Visibility {
 public static final org.codehaus.jackson.annotate.JsonAutoDetect$Visibility *; }
