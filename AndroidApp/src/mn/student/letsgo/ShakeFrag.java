package mn.student.letsgo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mn.student.letsgo.common.ShakeListener;
import mn.student.letsgo.common.ShakeListener.OnShakeListener;
import mn.student.letsgo.model.Mood;
import mn.student.letsgo.model.PlaceGoogle;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
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
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener,
		OnSeekBarChangeListener {
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
	// private ImageView shake;
	private String lng;
	private int radius = 1;
	private int mood = 0;
	private View v;
	private Vibrator vibrator;
	private List<Mood> moodList;
	private Dialog placeDialog;
	private SharedPreferences proSp;
	private Spinner spin;
	private SeekBar seekDistance;

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
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		proSp = getActivity().getSharedPreferences("user", 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mLocationClient = new LocationClient(getActivity(), this, this);
		vibrator = (Vibrator) getActivity().getSystemService(
				Context.VIBRATOR_SERVICE);
		v = inflater.inflate(R.layout.shake, container, false);
		spin = (Spinner) v.findViewById(R.id.mood_spinner);
		seekDistance = (SeekBar) v.findViewById(R.id.seekDistance);
		// shake = (ImageView) v.findViewById(R.id.shake_img);
		// shake.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// onShake();
		// }
		// });
		shake = new ShakeListener(getActivity());
		seekDistance.setOnSeekBarChangeListener(this);

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
							if (response.getInt("response") == 1) {

								final JSONObject obj = response
										.getJSONObject("data");
								if (!response.getBoolean("google_place")) {

									Places place = new Places();
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
									place.mood_id = obj.getInt("mood_id");
									showPlace(place);
								} else {
									PlaceGoogle goo = new PlaceGoogle();
									goo.name = obj.getString("name");
									try {
										goo.address = obj
												.getString("formatted_address");
									} catch (JSONException e) {
									}
									try {
										goo.phone = obj
												.getString("international_phone_number");
									} catch (JSONException e) {
									}
									goo.icon = obj.getString("icon");
									JSONObject loc = obj.getJSONObject(
											"geometry").getJSONObject(
											"location");
									goo.lat = loc.getDouble("lat");
									goo.lng = loc.getDouble("lng");
									try {
										JSONArray weektext = obj.getJSONObject(
												"opening_hours").getJSONArray(
												"weekday_text");
										goo.schedule = "";
										for (int i = 0; i < weektext.length(); i++) {
											goo.schedule = goo.schedule.concat(" "
													+ weektext.getString(i));
										}
									} catch (JSONException e) {
									}
									try {
										goo.rating = obj.getDouble("rating");
										goo.ratedPeople = obj
												.getInt("user_ratings_total");
									} catch (JSONException e) {
									}
									try {
										goo.website = obj.getString("website");
									} catch (JSONException e) {
									}
									try {
										goo.image = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=600&photoreference="
												+ obj.getJSONArray("photos")
														.getJSONObject(0)
														.getString(
																"photo_reference")
												+ "&key=AIzaSyCco2DtZ2Zgr16tJTfeqZCBTcDjIF0NGsQ";
									} catch (JSONException e) {
									}
									try {
										goo.url = obj.getString("url");
									} catch (JSONException e) {
									}
									JSONArray type = obj.getJSONArray("types");
									goo.category = "";
									for (int k = 0; k < type.length(); k++) {
										goo.category = goo.category.concat(" "
												+ type.getString(k).replace(
														"_", ""));

									}
									showPlace(goo);
								}

							} else {
								Toast.makeText(
										getActivity(),
										getActivity().getString(
												R.string.noData_Mood),
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
				params.put("user_id", proSp.getString("my_id", "0") + "");
				params.put("range", radius + "");
				if (mood != 0) {

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
		Button det = (Button) placeDialog.findViewById(R.id.place_detail);
		det.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Bundle b = new Bundle();
				b.putString("id", place.id + "");
				Intent det = new Intent(getActivity(), PlaceDet.class);
				det.putExtras(b);
				getActivity().startActivity(det);
			}
		});
		ImageLoader loader = MySingleton.getInstance(getActivity())
				.getImageLoader();
		Regular name = (Regular) placeDialog.findViewById(R.id.place_name);
		Regular cat = (Regular) placeDialog.findViewById(R.id.place_cat);
		Bold distance = (Bold) placeDialog.findViewById(R.id.place_distance);
		Light mood = (Light) placeDialog.findViewById(R.id.place_mood);
		Light visit = (Light) placeDialog.findViewById(R.id.place_visit);
		CircleImageView user_image = (CircleImageView) placeDialog
				.findViewById(R.id.place_user_img);
		Light username = (Light) placeDialog.findViewById(R.id.place_username);
		cat.setText(place.category.replace("_", " ").toUpperCase());
		distance.setText(Utils.numberToFormat((int) (place.distance * 1000))
				+ "m");

		mood.setText(place.mood);
		visit.setText(place.visits + " " + getString(R.string.visited));
		user_image.setImageUrl(place.user_img, loader);

		username.setText(place.username);

		image.setImageUrl(getActivity().getString(R.string.mainIpImage)
				+ place.image.replace("/public", ""), loader);

		name.setText(place.name);
		placeDialog.show();
	}

	private void showPlace(final PlaceGoogle place) {
		placeDialog = new Dialog(getActivity(), R.style.CustomDialogTheme);
		placeDialog.setContentView(R.layout.choosenplace);
		CircleImageView image = (CircleImageView) placeDialog
				.findViewById(R.id.place_img);
		Button go = (Button) placeDialog.findViewById(R.id.place_letsgo);
		Button det = (Button) placeDialog.findViewById(R.id.place_detail);

		go.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				placeDialog.dismiss();
				getActivity()
						.getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.container,
								ShowMap.newInstance(place.lat, place.lng, 0))
						.addToBackStack(place.name).commit();

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
		cat.setText(place.name);
		distance.setText(place.rating + place.ratedPeople
				+ getActivity().getString(R.string.rated));

		mood.setText(place.category.replace("_", " "));

		user_image.setImageUrl(place.icon, loader);

		username.setText(" Google Places API");

		image.setImageUrl(place.image, loader);
		if (place.website != null && place.website.length() > 1) {
			det.setText(getActivity().getString(R.string.showWebSite));
			det.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Utils.openWebIntent(getActivity(), place.website);
				}
			});

		} else {
			det.setVisibility(View.GONE);
		}
		name.setText(place.name);
		placeDialog.show();
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		((MainActivity) getActivity()).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}

	private void sendLetGo(final Places place) {

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
				.replace(
						R.id.container,
						ShowMap.newInstance(place.lat, place.lng, place.mood_id))
				.addToBackStack(place.name).commit();

	}

	public void setupLoc() {
		// Get the current location's latitude & longitude
		Location currentLocation = mLocationClient.getLastLocation();

		lat = Double.toString(currentLocation.getLatitude());
		lng = Double.toString(currentLocation.getLongitude());
		Log.v("long", lng + "");
		Log.v("lat", lat + "");

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
		all.name = getActivity().getString(R.string.all);
		moodList.add(all);
		for (int i = 0; i < data.length(); i++) {
			Mood mood = new Mood();
			JSONObject obj = data.getJSONObject(i);
			mood.id = obj.getInt("id");
			mood.name = obj.getString("name");
			moodList.add(mood);
		}

		spin.setAdapter(new MoodAdapter(getActivity(), moodList));
		spin.setSelection(mood);
		spin.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Mood sel = (Mood) spin.getAdapter().getItem(position);
				mood = sel.id;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				mood = 0;
			}
		});
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

		}

		return true;

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		radius = progress + 1;
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}
}
