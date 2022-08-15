
-keep public class * extends android.webkit.WebChromeClient
-keep public class * extends com.web.bridge.iplugin.AbsPlugin
# Preserve annotated Javascript interface methods.
-keepclassmembers class * { @android.webkit.JavascriptInterface <methods>; }
-keepclassmembers public class * extends com.web.bridge.iplugin.AbsPlugin { <init>(***); }
