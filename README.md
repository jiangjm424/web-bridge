对webview js调用本地方法的封装

本库提供了一fragment用于webview的展示。在使用时也可以简单的将该fragment放置任何你想放到的位置

```kotlin

val f = WebFragment.newInstance(url)
supportFragmentManager.beginTransaction().add(R.id.container, f).commitAllowingStateLoss()

```
####gradle 引入
由于本项目依赖了agentWeb，而其所在仓库是jitpack.io，所以我们在使用时除了需要默认的mavencenter(), 需要另外引入jitpack
```gralde

allprojects {
	repositories {
		...
		mavenCenterl()
		maven { url 'https://jitpack.io' }
	}
}

```

####相关
```
https://github.com/Justson/AgentWeb
```
