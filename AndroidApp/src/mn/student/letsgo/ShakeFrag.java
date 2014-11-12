package mn.student.letsgo;

import java.util.HashMap;
import java.util.Map;

import mn.student.letsgo.common.ShakeListener.OnShakeListener;
import mn.student.letsgo.utils.CustomRequest;
import mn.student.letsgo.utils.MySingleton;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;

public class ShakeFrag extends Fragment implements OnShakeListener {
	private static final String ARG_SECTION_NUMBER = "section_number";
	private ProgressDialog progress;
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
//	private ShakeListener shake;
	private LocationManager lm;
	private MyLocationListener locationListener;
	private String lat;
	private String lng;
	private int radius = 1;
	private int mood = 0;
	private View v;

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
//		shake.pause();
		super.onPause();
	}

	@Override
	public void onResume() {
//		shake.resume();
//		shake.setOnShakeListener(this);
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.fragment_main, container, false);
//		shake = new ShakeListener(getActivity());
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
									Settings.ACTION_SECURITY_SETTINGS);
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
		locationListener = new MyLocationListener();
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10,
				locationListener);
	}

	private class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc) {

			lng = "" + loc.getLongitude();
			Log.v("dsas", lng);
			lat = " " + loc.getLatitude();
			Log.v("asdsa", lat);

		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	@Override
	public void onShake() {
		// TODO Auto-generated method stub
		progress = ProgressDialog.show(getActivity(), "",
				getString(R.string.loading));
		CustomRequest loginReq = new CustomRequest(Method.POST, getActivity()
				.getString(R.string.mainIp) + "place", null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.e("fb register", response.toString());

						progress.dismiss();
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

}
