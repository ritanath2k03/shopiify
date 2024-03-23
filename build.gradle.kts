import com.android.build.gradle.internal.dsl.decorator.SupportedPropertyType.Collection.List.type

buildscript {
    dependencies {
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.40.1")
        classpath ("com.google.gms:google-services:4.3.13")

        val nav_version = "2.5.0"
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
    }

}

plugins {
    id ("com.android.application") version ("7.3.0") apply false
    id ("com.android.library") version ("7.3.0") apply false
    id ("org.jetbrains.kotlin.android") version ("1.9.0") apply false
    id("com.google.dagger.hilt.android") version ("2.48") apply false
}


