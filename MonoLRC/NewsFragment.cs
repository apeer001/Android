using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Xml.Linq;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Views;
using Android.Widget;

namespace Monolrc
{
	public class NewsFragment : ListFragment {
		public override void OnActivityCreated (Bundle savedInstanceState)
		{
			base.OnActivityCreated (savedInstanceState);
			
			SetHasOptionsMenu(true);
			
			GetRssContentFromUrl(Arguments.GetString("url"));
		}
		
		public override void OnCreateOptionsMenu (IMenu menu, MenuInflater inflater)
		{
			inflater.Inflate(Resource.Menu.feed_fragment, menu);
		}
		
		public override bool OnOptionsItemSelected (IMenuItem item)
		{
			if (item.ItemId == Resource.Id.menu_refresh)
			{
				GetRssContentFromUrl(Arguments.GetString("url"));
			}
			return base.OnOptionsItemSelected (item);
		}
		
		protected void GetRssContentFromUrl(string urlString)
		{
			var client = new WebClient();
			client.DownloadStringAsync(new Uri(urlString));
			client.DownloadStringCompleted += (sender, e) =>
			{
				var tempList = XElement.Parse(e.Result)
					.Descendants("item")
					.Select(item => new News()
					{
						Title = (string) item.Element("title"),
						PubDate = (string) item.Element("pubDate"),
						Link = (string) item.Element("link"),
						Description = (string) item.Element("description")
					}).ToList();
				Activity.RunOnUiThread(delegate
				{
					ListAdapter = new NewsListAdapter(Activity, tempList);
				});
			};
		}
		
		public override void OnListItemClick (ListView l, View v, int position, long id)
		{
			News news = (News) ListAdapter.GetItem (position);
			var intent = new Intent(Activity, typeof(BrowserDetailActivity));
			intent.PutExtra("url", news.Link);
			StartActivity(intent);
		}

		class NewsListAdapter : ArrayAdapter<News>
		{
			LayoutInflater mInflater;

			internal NewsListAdapter(Context context, IList<News> data) : base(context, 0, data)
			{
				mInflater = LayoutInflater.From(context);
			}

			public override View GetView (int position, View convertView, ViewGroup parent)
			{
				// A ViewHolder keeps references to children views to avoid unneccessary calls
				// to findViewById() on each row.
				ViewHolder holder;

				// When convertView is not null, we can reuse it directly, there is no need
				// to reinflate it. We only inflate a new View when the convertView supplied
				// by ListView is null.
				if (convertView == null) {
					convertView = mInflater.Inflate(Resource.Layout.headlines_item, null);
					
					holder = new ViewHolder();
					holder.Initialize(convertView);
					convertView.Tag = holder;
				} else {
					holder = (ViewHolder) convertView.Tag;
				}

				// Bind the data efficiently with the holder.
				News news = GetItem(position);
				holder.MTitle.Text = news.Title;
				holder.MDate.Text = news.PubDate;
				holder.MDesc.Text = news.Description;

				return convertView;
			}

			// extend Java.Lang.Object or you will run into all kinds of type/cast issues when trying to push/pull on the View.Tag
			class ViewHolder : Java.Lang.Object
			{
				public TextView MTitle;
				public TextView MDate;
				public TextView MDesc;
				
				public void Initialize(View view)
				{
					MTitle = view.FindViewById(Resource.Id.title) as TextView;
					MDate = view.FindViewById(Resource.Id.date) as TextView;
					MDesc = view.FindViewById(Resource.Id.description) as TextView;
				}
			}
		}
	}
}