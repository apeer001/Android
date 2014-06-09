package com.inoles.collegesports;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;
import android.widget.ShareActionProvider;

/**
 * A fragment that displays a WebView.
 *
 * The WebView is automatically paused or resumed when the Fragment is paused or resumed.
 */
public class BrowserDetailFragment extends WebViewFragment {

    public static BrowserDetailFragment newInstance(String urlString) {
        BrowserDetailFragment f = new BrowserDetailFragment();

        // Supply url input as an argument.
        Bundle args = new Bundle();
        args.putString("url", urlString);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getWebView() == null) {
            return;
        }

        setHasOptionsMenu(true);

        WebSettings settings = getWebView().getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setUserAgentString("Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36");
        getWebView().setWebViewClient(new MyWebViewClient());

        if (getArguments() != null) {
            getWebView().loadUrl(getArguments().getString("url"));
        }
    }

    private static class MyWebViewClient extends WebViewClient {
        @Override
        public void onReceivedError(WebView view, int errorCode, String description,
                                    String failingUrl) {
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate menu resource file.
        inflater.inflate(R.menu.share_detail, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem menuItem = menu.findItem(R.id.menu_share);
        if (menuItem == null) {
            return;
        }

        // Set the share intent
        ShareActionProvider actionProvider = (ShareActionProvider) menuItem.getActionProvider();
        if (actionProvider != null && getWebView() != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, getWebView().getUrl());
            actionProvider.setShareIntent(shareIntent);
        }
    }
}
