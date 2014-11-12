package mn.student.letsgo;

import com.google.android.gcm.GCMBaseIntentService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import static mn.student.letsgo.walkthrough.CommonUtil.SENDER_ID;
import static mn.student.letsgo.walkthrough.CommonUtil.displayMessage;

public class GCMIntentService extends GCMBaseIntentService {

	public GCMIntentService() {
		super(SENDER_ID);

	}

	/**
	 * Method called on device registered
	 **/
	@Override
	protected void onRegistered(Context context, String registrationId) {
		displayMessage(context, "Your device registred with GCM");
		Log.i("reg_id", registrationId);
		SharedPreferences sp = getSharedPreferences("gcm", MODE_PRIVATE);
		sp.edit().putString("reg_id", registrationId).commit();

	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
	}

	/**
	 * Method called on Receiving a new message
	 * */
	@Override
	protected void onMessage(final Context context, Intent intent) {

		final String message = intent.getExtras().getString("message");

		final int comment_id = intent.getExtras().getInt("comment_id");

		generateNotification(context, message, comment_id);

	}

	/**
	 * Method called on receiving a deleted message
	 * */
	@Override
	protected void onDeletedMessages(Context context, int total) {

		String message = "deleted";
		// notifies user
		generateNotification(context, message, 0);
	}

	/**
	 * Method called on Error
	 * */
	@Override
	public void onError(Context context, String errorId) {

		displayMessage(context, "Error:" + errorId);

	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		// Log.i(TAG, "Received recoverable error: " + errorId);
		displayMessage(context, "Error from onRecover:" + errorId);
		return super.onRecoverableError(context, errorId);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	@SuppressWarnings("deprecation")
	private static void generateNotification(Context context, String message,
			int com_id) {
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher,
				message, when);
		Intent notificationIntent;

		notificationIntent = new Intent(context, MainActivity.class);

		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, "YouGo", message, intent);

		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Play default notification sound
		notification.defaults |= Notification.DEFAULT_SOUND;

		// Vibrate if vibrate is enabled
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify(0, notification);

	}

}