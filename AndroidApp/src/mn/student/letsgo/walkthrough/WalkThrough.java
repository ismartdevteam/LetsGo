package mn.student.letsgo.walkthrough;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import mn.student.letsgo.MainActivity;
import mn.student.letsgo.R;
import mn.student.letsgo.text.Regular;
import mn.student.letsgo.utils.CustomRequest;
import mn.student.letsgo.utils.MySingleton;
import mn.student.letsgo.utils.WakeLocker;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gcm.GCMRegistrar;
import com.nvanbenschoten.motion.ParallaxImageView;
import com.viewpagerindicator.CirclePageIndicator;
import static mn.student.letsgo.walkthrough.CommonUtil.SENDER_ID;

public class WalkThrough extends FragmentActivity {
	ViewPager pager;
	CirclePageIndicator indicator;
	ParallaxImageView back;
	private SharedPreferences GCMsp;
	private static boolean mReceiverSet = false;

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!mReceiverSet) {
				mReceiverSet = true;

			}

			WakeLocker.acquire(getApplicationContext());

			WakeLocker.release();
		}
	};

	@Override
	protected void onDestroy() {

		try {
			unregisterReceiver(mHandleMessageReceiver);
			GCMRegistrar.onDestroy(this);
		} catch (Exception e) {

		}
		super.onDestroy();

	}

	private void pushReg() {
		GCMsp = getSharedPreferences("gcm", MODE_PRIVATE);
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				"mn.sorako.mrpizza.DISPLAY_MESSAGE"));

		GCMRegistrar.checkManifest(getApplicationContext());
		String reg_id = GCMRegistrar.getRegistrationId(getApplicationContext());
		if (reg_id.equals("")) {
			GCMRegistrar.register(getApplicationContext(), SENDER_ID);

		} else {
			if (GCMsp.getString("reg_id", "").equals("")) {
				GCMsp.edit().putString("reg_id", reg_id).commit();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.walkthrough);
		back = (ParallaxImageView) findViewById(R.id.walk_back);
		back.registerSensorManager();
		pager = (ViewPager) findViewById(R.id.walk_pager);
		pager.setAdapter(new WalkPageAdapter(getSupportFragmentManager()));
		indicator = (CirclePageIndicator) findViewById(R.id.walk_indicator);
		indicator.setViewPager(pager);
		pushReg();
	}

	public class WalkPageAdapter extends FragmentStatePagerAdapter {

		public WalkPageAdapter(FragmentManager fm) {
			super(fm);

		}

		@Override
		public Fragment getItem(int position) {
			if (position == 2) {
				return new Login().newInstance();
			} else {
				return WalkItem.newInstance(position);
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 3;
		}
	}

	public static class WalkItem extends Fragment {
		int mNum;
		View v;
		int images[] = { R.drawable.walk_one, R.drawable.walk_two };

		public static WalkItem newInstance(int num) {
			WalkItem f = new WalkItem();
			// Supply num input as an argument.
			Bundle args = new Bundle();
			args.putInt("num", num);
			f.setArguments(args);
			return f;
		}

		/**
		 * When creating, retrieve this instance's number from its arguments.
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mNum = getArguments() != null ? getArguments().getInt("num") : 1;

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			v = inflater.inflate(R.layout.walk_item, container, false);
			ImageView image = (ImageView) v.findViewById(R.id.walk_item_image);
			image.setImageResource(images[mNum]);
			Regular text = (Regular) v.findViewById(R.id.walk_item_text);
			switch (mNum) {
			case 0:
				text.setText(getActivity().getString(R.string.walk_one));
				break;
			case 1:
				text.setText(getActivity().getString(R.string.walk_two));
				break;
			default:
				break;
			}
			return v;
		}
	}

	public class Login extends Fragment implements OnClickListener{

		int mNum;
		View v;
		private Button skip;
		private ProgressDialog progress;
		private LoginButton loginButton;
		private GraphUser user;
		private UiLifecycleHelper uiHelper;
		private SharedPreferences preferences;
		private Session.StatusCallback callback = new Session.StatusCallback() {
			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				onSessionStateChange(session, state, exception);
			}
		};

		private void onSessionStateChange(Session session, SessionState state,
				Exception exception) {
			Log.d("facebook state", state.toString());
			if (state.isOpened()) {
				Log.i("", "Logged in...");
			} else if (state.isClosed()) {
				Log.i("", "Logged out...");
			}
		}

		public Login newInstance() {

			Login f = new Login();
			// Supply num input as an argument.
			Bundle args = new Bundle();
			args.putInt("num", 3);

			f.setArguments(args);

			return f;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			Log.d("title", "onCreate");
			mNum = getArguments() != null ? getArguments().getInt("num") : 1;
			uiHelper = new UiLifecycleHelper(getActivity(), callback);
			uiHelper.onCreate(savedInstanceState);
			preferences = getActivity().getSharedPreferences("user",
					MODE_PRIVATE);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			v = inflater.inflate(R.layout.walk_login, container, false);
			loginButton = (LoginButton) v.findViewById(R.id.facebookLogin);
			
			loginButton.setFragment(this);
			skip=(Button)v.findViewById(R.id.skip);
			skip.setOnClickListener(this);
			return v;
		}

		@Override
		public void onActivityCreated(@Nullable Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onActivityCreated(savedInstanceState);
			loginButton.setPublishPermissions(Arrays.asList("publish_actions"));
			loginButton
					.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {

						@Override
						public void onUserInfoFetched(GraphUser user) {

							Login.this.user = user;
							loginCheck();

						}
					});
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode,
				Intent data) {
			// TODO Auto-generated method stub
			super.onActivityResult(requestCode, resultCode, data);
			uiHelper.onActivityResult(requestCode, resultCode, data);
		}

		@Override
		public void onPause() {
			super.onPause();
			uiHelper.onPause();
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			uiHelper.onDestroy();
		}

		@Override
		public void onSaveInstanceState(Bundle outState) {
			super.onSaveInstanceState(outState);
			uiHelper.onSaveInstanceState(outState);
		}

		@Override
		public void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			uiHelper.onResume();
		}

		private void makeReq() {

			CustomRequest loginReq = new CustomRequest(Method.POST,
					getActivity().getString(R.string.mainIp) + "login", null,
					new Response.Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {
							Log.e("fb register", response.toString());
							try {

								if (response.getInt("response") == 1
										|| response.getInt("response") == 2) {
									Editor edit = preferences.edit();
									edit.putString("username", user.getName());
						
									edit.putString("my_id",
											response.getString("user_id"));
									edit.putString("pro_img",
											"https://graph.facebook.com/"
													+ user.getId()
													+ "/picture?type=large");
									edit.putBoolean("login", true);
									edit.commit();
									finish();
									startActivity(new Intent(getActivity(),
											MainActivity.class));
								} else {
									if (response.getInt("response") == 0) {
										Toast.makeText(getActivity(),
												"error haha",
												Toast.LENGTH_SHORT).show();
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

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
					params.put("name", user.getName() + "");
					params.put("fb_id", user.getId() + "");
					params.put("email", (String) user.asMap().get("email") + "");
					params.put("push_id", GCMsp.getString("reg_id", "0"));
					params.put("profile_img", "https://graph.facebook.com/"
							+ user.getId() + "/picture?type=large");
					return params;
				}
			};
			MySingleton.getInstance(getActivity()).addToRequestQueue(loginReq);
		}

		private void loginCheck() {

			if (user != null) {
				Log.i(user.getFirstName(), user.getId());
				progress = ProgressDialog.show(getActivity(), "",
						getString(R.string.loading));
				makeReq();
			}
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v==skip){
				finish();
				startActivity(new Intent(getActivity(),
						MainActivity.class));
			}
		}

	}
}
