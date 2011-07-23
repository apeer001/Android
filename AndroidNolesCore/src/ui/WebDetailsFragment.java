/*
 * Copyright (C) 2011 Jonathan Steele
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

public class WebDetailsFragment extends Fragment
{
    private WebView mWebView;

	public static WebDetailsFragment newInstance(String urlString)
	{
	    final WebDetailsFragment f = new WebDetailsFragment();

		// Supply url and index input as an argument.
		final Bundle args = new Bundle();
		args.putString("url", urlString);
		f.setArguments(args);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
	    mWebView = new WebView(getActivity());
	    return mWebView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
	    super.onActivityCreated(savedInstanceState);

	    mWebView.getSettings().setJavaScriptEnabled(true);
	    mWebView.getSettings().setBuiltInZoomControls(true);
	    mWebView.setWebViewClient(new WebViewClient() {
	        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
	 	    {
	 	 	    Toast.makeText(getActivity(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
	 	    }
	    });
	    mWebView.loadUrl(getArguments().getString("url"));
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mWebView.freeMemory();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mWebView.destroy();
    }
}