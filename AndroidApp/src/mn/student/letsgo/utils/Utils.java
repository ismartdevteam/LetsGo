package mn.student.letsgo.utils;

import java.text.DecimalFormat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

public class Utils {
	public static final String EXTERNAL_ERROR = "ERROR";
	public static final String UNZIP_SUCCESS = "unzip_success";
	public static final String UNZIP_ERROR = "unzip_error";

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	public static String numberToFormat(int price) {
		DecimalFormat decimalFormat = new DecimalFormat("#.#");
		decimalFormat.setGroupingUsed(true);
		decimalFormat.setGroupingSize(3);
		return decimalFormat.format(price) + "";
	}
	public static void openCallIntent(Context context, String phoneNumber) {
		String uri = "tel:" + phoneNumber;
		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setData(Uri.parse(uri));
		context.startActivity(callIntent);
	}

	public static void openEmailIntent(Context context, String subject) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		Intent mailer = Intent.createChooser(intent, null);
		context.startActivity(mailer);
	}

	public static void openWebIntent(Context context, String url) {
		if (!url.startsWith("http://") && !url.startsWith("https://"))
			url = "http://" + url;

		Intent callIntent = new Intent(Intent.ACTION_VIEW);
		callIntent.setData(Uri.parse(url));
		context.startActivity(callIntent);
	}

	public static void openMapIntent(Context context, String latitude,
			String longitude) {
		String url = "http://maps.google.com/maps?daddr=" + latitude + ","
				+ longitude;
		Intent mapIntent = new Intent(Intent.ACTION_VIEW);
		mapIntent.setData(Uri.parse(url));
		context.startActivity(mapIntent);
	}

}
