package mn.student.letsgo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import mn.student.letsgo.model.Mood;
import mn.student.letsgo.text.Bold;
import mn.student.letsgo.utils.CustomRequest;
import mn.student.letsgo.utils.MySingleton;
import mn.student.letsgo.utils.Utils;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class AddPlaceAc extends ActionBarActivity implements OnClickListener {

	private ActionBar bar;
	private static final int SELECT_PHOTO1 = 100;
	private ImageView image;
	private Uri imageUri;
	private LatLng loc;
	private Button add;
	private ProgressDialog progress;
	private SharedPreferences proSp;
	private EditText name;
	private EditText intro;
	private EditText services;
	private EditText address;
	private EditText phone;
	private EditText timetable;
	private List<Mood> moodList;
	private Spinner spin;
	private int mood = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_place);
		initEdit();
		proSp = getSharedPreferences("user", 0);

		image = (ImageView) findViewById(R.id.add_image);
		image.setOnClickListener(this);
		add = (Button) findViewById(R.id.add_place);
		add.setOnClickListener(this);
		bar = getSupportActionBar();
		bar.setDisplayShowHomeEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setHomeButtonEnabled(true);
		bar.setTitle(R.string.addPlace);
		bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(
				R.color.light_red)));
		getSupportFragmentManager().beginTransaction()
				.add(R.id.add_map, new MapsFrag()).commit();
	}

	private void initEdit() {
		name = (EditText) findViewById(R.id.add_name);
		intro = (EditText) findViewById(R.id.add_desc);
		services = (EditText) findViewById(R.id.add_service);
		address = (EditText) findViewById(R.id.add_address);
		phone = (EditText) findViewById(R.id.add_phone);
		timetable = (EditText) findViewById(R.id.add_schedule);
		spin = (Spinner) findViewById(R.id.add_cat);
		getMood();
	}

	public class MapsFrag extends Fragment implements
			OnInfoWindowClickListener, LocationListener, OnMapClickListener {
		private GoogleMap mMap;
		private View view;
		private UiSettings mUiSettings;

		private LocationManager locationManager;
		private static final long MIN_TIME = 400;
		private static final float MIN_DISTANCE = 1000;

		@Override
		public View onCreateView(LayoutInflater inflater,
				@Nullable ViewGroup container,
				@Nullable Bundle savedInstanceState) {
			// TODO Auto-generated method stub

			if (view != null) {
				ViewGroup parent = (ViewGroup) view.getParent();
				if (parent != null)
					parent.removeView(view);
			}
			try {

				view = inflater.inflate(R.layout.add_map, container, false);

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

		@Override
		public void onResume() {
			super.onResume();
			setUpMapIfNeeded();
		}

		private void setUpMapIfNeeded() {
			// Do a null check to confirm that we have not already instantiated
			// the
			// map.

			if (mMap == null) {
				// Try to obtain the map from the SupportMapFragment.
				mMap = ((SupportMapFragment) getActivity()
						.getSupportFragmentManager()
						.findFragmentById(R.id.amap)).getMap();
				// Check if we were successful in obtaining the map.
				if (mMap != null) {
					setUpMap();
				}
			}
			// if (!fromBranch) {

		}

		private void setUpMap() {

			mUiSettings = mMap.getUiSettings();
			mMap.setMyLocationEnabled(true);

			mUiSettings.setMyLocationButtonEnabled(true);

			mUiSettings = mMap.getUiSettings();
			mMap.setMyLocationEnabled(true);

			mUiSettings.setMyLocationButtonEnabled(true);

			locationManager = (LocationManager) getActivity().getSystemService(
					Context.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE,
					this);

		}

		@Override
		public void onInfoWindowClick(Marker arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			LatLng latLng = new LatLng(location.getLatitude(),
					location.getLongitude());
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
					latLng, 12);
			mMap.animateCamera(cameraUpdate);
			mMap.setOnMapClickListener(this);
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

		@Override
		public void onMapClick(LatLng point) {
			// TODO Auto-generated method stub
			mMap.clear();
			loc = new LatLng(point.latitude, point.longitude);
			MarkerOptions marker = new MarkerOptions().position(loc).title(
					"This place");
			mMap.addMarker(marker);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == image) {
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent, ""),
					SELECT_PHOTO1);
		}
		if (v == add) {
			if (Utils.isNetworkAvailable(AddPlaceAc.this)) {

				if (name.getText().length() < 1) {
					ShowToast(getString(R.string.name));
					return;
				}
				if (intro.getText().length() < 1) {
					ShowToast(getString(R.string.desc));
					return;
				}
				if (services.getText().length() < 1) {
					ShowToast(getString(R.string.service));
					return;
				}
				if (address.getText().length() < 1) {
					ShowToast(getString(R.string.address));
					return;
				}
				if (phone.getText().length() < 1) {
					ShowToast(getString(R.string.phone));
					return;
				}
				if (timetable.getText().length() < 1) {
					ShowToast(getString(R.string.timetable));
					return;
				}
				if (mood < 1) {
					ShowToast(getString(R.string.mood));
					return;
				}
				if (loc == null) {
					ShowToast(getString(R.string.chooseLoc));
					return;
				}
				if (imageUri == null) {
					ShowToast(getString(R.string.chooseImage));
					return;

				}
				String userId = "user_id=" + proSp.getString("my_id", "1")
						+ "&";

				String nameStr = "name=" + name.getText().toString().trim()
						+ "&";
				String introStr = "desc=" + intro.getText().toString().trim()
						+ "&";
				String serviceStr = "service="
						+ services.getText().toString().trim() + "&";
				String addressStr = "address="
						+ address.getText().toString().trim() + "&";
				String phoneStr = "phone=" + phone.getText().toString().trim()
						+ "&";
				String timeStr = "time_schedule="
						+ timetable.getText().toString().trim() + "&";
				String lat = "lat=" + loc.latitude + "&";
				String lng = "lng=" + loc.longitude + "&";
				String cat = "category_id=" + mood;
				String requestStr = userId + nameStr + introStr + serviceStr
						+ addressStr + phoneStr + timeStr + lat + lng + cat;
				Log.e("requeset", requestStr + "");
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
						.permitAll().build();
				StrictMode.setThreadPolicy(policy);
				progress = ProgressDialog.show(AddPlaceAc.this, "",
						getString(R.string.loading));
				// TODO Auto-generated method stub

				String requestURL = getString(R.string.mainIp) + "addPlace";
				try {
					String response = multipartRequest(requestURL, requestStr,
							Utils.getPath(AddPlaceAc.this, imageUri), "file");
					progress.dismiss();
					Log.e("responce", response);
					if (response.equals("error"))
						Toast.makeText(AddPlaceAc.this,
								getString(R.string.addErr), Toast.LENGTH_SHORT)
								.show();
					else {
						JSONObject obj = new JSONObject(response);
						if (obj.getInt("response") == 1) {
							Toast.makeText(AddPlaceAc.this,
									getString(R.string.addSuc),
									Toast.LENGTH_SHORT).show();
							finish();
						} else {
							Toast.makeText(AddPlaceAc.this,
									getString(R.string.addErr),
									Toast.LENGTH_SHORT).show();
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

			} else {
				Toast.makeText(AddPlaceAc.this,
						R.string.noNet, Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	private void ShowToast(String field) {
		Toast.makeText(AddPlaceAc.this, getString(R.string.add_fill) + field,
				Toast.LENGTH_SHORT).show();

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
						image.setImageBitmap(bitmap);
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

	private void moodData(JSONArray data) throws JSONException {
		moodList = new ArrayList<Mood>();
		Mood all = new Mood();
		all.id = 0;
		all.name = getString(R.string.selectMood);
		moodList.add(all);
		for (int i = 0; i < data.length(); i++) {
			Mood mood = new Mood();
			JSONObject obj = data.getJSONObject(i);
			mood.id = obj.getInt("id");
			mood.name = obj.getString("name").replace("_", " ");
			moodList.add(mood);
		}

		spin.setAdapter(new MoodAdapter(AddPlaceAc.this, moodList));
		spin.setSelection(mood);
		spin.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				Mood sel = (Mood) spin.getAdapter().getItem(position);
				mood = sel.id;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				mood = 0;
			}
		});

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
		CustomRequest loginReq = new CustomRequest(Method.GET,
				getString(R.string.mainIp) + "category", null,
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
		MySingleton.getInstance(this).addToRequestQueue(loginReq);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == android.R.id.home)
			onBackPressed();
		return true;
	}

	public static String multipartRequest(String urlTo, String post,
			String filepath, String filefield) throws ParseException,
			IOException {
		Log.wtf("imagePath", filepath);
		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;
		InputStream inputStream = null;

		String twoHyphens = "--";
		String boundary = "*****" + Long.toString(System.currentTimeMillis())
				+ "*****";
		String lineEnd = "\r\n";

		String result = "";

		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;

		try {
			File file = new File(filepath);
			FileInputStream fileInputStream = new FileInputStream(file);

			URL url = new URL(urlTo);
			connection = (HttpURLConnection) url.openConnection();

			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("User-Agent",
					"Android Multipart HTTP Client 1.0");
			connection.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + boundary);

			outputStream = new DataOutputStream(connection.getOutputStream());
			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
			outputStream.writeBytes("Content-Disposition: form-data; name=\""
					+ filefield + "\"; filename=\"" + file.getName() + "\""
					+ lineEnd);
			outputStream.writeBytes("Content-Type: image/jpeg" + lineEnd);
			outputStream.writeBytes("Content-Transfer-Encoding: binary"
					+ lineEnd);
			outputStream.writeBytes(lineEnd);

			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			while (bytesRead > 0) {
				outputStream.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			outputStream.writeBytes(lineEnd);

			// Upload POST Data
			String[] posts = post.split("&");
			int max = posts.length;
			for (int i = 0; i < max; i++) {
				outputStream.writeBytes(twoHyphens + boundary + lineEnd);
				String[] kv = posts[i].split("=");
				outputStream
						.writeBytes("Content-Disposition: form-data; name=\""
								+ kv[0] + "\"" + lineEnd);
				outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
				outputStream.writeBytes(lineEnd);
				outputStream.writeBytes(kv[1]);
				outputStream.writeBytes(lineEnd);
			}

			outputStream.writeBytes(twoHyphens + boundary + twoHyphens
					+ lineEnd);

			inputStream = connection.getInputStream();
			result = convertStreamToString(inputStream);

			fileInputStream.close();
			inputStream.close();
			outputStream.flush();
			outputStream.close();

			return result;
		} catch (Exception e) {
			Log.e("MultipartRequest", "Multipart Form Upload Error");
			e.printStackTrace();

			return "error";
		}
	}

	public static String multipartRequest(String urlTo, String post
			) throws ParseException, IOException {

		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;
		InputStream inputStream = null;

		String twoHyphens = "--";
		String boundary = "*****" + Long.toString(System.currentTimeMillis())
				+ "*****";
		String lineEnd = "\r\n";

		String result = "";


		try {

			URL url = new URL(urlTo);
			connection = (HttpURLConnection) url.openConnection();

			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("User-Agent",
					"Android Multipart HTTP Client 1.0");
			connection.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + boundary);

			 outputStream = new
			 DataOutputStream(connection.getOutputStream());
	

			// Upload POST Data
			String[] posts = post.split("&");
			int max = posts.length;
			for (int i = 0; i < max; i++) {
				outputStream.writeBytes(twoHyphens + boundary + lineEnd);
				String[] kv = posts[i].split("=");
				outputStream
						.writeBytes("Content-Disposition: form-data; name=\""
								+ kv[0] + "\"" + lineEnd);
				outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
				outputStream.writeBytes(lineEnd);
				outputStream.writeBytes(kv[1]);
				outputStream.writeBytes(lineEnd);
			}

			outputStream.writeBytes(twoHyphens + boundary + twoHyphens
					+ lineEnd);

			inputStream = connection.getInputStream();
			result = convertStreamToString(inputStream);

			// fileInputStream.close();
			inputStream.close();
			outputStream.flush();
			outputStream.close();

			return result;
		} catch (Exception e) {
			Log.e("MultipartRequest", "Multipart Form Upload Error");
			e.printStackTrace();

			return "error";
		}
	}

	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
