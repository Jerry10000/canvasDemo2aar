# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\SDK\android-sdk-windows/tools/proguard/proguard-android.txt
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

#-libraryjars 'C:\Program Files\Java\jre1.8.0_141\lib\rt.jar'
#
#-libraryjars 'D:\SDK\android-sdk-windows\platforms\android-25\android.jar'
#
#-optimizationpasses 5
#
#-dontusemixedcaseclassnames
#
## -keep public class * extends android.app.Activity
#
#-keep class com.hanvon.hwlibrary.* {
#
#public <fields>;
#
#public <methods>;
#
#}

-optimizationpasses  5         # 指定代码的压缩级别

-libraryjars 'C:\Program Files\Java\jre1.8.0_141\lib\rt.jar'
#-libraryjars 'D:\SDK\android-sdk-windows\platforms\android-25\android.jar'

-ignorewarnings
-dontshrink

-dontusemixedcaseclassnames   # 是否使用大小写混合
-dontpreverify           # 混淆时是否做预校验
-verbose                # 混淆时是否记录日志
-ignorewarnings
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*  # 混淆时所采用的算法

-keep class com.hanvon.canvasdemo.engine.HwPenEngine{
      public *;
  }
-keep class com.hanvon.canvasdemo.Utils.IOUtils


-keep public class * extends android.app.Activity      # 保持哪些类不被混淆
-keep public class * extends android.app.Application   # 保持哪些类不被混淆
-keep public class * extends android.app.Service       # 保持哪些类不被混淆
-keep public class * extends android.content.BroadcastReceiver  # 保持哪些类不被混淆
-keep public class * extends android.content.ContentProvider    # 保持哪些类不被混淆
-keep public class * extends android.app.backup.BackupAgentHelper # 保持哪些类不被混淆
-keep public class * extends android.preference.Preference        # 保持哪些类不被混淆
-keep public class * extends android.support.v4.app.Fragment        # 保持哪些类不被混淆
-keep public class com.android.vending.licensing.ILicensingService    # 保持哪些类不被混淆

-keep class org.simalliance.openmobileapi.** {*;}
-keep class org.simalliance.openmobileapi.service.** {*;}

-keep class com.unionpay.** {*;}
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }

-keepclasseswithmembernames class * {  # 保持 native 方法不被混淆
    native <methods>;
}
-keepclasseswithmembers class * {   # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {# 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity { # 保持自定义控件类不被混淆
    public void *(android.view.View);
}
-keepclassmembers enum * {     # 保持枚举 enum 类不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable { # 保持 Parcelable 不被混淆
    public static final android.os.Parcelable$Creator *;
}
 # 百度地图代码混淆
-keep class com.baidu.** { *; }
-keep class vi.com.gdi.bgl.android.**{*;}

-keep class com.tencent.bugly.**{*;}
-keep class com.synnapps.carouselview.**{*;}
-keep class de.mindpipe.android.logging.log4j.**{*;}
-keep class de.hdodenhof.circleimageview.**{*;}
-keep class com.android.datetimepicker.**{*;}
-keep class org.apache.http.**{*;}
-keep class org.apache.log4j.**{*;}
#-keep class com.edg.faker.**{*;}
-keep class com.sharesdk.**{*;}
-keep class com.bufeng.wyt.model.**{*;}
-keep class com.bufeng.wyt.bean.**{*;}
-keepattributes Signature
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class **.R$* {*;}
-keep class **.R{*;}
-dontwarn cn.sharesdk.**
-dontwarn **.R$*
-keep class m.framework.**{*;}
-keep class android.net.http.SslError
-keep class android.webkit.**{*;}
-keep class com.mob.tools.utils
-keep class cn.sharesdk.**{*;}
-keep class com.hanvon.penenginejni.**{*;}
-keep class com.hanvon.xboard.bean.**{*;}


