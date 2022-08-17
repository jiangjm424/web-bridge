对webview js调用本地方法的封装

本库提供了一fragment用于webview的展示。在使用时也可以简单的将该fragment放置任何你想放到的位置

```kotlin

val f = WebFragment.newInstance(url)
supportFragmentManager.beginTransaction().add(R.id.container, f).commitAllowingStateLoss()

```
为js提供native方法，在这里只需要继承框架中的AbsPlugin，可以参考demo中的TestPlugin3,之后在js调用时第一个参数是java类的全路径名，第二个参数是传到子类中选择方法的作用，第三个参数是js交给native对应方法的参数，第四个参数是需要native回调到js的callback
--- 注意，虽然dispatchJsEvent返回的是一个gson串，但是为了规范，在使用时建议大家通过JsApiResult的接口返回JsCommonResp的json串。
```kotlin
class TestPlugin3(activity: AppCompatActivity) : AbsPlugin(activity) {
    companion object {
        private const val TAG = "TestPlugin3"
    }

    override fun dispatchJsEvent(method: String, params: String?): String {
        Log.i(TAG, "method:$method ,  param $params aa $activity")
        return JsApiResult.success("plugin 3 sucess")
    }
}
```

#### js调用native方法
```script
window.Native.nativeMethod("com.grank.db.demo.plugins.TestPlugin3","null","{\"testUserPlugin\":\"plugin 3\"}","callByAndroidParam");
```
#### gradle 引入
由于本项目依赖了agentWeb，而其所在仓库是jitpack.io，所以我们在使用时除了需要默认的mavenCentral(), 需要另外引入jitpack
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
