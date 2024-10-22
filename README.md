对webview js调用本地方法的封装

本库提供了一fragment用于webview的展示。在使用时也可以简单的将该fragment放置任何你想放到的位置

```kotlin

val f = WebFragment.newInstance(url)
supportFragmentManager.beginTransaction().add(R.id.container, f).commitAllowingStateLoss()

```

为js提供native方法，在这里只需要继承框架中的AbsJsPlugin，可以参考demo中的TestPlugin3,

> 注意在2.0.0版本中方法参数及native方法实现有变。 所有实现的方法在onConfig中进行初始化好，
> 将所有方法放到methods这个hashmap中， 其中，key表示从 h5中传入的方法名，value即是本地的实现，需要返回一个字符串
> 为了规范，建议在返回的时候使用@{JsApiResult}对结果进行封装

```kotlin
class TestPlugin(activity: AppCompatActivity, jsApiProxy: JsApiProxy?) :
    AbsJsPlugin(activity, jsApiProxy) {

    override fun onConfig(methods: MutableMap<String, (JsCommonReq) -> String>) {
        methods["one"] = put@{ req: JsCommonReq ->
            Log.i("Plugin", "method one 2")
            notifyJs("callByAndroid", JsApiResult.success(0, null, "fdasfa"))
            return@put JsApiResult.success(0, reqId = req.reqId, "on")
        }
        methods.put("two", two)
    }

    private val two = two@{ req: JsCommonReq ->
        return@two ""
    }
}
```

如果需要回调js， 本库提供了一个方便的接口供子类调用：notifyJs(method: String, resp: String?)

#### js调用native方法

```script
window.Native.nativeMethod("com.grank.db.demo.plugins.TestPlugin3","one","null","{\"testUserPlugin\":\"plugin 3\"}","callByAndroidParam");
//其中，Native是原生方法默认的挂载名称，如果需要换成其他的名称，可以在创建Webfragment之前通过调用
WebJsConfig.attachNodeName进行修改
```

#### gradle 引入

由于本项目依赖了agentWeb，而其所在仓库是jitpack.io，所以我们在使用时除了需要默认的mavenCentral(),
需要另外引入jitpack

```gralde

allprojects {
	repositories {
		...
		mavenCentral()
		maven { url 'https://jitpack.io' }
	}
}

```

#### 相关

```
https://github.com/Justson/AgentWeb
```
