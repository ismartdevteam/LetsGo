package mn.student.letsgo;

import java.util.HashMap;
import java.util.Map;

import mn.student.letsgo.utils.CustomRequest;
import mn.student.letsgo.utils.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFrag extends Fragment implements OnInfoWindowClickListener,
		LocationListener {
	private GoogleMap mMap;
	private static View view;
	private static final String ARG_SECTION_NUMBER = "section_number";
	private UiSettings mUiSettings;
	private int radius = 10;
	private int[] markers = { R.drawable.marker_mood_2,
			R.drawable.marker_mood_3, R.drawable.marker_mood_4,
			R.drawable.marker_mood_5, R.drawable.marker_mood_6,
			R.drawable.marker_mood_7, R.drawable.marker_mood_8,
			R.drawable.marker_mood_9, R.drawable.marker_mood_10,
			R.drawable.marker_mood_11 };
	private LocationManager locationManager;
	private static final long MIN_TIME = 400;
	private static final float MIN_DISTANCE = 1000;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}
		try {

			view = inflater.inflate(R.layout.search_place, container, false);

		} catch (InflateException e) {
		}

		return view;

	}

	private void getMapdata(final double lat, final double lng) {
		CustomRequest loginReq = new CustomRequest(Method.POST, getActivity()
				.getString(R.string.mainIp) + "nearby", null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						try {
							if (response.getInt("response") == 1) {
								JSONArray data = response.getJSONArray("data");
								for (int i = 0; i < data.length(); i++) {
									JSONObject item = data.getJSONObject(i);

									LatLng loc = new LatLng(item
											.getDouble("lat"), item
											.getDouble("lng"));
									int markerIcon = markers[item
											.getInt("mood_id") - 1];
									mMap.addMarker(new MarkerOptions()
											.title(item.getString("name") + "-"
													+ item.getInt("id"))
											.icon(BitmapDescriptorFactory
													.fromResource(markerIcon))
											.snippet(
													item.getString("address")
															+ " "
															+ item.getString("phone"))
											.position(loc));

								}
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.i("nearby place", error.getMessage());
					}
				}) {

			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("lat", lat + "");
				params.put("lng", lng + "");
				params.put("range", radius + "");

				return params;
			}
		};
		MySingleton.getInstance(getActivity()).addToRequestQueue(loginReq);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		setUpMapIfNeeded();

	}

	public static MapsFrag newInstance(int sectionNumber) {
		MapsFrag fragment = new MapsFrag();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}

	@Override
	public void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.

		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getActivity()
					.getSupportFragmentManager().findFragmentById(R.id.mmap))
					.getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap();
			}
		}

	}

	private void setUpMap() {

		mUiSettings = mMap.getUiSettings();
		mMap.setMyLocationEnabled(true);

		mUiSettings.setMyLocationButtonEnabled(true);

		mUiSettings = mMap.getUiSettings();
		mMap.setMyLocationEnabled(true);
		mMap.setOnInfoWindowClickListener(this);
		mUiSettings.setMyLocationButtonEnabled(true);

		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);

	}

	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub
		Bundle b = new Bundle();

		b.putString("id", arg0.getTitle().split("-")[1]);
		Intent adIntent = new Intent(getActivity(), PlaceDet.class);
		adIntent.putExtras(b);
		startActivity(adIntent);
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		LatLng latLng = new LatLng(location.getLatitude(),
				location.getLongitude());
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,
				12);
		mMap.animateCamera(cameraUpdate);
		getMapdata(location.getLatitude(), location.getLongitude());
		locationManager.removeUpdates(this);

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}
}