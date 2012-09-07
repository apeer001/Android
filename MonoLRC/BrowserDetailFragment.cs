using Android.App;
using Android.Content;
using Android.OS;
using Android.Views;
using Android.Webkit;
using Android.Widget;

namespace Monolrc
{
	public class BrowserDetailFragment : Fragment
	{
		WebView mWebView;

		public override void OnActivityCreated (Bundle savedInstanceState)
		{
			base.OnActivityCreated (savedInstanceState);
			
			SetHasOptionsMenu(true);
			mWebView = View.FindViewById(Resource.Id.webview) as WebView;
			mWebView.Settings.SetSupportZoom(true);
			
			var progressBar = View.FindViewById(Resource.Id.empty_loading) as ProgressBar;
			mWebView.SetWebChromeClient(new DetailChromeClient(progressBar));
			mWebView.SetWebViewClient(new DetailWebClient());
			mWebView.LoadUrl(Arguments.GetString("url"));
		}
		
		public override View OnCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			return inflater.Inflate(Resource.Layout.browser_fragment, null);
		}
		
		public override void OnLowMemory ()
		{
			base.OnLowMemory();

			if (mWebView != null)
			{
				mWebView.FreeMemory();
			}
		}
		
		public override void OnDestroy ()
		{	
			if (mWebView != null)
			{
				mWebView.FreeMemory();
				mWebView.Destroy();
			}

			base.OnDestroy ();
		}
		
		public override void OnCreateOptionsMenu (IMenu menu, MenuInflater inflater)
		{
			inflater.Inflate(Resource.Menu.webdetail_share, menu);
			
			var item = menu.FindItem(Resource.Id.menu_share);
			var actionProvider = item.ActionProvider as ShareActionProvider;
			var shareIntent = new Intent(Intent.ActionSend);
			shareIntent.SetType("text/plain");
			shareIntent.PutExtra(Intent.ExtraText, Arguments.GetString("url"));
			actionProvider.SetShareIntent(shareIntent);
		}
	
	    class DetailChromeClient : WebChromeClient
		{
			ProgressBar mProgressBar;
			public DetailChromeClient(ProgressBar bar)
			{
				mProgressBar = bar;
			}

			public override void OnProgressChanged (WebView view, int newProgress)
			{
				mProgressBar.Progress = newProgress;
				mProgressBar .Visibility = ViewStates.Visible;
				if (newProgress == 100)
					 mProgressBar.Visibility = ViewStates.Gone;
			}
		}
		
		class DetailWebClient : WebViewClient
		{
			public override void OnReceivedError (WebView view, ClientError errorCode, string description, string failingUrl)
			{
			}
		}
	}
}