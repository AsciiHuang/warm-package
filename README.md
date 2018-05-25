# Warm Package
這是一個使用 Kotlin 語言撰寫的 Android 專案，此 App 的功能是在畫面上顯示現在的電池溫度，你可以使用 seekbar 調整想要讓手機發燙到幾度，按了 float action button 後 App 就會開始做事情讓手機發燙，發燙期間你可以離開應用程式，它會在 Service 繼續發燙，你也可以隨時透過 notification 中得知目前發燙的進度和進入 App 停止程序。

## 這個專案示範了哪些技能

 -  Kotlin 各種語法示範
 -  Android Application 操作
 -  Android Service 操作
 -  Android Activity
 -  MVP 架構
 -  Generic 泛型操作
 -  Kotlin 操作 Java 的方式 (GSON、Volley 等)

## JVM、Java、Kotlin 關係簡介

<table>
  <tr align="center">
    <td bgcolor="#999999">Language</td><td>Java</td><td>Kotlin</td>
  </tr>
  <tr align="center">
    <td bgcolor="999999">Compiler</td><td>javac</td><td>kotlinc</td>
  </tr>
  <tr align="center">
    <td bgcolor="999999">Compile commend</td><td>javac text.java</td><td>kotlinc text.kt -include-runtime -d test.jar</td>
  </tr>
  <tr align="center">
    <td bgcolor="999999">Execution file</td><td colspan="2">Byte Code ( .class / .jar / .dex ) </td>
  </tr>
  <tr align="center">
    <td bgcolor="999999">Runtime Environment</td><td colspan="2">JVM / Dalvik / ART</td>
  </tr>
  <tr align="center">
    <td bgcolor="999999">Native Library</td><td colspan="2">Windows / Mac OS / Linux / Solaris / Android</td>
  </tr>
</table>

## Kotlin 相對於 Java 有什麼優點

 - 可以直接市面上使用 Java 撰寫的 Library，轉換成本僅是語言層面，不牽扯到框架層面
 - 支援 Functional programming 與 Closure、Property 等概念，相較於 Java 8 才支援 Lambda 來得先進靈活許多
 - Java 僅能被編譯成 Byte Code 執行在 JVM 上，而 Kotlin 除了可以編譯為 Byte Code 執行在任何 JVM 環境上，例如 Spring、Android 之外，還可以被編譯為 JavaScript 產生 .js 檔案。
 - 理論上學一套 Kotlin 就可以吃遍全端

## 如何使用 Kotlin

因為 Kotlin 運行於 JVM 之上，所以還是要在對應平台上安裝 Java Runtime、JDK 後再安裝 Kotlin 編譯器。如果你只是要拿來開發 Android App 且不打算在 commend line 中執行，那安裝 Android Studio 3.0 以上版本即自帶 Kotlin 編譯器。

在原有的 Android 專案中，僅需在 build.gradle 加入 plugin 即可

 - +apply plugin: 'kotlin-android'
 - +apply plugin: 'kotlin-android-extensions'
 - implementation 'org.jetbrains.kotlin:kotlin-stdlib-jre7:1.1.51'
 - compile org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.1.51

## Kotlin 的 Coding Conventions

Kotlin 沒有 Java 的 int、boolean、double、void 這種非類別的成員與 Integer、Boolean、Double、Void 這種類別的差異，所以在 Kotlin 中類別名稱一律使用大寫開頭，例如 Int、Boolean、Double、Unit、Nothing 等等，其他基本上與 Java 相同，詳情請見 JetBrains 官方文件：[Coding Conventions for Kotlin] (https://kotlinlang.org/docs/reference/coding-conventions.html)

## 最基礎必須要懂的 Kotlin 語法

### static 成員使用 companion object 取代
 - WarmApp.kt - Line 14

```
// Java 寫法
public static final String DEFAULT_CHANNEL_ID = "WarmPackage";

// Kotlin 寫法
companion object {
    const val DEFAULT_CHANNEL_ID: String = "WarmPackage"
}
```

### Variable
 -  WarmApp.kt - Line 25

```
// Java 寫法
Intent startServiceIntent = new Intent(this, WarmService.class)

// Kotlin 寫法 (型態 :Intent 可省略，結尾免分號)
val startServiceIntent: Intent = Intent(this, WarmService::class.java)
```

### Function
 - WarmApp.kt - Line 34

```
// Java 寫法
public void createNotificationChannel() {
    notificationManager.createNotificationChannel(notificationChannel)
}

// Kotlin 寫法 (指令尾端不用分號)
private fun createNotificationChannel(): Unit {
    notificationManager.createNotificationChannel(notificationChannel)
}
```

### Kotlin 操作 Java Class<T>
 - WarmApp.kt - Line 43

```
val notificationManager = getSystemService(NotificationManager::class.java)

```

### 繼承與實作
- MainActivity.kt - Line 21

```
// Java 寫法
class MainActivity extends AppCompatActivity implements WarmPackageView

// Kotlin 寫法
class MainActivity : AppCompatActivity(), WarmPackageView
```

### final const 成員
- MainActivity.kt - Line 23

```
// 採用 val 而非 var 宣告常數值 (val 是 value 的意思)
private val MAX_TEMPERATURE:Int = 50
```
