using Android.App;
using Android.OS;
using Android.Views;

namespace Monolrc
{
	[Activity (Label = "BrowserDetailActivity")]			
	public class BrowserDetailActivity : Activity
	{
		protected override void OnCreate (Bundle bundle)
		{
			base.OnCreate (bundle);
			
			ActionBar.SetDisplayHomeAsUpEnabled (true);
			
			if (bundle == null) {
				var details = new BrowserDetailFragment();
				details.Arguments = Intent.Extras;
				FragmentTransaction transaction = FragmentManager.BeginTransaction();
				transaction.Add(Android.Resource.Id.Content, details).Commit();
			}
		}
		
		public override bool OnOptionsItemSelected (IMenuItem item)
		{
			if (item.ItemId == Android.Resource.Id.Home)
			{
				Finish();
			}
			return base.OnOptionsItemSelected (item);
		}
	}
}