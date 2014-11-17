package mn.student.letsgo.walkthrough;

import mn.student.letsgo.MainActivity;
import mn.student.letsgo.R;
import android.R.anim;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends Activity {
	SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		sp = getSharedPreferences("user", MODE_PRIVATE);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				finish();
				if (!sp.getBoolean("isSeen", false))
					startActivity(new Intent(Splash.this, WalkThrough.class));
				else {
					startActivity(new Intent(Splash.this, MainActivity.class));
				}
				overridePendingTransition(anim.slide_in_left,
						anim.slide_out_right);
			}
		}, 2000);
	}

}
