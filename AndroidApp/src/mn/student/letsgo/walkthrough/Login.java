package mn.student.letsgo.walkthrough;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import mn.student.letsgo.MainActivity;
import mn.student.letsgo.R;
import mn.student.letsgo.utils.CustomRequest;
import mn.student.letsgo.utils.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

public class Login extends Fragment implements OnClickListener {

	int mNum;
	View v;
	private Button skip;
	private ProgressDialog progress;
	private LoginButton loginButton;
	private GraphUser user;
	private UiLifecycleHelper uiHelper;
	private SharedPreferences GCMsp;
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
		preferences = getActivity().getSharedPreferences("user", 0);
		GCMsp = getActivity().getSharedPreferences("gcm", 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		v = inflater.inflate(R.layout.walk_login, container, false);
		loginButton = (LoginButton) v.findViewById(R.id.facebookLogin);

		loginButton.setFragment(this);
		skip = (Button) v.findViewById(R.id.skip);
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

		CustomRequest loginReq = new CustomRequest(Method.POST, getActivity()
				.getString(R.string.mainIp) + "login", null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.e("fb register", response.toString());
						try {

							if (response.getInt("response") == 1
									|| response.getInt("response") == 2) {
								Editor edit = preferences.edit();
								edit.putString("username", user.getName());
								edit.putInt("visits", response.getInt("visit"));
								edit.putString("my_id",
										response.getString("user_id"));
								edit.putString(
										"pro_img",
										"https://graph.facebook.com/"
												+ user.getId()
												+ "/picture?type=large");
								edit.putBoolean("login", true);
								edit.commit();
								getActivity().finish();
								startActivity(new Intent(getActivity(),
										MainActivity.class));
							} else {
								if (response.getInt("response") == 0) {
									Toast.makeText(getActivity(), "error haha",
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
				params.put("profile_img",
						"https://graph.facebook.com/" + user.getId()
								+ "/picture?type=large");
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
		if (v == skip) {
			preferences.edit().putBoolean("isSeen", true).commit();
			getActivity().finish();
			startActivity(new Intent(getActivity(), MainActivity.class));
		}
	}

}