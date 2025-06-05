package com.example.charityua_android

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LiqPayWebViewActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DATA = "extra_data"
        const val EXTRA_SIGNATURE = "extra_signature"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val webView = WebView(this)
        setContentView(webView)

        val data = intent.getStringExtra(EXTRA_DATA)
        val signature = intent.getStringExtra(EXTRA_SIGNATURE)

        if (data.isNullOrEmpty() || signature.isNullOrEmpty()) {
            Toast.makeText(this, "Невірні параметри LiqPay", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (url != null && url.contains("/liqpay/redirect")) {
                    Toast.makeText(this@LiqPayWebViewActivity, "Оплата виконана!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        val liqPayHtml = """
            <html>
            <body onload="document.forms[0].submit()">
                <form action="https://www.liqpay.ua/api/3/checkout" method="post">
                    <input type="hidden" name="data" value="$data"/>
                    <input type="hidden" name="signature" value="$signature"/>
                </form>
            </body>
            </html>
        """.trimIndent()

        webView.loadDataWithBaseURL(null, liqPayHtml, "text/html", "utf-8", null)
    }
}
