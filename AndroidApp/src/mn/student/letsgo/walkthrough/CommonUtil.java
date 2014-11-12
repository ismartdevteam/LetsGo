package mn.student.letsgo.walkthrough;

import android.content.Context;
import android.content.Intent;

public class CommonUtil {

	public static final String SENDER_ID = "872400995155";

	static final String DISPLAY_MESSAGE_ACTION = "mn.student.letsgo.DISPLAY_MESSAGE";

	static final String EXTRA_MESSAGE = "message";

	public static void displayMessage(Context context, String message) {
		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}
}
