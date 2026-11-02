package com.d4tech.d4ai;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * D4AI-1 standalone shell — offline-first local UI in assets, optional online enhance.
 */
public class MainActivity extends AppCompatActivity {

    private static final int NAVY = Color.parseColor("#0b1b3a");
    private WebView web;
    private FrameLayout splash;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.setStatusBarColor(NAVY);
            if (Build.VERSION.SDK_INT >= 21) w.setNavigationBarColor(NAVY);

            FrameLayout root = new FrameLayout(this);
            root.setBackgroundColor(NAVY);

            web = new WebView(this);
            web.setBackgroundColor(NAVY);
            root.addView(web, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            splash = buildSplash();
            root.addView(splash);
            setContentView(root);

            WebSettings s = web.getSettings();
            s.setJavaScriptEnabled(true);
            s.setDomStorageEnabled(true);
            s.setAllowFileAccess(true);
            s.setMediaPlaybackRequiresUserGesture(false);
            if (Build.VERSION.SDK_INT >= 21) {
                s.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
            }
            web.setWebChromeClient(new WebChromeClient());
            web.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    hideSplash();
                }
            });
            // Fully offline local UI (works without Vercel)
            web.loadUrl("file:///android_asset/index.html");
        } catch (Throwable t) {
            TextView e = new TextView(this);
            e.setText("D4AI failed: " + t.getMessage());
            e.setTextColor(Color.WHITE);
            e.setBackgroundColor(NAVY);
            e.setGravity(Gravity.CENTER);
            setContentView(e);
        }
    }

    private FrameLayout buildSplash() {
        FrameLayout s = new FrameLayout(this);
        s.setBackgroundColor(NAVY);
        s.setLayoutParams(new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        final ImageView logo = new ImageView(this);
        try { logo.setImageResource(R.drawable.d4ai_logo); } catch (Throwable ignored) {}
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(dp(160), dp(160));
        lp.gravity = Gravity.CENTER;
        lp.bottomMargin = dp(24);
        logo.setLayoutParams(lp);
        logo.animate().scaleX(1.08f).scaleY(1.08f).setDuration(900).withEndAction(new Runnable() {
            @Override public void run() {
                try {
                    logo.animate().scaleX(1f).scaleY(1f).setDuration(900).withEndAction(this).start();
                } catch (Throwable ignored) {}
            }
        }).start();
        s.addView(logo);

        TextView title = new TextView(this);
        title.setText("D4AI");
        title.setTextColor(Color.WHITE);
        title.setTextSize(28f);
        title.setGravity(Gravity.CENTER);
        title.setLetterSpacing(0.12f);
        FrameLayout.LayoutParams tp = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tp.gravity = Gravity.CENTER;
        tp.topMargin = dp(100);
        title.setLayoutParams(tp);
        s.addView(title);

        TextView tag = new TextView(this);
        tag.setText("CREATING SOLUTION, INNOVATING LIVES");
        tag.setTextColor(Color.parseColor("#94a3b8"));
        tag.setTextSize(9f);
        tag.setLetterSpacing(0.15f);
        tag.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams gp = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        gp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        gp.bottomMargin = dp(48);
        tag.setLayoutParams(gp);
        s.addView(tag);
        return s;
    }

    private void hideSplash() {
        if (splash == null) return;
        splash.post(new Runnable() {
            @Override public void run() {
                try { if (splash != null) splash.setVisibility(View.GONE); } catch (Throwable ignored) {}
            }
        });
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onBackPressed() {
        try {
            if (web != null && web.canGoBack()) { web.goBack(); return; }
        } catch (Throwable ignored) {}
        super.onBackPressed();
    }
}
