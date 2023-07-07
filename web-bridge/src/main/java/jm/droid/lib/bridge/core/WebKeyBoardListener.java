package jm.droid.lib.bridge.core;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.lang.reflect.Method;

/**
 * 解决webView键盘遮挡问题的类
 * https://www.jb51.net/article/95955.htm 参考该文章
 */
public class WebKeyBoardListener {
    private final static int UNKNOWN_HEIGHT = -1;
    private View mChildOfContent;
    private int usableHeightPrevious = UNKNOWN_HEIGHT;
    private FrameLayout.LayoutParams frameLayoutParams;

    private int height = 0;
    private int widget = 0;
    private Context applicationContext;


    public void init(Activity activity) {
        this.applicationContext = activity.getApplicationContext();
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(
                () -> possiblyResizeChildOfContent(activity)
        );
    }

    public void reset() {
        usableHeightPrevious = UNKNOWN_HEIGHT;
    }

    //此处由于设置了layoutParam里面的宽度，会导致后面横竖屏切换后onMeasure时出现错误
    private void possiblyResizeChildOfContent(Activity activity) {
        if (usableHeightPrevious == UNKNOWN_HEIGHT) {
            usableHeightPrevious = computeUsableHeight();
            return;
        }
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = 0;
            if (height == 0) {
                height = mChildOfContent.getRootView().getHeight();
            }
            if (widget == 0) {
                widget = mChildOfContent.getRootView().getWidth();
            }

            if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT || activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
                //竖屏
                usableHeightSansKeyboard = height;
                // TODO 有时候全屏播放视频时 屏幕横屏，此时退出全屏，切换为竖屏，有概率会出现获得的 usableHeightNow 只有横屏的1080高度，这里竖屏情况下强制设置成屏幕高度
                if (usableHeightNow == widget) {
                    usableHeightNow = height;
                }
            } else {
                //横屏
                usableHeightSansKeyboard = widget;
            }


            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                // keyboard probably just became visible
                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
            } else {
                // keyboard probably just became hidden
                int num = getVirtualBarHeigh(applicationContext);
                frameLayoutParams.height = usableHeightSansKeyboard - num;
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }


    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return r.bottom;
    }


    /**
     * 获取虚拟功能键高度
     * todo: 在vivo上全面屏时也是能获取到高度。也需要额外兼容处理
     * @param context
     * @return
     */
    private int getVirtualBarHeigh(Context context) {
        // 小米 黑鲨系统，在全面屏模式下，还是能获取到虚拟按键高度，所以这里要设置高度为0
        if (Build.MODEL.contains("SHARK")) {
            if (Settings.Global.getInt(context.getContentResolver(), "force_fsg_nav_bar", 0) != 0) {
                return 0;
            }
        }
        int vh = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            vh = dm.heightPixels - windowManager.getDefaultDisplay().getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vh;
    }

    public boolean checkDeviceHasNavigationBar2(Context context) {
        //通过判断设备是否有返回键、菜单键(不是虚拟键,是手机屏幕外的按键)来确定是否有navigation bar
        boolean hasMenuKey = ViewConfiguration.get(context)
                .hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap
                .deviceHasKey(KeyEvent.KEYCODE_BACK);

        if (!hasMenuKey && !hasBackKey) {
            // 没有虚拟按键返回 true
            return true;
        }
        // 有虚拟按键返回 false
        return false;
    }


}
