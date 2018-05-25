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
 - WarmApp.kt Line 14

```
// Java 寫法
public static final String DEFAULT_CHANNEL_ID = "WarmPackage";

// Kotlin 寫法
companion object {
    const val DEFAULT_CHANNEL_ID: String = "WarmPackage"
}
```

### Variable
 -  WarmApp.kt Line 25

```
// Java 寫法
Intent startServiceIntent = new Intent(this, WarmService.class)

// Kotlin 寫法 (型態 :Intent 可省略，結尾免分號)
val startServiceIntent: Intent = Intent(this, WarmService::class.java)
```

### Function
 - WarmApp.kt Line 34

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
 - WarmApp.kt Line 43

```
val notificationManager = getSystemService(NotificationManager::class.java)

```

### 繼承與實作
- MainActivity.kt Line 21

```
// Java 寫法
class MainActivity extends AppCompatActivity implements WarmPackageView

// Kotlin 寫法
class MainActivity : AppCompatActivity(), WarmPackageView
```

### final constant 成員
- MainActivity.kt Line 23

```
// Java 寫法
private final int MAX_TEMPERATURE = 50;

// Kotlin 寫法
// 採用 val 而非 var 宣告常數值 (val 是 value 的意思)
private val MAX_TEMPERATURE:Int = 50
```

### 可為 null 的變數
 - MainActivity.kt Line 25

```
// warmService 這個變數可為 null
// 所有類別成員變數若在建構式前無法給值，一律需宣告為可 null
private var warmService: WarmService? = null
```

### 操作可為 null 的變數
 - MainActivity.kt Line 61

```
// 操作物件成員前要使用 ?.
// 意指若 presenter 為 null 時會直接回傳 null 給 targetTemperature
var targetTemperature:Int = presenter?.getTargetTemperature()?.toInt() ?: 0
```

### if not null 判斷的寫法
 - MainActivity.kt Line 80

```
// Java 寫法
if (warmService != null) {
    if (presenter != null) {
        presenter.attachModel(warmService);
        presenter.initial();
    }
}

// Kotlin 寫法
// 多行或需要對非 null 回傳值做運算時用 ?.let { ... }
// 單行時用 ?. 就夠了
warmService?.let {
    presenter?.attachModel(it)
    presenter?.initial()
}
```

### 操作可 null 的變數時若需要預設值
 - MainActivity.kt Line 61

```
// 在 ?: 後提供左側任何環節發生 null 時該提供的預設值
var targetTemperature:Int = presenter?.getTargetTemperature()?.toInt() ?: 0
```

### 可為 null 變數用於判斷式的注意事項
- MainActivity.kt Line 45

```
// 若 presenter 是 null 時，會直接回傳 null
// 也就是說最終拿來判斷是否 == false 的不一定是 getIsRunning() 的值
// 所以這時後你的判斷式如果是 if (presenter?.getIsRunning() != true)
// 可能會發生 null != true 而進去流程的狀況
// 請注意判斷式習慣正向判斷 == true 或 == false，別用 !=

if (presenter?.getIsRunning() == false) {
    presenter?.closeService()
}
```

### 比 String.format 更方便的語法
 - MainActivity.kt Line 96

```
// 在 "" 雙引號範圍內，可以使用 $ 來使用變數
textviewTemperature.setText("$temperature")

// 甚至可以使用 ${} 來用運算式, 例如寫這樣也行
textviewTemperature.setText("${initTargetTemp + progress}")

// 當然也可以用在 String.format("") 中，例如
// 相對於使用 %d 來得方便許多
Log.e(tag, String.format("${initTargetTemp + progress}"))
```

### function return value 用於判斷式的限制
 - MainActivity.kt Line 116

```
// 因為 presenter 可能是 null
// 所以不能像 Java 只寫 if (presenter.getIsRunning()) { ... }
if (presenter?.getIsRunning() == true) { ... }
```

### Property 的概念
 - MainActivity.kt Line 125

```
// Java 寫法
textviewCurrentTemperature.setText(currentTemp.toString())

// Kotlin 寫法
// 在 Kotlin 內 getter、setter 是自動產生的
// 所以直接操作 text 其實是操作了自動產生的 text setter
// 而不是真的操作到 TextView 內的 text 這個 member
textviewCurrentTemperature.text = currentTemp.toString()
```

### Switch case 的寫法
 - MainActivity.kt Line 132

```
// 不像 Switch 有 break 可以用
// 有多個條件成立的狀況是用逗號相接
// 例如第二個可以寫成 false, null -> { ... }
when (uiStatus) {
    true -> {
        fab.setEnabled(false)
        seekbarTemperature.setEnabled(false)
        createSnackBar()
    }
    false -> {
        fab.isEnabled = true
        seekbarTemperature.isEnabled = true
        closeSnackBar()
    }
}
```

### 沒有回傳值的 function
 - MainActivity.kt Line 152

```
// Java 寫法
private void createSnackBar() { ... }

// Kotlin 寫法
// 然後 Unit 可以省略
// 有一個很類似的類別叫 Nothing 可以混用但記得義意是不同的
private fun createSnackBar(): Unit { ... }
```

### 暱名物件的寫法
 - MainActivity.kt Line 155

```
// Java 寫法
// override 採用 annotation 的方式指定
new View.OnClickListener() {
    @Override
    public void onClick(View view) {
    }
}

// Kotlin 寫法
// override 寫在 function 宣告中
object : View.OnClickListener {
    override fun onClick(v: View?) {
    }
}
```

### 關於常用的 MainActivity.this
 - WarmService.kt Line 66

```
// Java 寫法
return WarmService.this;

// Kotlin 寫法
return this@WarmService
```

### 關於轉型
 - WarmService.kt Line 70

```
// 使用 as 轉型，而非在前面使用 (Type)
// 如果想避免 CaseException 也可以改用 as? 在無法轉型時回傳 null
var sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
```

### for 用於 List 物件的寫法
 - WarmService.kt Line 88

```
// 正常是寫 for (sensor:Sensor in allSensor)
// 但 Kotlin 可自動辨示所以型態可以省略
for (sensor in allSensor) {
    Log.e("Sensor", sensor.toString())
    if (sensor.name.toLowerCase().indexOf("temp") >= 0) {
        temperatureSensor = sensor
        break
    }
}
```

### for 用於整數計算的寫法
 - WarmService.kt Line 113

```
// for (int i=1; i<=30; ++i) 的意思
for (i in 1..30) {
    try {
        var runnable = WarmRunnable()
        threadList.add(runnable)
        Thread(runnable).start()
    } catch (e: Exception) {
        break
    }
}
```

### 預設類別建構式
 - APIBase.kt Line 11

```
// 預設建構式有指定參數時，就不再擁有無參數的建構式了
// 以這個例子來看就是 queue: ResquestQueue
// 所以無法用 var api = InvoiceAPI() 來建構，一定要餵個 queue object
abstract class APIBase<APIType: APIBase<APIType, ResultType>, ResultType>(queue: RequestQueue): API<ResultType>
```

### 類別成員與建構式參數
 - APIBase.kt Line 14

```
// 類別成員宣告時可以直接調用建構式中的參數
private var requestQueue: RequestQueue = queue

// 如果有要在建構式做其他事的話就是醬寫
// 需要注意 init function 位置必須在操作到的成員變數之下，否則無法編譯
// init {
//     requestQueue = queue
// }
```
 
### 類別建構式 Overloading
 - InvoiceAPI.kt Line 12

```
// 如果需要寫第二種建構式就是醬寫
// 預設建構式以外的建構式都一定要傳入預設建構式所需的參數
// 以此為例就是 queue:RequestQueue
// 如果拿掉 queue:RequestQueue 就會無法編譯
constructor(queue:RequestQueue,
            successListener: API.APISuccessListener<InvoiceResult>,
            failListener: API.APIFailListener): this(queue) {
    this.successListener = successListener
    this.failListener = failListener
}
```

### Functional programming 風格的語法
 - InvoiceResult.kt Line 20

```
// 省略 { return "..." } function body 的寫法
// 背後的概念是指函數可以被賦值
override fun getUrl(): String = "https://asciihuang.github.io/invoice.json"
```

