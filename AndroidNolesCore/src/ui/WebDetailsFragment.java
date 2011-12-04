/*
 * Copyright (C) 2011 Jonathan Steele
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

package com.itnoles.shared.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebDetailsFragment extends Fragment {
    private WebView mWebView;

	public static WebDetailsFragment newInstance(String urlString) {
	    final WebDetailsFragment f = new WebDetailsFragment();

		// Supply url and index input as an argument.
		final Bundle args = new Bundle();
		args.putString("url", urlString);
		f.setArguments(args);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    mWebView = new WebView(getActivity());
	    return mWebView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);

	    mWebView.getSettings().setJavaScriptEnabled(true);
	    mWebView.getSettings().setBuiltInZoomControls(true);
	    mWebView.setWebViewClient(new WebViewClient() {
	        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
	 	 	    Toast.makeText(getActivity(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
	 	    }
	    });
	    mWebView.loadUrl(getArguments().getString("url"));
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mWebView.freeMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mWebView != null) {
            mWebView.freeMemory();
            mWebView.destroy();
            mWebView = null;
        }
    }
}