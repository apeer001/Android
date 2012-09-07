using Android.App;
using Android.Content;
using Android.OS;
using Android.Widget;

namespace Monolrc
{
	public class LinkFragment : ListFragment
	{
		public override void OnActivityCreated (Bundle savedInstanceState)
		{
			base.OnActivityCreated (savedInstanceState);
			
			string[] item = {"Facebook", "Twitter", "Store"};
			ListAdapter = new ArrayAdapter<string>(Activity, Android.Resource.Layout.SimpleListItem1, item);
		}
		
		public override void OnListItemClick (ListView l, Android.Views.View v, int position, long id)
		{
			string[] value = {"https://www.facebook.com/LewRockwell", "https://twitter.com/#!/lewrockwell", "https://www.lewrockwell.com/store"};
			var intent = new Intent(Activity, typeof(BrowserDetailActivity));
			intent.PutExtra("url", value[position]);
			StartActivity(intent);
		}
	}
}