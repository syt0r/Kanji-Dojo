package ua.syt0r.kanji.ui

import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAdView
import ua.syt0r.kanji.R

private const val TEST_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"

@Composable
fun Ads(modifier: Modifier = Modifier) {

    AndroidView(
        modifier = modifier,
        factory = { context ->

            val adView = LayoutInflater.from(context)
                .inflate(R.layout.native_ads, null) as NativeAdView

            val adLoader = AdLoader.Builder(context, TEST_AD_UNIT_ID)
                .forNativeAd { nativeAd ->

                    adView.findViewById<TextView>(R.id.title).apply {
                        text = nativeAd.headline
                        adView.headlineView = this
                    }

                    nativeAd.icon?.drawable?.let {
                        adView.findViewById<ImageView>(R.id.icon).apply {
                            setImageDrawable(it)
                            adView.iconView = this
                        }
                    }

                    adView.findViewById<Button>(R.id.cta).apply {
                        text = nativeAd.callToAction
                        adView.callToActionView = this
                    }

                    adView.setNativeAd(nativeAd)
                }
                .build()

            adLoader.loadAd(AdRequest.Builder().build())

            adView

        }
    )

}