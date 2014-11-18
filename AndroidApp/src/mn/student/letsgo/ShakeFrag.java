package mn.student.letsgo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mn.student.letsgo.common.ShakeListener.OnShakeListener;
import mn.student.letsgo.model.Mood;
import mn.student.letsgo.model.Places;
import mn.student.letsgo.text.Bold;
import mn.student.letsgo.text.Light;
import mn.student.letsgo.text.Regular;
import mn.student.letsgo.utils.CircleImageView;
import mn.student.letsgo.utils.CustomRequest;
import mn.student.letsgo.utils.MySingleton;
import mn.student.letsgo.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;

public class ShakeFrag extends Fragment implements OnShakeListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {
	private static final String ARG_SECTION_NUMBER = "section_number";
	private ProgressDialog progress;
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	// private ShakeListener shake;
	LocationClient mLocationClient;
	private LocationManager lm;
	// private MyLocationListener locationListener;
	private String lat;
	private ImageView shake;
	private String lng;
	private int radius = 1000;
	private int mood = 0;
	private View v;
	private Vibrator vibrator;
	private List<Mood> moodList;
	private Dialog placeDialog;

	public static ShakeFrag newInstance(int sectionNumber) {
		ShakeFrag fragment = new ShakeFrag();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public ShakeFrag() {
	}

	@Override
	public void onPause() {
		// shake.pause();
		super.onPause();
	}

	@Override
	public void onResume() {
		// shake.resume();
		// shake.setOnShakeListener(this);
		super.onResume();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mLocationClient = new LocationClient(getActivity(), this, this);
		vibrator = (Vibrator) getActivity().getSystemService(
				Context.VIBRATOR_SERVICE);
		v = inflater.inflate(R.layout.shake, container, false);
		shake = (ImageView) v.findViewById(R.id.shake_img);
		shake.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onShake();
			}
		});
		// shake = new ShakeListener(getActivity());
		return v;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		if (Utils.isNetworkAvailable(getActivity()))
			getMood();
		LocationCheck();
	}

	private void LocationCheck() {
		lm = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		boolean gps_enabled = false, network_enabled = false;
		if (lm == null)
			lm = (LocationManager) getActivity().getSystemService(
					Context.LOCATION_SERVICE);
		try {
			gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
		}
		try {
			network_enabled = lm
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
		}

		if (!gps_enabled && !network_enabled) {
			Builder dialog = new AlertDialog.Builder(getActivity());
			dialog.setMessage(getActivity().getString(R.string.onGPS));
			dialog.setPositiveButton(getActivity().getString(R.string.yes),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(
								DialogInterface paramDialogInterface,
								int paramInt) {
							Intent myIntent = new Intent(
									Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivity(myIntent);
						}
					});
			dialog.setNegativeButton(getActivity().getString(R.string.no),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(
								DialogInterface paramDialogInterface,
								int paramInt) {
							// TODO Auto-generated method stub

						}
					});
			dialog.show();

		}
	}

	@Override
	public void onShake() {
		// TODO Auto-generated method stub
		// setupLoc();
		vibrator.vibrate(500);
		progress = ProgressDialog.show(getActivity(), "",
				getString(R.string.loading));
		CustomRequest loginReq = new CustomRequest(Method.POST, getActivity()
				.getString(R.string.mainIp) + "place", null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.e("place responce", response.toString());
						progress.dismiss();
						try {
							if (response.getInt("response") == 1) {
								Places place = new Places();
								if (!response.getBoolean("google_place")) {
									final JSONObject obj = response
											.getJSONObject("data");

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
									place.visits = obj.getInt("visits");
									place.category = obj
											.getString("category_name");
									place.mood = obj.getString("mood_name");
									place.distance = obj.getDouble("distance");
									place.user_id = obj.getInt("user_id");
									place.username = obj.getString("username");
									place.user_img = obj.getString("user_img");

								}

								showPlace(place);
							} else {
								Toast.makeText(
										getActivity(),
										getActivity()
												.getString(R.string.noData),
										Toast.LENGTH_SHORT).show();
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
				if (lat != null && lng != null) {
					params.put("lat", lat + "");
					params.put("lng", lng + "");
				} else {
					params.put("lat", "47.9200516");
					params.put("lng", "106.8830923");
				}
				params.put("range", radius + "");
				if (mood != 0) {
					Log.i("selMood id", mood + "");
					params.put("mood", mood + "");
				}
				return params;
			}
		};
		MySingleton.getInstance(getActivity()).addToRequestQueue(loginReq);

	}

	private void showPlace(final Places place) {
		placeDialog = new Dialog(getActivity(), R.style.CustomDialogTheme);
		placeDialog.setContentView(R.layout.choosenplace);
		CircleImageView image = (CircleImageView) placeDialog
				.findViewById(R.id.place_img);
		Button go = (Button) placeDialog.findViewById(R.id.place_letsgo);
		go.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				placeDialog.dismiss();
				sendLetGo(place);
			}
		});
		ImageLoader loader = MySingleton.getInstance(getActivity())
				.getImageLoader();
		Regular name = (Regular) placeDialog.findViewById(R.id.place_name);
		Regular cat = (Regular) placeDialog.findViewById(R.id.place_cat);
		Bold distance = (Bold) placeDialog.findViewById(R.id.place_distance);
		Light mood = (Light) placeDialog.findViewById(R.id.place_mood);
		CircleImageView user_image = (CircleImageView) placeDialog
				.findViewById(R.id.place_user_img);
		Light username = (Light) placeDialog.findViewById(R.id.place_username);
		cat.setText(place.category.replace("_", " ").toUpperCase());
		distance.setText(Utils.numberToFormat((int) (place.distance * 1000))
				+ "m");

		mood.setText(place.mood);

		user_image.setImageUrl(place.user_img, loader);

		username.setText(place.username);

		image.setImageUrl(getActivity().getString(R.string.mainIpImage)
				+ place.image.replace("/public", ""), loader);

		name.setText(place.name);
		placeDialog.show();
	}

	private void sendLetGo(final Places place) {
		final SharedPreferences proSp = getActivity().getSharedPreferences(
				"user", 0);
		if (proSp.getBoolean("login", false)) {
			CustomRequest letgoReq = new CustomRequest(Method.POST,
					getActivity().getString(R.string.mainIp) + "letsgo", null,
					new Response.Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {
							Log.e("place responce", response.toString());

						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							Log.e("place responce", error.getMessage());
						}
					}) {

				@Override
				protected Map<String, String> getParams() {
					Map<String, String> params = new HashMap<String, String>();
					params.put("user_id", proSp.getString("my_id", "0") + "");
					params.put("place_id", place.id + "");
					return params;
				}
			};
			MySingleton.getInstance(getActivity()).addToRequestQueue(letgoReq);
		}
		getActivity()
				.getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.container,
						ShowMap.newInstance(place.lat, place.lng))
				.addToBackStack(place.name).commit();

	}

	public void setupLoc() {
		// Get the current location's latitude & longitude
		Location currentLocation = mLocationClient.getLastLocation();

		lat = Double.toString(currentLocation.getLatitude());
		lng = Double.toString(currentLocation.getLongitude());
		Log.v("long", lng);
		Log.v("lat", lat);

	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		if (arg0.hasResolution()) {
			try {
				arg0.startResolutionForResult(getActivity(), 0);
			} catch (IntentSender.SendIntentException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.i("location", "connected");

	}

	@Override
	public void onDisconnected() {
		Log.i("location", "disconnected");

	}

	@Override
	public void onStart() {
		super.onStart();
		// Connect the client.
		mLocationClient.connect();
	}

	@Override
	public void onStop() {
		// Disconnect the client.
		mLocationClient.disconnect();
		super.onStop();
	}

	private void moodData(JSONArray data) throws JSONException {
		moodList = new ArrayList<Mood>();
		Mood all = new Mood();
		all.id = 0;
		all.name = getString(R.string.all);
		moodList.add(all);
		for (int i = 0; i < data.length(); i++) {
			Mood mood = new Mood();
			JSONObject obj = data.getJSONObject(i);
			mood.id = obj.getInt("id");
			mood.name = obj.getString("name");
			moodList.add(mood);
		}
		// ActionBar bar = ((ActionBarActivity) getActivity())
		// .getSupportActionBar();
		// bar.setListNavigationCallbacks(
		// new MoodAdapter(getActivity(), moodList), this);

	}

	private class MoodAdapter extends ArrayAdapter<Mood> {
		Context mContext;

		public MoodAdapter(Context context, List<Mood> objects) {
			super(context, 0, 0, objects);
			this.mContext = context;
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
			return getCustomView(position, cnvtView, prnt);
		}

		@Override
		public View getView(int pos, View cnvtView, ViewGroup prnt) {
			return getCustomView(pos, cnvtView, prnt);
		}

		public View getCustomView(int position, View v, ViewGroup parent) {
			ViewHolder hol;
			if (v == null) {
				v = ((Activity) mContext).getLayoutInflater().inflate(
						R.layout.navi_menu_item, parent, false);
				hol = new ViewHolder();
				hol.name = (Bold) v.findViewById(R.id.list_item);
				v.setTag(hol);

			} else
				hol = (ViewHolder) v.getTag();
			hol.name.setText(getItem(position).name);
			return v;
		}

		class ViewHolder {
			Bold name;
		}
	}

	private void getMood() {
		CustomRequest loginReq = new CustomRequest(Method.GET, getActivity()
				.getString(R.string.mainIp) + "mood", null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {

						try {
							if (response.getInt("response") == 1) {
								moodData(response.getJSONArray("data"));
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("getMood", error.getMessage());

					}
				}) {

		};
		MySingleton.getInstance(getActivity()).addToRequestQueue(loginReq);
	}

	@Override
	public void onLocationChanged(Location currentLocation) {
		// TODO Auto-generated method stub
		lat = Double.toString(currentLocation.getLatitude());
		lng = Double.toString(currentLocation.getLongitude());
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (!MainActivity.mNavigationDrawerFragment.isDrawerOpen()) {
			inflater.inflate(R.menu.shake, menu);
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_mood:
			final Dialog moodDialog = new Dialog(getActivity(),
					R.style.CustomDialogTheme);
			moodDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			moodDialog.setContentView(R.layout.mood_spinner_dialog);
			final Spinner spin = (Spinner) moodDialog
					.findViewById(R.id.mood_spinner);
			spin.setAdapter(new MoodAdapter(getActivity(), moodList));
			spin.setSelection(mood);
			Button done = (Button) moodDialog.findViewById(R.id.mood_done);
			done.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					moodDialog.dismiss();
					Mood selMood = (Mood) spin.getSelectedItem();
					mood = selMood.id;
					// onShake();

				}
			});
			moodDialog.show();
			break;
		}

		return true;

	}
}
