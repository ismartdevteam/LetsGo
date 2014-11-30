package mn.student.letsgo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mn.student.letsgo.model.Places;
import mn.student.letsgo.text.Bold;
import mn.student.letsgo.text.Light;
import mn.student.letsgo.text.Regular;
import mn.student.letsgo.user.UserAc;
import mn.student.letsgo.user.UserActivity;
import mn.student.letsgo.user.UserComAdapter;
import mn.student.letsgo.utils.CircleImageView;
import mn.student.letsgo.utils.CustomRequest;
import mn.student.letsgo.utils.MySingleton;
import mn.student.letsgo.utils.Utils;
import net.danlew.android.joda.DateUtils;
import net.danlew.android.joda.JodaTimeAndroid;

import org.apache.http.ParseException;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class PlaceDet extends ActionBarActivity implements OnClickListener,
		OnScrollListener {
	Bundle b;
	private View profileView;
	private ProgressDialog progress;
	private String id;
	private String user_id;
	private ImageLoader loader;
	private CircleImageView image;
	private CircleImageView panelimage;
	private Light panelTitle;
	private ActionBar bar;
	private Places place;
	private EditText comEdit;
	private SlidingUpPanelLayout mLayout;
	private ListView mListView;
	private List<UserActivity> mListItems;
	private View load_footer;
	private UserComAdapter adapter;
	private LinearLayout photo;
	private ImageView comHolder;
	private Bold send;
	private boolean isFinish = false;
	private boolean flag_loading = false;
	private int index = 0;
	private SharedPreferences proSp;
	private CircleImageView pro_img;
	private static final int SELECT_PHOTO1 = 100;
	private Uri imageUri;
	private String response;

	@Override
	public void onBackPressed() {
		if (mLayout != null && mLayout.isPanelExpanded()
				|| mLayout.isPanelAnchored()) {
			mLayout.collapsePanel();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.place_det);
		loader = MySingleton.getInstance(this).getImageLoader();
		mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
		mListView = (ListView) findViewById(R.id.place_det_reviewList);
		panelimage = (CircleImageView) findViewById(R.id.place_det_panel_user_img);
		panelTitle = (Light) findViewById(R.id.place_det_panel_title);
		panelTitle.setText(getString(R.string.noReview));
		load_footer = getLayoutInflater().inflate(R.layout.list_load_footer,
				mListView, false);
		mListView.addFooterView(load_footer);
		mListItems = new ArrayList<UserActivity>();

		adapter = new UserComAdapter(this, mListItems);
		mListView.setAdapter(adapter);
		b = getIntent().getExtras();
		id = b.getString("id");
		if (Utils.isNetworkAvailable(this)) {
			getActivityFeed(index, id, false);
		} else
			Toast.makeText(this, getString(R.string.noNet), Toast.LENGTH_SHORT)
					.show();
		JodaTimeAndroid.init(this);

		getData();
		bar = getSupportActionBar();
		bar.setDisplayShowHomeEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setHomeButtonEnabled(true);
		bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(
				R.color.light_red)));
		proSp = getSharedPreferences("user", 0);
		if (proSp.getBoolean("login", false)) {
			user_id = proSp.getString("my_id", "1");
			profileView = getLayoutInflater().inflate(R.layout.add_comment,
					mListView, false);
			pro_img = (CircleImageView) profileView
					.findViewById(R.id.place_com_user_img);
			pro_img.setImageUrl(proSp.getString("pro_img", ""), loader);

			comEdit = (EditText) profileView.findViewById(R.id.place_com_title);
			photo = (LinearLayout) profileView.findViewById(R.id.place_com_img);
			comHolder = (ImageView) profileView
					.findViewById(R.id.place_com_img_holder);
			photo.setOnClickListener(this);
			send = (Bold) profileView.findViewById(R.id.place_com_send);
			send.setOnClickListener(this);
		} else {

			profileView = getLayoutInflater().inflate(R.layout.no_profile,
					mListView, false);
			profileView.setOnClickListener(this);

		}

		mListView.addHeaderView(profileView);
	}

	private void getData() {
		progress = ProgressDialog.show(PlaceDet.this, "",
				getString(R.string.loading));
		CustomRequest loginReq = new CustomRequest(Method.POST,
				getString(R.string.mainIp) + "placeDet", null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {

						JSONObject obj = null;
						try {
							obj = response.getJSONObject("data");

							progress.dismiss();
							{

								place = new Places();
								place.id = obj.getInt("id");
								place.name = obj.getString("name");
								place.desc = obj.getString("desc");
								place.image = obj.getString("image");
								place.address = obj.getString("address");
								place.phone = obj.getString("phone");
								place.service = obj.getString("service");
								place.lat = obj.getDouble("lat");
								place.lng = obj.getDouble("lng");
								place.time_schedule = obj
										.getString("time_schedule");
								place.created_date = obj
										.getString("formatted_date");
								place.visits = obj.getInt("visits");
								place.category = obj.getString("category_name");
								place.mood = obj.getString("mood_name");
								place.user_id = obj.getInt("user_id");
								place.username = obj.getString("username");
								place.user_img = obj.getString("user_img");
								place.mood_id = obj.getInt("mood_id");
								showPlace();

							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

						progress.dismiss();
					}
				}) {

			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("place_id", id + "");

				return params;
			}
		};
		MySingleton.getInstance(this).addToRequestQueue(loginReq);
	}

	protected void showPlace() {
		// TODO Auto-generated method stub
		image = (CircleImageView) findViewById(R.id.place_det_img);

		Regular name = (Regular) findViewById(R.id.place_det_name);
		Light mood = (Light) findViewById(R.id.place_det_mood);
		Light desc = (Light) findViewById(R.id.place_det_desc);
		Light address = (Light) findViewById(R.id.place_det_address);
		Light service = (Light) findViewById(R.id.place_det_service);

		Light times = (Light) findViewById(R.id.place_det_timeSchedule);
		Light phone = (Light) findViewById(R.id.place_det_phone);
		Light create_date = (Light) findViewById(R.id.place_det_date);
		Light visit = (Light) findViewById(R.id.place_det_visit);
		CircleImageView user_image = (CircleImageView) findViewById(R.id.place_det_user_img);
		Light username = (Light) findViewById(R.id.place_det_username);
		desc.setText(place.desc);
		address.setText(place.address);
		service.setText(place.service);

		phone.setText(place.phone);
		phone.setOnClickListener(this);
		bar.setTitle(place.category.replace("_", " ").toUpperCase());

		mood.setText(place.mood);
		visit.setText(place.visits + " " + getString(R.string.visited));
		user_image.setImageUrl(place.user_img, loader);

		username.setText(place.username);

		image.setImageUrl(
				getString(R.string.mainIpImage)
						+ place.image.replace("/public", ""), loader);
		times.setText(place.time_schedule);
		name.setText(place.name);
		String dates[] = place.created_date.split("-");

		DateTime time = new DateTime(Integer.parseInt(dates[0]),
				Integer.parseInt(dates[1]), Integer.parseInt(dates[2]),
				Integer.parseInt(dates[3]), Integer.parseInt(dates[4]),
				Integer.parseInt(dates[5]));

		create_date.setText(DateUtils.getRelativeTimeSpanString(PlaceDet.this,
				time) + "");
	}

	private void getActivityFeed(int sIndex, String place_id,
			final boolean refresh) {
		mListView.addFooterView(load_footer);
		CustomRequest adReq = new CustomRequest(Method.GET,
				this.getString(R.string.mainIp) + "comment/place_id="
						+ place_id + "&index=" + sIndex, null,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						try {
							if (response != null
									&& response.getInt("response") == 1) {

								int num_rows = response.getInt("data_count");
								Log.i("row", num_rows + "");
								if (num_rows < 10) {
									isFinish = true;
								}

								index = index + 10;
								JSONArray data = response.getJSONArray("data");

								makeAc(data, refresh);

							}
							flag_loading = false;
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();

						}
						mListView.removeFooterView(load_footer);

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.i("error", error.getMessage() + "");
						mListView.removeFooterView(load_footer);

					}

				}) {

		};
		MySingleton.getInstance(PlaceDet.this).addToRequestQueue(adReq);
	}

	protected void makeAc(JSONArray data, boolean refresh) throws JSONException {
		// TODO Auto-generated method stub
		if (data.length() > 0) {
			JSONObject first = data.getJSONObject(0);
			if (refresh)
				mListItems.clear();
			panelimage.setImageUrl(first.getString("profile_img"), loader);
			panelTitle.setText(first.getString("title"));
			for (int i = 0; i < data.length(); i++) {
				UserActivity ac = new UserActivity();
				JSONObject obj = data.getJSONObject(i);
				ac.place_id = obj.getInt("place_id");
				ac.place_name = obj.getString("name");
				ac.place_img = obj.getString("profile_img");
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.comment, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == android.R.id.home)
			onBackPressed();
		// if (id == R.id.action_add_com) {
		//
		// }
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == profileView) {

			startActivity(new Intent(PlaceDet.this, UserAc.class));
		}
		if (v == photo) {
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent, ""),
					SELECT_PHOTO1);
		}
		if (v == send) {
			if (comEdit.getText().length() < 1) {
				Toast.makeText(PlaceDet.this, getString(R.string.writeReview),
						Toast.LENGTH_SHORT).show();
				return;
			}
			progress = ProgressDialog.show(PlaceDet.this, "",
					getString(R.string.loading));
			String title = "title=" + comEdit.getText() + "&";
			String rating = "rating=3&";
			String reqString = title + rating + "user_id=" + user_id + "&"
					+ "place_id=" + id;
			String requestURL = getString(R.string.mainIp) + "addComment";
			try {

				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
						.permitAll().build();
				StrictMode.setThreadPolicy(policy);
				if (imageUri != null) {
					response = AddPlaceAc.multipartRequest(requestURL,
							reqString, Utils.getPath(PlaceDet.this, imageUri),
							"file");
				} else {
					response = AddPlaceAc.multipartRequest(requestURL,
							reqString);
				}

				progress.dismiss();
				Log.e("responce", response);
				if (response.equals("error"))
					Toast.makeText(PlaceDet.this, getString(R.string.addErr),
							Toast.LENGTH_SHORT).show();
				else {
					JSONObject obj = new JSONObject(response);
					if (obj.getInt("response") == 1) {
						Toast.makeText(PlaceDet.this,
								getString(R.string.addSuc), Toast.LENGTH_SHORT)
								.show();
						comHolder.setImageBitmap(null);
						comEdit.setText("");
						index = 0;
						getActivityFeed(index, id, true);
					} else {
						Toast.makeText(PlaceDet.this,
								getString(R.string.addErr), Toast.LENGTH_SHORT)
								.show();
					}
				}

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				progress.dismiss();
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				progress.dismiss();
				e.printStackTrace();
			} catch (JSONException e) {
				progress.dismiss();
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		switch (v.getId()) {
		case R.id.place_det_phone:
			Utils.openCallIntent(PlaceDet.this, place.phone);
			break;

		default:
			break;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		if (firstVisibleItem + visibleItemCount == totalItemCount
				&& totalItemCount != 0) {
			if (flag_loading == false && isFinish == false) {
				flag_loading = true;
				getActivityFeed(index, id, false);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case SELECT_PHOTO1:
			if (resultCode == RESULT_OK) {
				if (data != null) {
					imageUri = data.getData();
					try {
						Bitmap bitmap = getThumbnail(imageUri);
						comHolder.setImageBitmap(bitmap);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
			break;

		}
	}

	public Bitmap getThumbnail(Uri uri) throws FileNotFoundException,
			IOException {
		InputStream input = getContentResolver().openInputStream(uri);

		BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
		onlyBoundsOptions.inJustDecodeBounds = true;
		onlyBoundsOptions.inDither = true;// optional
		onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
		BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
		input.close();
		if ((onlyBoundsOptions.outWidth == -1)
				|| (onlyBoundsOptions.outHeight == -1))
			return null;

		int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight
				: onlyBoundsOptions.outWidth;

		double ratio = (originalSize > 500) ? (originalSize / 500) : 1.0;

		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
		bitmapOptions.inDither = true;// optional
		bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
		input = getContentResolver().openInputStream(uri);
		Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
		input.close();
		return bitmap;
	}

	private static int getPowerOfTwoForSampleRatio(double ratio) {
		int k = Integer.highestOneBit((int) Math.floor(ratio));
		if (k == 0)
			return 1;
		else
			return k;
	}

}
