package mn.student.letsgo.walkthrough;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import mn.student.letsgo.MainActivity;
import mn.student.letsgo.R;
import android.R.anim;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

public class Splash extends Activity {
	SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		sp = getSharedPreferences("user", MODE_PRIVATE);
		 try {
		        PackageInfo info = getPackageManager().getPackageInfo(
		                "mn.student.letsgo", 
		                PackageManager.GET_SIGNATURES);
		        for (Signature signature : info.signatures) {
		            MessageDigest md = MessageDigest.getInstance("SHA");
		            md.update(signature.toByteArray());
		            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
		            }
		    } catch (NameNotFoundException e) {

		    } catch (NoSuchAlgorithmException e) {
		    }
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
