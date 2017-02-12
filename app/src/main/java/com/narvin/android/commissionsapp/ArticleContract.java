package com.narvin.android.commissionsapp;

import android.graphics.drawable.Drawable;

/**
 * Created by michaeldnarvaez on 1/26/17.
 */

public class ArticleContract {

    private Drawable mImage;
    private String mHeader;
    private String mSubHeader;
    private String mUrl;

    /**
     * Public Constructor
     * @param url ArticleContract Url to be passed on to the webView or share features
     * @param header ArticleContract main title
     * @param image ArticleContract thumbnail
     * @param subHeader ArticleContract sub header
     */
    public ArticleContract(String url, String header, Drawable image, String subHeader) {
        mUrl = url;
        mHeader = header;
        mImage = image;
        mSubHeader = subHeader;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getSubHeader() {
        return mSubHeader;
    }

    public Drawable getImage() {
        return mImage;
    }

    public String getHeader() {
        return mHeader;
    }

}
