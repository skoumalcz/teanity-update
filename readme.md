<p align="center">
  <img src="https://raw.githubusercontent.com/skoumalcz/teanity/1.2/art/logo.png" width="128px" />
</p>
<p align="center">
    <a href="https://jitpack.io/#com.skoumal/teanity-update"><img src="https://jitpack.io/v/com.skoumal/teanity-update.svg?style=flat-square" width="128px" /></a>
</p>

Teanity update is an extension of Play Core library providing additional widgets to make your life
easier.

### Preview

<p align="center">
    <img src="art/preview.gif" width="300" />
</p>

### Functionality

This library contains multiple widgets that can be used together or independently of each other.

#### `UpdateProgressView`

…is a widget that reacts to changes provided via your play-core library implementation. It's as
easy as pushing `AppUpdateResult`s through the view's method `setAppUpdateResult`. View will
automatically adjust to the content that's currently being provided.

Optionally you can set a `OnClickListener` which will only be available **after**
`AppUpdateResult.Downloaded` is delivered to the widget. However if the listener is not set, it
will automatically set the listener to `result.completeUpdate()`.

```xml
<com.skoumal.teanity.update.ui.UpdateProgressView
    android:id="@+id/progress_view"
    style="@style/Widget.PlayCore.Update.Progress"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:theme="@style/ThemeOverlay.PlayCore.Update.OnSurface" />
```

```kotlin
suspend fun setUpUpdateProgress() {
    manager // AppUpdateManagerFactory.create(applicationContext)
        .requestUpdateFlow() // from "com.google.android.play:core-ktx:${playCoreVersion}"
        .collect {
            updateProgressView.setAppUpdateResult(it)
        }
}
```

#### `UpdateProgressTooltipLayout`

…is a layout that provides neat dialog-like wrapper to the contents. In its nature it's purely
optional, however it's designed to look great together with `UpdateProgressView`.

```xml
<com.skoumal.teanity.update.ui.UpdateProgressTooltipLayout
    android:id="@+id/progress_container"
    style="@style/Widget.PlayCore.Update.Progress.Tooltip"
    android:layout_width="200dp"
    android:layout_height="wrap_content"
    android:layout_gravity="center"
    android:visibility="gone">

    <com.skoumal.teanity.update.ui.UpdateProgressView
        android:id="@+id/progress_view"
        style="@style/Widget.PlayCore.Update.Progress"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:theme="@style/ThemeOverlay.PlayCore.Update.OnSurface" />

</com.skoumal.teanity.update.ui.UpdateProgressTooltipLayout>
```

### Where to start?

Include jitpack for all projects: _`$projectRoot/build.gradle`_

```groovy
allprojects {
    repositories {
        //...
        maven { url 'https://jitpack.io' }
    }
}
```

Add the library to your module: _`$projectRoot/app/build.gradle`_

```groovy
dependencies {
    // todo replace with specific version
    implementation("com.skoumal:teanity-update:+")

    // Teanity does not provide these dependencies, they need to be included as well
    implementation("com.google.android.play:core:+")
    implementation("com.google.android.play:core-ktx:+")
}
```