# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Environment\android-sdk-windows/tools/proguard/proguard-android.txt
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
##忽略警告
-ignorewarnings
-optimizationpasses 5
##【混淆时不会产生形形色色的类名 】
-dontusemixedcaseclassnames
#【指定不去忽略非公共的库类。 】
-dontskipnonpubliclibraryclasses
#【不预校验】
-dontpreverify
#预校验
#-dontoptimize
-verbose
#优化
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
##保证是独立的jar,没有任何项目引用,如果不写就会认为我们所有的代码是无用的,从而把所有的代码压缩掉,导出一个空的jar
#-dontshrink
##保护泛型
#-keepattributes Signature

-keep public class * extends android.app.Activity
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public <init>(android.content.Context);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers class * extends android.support.v4.app.Fragment {
    public protected <methods>;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep class com.joey.net.protocol.**{*;}

-keep class com.joey.utils.NetWorkUtil{
    public static void init(android.content.Context);
    public static java.lang.String getCurrentNetworkType();
    public static boolean IsNetWorkEnable();
}

-keep class com.joey.utils.ResourcesUnusualUtil{
   #【所有方法不进行混淆】
  public protected <methods>;

}
-keep class com.joey.hybrid.HybridWebClient{
    public static java.lang.String *(java.lang.String);
    public <methods>;
}
-keep class * implements com.joey.update.UpdateProgressListener {
#    void onProgress(long, long);
#    void onStartProgress();
#    void onFinish();
#    void onError();
    public protected <methods>;
}
-keep class com.joey.update.CheckBean{*;}
-keep class com.joey.update.UpdateManager{
    public <methods>;
#     public <init>;
}
#-keepclasseswithmembers class com.joey.update.UpdateManager {
#    public <init>(android.content.Context, com.joey.update.UpdateProtocol);
#}

-keep class com.joey.update.UpdateProtocol{*;}
-keep class com.joey.update.UpdateConsts{*;}
-keep class com.joey.update.NotificationUtils{*;}
-keep interface com.joey.update.UpdateCheckListener{*;}
-keep interface com.joey.update.UpdateProgressListener{*;}
-keep interface com.joey.hybrid.OnWebTitleListener{*;}
-keep class * implements com.joey.update.UpdateCheckListener {
#    void onProgress(long, long);
#    void onStartProgress();
#    void onFinish();
#    void onError();
    public protected <methods>;
}

-keep class * extends com.joey.update.UpdateProtocol {
    public void download(java.lang.String, com.joey.update.UpdateProgressListener, java.lang.String);
    public void checkVersionCode(com.joey.utils.NetWorkUtil,int, com.joey.update.UpdateCheckListener);
    public void release();
}

#-keep class com.joey.update.**{*;}


-keep class com.android.volley.**{*;}
-keep interface com.android.volley.**{*;}
-keep class com.android.volley.toolbox.**{*;}
-keep interface com.android.volley.toolbox.**{*;}






