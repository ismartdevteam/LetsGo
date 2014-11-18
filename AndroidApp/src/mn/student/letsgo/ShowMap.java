package mn.student.letsgo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class ShowMap extends Fragment implements OnInfoWindowClickListener {
	private GoogleMap mMap;
	private static View view;
	private UiSettings mUiSettings;

	private Double lat;
	private Double lng;

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

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		setUpMapIfNeeded();

	}

	public static ShowMap newInstance(double lat, double lng) {
		ShowMap fragment = new ShowMap();
		Bundle args = new Bundle();
		args.putDouble("lat", lat);
		args.putDouble("lng", lng);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(8);
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

		mUiSettings.setMyLocationButtonEnabled(true);

		LatLng loc = new LatLng(lat, lng);
		mMap.addMarker(new MarkerOptions()

		.position(loc));
		findDirections(mMap.getMyLocation().getLatitude(), mMap.getMyLocation()
				.getLongitude(), loc.latitude, loc.longitude, "driving");
	}

	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// mPosition = getArguments().getInt(ARG_POSITION);
		lat = getArguments().getDouble("lat");
		lng = getArguments().getDouble("lng");

	}

	public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints) {
		PolylineOptions rectLine = new PolylineOptions().width(3).color(
				Color.RED);

		for (int i = 0; i < directionPoints.size(); i++) {
			rectLine.add(directionPoints.get(i));
		}
		Polyline newPolyline = mMap.addPolyline(rectLine);
	}

	@SuppressWarnings("unchecked")
	public void findDirections(double fromPositionDoubleLat,
			double fromPositionDoubleLong, double toPositionDoubleLat,
			double toPositionDoubleLong, String mode) {
		Map<String, String> map = new HashMap<String, String>();
		map.put(GetDirectionsAsyncTask.USER_CURRENT_LAT,
				String.valueOf(fromPositionDoubleLat));
		map.put(GetDirectionsAsyncTask.USER_CURRENT_LONG,
				String.valueOf(fromPositionDoubleLong));
		map.put(GetDirectionsAsyncTask.DESTINATION_LAT,
				String.valueOf(toPositionDoubleLat));
		map.put(GetDirectionsAsyncTask.DESTINATION_LONG,
				String.valueOf(toPositionDoubleLong));
		map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);

		GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(
				getActivity());
		asyncTask.execute(map);
	}

	public class GetDirectionsAsyncTask extends
			AsyncTask<Map<String, String>, Object, ArrayList<LatLng>> {

		public static final String USER_CURRENT_LAT = "user_current_lat";
		public static final String USER_CURRENT_LONG = "user_current_long";
		public static final String DESTINATION_LAT = "destination_lat";
		public static final String DESTINATION_LONG = "destination_long";
		public static final String DIRECTIONS_MODE = "directions_mode";

		private Exception exception;

		private ProgressDialog progressDialog;
		private Context activity;

		public GetDirectionsAsyncTask(Context activity/* String url */) {
			super();
			this.activity = activity;
		}

		public void onPreExecute() {
			progressDialog = ProgressDialog.show(activity, "Getting direction",
					"loading");
		}

		@Override
		public void onPostExecute(ArrayList<LatLng> result) {
			progressDialog.dismiss();

			if (exception == null) {
				handleGetDirectionsResult(result);
			} else {
				processException();
			}
		}

		@Override
		protected ArrayList<LatLng> doInBackground(
				Map<String, String>... params) {

			Map<String, String> paramMap = params[0];
			try {
				LatLng fromPosition = new LatLng(Double.valueOf(paramMap
						.get(USER_CURRENT_LAT)), Double.valueOf(paramMap
						.get(USER_CURRENT_LONG)));
				LatLng toPosition = new LatLng(Double.valueOf(paramMap
						.get(DESTINATION_LAT)), Double.valueOf(paramMap
						.get(DESTINATION_LONG)));
				GMapV2Direction md = new GMapV2Direction();
				Document doc = md.getDocument(fromPosition, toPosition,
						paramMap.get(DIRECTIONS_MODE));
				ArrayList<LatLng> directionPoints = md.getDirection(doc);
				return directionPoints;

			} catch (Exception e) {
				exception = e;
				return null;
			}
		}

		private void processException() {
			Toast.makeText(activity, "error", Toast.LENGTH_LONG).show();
		}

	}

}