package csci551.hw9;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class TabFragment2 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("fragment", "fragment2");

        Intent intent = getActivity().getIntent();
        final String input_text = intent.getStringExtra("input_text");

        View fragment2 = inflater.inflate(R.layout.content_tab_fragment2, container, false);

        WebView webView = (WebView) fragment2.findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);

        /* WebViewClient must be set BEFORE calling loadUrl!
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.e("symbol", a);
                webView.loadUrl("javascript:init("+a+")");
                // Log.v("start","finish");
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);
                // Log.v("start", "start");
            }
        });
        */

        webView.loadUrl("file:///android_asset/highcharts.html?symbol="+input_text);

        return fragment2;
    }
}
