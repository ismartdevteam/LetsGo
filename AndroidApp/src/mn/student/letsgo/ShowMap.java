package mn.student.letsgo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class ShowMap extends Fragment implements OnInfoWindowClickListener,
		LocationListener {
	private GoogleMap mMap;
	private static View view;
	private UiSettings mUiSettings;
	private int[] markers = { R.drawable.marker_mood_2,
			R.drawable.marker_mood_3, R.drawable.marker_mood_4,
			R.drawable.marker_mood_5, R.drawable.marker_mood_6,
			R.drawable.marker_mood_7, R.drawable.marker_mood_8,
			R.drawable.marker_mood_9, R.drawable.marker_mood_10,
			R.drawable.marker_mood_11 };
	private Double lat;
	private int mood;
	private Double lng;
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

			view = inflater.inflate(R.layout.show_place, container, false);

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

	public static ShowMap newInstance(double lat, double lng, int mood) {
		ShowMap fragment = new ShowMap();
		Bundle args = new Bundle();
		args.putDouble("lat", lat);
		args.putInt("mood", mood);
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

	}

	private void setUpMap() {

		mUiSettings = mMap.getUiSettings();
		mMap.setMyLocationEnabled(true);

		mUiSettings.setMyLocationButtonEnabled(true);

		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);

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
		mood = getArguments().getInt("mood");
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

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		LatLng latLng = new LatLng(location.getLatitude(),
				location.getLongitude());
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,
				17);
		mMap.animateCamera(cameraUpdate);
		LatLng loc = new LatLng(lat, lng);
		BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
		if (mood > 0)
			bitmapDescriptor = BitmapDescriptorFactory
					.fromResource(markers[mood-1]);

		mMap.addMarker(new MarkerOptions().position(loc).icon(bitmapDescriptor));
		findDirections(location.getLatitude(), location.getLongitude(),
				loc.latitude, loc.longitude, "driving");
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