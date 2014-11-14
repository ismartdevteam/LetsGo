package mn.student.letsgo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mn.student.letsgo.common.ShakeListener;
import mn.student.letsgo.common.ShakeListener.OnShakeListener;
import mn.student.letsgo.model.Mood;
import mn.student.letsgo.text.Bold;
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
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener,
		ActionBar.OnNavigationListener {
	private static final String ARG_SECTION_NUMBER = "section_number";
	private ProgressDialog progress;
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	private ShakeListener shake;
	LocationClient mLocationClient;
	private LocationManager lm;
	// private MyLocationListener locationListener;
	private String lat;
	private String lng;
	private int radius = 1000;
	private int mood = 0;
	private View v;
	private Vibrator vibrator;
	private List<Mood> moodList;

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
		shake.pause();
		super.onPause();
	}

	@Override
	public void onResume() {
		shake.resume();
		shake.setOnShakeListener(this);
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mLocationClient = new LocationClient(getActivity(), this, this);
		vibrator = (Vibrator) getActivity().getSystemService(
				Context.VIBRATOR_SERVICE);
		v = inflater.inflate(R.layout.shake, container, false);
		shake = new ShakeListener(getActivity());
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
		// locationListener = new MyLocationListener();
		// lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10,
		// locationListener);
	}

	public void turnGPSOn() {
		Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
		intent.putExtra("enabled", true);
		getActivity().sendBroadcast(intent);

		String provider = Settings.Secure.getString(getActivity()
				.getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (!provider.contains("gps")) { // if gps is disabled
			final Intent poke = new Intent();
			poke.setClassName("com.android.settings",
					"com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			getActivity().sendBroadcast(poke);

		}
	}

	@Override
	public void onShake() {
		// TODO Auto-generated method stub
		setupLoc();
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
							if (response.getInt("response") == 1
									&& !response.getBoolean("google_place")) {
								final JSONObject obj = response.getJSONObject("data");
								Dialog dialog = new Dialog(
										getActivity(),
										android.R.style.Theme_DeviceDefault_Dialog);
								dialog.setContentView(R.layout.choosenplace);
								CircleImageView image = (CircleImageView) dialog
										.findViewById(R.id.place_img);
								Button go = (Button) dialog
										.findViewById(R.id.place_letsgo);
								go.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										try {
											sendLetGo(obj.getInt("id"));
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								});
								Regular name = (Regular) dialog
										.findViewById(R.id.place_name);
								ImageLoader loader = MySingleton.getInstance(
										getActivity()).getImageLoader();
								image.setImageUrl(obj.getString("image"),
										loader);
								name.setText(obj.getString("name"));
								dialog.show();

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
				params.put("lat", lat + "");
				params.put("lng", lng + "");
				params.put("range", radius + "");
				if (mood != 0)
					params.put("mood", mood + "");
				return params;
			}
		};
		MySingleton.getInstance(getActivity()).addToRequestQueue(loginReq);

	}

	private void sendLetGo(final int place_id) {
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
					params.put("place_id", place_id + "");
					return params;
				}
			};
			MySingleton.getInstance(getActivity()).addToRequestQueue(letgoReq);
		}
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
		Toast.makeText(getActivity(),
				"Connection Failure : " + arg0.getErrorCode(),
				Toast.LENGTH_SHORT).show();
		if (arg0.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				arg0.startResolutionForResult(getActivity(), 0);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		Log.i("location", "connected");

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
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
		for (int i = 0; i < data.length(); i++) {
			Mood mood = new Mood();
			JSONObject obj = data.getJSONObject(i);
			mood.id = obj.getInt("id");
			mood.name = obj.getString("name");
			moodList.add(mood);
		}
		ActionBar bar = ((ActionBarActivity) getActivity())
				.getSupportActionBar();
		bar.setListNavigationCallbacks(
				new MoodAdapter(getActivity(), moodList), this);

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
						R.layout.navi_menu_item, null);
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
						Log.i("mood", response + "");
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
	public boolean onNavigationItemSelected(int arg0, long arg1) {
		// TODO Auto-generated method stub
		return false;
	}
}
