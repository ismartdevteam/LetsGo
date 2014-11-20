package mn.student.letsgo.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mn.student.letsgo.R;
import mn.student.letsgo.model.Places;
import mn.student.letsgo.text.Bold;
import mn.student.letsgo.text.Light;
import mn.student.letsgo.utils.CircleImageView;
import mn.student.letsgo.utils.CustomRequest;
import mn.student.letsgo.utils.MySingleton;
import mn.student.letsgo.utils.Utils;
import mn.student.letsgo.walkthrough.WalkThrough;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.facebook.Session;

public class UserAc extends ActionBarActivity {
	private SharedPreferences preferences;

	private Bold name;
	private static Light visits;
	CircleImageView user_img;
	private ImageLoader mImageLoader;

	private ActionBar bar;
	private static String UserId;
	private String userFeed[] = new String[2];
	ViewPager pager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_profile);
		userFeed[0] = getString(R.string.myActivity);
		userFeed[1] = getString(R.string.myPlace);
		bar = getSupportActionBar();
		bar.setDisplayShowHomeEnabled(true);
		bar.setHomeButtonEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(
				R.color.light_blue)));
		name = (Bold) findViewById(R.id.user_name);
		visits = (Light) findViewById(R.id.user_visit);
		user_img = (CircleImageView) findViewById(R.id.user_img);
		pager = (ViewPager) findViewById(R.id.user_pager);
		preferences = getSharedPreferences("user", MODE_PRIVATE);

		mImageLoader = MySingleton.getInstance(this).getImageLoader();
		UserId = preferences.getString("my_id", "0");
		name.setText(preferences.getString("username", ""));

		user_img.setImageUrl(preferences.getString("pro_img", ""), mImageLoader);
		pager.setAdapter(new PagerAdapter(getSupportFragmentManager(), userFeed));
	}

	public class PagerAdapter extends FragmentStatePagerAdapter {
		String[] title;

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public PagerAdapter(FragmentManager fm, String[] title) {
			super(fm);
			this.title = title;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return title[position];
		}

		@Override
		public int getCount() {
			return title.length;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return UserTabFrag.newInstance(position);
			case 1:
				return UserPlaceFrag.newInstance(position);
			default:
				return UserTabFrag.newInstance(position);

			}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.user, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		// if (id == R.id.action_search_ad) {
		// mPagerAdapter.filterCar();
		// }
		if (id == R.id.action_logout) {
			AlertDialog.Builder build = new AlertDialog.Builder(UserAc.this);
			build.setTitle(R.string.logout_title);
			build.setMessage(R.string.logout_not);
			build.setNegativeButton(R.string.no, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
			build.setPositiveButton(R.string.yes, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					finish();
					Session session = Session.getActiveSession();
					if (session != null) {

						if (!session.isClosed()) {
							session.closeAndClearTokenInformation();
							// clear your preferences if saved
						}
					} else {

						session = new Session(UserAc.this);
						Session.setActiveSession(session);

						session.closeAndClearTokenInformation();
						// clear your preferences if saved

					}
					preferences.edit().clear().commit();
					Intent intent = new Intent(UserAc.this, WalkThrough.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			});
			build.create().show();
		}
		if (id == android.R.id.home)
			onBackPressed();
		return true;
	}

	public static class UserTabFrag extends Fragment implements
			OnScrollListener, OnRefreshListener {

		private int index = 0;
		private ListView mListView;
		private List<UserActivity> mListItems;
		private View load_footer;
		private UserAcAdapter adapter;
		private boolean isFinish = false;
		private boolean flag_loading = false;
		private View v;
		int mNum;
		TextView nodata;
		SwipeRefreshLayout swipeLayout;

		public static Fragment newInstance(int num) {

			UserTabFrag f = new UserTabFrag();
			Bundle b = new Bundle();
			b.putInt("num", num + 1);
			f.setArguments(b);
			return f;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			// mPosition = getArguments().getInt(ARG_POSITION);
			mNum = getArguments() != null ? getArguments().getInt("num") : 1;

		}

		@SuppressWarnings("deprecation")
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			v = inflater.inflate(R.layout.fragment_list, container, false);
			mListView = (ListView) v.findViewById(R.id.listView);
			nodata = (TextView) v.findViewById(R.id.list_nodata);
			load_footer = inflater.inflate(R.layout.list_load_footer,
					mListView, false);
			mListView.addFooterView(load_footer);
			swipeLayout = (SwipeRefreshLayout) v
					.findViewById(R.id.swipe_container);
			swipeLayout.setOnRefreshListener(this);
			swipeLayout.setColorScheme(R.color.dark_blue, R.color.dark_red,
					R.color.dark_yellow, R.color.dark_green);
			return v;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			mListItems = new ArrayList<UserActivity>();

			adapter = new UserAcAdapter(getActivity(), mListItems);
			mListView.setAdapter(adapter);

			if (Utils.isNetworkAvailable(getActivity())) {
				getActivityFeed(index, UserAc.UserId, false);
			} else
				Toast.makeText(getActivity(),
						getActivity().getString(R.string.noNet),
						Toast.LENGTH_SHORT).show();
			mListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					// Bundle b = new Bundle();
					// b.putInt("ad_id", mListItems.get(position - 1).id);

					// Intent adIntent = new Intent(getActivity(),
					// AdDetail.class);
					// adIntent.putExtras(b);
					// startActivity(adIntent);
				}
			});
		}

		private void getActivityFeed(int sIndex, String user_id,
				final boolean refresh) {
			mListView.addFooterView(load_footer);
			CustomRequest adReq = new CustomRequest(Method.GET,
					this.getString(R.string.mainIp) + "activity/user_id="
							+ user_id + "&index=" + sIndex, null,
					new Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {
							try {
								if (response != null
										&& response.getInt("response") == 1) {

									int num_rows = response
											.getInt("data_count");
									Log.i("row", num_rows + "");
									if (num_rows < 10) {
										isFinish = true;
									}
									if (num_rows == 0
											&& adapter.getCount() == 0)
										nodata.setVisibility(View.VISIBLE);
									else {
										nodata.setVisibility(View.GONE);
									}

									index = index + 10;
									JSONArray data = response
											.getJSONArray("data");

									makeAc(data, refresh);

								}
								flag_loading = false;
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();

							}
							mListView.removeFooterView(load_footer);
							if (refresh)
								swipeLayout.setRefreshing(false);
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							// TODO Auto-generated method stub
							Log.i("error", error.getMessage() + "");
							mListView.removeFooterView(load_footer);
							if (refresh)
								swipeLayout.setRefreshing(false);
						}

					}) {

			};
			MySingleton.getInstance(getActivity()).addToRequestQueue(adReq);
		}

		protected void makeAc(JSONArray data, boolean refresh)
				throws JSONException {
			// TODO Auto-generated method stub
			if (data.length() > 0) {
				visits.setText(data.getJSONObject(0).getInt("visits") + "");
				if (refresh)
					mListItems.clear();
				for (int i = 0; i < data.length(); i++) {
					UserActivity ac = new UserActivity();
					JSONObject obj = data.getJSONObject(i);
					ac.place_id = obj.getInt("place_id");
					ac.place_name = obj.getString("place_name");
					ac.place_img = obj.getString("place_img");
					ac.user_id = obj.getInt("user_id");
					ac.title = obj.getString("title");
					ac.rating = obj.getInt("rating");

					ac.image = obj.getString("image_url");
					ac.created_date = obj.getString("created_date");

					mListItems.add(ac);

				}
			} else {
				isFinish = true;
			}

			adapter.notifyDataSetChanged();

			mListView.setOnScrollListener(this);
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

			if (firstVisibleItem + visibleItemCount == totalItemCount
					&& totalItemCount != 0) {
				if (flag_loading == false && isFinish == false) {
					flag_loading = true;
					getActivityFeed(index, UserAc.UserId, false);
				}
			}
		}

		@Override
		public void onRefresh() {
			// TODO Auto-generated method stub
			index = 0;
			getActivityFeed(index, UserId, true);
		}

	}

	public static class UserPlaceFrag extends Fragment implements
			OnScrollListener, OnRefreshListener {

		private int index = 0;
		private ListView mListView;
		private List<Places> mListItems;
		private View load_footer;
		private UserPlaceAdapter adapter;
		private boolean isFinish = false;
		private boolean flag_loading = false;
		private View v;
		int mNum;
		TextView nodata;
		SwipeRefreshLayout swipeLayout;

		public static Fragment newInstance(int num) {

			UserPlaceFrag f = new UserPlaceFrag();
			Bundle b = new Bundle();
			b.putInt("num", num + 1);
			f.setArguments(b);
			return f;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			// mPosition = getArguments().getInt(ARG_POSITION);
			mNum = getArguments() != null ? getArguments().getInt("num") : 1;

		}

		@SuppressWarnings("deprecation")
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			v = inflater.inflate(R.layout.fragment_list, container, false);
			mListView = (ListView) v.findViewById(R.id.listView);
			nodata = (TextView) v.findViewById(R.id.list_nodata);
			load_footer = inflater.inflate(R.layout.list_load_footer,
					mListView, false);
			mListView.addFooterView(load_footer);
			swipeLayout = (SwipeRefreshLayout) v
					.findViewById(R.id.swipe_container);
			swipeLayout.setOnRefreshListener(this);
			swipeLayout.setColorScheme(R.color.dark_blue, R.color.dark_red,
					R.color.dark_yellow, R.color.dark_green);
			return v;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			mListItems = new ArrayList<Places>();

			adapter = new UserPlaceAdapter(getActivity(), mListItems);
			mListView.setAdapter(adapter);

			if (Utils.isNetworkAvailable(getActivity())) {
				getPlaces(index, UserAc.UserId, false);
			} else
				Toast.makeText(getActivity(),
						getActivity().getString(R.string.noNet),
						Toast.LENGTH_SHORT).show();
			mListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					// Bundle b = new Bundle();
					// b.putInt("ad_id", mListItems.get(position - 1).id);

					// Intent adIntent = new Intent(getActivity(),
					// AdDetail.class);
					// adIntent.putExtras(b);
					// startActivity(adIntent);
				}
			});
		}

		private void getPlaces(int sIndex, String user_id, final boolean refresh) {
			mListView.addFooterView(load_footer);
			CustomRequest adReq = new CustomRequest(Method.POST,
					this.getString(R.string.mainIp) + "myPlace", null,
					new Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {
							Log.e("responce", response + "");
							try {
								if (response != null
										&& response.getInt("response") == 1) {

									int num_rows = response
											.getInt("data_count");
									Log.i("row", num_rows + "");
									if (num_rows < 10) {
										isFinish = true;
									}
									if (num_rows == 0
											&& adapter.getCount() == 0)
										nodata.setVisibility(View.VISIBLE);
									else {
										nodata.setVisibility(View.GONE);
									}

									index = index + 10;
									JSONArray data = response
											.getJSONArray("data");

									makeAc(data, refresh);

								}
								flag_loading = false;
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();

							}
							mListView.removeFooterView(load_footer);
							if (refresh)
								swipeLayout.setRefreshing(false);
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							// TODO Auto-generated method stub
							Log.i("error", error.getMessage() + "");
							mListView.removeFooterView(load_footer);
							if (refresh)
								swipeLayout.setRefreshing(false);
						}

					}) {
				@Override
				protected Map<String, String> getParams() {
					Map<String, String> params = new HashMap<String, String>();

					params.put("user_id", UserId + "");
					params.put("index", index + "");

					return params;
				}

			};
			MySingleton.getInstance(getActivity()).addToRequestQueue(adReq);
		}

		protected void makeAc(JSONArray data, boolean refresh)
				throws JSONException {
			// TODO Auto-generated method stub
			if (data.length() > 0) {
				if (refresh)
					mListItems.clear();
				for (int i = 0; i < data.length(); i++) {
					Places ac = new Places();
					JSONObject obj = data.getJSONObject(i);
					ac.id = obj.getInt("id");
					ac.name = obj.getString("name");
					ac.image = obj.getString("image");
					ac.visits = obj.getInt("visits");
					ac.desc = obj.getString("desc");
					ac.service = obj.getString("service");
					ac.lat = obj.getDouble("lat");
					ac.lng = obj.getDouble("lng");
					ac.category = obj.getString("category_id");
					ac.address = obj.getString("address");
					ac.phone = obj.getString("phone");
					ac.time_schedule = obj.getString("time_schedule");

					ac.rating = obj.getDouble("rating");
					ac.ratedPeople = obj.getInt("rated_people");
					ac.image = obj.getString("image");
					ac.created_date = obj.getString("formatted_date");

					mListItems.add(ac);

				}
			} else {
				isFinish = true;
			}

			adapter.notifyDataSetChanged();

			mListView.setOnScrollListener(this);
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

			if (firstVisibleItem + visibleItemCount == totalItemCount
					&& totalItemCount != 0) {
				if (flag_loading == false && isFinish == false) {
					flag_loading = true;
					getPlaces(index, UserAc.UserId, false);
				}
			}
		}

		@Override
		public void onRefresh() {
			// TODO Auto-generated method stub
			index = 0;
			getPlaces(index, UserId, true);
		}

	}
}
