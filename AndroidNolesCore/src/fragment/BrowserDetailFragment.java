/*
 * Copyright (C) 2013 Jonathan Steele
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.itnoles.shared.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.itnoles.shared.R;

/**
 * A fragment that displays a WebView.
 *
 * The WebView is automically paused or resumed when the Fragment is paused or resumed.
 */
public class BrowserDetailFragment extends SherlockFragment {
    private WebView mWebView;
    private ProgressBar mProgressBar;

    public static BrowserDetailFragment newInstance(String urlString) {
        final BrowserDetailFragment f = new BrowserDetailFragment();

        // Supply url input as an argument.
        final Bundle args = new Bundle();
        args.putString("url", urlString);
        f.setArguments(args);
        return f;
    }

    /**
     * Called to instantiate the view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.browser_fragment, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        mProgressBar = (ProgressBar) getView().findViewById(R.id.empty_loading);

        mWebView = (WebView) getView().findViewById(R.id.webview);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);

        mWebView.setWebChromeClient(new WebChromeClient() {
            // Set Progress Bar %
            public void onProgressChanged(WebView view, int newProgress) {
                mProgressBar.setProgress(newProgress);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            }

            // Visible Progress Bar
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            // Hide Progress Bar
            @Override
            public void onPageFinished(WebView view, String url) {
                mProgressBar.setVisibility(View.GONE);
            }
        });

        mWebView.loadUrl(getArguments().getString("url"));
    }

    /**
     * Called when the overall system is running low on memory.
     * Free Memory on WebView if it is not null
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();

        if (mWebView != null) {
            mWebView.freeMemory();
        }
    }

    /**
     * Called when the fragment is no longer in use.
     * Free memory and destroy the internal state of the WebView.
     */
    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.freeMemory();
            mWebView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate menu resource file.
        inflater.inflate(R.menu.webdetail_share, menu);

        // Locate MenuItem with ShareActionProvider
        final MenuItem menuItem = menu.findItem(R.id.menu_share);

        // Set the share intent
        final ShareActionProvider actionProvider = (ShareActionProvider) menuItem.getActionProvider();
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getArguments().getString("url"));
        actionProvider.setShareIntent(shareIntent);
    }
}