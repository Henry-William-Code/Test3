# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/macpro/Library/Android/sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the groupLast number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the groupLast number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#=======================================================================================================
# 指定代码的压缩级别 0 - 7(指定代码进行迭代优化的次数，在Android里面默认是5，这条指令也只有在可以优化时起作用。)
-optimizationpasses 5
# 混淆时不会产生形形色色的类名(混淆时不使用大小写混合类名)
-dontusemixedcaseclassnames
# 指定不去忽略非公共的库类(不跳过library中的非public的类)
-dontskipnonpubliclibraryclasses
# 指定不去忽略包可见的库类的成员
-dontskipnonpubliclibraryclassmembers
#不进行优化，建议使用此选项，
-dontoptimize
 # 不进行预校验,Android不需要,可加快混淆速度。
-dontpreverify
-dontwarn com.android.installreferrer

# 屏蔽警告
-ignorewarnings
# 指定混淆是采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不做更改
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
# 保护代码中的Annotation不被混淆
-keepattributes *Annotation*
# 避免混淆泛型, 这在JSON实体映射时非常重要
-keepattributes Signature
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
 #优化时允许访问并修改有修饰符的类和类的成员，这可以提高优化步骤的结果。
# 比如，当内联一个公共的getter方法时，这也可能需要外地公共访问。
# 虽然java二进制规范不需要这个，要不然有的虚拟机处理这些代码会有问题。当有优化和使用-repackageclasses时才适用。
#指示语：不能用这个指令处理库中的代码，因为有的类和类成员没有设计成public ,而在api中可能变成public
-allowaccessmodification
#当有优化和使用-repackageclasses时才适用。
-repackageclasses ''
 # 混淆时记录日志(打印混淆的详细信息)
 # 这句话能够使我们的项目混淆后产生映射文件
 # 包含有类名->混淆后类名的映射关系
-verbose
# ----------------------------- 默认保留 -----------------------------
# 保持哪些类不被混淆
#继承activity,application,service,broadcastReceiver,contentprovider....不进行混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.support.multidex.MultiDexApplication
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep class com.android.** {*;}## 保留okhttp的所有类及其内部类
-keep class android.support.** {*;}## 保留support下的所有类及其内部类

#-keep public class com.google.vending.licensing.ILicensingService
#-keep public class com.android.vending.licensing.ILicensingService
#表示不混淆上面声明的类，最后这两个类我们基本也用不上，是接入Google原生的一些服务时使用的。
#----------------------------------------------------

# 保留继承的
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

#表示不混淆任何包含native方法的类的类名以及native方法名，这个和我们刚才验证的结果是一致
-keepclasseswithmembernames class * {
    native <methods>;
}

#这个主要是在layout 中写的onclick方法android:onclick="onClick"，不进行混淆
#表示不混淆Activity中参数是View的方法，因为有这样一种用法，在XML中配置android:onClick=”buttonClick”属性，
#当用户点击该按钮时就会调用Activity中的buttonClick(View view)方法，如果这个方法被混淆的话就找不到了
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

#表示不混淆枚举中的values()和valueOf()方法，枚举我用的非常少，这个就不评论了
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#表示不混淆任何一个View中的setXxx()和getXxx()方法，
#因为属性动画需要有相应的setter和getter的方法实现，混淆了就无法工作了。
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#表示不混淆Parcelable实现类中的CREATOR字段，
#毫无疑问，CREATOR字段是绝对不能改变的，包括大小写都不能变，不然整个Parcelable工作机制都会失败。
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
# 这指定了继承Serizalizable的类的如下成员不被移除混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
# 保留R下面的资源
-keep class **.R$* {
 *;
}
#不混淆资源类下static的
-keepclassmembers class **.R$* {
    public static <fields>;
}

# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}

# 保留我们自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep class com.example.odm.garbagesorthelper.widget.** { *; }


#
#----------------------------- WebView(项目中没有可以忽略) -----------------------------
#
#webView需要进行特殊处理

#在app中与HTML5的JavaScript的交互进行特殊处理
#我们需要确保这些js要调用的原生方法不能够被混淆，于是我们需要做如下处理：


#
#---------------------------------实体类---------------------------
#--------(实体Model不能混淆，否则找不到对应的属性获取不到值)-----
#
-keep class com.example.odm.garbagesorthelper.model.entity.** { *; }
-dontwarn  com.example.odm.garbagesorthelper.model.entity.**

#对含有反射类的处理
-keep class com.example.odm.garbagesorthelper.utils.** { *; }

#
# ----------------------------- 其他的 -----------------------------
#
# 删除代码中Log相关的代码
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# 保持测试相关的代码
-dontnote junit.framework.**
-dontnote junit.runner.**
-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**
#
# ----------------------------- 第三方库、框架、SDK -----------------------------
#logger
-dontwarn com.orhanobut.logger.**
-keep class com.orhanobut.logger.**{*;}
-keep interface com.orhanobut.logger.**{*;}

# Gson
-keep class com.google.gson.stream.** { *; }
-keepattributes EnclosingMethod
-dontwarn com.google.gson.**
-keep class com.google.gson.**{*;}
-keep interface com.google.gson.**{*;}
#gson
#如果用到Gson解析包的，直接添加下面这几行就能成功混淆，不然会报错。

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
# Application classes that will be serialized/deserialized over Gso


# OkHttp3
-dontwarn okhttp3.logging.**
-keep class okhttp3.internal.**{*;}
-dontwarn okio.**

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keep class com.tencent.** { *; }
-keep class com.qgnix.** { *; }
#-keepattributes Signature-keepattributes Exceptions

# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform.Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions


# RxJava RxAndroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
#-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
#    rx.internal.util.atomic.LinkedQueueNode producerNode;
#}
#-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
#    rx.internal.util.atomic.LinkedQueueNode consumerNode;
#}

#LiveEventBus
-dontwarn com.jeremyliao.liveeventbus.**
-keep class com.jeremyliao.liveeventbus.** { *; }
-keep class androidx.lifecycle.** { *; }
-keep class androidx.arch.core.** { *; }

#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#utilcodex
-keep class  com.blankj.utilcode.util.** { *; }

#
-keep class site.gemus.** { *; }

#XUI
-keep class com.xuexiang.xui.** { *; }

#讯飞SDK
#-libraryjars libs/Msc.jar
#-libraryjars src/main/jniLibs/Msc.jar
#-keep class com.iflytek.**{*;}
#-keepattributes Signature

#RxPermission
-keep class com.tbruyelle.rxpermissions2.** { *; }

#XBanner
-keep class com.stx.xhb.androidx.XBanner.** { *; }

#底部导航栏
-keep class com.aurelhubert.ahbottomnavigation.** { *; }


#Bugly SDK
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
-keep class android.support.**{*;}
#===============================================================================================================
# Retrofit 2.X

## https://square.github.io/retrofit/ ##

-dontwarn retrofit2.**

-keep class retrofit2.** { *; }

-keepattributes Signature

-keepattributes Exceptions

#-keepclasseswithmembers class * {
#@retrofit2.http.* ;
#
#}

#proguard-square-retrofit.pro

# Retrofit 1.X

-keep class com.squareup.okhttp.** { *; }

-keep class retrofit.** { *; }

-keep interface com.squareup.okhttp.** { *; }

-dontwarn com.squareup.okhttp.**

-dontwarn okio.**

-dontwarn retrofit.**

-dontwarn rx.**

#-keepclasseswithmembers class * {
#@retrofit.http.*;
#
#}

# If in your rest service interface you use methods with Callback argument.

-keepattributes Exceptions

# If your rest service methods throw custom exceptions, because you've defined an ErrorHandler.

-keepattributes Signature

# Also you must note that if you are using GSON for conversion from JSON to POJO representation, you must ignore those POJO classes from being obfuscated.

# Here include the POJO's that have you have created for mapping JSON response to POJO for example.

#proguard-square-otto.pro
#
### Square Otto specific rules ##
#
### https://square.github.io/otto/ ##
#
#-keepattributes *Annotation*
#
#-keepclassmembers class ** {
#@com.squareup.otto.Subscribe public *;
#
#@com.squareup.otto.Produce public *;
#
#}

#proguard-square-picasso.pro

## Square Picasso specific rules ##

## https://square.github.io/picasso/ ##

-dontwarn com.squareup.okhttp.**

#proguard-square-okhttp3.pro

# OkHttp

-keepattributes Signature

-keepattributes *Annotation*

-keep class okhttp3.** { *; }

-keep interface okhttp3.** { *; }

-dontwarn okhttp3.**

#proguard-square-dagger.pro

# Dagger ProGuard rules.

# https://github.com/square/dagger

#-dontwarn dagger.internal.codegen.**
#
#-keepclassmembers,allowobfuscation class * {
#@javax.inject.* *;
#
#@dagger.* *;
#
#();
#
#}
#
#-keep class dagger.* { *; }
#
#-keep class javax.inject.* { *; }

#-keep class * extends dagger.internal.Binding
#
#-keep class * extends dagger.internal.ModuleAdapter
#
#-keep class * extends dagger.internal.StaticInjection

#proguard-rx-java.pro
#
## RxJava 0.21
#
#-keep class rx.schedulers.Schedulers {
#public static ;
#
#}
#
#-keep class rx.schedulers.ImmediateScheduler {
#public ;
#
#}
#
#-keep class rx.schedulers.TestScheduler {
#public ;
#
#}
#
#-keep class rx.schedulers.Schedulers {
#public static ** test();
#
#}

#proguard-guava.pro

# Configuration for Guava 18.0

#

# disagrees with instructions provided by Guava project: https://code.google.com/p/guava-libraries/wiki/UsingProGuardWithGuava

#-keep class com.google.common.io.Resources {
#public static ;
#
#}
#
#-keep class com.google.common.collect.Lists {
#public static ** reverse(**);
#
#}
#
#-keep class com.google.common.base.Charsets {
#public static ;
#
#}

#-keep class com.google.common.base.Joiner {
#public static com.google.common.base.Joiner on(java.lang.String);
#
#public ** join(...);
#
#}

#-keep class com.google.common.collect.MapMakerInternalMap$ReferenceEntry

#-keep class com.google.common.cache.LocalCache$ReferenceEntry

# http://stackoverflow.com/questions/9120338/proguard-configuration-for-guava-with-obfuscation-and-optimization

-dontwarn javax.annotation.**

-dontwarn javax.inject.**

-dontwarn sun.misc.Unsafe

# Guava 19.0

-dontwarn java.lang.ClassValue

-dontwarn com.google.j2objc.annotations.Weak

-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

#proguard-gson.pro

## GSON 2.2.4 specific rules ##

# Gson uses generic type information stored in a class file when working with fields. Proguard

# removes such information by default, so configure it to keep all of it.

-keepattributes Signature

# For using GSON @Expose annotation

-keepattributes *Annotation*

-keepattributes EnclosingMethod

# Gson specific classes

#-keep class sun.misc.Unsafe { *; }

-keep class com.google.gson.stream.** { *; }

#proguard-greendao.pro
#
## GreenDao rules
#
## Source: http://greendao-orm.com/documentation/technical-faq
#
##
#
#-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
#public static java.lang.String TABLENAME;
#
#}

-keep class **$Properties

#proguard-glide.pro

# Glide specific rules #

# https://github.com/bumptech/glide

-keep public class * implements com.bumptech.glide.module.GlideModule

-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
**[] $VALUES;

public *;

}

#proguard-facebook-fresco.pro
#
## Fresco v0.8.1 ProGuard rules.
#
## https://github.com/facebook/fresco
#
#-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip
#
## Do not strip any method/class that is annotated with @DoNotStrip
#
#-keep @com.facebook.common.internal.DoNotStrip class *
#
#-keepclassmembers class * {
#@com.facebook.common.internal.DoNotStrip *;
#
#}
#
## Keep native methods
#
#-keepclassmembers class * {
#native ;
#
#}

-dontwarn okio.**

-dontwarn javax.annotation.**

-dontwarn com.android.volley.toolbox.**

#proguard-eventbus-3.pro

## New rules for EventBus 3.0.x ##

# http://greenrobot.org/eventbus/documentation/proguard/

-keepattributes *Annotation*

-keepclassmembers class ** {
@org.greenrobot.eventbus.Subscribe *;

}

-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor

-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
*(java.lang.Throwable);

}


#Easy-Adapter v1.5.0

#-keepattributes *Annotation*
#
#-keepclassmembers class * extends uk.co.ribot.easyadapter.ItemViewHolder {
#public *(...);
#
#}


# Crashlytics 2.+

-keep class com.crashlytics.** { *; }

-keep class com.crashlytics.android.**

-keepattributes SourceFile, LineNumberTable, *Annotation*

# If you are using custom exceptions, add this line so that custom exception types are skipped during obfuscation:

-keep public class * extends java.lang.Exception

# For Fabric to properly de-


# ButterKnife 7

#-keep class butterknife.** { *; }
#
#-dontwarn butterknife.internal.**
#
#-keep class **$$ViewBinder { *; }
#
#-keepclasseswithmembernames class * {
#@butterknife.* ;
#
#}
#
#-keepclasseswithmembernames class * {
#@butterknife.*;
#
#}


# fastjson proguard rules

# https://github.com/alibaba/fastjson

-dontwarn com.alibaba.fastjson.**

-keepattributes Signature

-keepattributes *Annotation*


## ActionBarSherlock 4.4.0 specific rules ##

-keep class android.support.v4.app.** { *; }

-keep interface android.support.v4.app.** { *; }

-keep class com.actionbarsherlock.** { *; }

-keep interface com.actionbarsherlock.** { *; }

-keepattributes *Annotation*

## hack for Actionbarsherlock 4.4.0, see https://github.com/JakeWharton/ActionBarSherlock/issues/1001 ##

-dontwarn com.actionbarsherlock.internal.**


## SVG Android ##

#https://github.com/pents90/svg-android

-keep class com.larvalabs.svgandroid.** {*;}

-keep class com.larvalabs.svgandroid.*$* {*;}


-keep public class android.support.v7.widget.** { *; }

-keep public class android.support.v7.internal.widget.** { *; }

-keep public class android.support.v7.internal.view.menu.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
public *(android.content.Context);

}

-dontwarn android.support.design.**
-keep class android.support.design.** { *; }

-keep interface android.support.design.** { *; }

-keep public class android.support.design.R$* { *; }
#=========================self================================
-keep class com.tencent.** {*;}
-keep class android.webkit.** {*;}
-keep class android.view.** {*;}


-keepattributes *Annotation*
-keepclassmembers class ** {
@org.greenrobot.eventbus.Subscribe *;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
#(Java.lang.Throwable);
}

-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep public class com.alibaba.android.arouter.facade.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}

# If you use the byType method to obtain Service, add the following rules to protect the interface:
-keep interface * implements com.alibaba.android.arouter.facade.template.IProvider

# If single-type injection is used, that is, no interface is defined to implement IProvider, the following rules need to be added to protect the implementation
# -keep class * implements com.alibaba.android.arouter.facade.template.IProvider

#--------------------------------------------
-keep class com.qgnix.common.bean.** {*;}
-keep class com.qgnix.game.bean.** {*;}
-keep class ccom.qgnix.live.bean.** {*;}
-keep class com.qgnix.main.bean.** {*;}

-keep class com.qgnix.game.event.** {*;}
-keep class com.qgnix.common.event.** {*;}
-keep class com.qgnix.live.event.** {*;}
-keep class com.qgnix.main.event.** {*;}
-keep class com.qgnix.** {*;}
-keep class com.tencent.** { *; }
-keep class com.** { *; }
-keep class tv.danmaku.** { *; }
#--------------------------------------------


