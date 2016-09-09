package uk.co.chrisjenx.calligraphy.sample;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static butterknife.ButterKnife.findById;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);

        // Inject pragmatically
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, PlaceholderFragment.getInstance())
                .commit();


        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                toolbar.setTitle("Calligraphy Added");
                toolbar.setSubtitle("Added subtitle");
            }
        }, 1000);

        handler.postDelayed(new Runnable() {
            @Override public void run() {
                toolbar.setTitle(null);
                toolbar.setSubtitle("Added subtitle");
            }
        }, 2000);

        handler.postDelayed(new Runnable() {
            @Override public void run() {
                toolbar.setTitle("Calligraphy added back");
                toolbar.setSubtitle("Added subtitle");
            }
        }, 3000);
        createFloatView(100);
    }

    /*
        Uncomment if you disable PrivateFactory injection. See CalligraphyConfig#disablePrivateFactoryInjection()
     */
//    @Override
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public View onCreateView(View parent, String name, @NonNull Context context, @NonNull AttributeSet attrs) {
//        return CalligraphyContextWrapper.onActivityCreateView(this, parent, super.onCreateView(parent, name, context, attrs), name, context, attrs);
//    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    private WindowManager wm;
    private View view;// 浮动按钮

    /**
     * 添加悬浮View
     * @param paddingBottom 悬浮View与屏幕底部的距离
     */
    protected void createFloatView(int paddingBottom) {
        int w = 200;// 大小
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        view = getLayoutInflater().inflate(R.layout.float_view, null);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
//        params.type = WindowManager.LayoutParams.TYPE_BASE_APPLICATION;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.format = PixelFormat.TRANSLUCENT;
        params.width = w;
        params.height = w;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        params.x = screenWidth - w;
        params.y = screenHeight - w - paddingBottom;
        view.setBackgroundColor(Color.TRANSPARENT);
        view.setVisibility(View.VISIBLE);
        view.setOnTouchListener(new View.OnTouchListener() {
            // 触屏监听
            float lastX, lastY;
            int oldOffsetX, oldOffsetY;
            int tag = 0;// 悬浮球 所需成员变量

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                float x = event.getX();
                float y = event.getY();
                if (tag == 0) {
                    oldOffsetX = params.x; // 偏移量
                    oldOffsetY = params.y; // 偏移量
                }
                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = x;
                    lastY = y;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    params.x += (int) (x - lastX) / 3; // 减小偏移量,防止过度抖动
                    params.y += (int) (y - lastY) / 3; // 减小偏移量,防止过度抖动
                    tag = 1;
                    wm.updateViewLayout(view, params);
                } else if (action == MotionEvent.ACTION_UP) {
                    int newOffsetX = params.x;
                    int newOffsetY = params.y;
                    // 只要按钮一动位置不是很大,就认为是点击事件
                    if (Math.abs(oldOffsetX - newOffsetX) <= 20
                            && Math.abs(oldOffsetY - newOffsetY) <= 20) {
                        onFloatViewClick();
                    } else {
                        tag = 0;
                    }
                }
                return true;
            }
        });
        wm.addView(view, params);
    }

    /**
     * 点击浮动按钮触发事件，需要override该方法
     */
    protected void onFloatViewClick() {
        Toast.makeText(this, "onFloatViewClick", Toast.LENGTH_SHORT).show();
    }

    /**
     * 将悬浮View从WindowManager中移除，需要与createFloatView()成对出现
     */
    protected void removeFloatView() {
        if (wm != null && view != null) {
            wm.removeViewImmediate(view);
//          wm.removeView(view);//不要调用这个，WindowLeaked
            view = null;
            wm = null;
        }
    }
    /**
     * 隐藏悬浮View
     */
    protected void hideFloatView() {
        if (wm != null && view != null&&view.isShown()) {
            view.setVisibility(View.GONE);
        }
    }
    /**
     * 显示悬浮View
     */
    protected void showFloatView(){
        if (wm != null && view != null&&!view.isShown()) {
            view.setVisibility(View.VISIBLE);
        }
    }
}
