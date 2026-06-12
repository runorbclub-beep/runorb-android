package com.cloud.runball.module;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cloud.runball.R;

import com.cloud.runball.databinding.LayoutWebBinding;

public class WebActivity extends AppCompatActivity {
    private LayoutWebBinding binding;
    WebView webView;
    Toolbar toolbar;
    TextView tvToolBarTitle;
    String url;
    String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LayoutWebBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        webView = binding.webView;
        url = this.getIntent().getStringExtra("url");
        title = this.getIntent().getStringExtra("title");
        if (title == null) {
            title = getString(R.string.app_name);
        }
        supportToolbar(title);
        initView();
    }

    protected void initView() {
        webView.setWebViewClient(webViewClient);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(true);
        webView.loadUrl(url);
    }

    protected void supportToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvToolBarTitle = (TextView) findViewById(R.id.tvToolBarTitle);
        toolbar.setTitle("");
        tvToolBarTitle.setText(title);
        //toolbar设置标题需要在setsupportActionbar（）的前面才有效。
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitleMargin(0, 5, 5, 5);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.btn_return);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void setNavigationTitle(String title) {
        if (toolbar != null) {
            toolbar.setTitle("");
            tvToolBarTitle.setText(title);
        }
    }

    /**
     * WebViewClient主要帮助WebView处理各种通知、请求事件
     */
    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //这里可以拦截url，做白名单
            return true;
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
