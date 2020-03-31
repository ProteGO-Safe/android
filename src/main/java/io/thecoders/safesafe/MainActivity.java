package io.thecoders.safesafe;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import androidx.annotation.Nullable;

public class MainActivity extends Activity {
  private WebView webView;

  @Override protected void onCreate(@Nullable Bundle inState) {
    super.onCreate(inState);
    setContentView(R.layout.content);
    webView = findViewById(R.id.webContent);
    webView.getSettings().setJavaScriptEnabled(true);
    webView.loadUrl("https://safesafe.thecoders.io/");
  }

  @Override public void onBackPressed() {
    if (webView.canGoBack()) {
      webView.goBack();
    } else {
      super.onBackPressed();
    }
  }
}
