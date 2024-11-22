package jo.toybreeze;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SearchAddressActivity extends AppCompatActivity {
    private static final String TAG = SearchAddressActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_address);

        WebView webview = findViewById(R.id.webview);

        webview.getSettings().setJavaScriptEnabled(true);
        webview.addJavascriptInterface(new BridgeInterface(), "Android");
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Android -> Javascript 함수 호출
                webview.loadUrl("javascript:sample2_execDaumPostcode();");
            }
        });

        // 최초 웹뷰 로드
        webview.loadUrl("https://searchaddress-edbe3.web.app");
    }

    private class BridgeInterface {
        @JavascriptInterface
        public void processDATA(String data) {
            // 카카오 주소 검색 API 검색 결과 값이 브릿지 통로를 통해 전달 받는다. (from javascript)
            Intent intent = new Intent();
            intent.putExtra("data", data);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
