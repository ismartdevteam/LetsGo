package mn.student.letsgo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import mn.student.letsgo.utils.MultipartUtility;
import mn.student.letsgo.utils.Utils;

import org.apache.http.ParseException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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
	private Bitmap upload_images;
	private Uri imageUri;
	private LatLng loc;
	private Button add;
	private ProgressDialog progress;
	private SharedPreferences proSp;
	File file;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_place);
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
		getSupportFragmentManager().beginTransaction()
				.add(R.id.add_map, new MapsFrag()).commit();
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

				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
						.permitAll().build();
				StrictMode.setThreadPolicy(policy);
				// TODO Auto-generated method stub

				String requestURL = getString(R.string.mainIp) + "addPlace";
				try {
					multipartRequest(requestURL, "post",
							Utils.getPath(AddPlaceAc.this, imageUri), "file");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// try {
				// MultipartUtility multipart = new MultipartUtility(
				// requestURL, charset);
				//
				// multipart.addHeaderField("User-Agent", "CodeJava");
				// multipart.addHeaderField("Test-Header", "Header-Value");
				//
				// multipart.addFormField("name", "name");
				// multipart.addFormField("desc", "desc");
				//
				// multipart.addFilePart("file", file);
				//
				// List<String> response = multipart.finish();
				//
				// System.out.println("SERVER REPLIED:");
				//
				// for (String line : response) {
				// System.out.println(line);
				// }
				// } catch (IOException ex) {
				// System.err.println(ex);
				// }

				// progress = ProgressDialog.show(AddPlaceAc.this, "",
				// getString(R.string.loading));
				//
				// MultipartRequest addReq = new MultipartRequest(
				// getString(R.string.mainIp) + "addPlace",
				// new Response.ErrorListener() {
				//
				// @Override
				// public void onErrorResponse(VolleyError error) {
				// Log.e("error", error.getMessage());
				// progress.dismiss();
				// }
				// }, new Response.Listener<JSONObject>() {
				//
				// @Override
				// public void onResponse(JSONObject response) {
				// Log.e("place responce", response.toString());
				// progress.dismiss();
				// }
				// }) {
				// @Override
				// public void addFileUpload(File file) {
				// // TODO Auto-generated method stub
				// super.addFileUpload(new File(imageUri + ""));
				// }
				//
				// };
				// // addReq.addFileUpload("file", new File(imageUri + ""));
				// //
				// // addReq.addStringUpload("user_id", proSp.getString("my_id",
				// // "0"));
				//
				// // addReq.addStringUpload("desc", "yes");
				// //
				// // addReq.addStringUpload("service", "yes");
				// //
				// // addReq.addStringUpload("lat", loc.latitude + "");
				// // addReq.addStringUpload("lng", loc.longitude + "");
				// // addReq.addStringUpload("address", "yes");
				// // addReq.addStringUpload("phone", "yes");
				// // addReq.addStringUpload("time_schedule", "yes");
				// // addReq.addStringUpload("category_id", "1");
				// MySingleton.getInstance(AddPlaceAc.this).addToRequestQueue(
				// addReq);
			} else {
				Toast.makeText(AddPlaceAc.this,
						R.string.no_internet_connection, Toast.LENGTH_SHORT)
						.show();
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

	public String multipartRequest(String urlTo, String post, String filepath,
			String filefield) throws ParseException, IOException {
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

		String[] q = file.getPath().split("/");
		int idx = q.length - 1;

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
					+ filefield + "\"; filename=\"" + q[idx] + "\"" + lineEnd);
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
			result = this.convertStreamToString(inputStream);

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

	private String convertStreamToString(InputStream is) {
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
