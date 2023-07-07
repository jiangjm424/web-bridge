
-keep public class * extends android.webkit.WebChromeClient
-keep public class * extends jm.droid.lib.bridge.iplugin.AbsPlugin
# Preserve annotated Javascript interface methods.
-keepclassmembers class * { @android.webkit.JavascriptInterface <methods>; }
-keepclassmembers public class * extends jm.droid.lib.bridge.iplugin.AbsPlugin { <init>(***); }
