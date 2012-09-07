using Android.App;
using Android.OS;

namespace Monolrc
{
	[Activity (Label = "MonoLRC", MainLauncher = true)]
	public class MainActivity : Activity
	{
		protected override void OnCreate (Bundle bundle)
		{
			base.OnCreate (bundle);

			// Set our view from the "main" layout resource
			SetContentView (Resource.Layout.Main);
			
			ActionBar.NavigationMode = ActionBarNavigationMode.Tabs;
			
			AddTab("News", NewsFragment.Instantiate(this, "NewsFragment", CreateBundleForURL("http://www.lewrockwell.com/rss.xml")));
			AddTab("Blogs", NewsFragment.Instantiate(this, "NewsFragment", CreateBundleForURL("http://www.lewrockwell.com/blog/feed")));
			AddTab("Threate", NewsFragment.Instantiate(this, "NewsFragment", CreateBundleForURL("http://www.lewrockwell.com/politicaltheatre/feed/")));
			AddTab("Link", new LinkFragment());
		}
		
		Bundle CreateBundleForURL (string url)
		{
			var bundle = new Bundle();
			bundle.PutString("url", url);
			return bundle;
		}
		
		void AddTab (string tabText, Fragment fragment)
		{
			var tab = ActionBar.NewTab ();
            tab.SetText (tabText);

			// must set event handler before adding tab
            tab.TabSelected += delegate(object sender, ActionBar.TabEventArgs e) {
                e.FragmentTransaction.Replace (Resource.Id.titles, fragment);
            };
			
			ActionBar.AddTab (tab);
		}
	}
}