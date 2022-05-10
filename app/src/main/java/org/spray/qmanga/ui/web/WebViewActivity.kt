package org.spray.qmanga.ui.web

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import org.spray.qmanga.R
import org.spray.qmanga.databinding.ActivityWebviewBinding
import org.spray.qmanga.network.UserAgentInterceptor
import org.spray.qmanga.ui.base.BaseActivity

@SuppressLint("SetJavaScriptEnabled")
class WebViewActivity : BaseActivity<ActivityWebviewBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityWebviewBinding.inflate(layoutInflater))

        val title = intent.getStringExtra(TITLE_KEY)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = title ?: String()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

        val url = intent.getStringExtra(URL_KEY)
        val destroyUrl = intent.getStringExtra(DESTROY_URL)

        with(binding) {
            webView.settings.javaScriptEnabled = true
            webView.settings.userAgentString = UserAgentInterceptor.userAgent
            webView.addJavascriptInterface(WebAppInterface(this@WebViewActivity), "Android")

            webView.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)

                    progressBar.setProgressCompat(newProgress, true)
                    if (newProgress >= 100) {
                        progressBar.visibility = View.GONE
                        progressBar.setProgressCompat(0, false)
                    } else progressBar.visibility = View.VISIBLE
                }
            }

            webView.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    toolbar.title = view?.title ?: String()

                    if (url == destroyUrl) {
                        close()
                    }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    toolbar.title = view?.title ?: String()
                }
            }

            if (savedInstanceState != null)
                return

            if (url.isNullOrEmpty()) {
                finishAfterTransition()
            } else {
                binding.webView.loadUrl(url)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.webView.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.webView.restoreState(savedInstanceState)
    }


    override fun onPause() {
        binding.webView.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.webView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.webView.destroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            close()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun close() {
        binding.webView.stopLoading()
        setResult(RESULT_OK)
        finishAfterTransition()
    }

    companion object {

        private const val TITLE_KEY = "title"
        private const val URL_KEY = "url"
        private const val DESTROY_URL = "destroy_url"

        fun createIntent(context: Context, title: String, url: String, destroy: String? = null): Intent {
            return Intent(context, WebViewActivity::class.java).apply {
                putExtra(TITLE_KEY, title)
                putExtra(URL_KEY, url)

                if (destroy != null)
                    putExtra(DESTROY_URL, destroy)
            }
        }
    }

    class WebAppInterface(private val mContext: Context) {

        @JavascriptInterface
        fun showToast(toast: String) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
        }
    }
}