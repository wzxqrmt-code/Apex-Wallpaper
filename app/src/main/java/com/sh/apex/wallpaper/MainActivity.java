package com.sh.apex.wallpaper;

import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import org.json.*;

public class MainActivity extends Activity {

    private WebView webview1;

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.main);
        if (android.os.Build.VERSION.SDK_INT >= 35) {
            final android.content.res.TypedArray _barColors = obtainStyledAttributes(new int[] {android.R.attr.statusBarColor, android.R.attr.navigationBarColor});
            int _statusBarColor = _barColors.getColor(0, android.graphics.Color.TRANSPARENT);
            int _navigationBarColor = _barColors.getColor(1, android.graphics.Color.TRANSPARENT);
            _barColors.recycle();
            final android.view.View _statusBarBackground = new android.view.View(this);
            _statusBarBackground.setBackgroundColor(_statusBarColor);
            final android.view.View _navigationBarBackground = new android.view.View(this);
            _navigationBarBackground.setBackgroundColor(_navigationBarColor);
            android.view.WindowInsetsController _barsController = getWindow().getInsetsController();
            if (_barsController != null) {
                if (android.graphics.Color.alpha(_statusBarColor) != 0) {
                    _barsController.setSystemBarsAppearance(android.graphics.Color.luminance(_statusBarColor) > 0.5f ? android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS : 0, android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
                }
                if (android.graphics.Color.alpha(_navigationBarColor) != 0) {
                    _barsController.setSystemBarsAppearance(android.graphics.Color.luminance(_navigationBarColor) > 0.5f ? android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS : 0, android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS);
                }
            }
            android.view.ViewGroup _decorView = (android.view.ViewGroup) getWindow().getDecorView();
            _decorView.addView(_statusBarBackground, new android.widget.FrameLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, 0, android.view.Gravity.TOP));
            _decorView.addView(_navigationBarBackground, new android.widget.FrameLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, 0, android.view.Gravity.BOTTOM));
            findViewById(android.R.id.content).setOnApplyWindowInsetsListener(new android.view.View.OnApplyWindowInsetsListener() {
                @Override
                public android.view.WindowInsets onApplyWindowInsets(android.view.View _view, android.view.WindowInsets _insets) {
                    int _types = android.view.WindowInsets.Type.systemBars() | android.view.WindowInsets.Type.displayCutout();
                    if ((getWindow().getAttributes().softInputMode & android.view.WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST) == android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE) {
                        _types |= android.view.WindowInsets.Type.ime();
                    }
                    android.graphics.Insets _bars = _insets.getInsets(_types);
                    _view.setPadding(_bars.left, _bars.top, _bars.right, _bars.bottom);
                    _statusBarBackground.getLayoutParams().height = _insets.getInsets(android.view.WindowInsets.Type.statusBars()).top;
                    _statusBarBackground.requestLayout();
                    _navigationBarBackground.getLayoutParams().height = _insets.getInsets(android.view.WindowInsets.Type.navigationBars()).bottom;
                    _navigationBarBackground.requestLayout();
                    return _insets;
                }
            });
        }
        initialize(_savedInstanceState);
        initializeLogic();
    }

    private void initialize(Bundle _savedInstanceState) {
        webview1 = findViewById(R.id.webview1);
        webview1.getSettings().setJavaScriptEnabled(true);
        webview1.getSettings().setSupportZoom(true);
        webview1.getSettings().setDomStorageEnabled(true);
        webview1.getSettings().setAllowFileAccess(true);
        webview1.getSettings().setAllowContentAccess(true);
        
        // ⭐️ فعال‌سازی کش برای کاهش مصرف اینترنت ⭐️
        webview1.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        // ⭐️⭐️⭐️ تنظیمات دانلود (برای دکمه دانلود عکس) ⭐️⭐️⭐️
        webview1.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                try {
                    DownloadManager.Request request = new DownloadManager.Request(android.net.Uri.parse(url));
                    request.setMimeType(mimetype);
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                    String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
                    if (fileName == null || fileName.isEmpty() || fileName.contains("Unknown")) {
                        fileName = "wallpaper_" + System.currentTimeMillis() + ".jpg";
                    }

                    request.setDestinationInExternalPublicDir(android.os.Environment.DIRECTORY_DOWNLOADS, fileName);

                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    dm.enqueue(request);

                    android.widget.Toast.makeText(MainActivity.this, "دانلود شروع شد", android.widget.Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    android.widget.Toast.makeText(MainActivity.this, "خطا در دانلود: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
                }
            }
        });

        webview1.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView _param1, String _param2, Bitmap _param3) {
                final String _url = _param2;
                super.onPageStarted(_param1, _param2, _param3);
            }

            @Override
            public void onPageFinished(WebView _param1, String _param2) {
                final String _url = _param2;
                super.onPageFinished(_param1, _param2);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // ⭐️ باز کردن مایکت ⭐️
                if (url.startsWith("myket://")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.setPackage("ir.mservices.market");
                        startActivity(intent);
                        return true;
                    } catch (ActivityNotFoundException e) {
                        try {
                            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://myket.ir/app/com.sh.apex.wallpaper"));
                            startActivity(webIntent);
                        } catch (Exception ex) {}
                        return true;
                    }
                }
                // ⭐️ باز کردن لینک‌های معمولی ⭐️
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    if (url.contains("supabase.co") || url.contains("wsrv.nl") || url.startsWith("file://")) {
                        return false;
                    }
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }
                return false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return shouldOverrideUrlLoading(view, request.getUrl().toString());
            }
        });
    }

    private void initializeLogic() {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            android.view.Window w = getWindow();
            w.clearFlags(
                android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            );
            w.addFlags(
                android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
            );
            w.setStatusBarColor(android.graphics.Color.TRANSPARENT);
            w.setNavigationBarColor(android.graphics.Color.TRANSPARENT);
            w.getDecorView().setSystemUiVisibility(
                android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
        }

        if (android.os.Build.VERSION.SDK_INT >= 29) {
            getWindow().setNavigationBarContrastEnforced(false);
        }

        webview1.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        webview1.loadUrl("file:///android_asset/index.html");
    }

}
