-keepattributes *Annotation*

# Keep Room entities
-keep class com.aibasedtyping.data.db.** { *; }

# Strip debug/verbose logs in release
-assumenosideeffects class android.util.Log {
    public static int d(...);
    public static int v(...);
}

