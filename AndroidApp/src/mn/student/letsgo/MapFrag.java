package mn.student.letsgo;

import java.util.HashMap;
import java.util.Map;

import mn.student.letsgo.utils.CustomRequest;
import mn.student.letsgo.utils.MySingleton;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.Marker;

public class MapFrag extends Fragment implements OnInfoWindowClickListener {
	private GoogleMap mMap;
	private static View view;
	private static final String ARG_SECTION_NUMBER = "section_number";
	private UiSettings mUiSettings;
	private int radius = 10;
	private String lat;
	private String lng;

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

	public void getMapdata() {
		CustomRequest loginReq = new CustomRequest(Method.POST, getActivity()
				.getString(R.string.mainIp) + "nearby", null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.e("fb register", response.toString());

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

	public static MapFrag newInstance(int sectionNumber) {
		MapFrag fragment = new MapFrag();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public MapFrag() {
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
					.getSupportFragmentManager().findFragmentById(R.id.map))
					.getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap();
			}
		}
		// if (!fromBranch) {

		LocationManager lm = null;
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
			dialog.setMessage("GPS унтраастай байна");
			dialog.setPositiveButton("Нээх",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(
								DialogInterface paramDialogInterface,
								int paramInt) {
							// TODO Auto-generated method stub
							Intent myIntent = new Intent(
									Settings.ACTION_SECURITY_SETTINGS);
							startActivity(myIntent);
						}
					});
			dialog.setNegativeButton("Болих",
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

	private void setUpMap() {

		mUiSettings = mMap.getUiSettings();
		mMap.setMyLocationEnabled(true);

		// mMap.setLocationSource(this);
		// Log.i("my", mMap.getMyLocation().getLatitude()+"");
		mUiSettings.setMyLocationButtonEnabled(true);

		// mark
		// try {
		// branches = helper.getBranchDao().queryForAll();
		//
		// for (int i = 0; i < branches.size(); i++) {
		// Branch branch = branches.get(i);
		// if (branch.Lat != 0.0 || branch.Long != 0.0) {
		// LatLng lng = new LatLng(branch.Lat, branch.Long);
		// mMap.addMarker(new MarkerOptions()
		// // .icon(BitmapDescriptorFactory
		// // .fromResource(R.drawable.logo))
		// .title(branch.title).position(lng)
		// .snippet(branch.shortaddress));
		// }
		// }
		// } catch (SQLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		mMap.setOnInfoWindowClickListener(this);
		mMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				// TODO Auto-generated method stub
				Log.i("marker", marker.getTitle());
				marker.showInfoWindow();

				// findDirections(mMap.getMyLocation().getLatitude(), mMap
				// .getMyLocation().getLongitude(),
				// marker.getPosition().latitude,
				// marker.getPosition().longitude,
				// GMapV2Direction.MODE_DRIVING);
				return false;
			}
		});

	}

	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub

	}
}