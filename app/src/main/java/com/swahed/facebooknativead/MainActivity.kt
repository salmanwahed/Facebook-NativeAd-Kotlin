package com.swahed.facebooknativead

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import com.facebook.ads.*
import kotlinx.android.synthetic.main.native_ad_layout.view.*
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val keyhash = generateHashId()
        Log.d(TAG, "KeyHasy: $keyhash")
        AdSettings.addTestDevice(keyhash)
        loadNativeAd()
    }

    fun generateHashId(): String{
        lateinit var keyhash: String
        val packageInfo: PackageInfo = packageManager.getPackageInfo("com.swahed.facebooknativead",
            PackageManager.GET_SIGNATURES)
        for (signature in packageInfo.signatures){
            val messageDigest = MessageDigest.getInstance("SHA")
            messageDigest.update(signature.toByteArray())
            keyhash = Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT)
        }
        return keyhash
    }

    fun loadNativeAd(){
        val nativeAd = NativeAd(this, "YOUR_PLACEMENT_ID")
        nativeAd.setAdListener(object : NativeAdListener{
            override fun onAdClicked(ad: Ad?) {
                Log.d(TAG, "Native ad clicked!");
            }

            override fun onMediaDownloaded(p0: Ad?) {
                Log.e(TAG, "Native ad finished downloading all assets.");
            }

            override fun onError(ad: Ad?, adError: AdError?) {
                Log.e(TAG, "Native ad failed to load: " + adError?.errorMessage)
            }

            override fun onAdLoaded(ad: Ad?) {
                Log.d(TAG, "Native ad is loaded and ready to be displayed!")
                inflateAd(nativeAd)
            }

            override fun onLoggingImpression(ad: Ad?) {
                Log.d(TAG, "Native ad impression logged!")
            }

        })

        nativeAd.loadAd()

    }

    private fun inflateAd(nativeAd: NativeAd){
        nativeAd.unregisterView()
        val nativeAdContainer = findViewById<ViewGroup>(R.id.native_ad_container)
        val adView = LayoutInflater.from(this)
            .inflate(R.layout.native_ad_layout, nativeAdContainer, false) as LinearLayout

        nativeAdContainer.addView(adView)

        val adChoiceContainter = findViewById<LinearLayout>(R.id.ad_choices_container)
        val adChoiceView = AdChoicesView(this, nativeAd, true)
        adChoiceContainter.addView(adChoiceView, 0)

        val nativeadIcon = adView.native_ad_icon
        val nativeAdTitle = adView.native_ad_title
        val nativeAdMedia = adView.native_ad_media
        val nativeAdSocialContext = adView.native_ad_social_context
        val nativeAdBody = adView.native_ad_body
        val nativeAdSponsoredLabel = adView.native_ad_sponsored_label
        val nativeAdCallToAction = adView.native_ad_call_to_action

        nativeAdTitle.text = nativeAd.advertiserName
        nativeAdBody.text = nativeAd.adBodyText
        nativeAdSocialContext.text = nativeAd.adSocialContext
        nativeAdCallToAction.text = nativeAd.adCallToAction
        nativeAdSponsoredLabel.text = nativeAd.sponsoredTranslation
        nativeAdCallToAction.visibility =if (nativeAd.hasCallToAction()) VISIBLE else GONE

        val clickableViews: ArrayList<View> = arrayListOf()
        clickableViews.add(nativeAdTitle)
        clickableViews.add(nativeAdCallToAction)

        nativeAd.registerViewForInteraction(adView, nativeAdMedia, nativeadIcon, clickableViews)
    }

    companion object {
        const val TAG: String = "AppTAG"
    }
}
