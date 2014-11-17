package mn.student.letsgo.whoisgonnapay;

import mn.student.letsgo.MainActivity;
import mn.student.letsgo.R;
import mn.student.letsgo.common.CircleLayout;
import mn.student.letsgo.common.CircleLayout.OnItemSelectedListener;
import mn.student.letsgo.common.CircleLayout.OnRotationFinishedListener;
import mn.student.letsgo.common.CircleView;
import mn.student.letsgo.common.ShakeListener;
import mn.student.letsgo.common.ShakeListener.OnShakeListener;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class WhoIsGonnaPay extends Fragment implements OnClickListener,
		OnShakeListener, OnSeekBarChangeListener, OnItemSelectedListener,
		OnRotationFinishedListener {

	private static final String ARG_SECTION_NUMBER = "section_number";
	View v;
	private ShakeListener shake;
	private Vibrator vibrator;

	/** Dialog heseg */
	private Dialog dialog;
	private SeekBar userCounterSeekBar;
	private int users;
	private Button btnContinue, btnDialogClose, btnSave, btnBack;
	private LinearLayout dialogSeekBarSection, dialogUsersSaveSection;

	/** Dialog usersList heseg */
	private ListView usersList;

	/** SharedPreferences heseg */
	SharedPreferences spUsers;
	SharedPreferences.Editor editor;

	/** CircleLayout heseg */
	private CircleLayout circleLayout;
	private CircleView circleView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		shake = new ShakeListener(getActivity());
		vibrator = (Vibrator) getActivity().getSystemService(
				Context.VIBRATOR_SERVICE);
		v = inflater.inflate(R.layout.who_is_gonna_pay, container, false);
		setHasOptionsMenu(true);
		spUsers = getActivity().getSharedPreferences(ARG_SECTION_NUMBER, 0);
		users = spUsers.getInt("user_counter", 0);

		circleLayout = (CircleLayout) v.findViewById(R.id.main_circle_layout);
		circleLayout.setOnItemSelectedListener(this);
		circleLayout.setOnRotationFinishedListener(this);
		return v;
	}

	@Override
	public void onPause() {
		shake.pause();
		super.onPause();
	}

	@Override
	public void onResume() {
		shake.resume();
		shake.setOnShakeListener(this);
		super.onResume();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (!MainActivity.mNavigationDrawerFragment.isDrawerOpen()) {
			inflater.inflate(R.menu.who_is_gonna_pay, menu);
			super.onCreateOptionsMenu(menu, inflater);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.who_is_gonna_settings:
			showDialog();

			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public static WhoIsGonnaPay newInstance(int section) {

		WhoIsGonnaPay f = new WhoIsGonnaPay();
		Bundle b = new Bundle();
		b.putInt(ARG_SECTION_NUMBER, section);
		f.setArguments(b);

		return f;
	}

	public WhoIsGonnaPay() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnDialogClose:
			dialog.dismiss();
			break;
		case R.id.btnNext:
			dialogSeekBarSection.setVisibility(View.GONE);
			dialogUsersSaveSection.setVisibility(View.VISIBLE);

			/*
			 * listItem = new ArrayList<UserListModel>(); for(int i = 1; i <=
			 * users+2; i++){ listItem.add(new
			 * UserListModel("player: "+String.valueOf(i),
			 * R.drawable.ic_action_back)); } adapter = new
			 * UserListAdapter(getActivity(), listItem);
			 * usersList.setAdapter(adapter);
			 */

			break;
		case R.id.btnBack:
			dialogSeekBarSection.setVisibility(View.VISIBLE);
			dialogUsersSaveSection.setVisibility(View.GONE);
			break;
		case R.id.btnSave:

			addUsers();
			editor = spUsers.edit();
			editor.putInt("user_counter", users);
			editor.commit();
			dialog.dismiss();

			break;

		default:
			break;
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		users = progress;
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		Toast.makeText(getActivity(), String.valueOf(users + 2),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onRotationFinished(View view, String name) {
		Animation animation = new RotateAnimation(0, 360, view.getWidth() / 2,
				view.getHeight() / 2);
		animation.setDuration(250);
		view.startAnimation(animation);
	}

	@Override
	public void onItemSelected(View view, String name) {
		Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onShake() {
		vibrator.vibrate(500);
		circleLayout.rotateView();
	}

	private void showDialog() {
		dialog = new Dialog(getActivity(), R.style.pay_dialog);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.who_is_gonna_pay_dialog);
		dialog.getWindow().setLayout(
				getActivity().getWindowManager().getDefaultDisplay().getWidth()
						- (int) (2 * getResources().getDimension(
								R.dimen.activity_horizontal_margin)), -2);
		dialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_BACK:
					dialog.dismiss();
					break;

				default:
					break;
				}
				return false;
			}
		});
		dialogSeekBarSection = (LinearLayout) dialog
				.findViewById(R.id.layoutSeekBarSection);
		dialogUsersSaveSection = (LinearLayout) dialog
				.findViewById(R.id.layoutSaveSection);

		usersList = (ListView) dialog.findViewById(R.id.listUsers);

		btnContinue = (Button) dialog.findViewById(R.id.btnNext);
		btnDialogClose = (Button) dialog.findViewById(R.id.btnDialogClose);
		btnSave = (Button) dialog.findViewById(R.id.btnSave);
		btnBack = (Button) dialog.findViewById(R.id.btnBack);

		btnContinue.setOnClickListener(this);
		btnDialogClose.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		btnBack.setOnClickListener(this);

		userCounterSeekBar = (SeekBar) dialog
				.findViewById(R.id.seekbar_who_is_gonna);
		userCounterSeekBar.setOnSeekBarChangeListener(this);

		userCounterSeekBar.setProgress(users);

		dialog.setCancelable(false);
		dialog.show();
	}

	@SuppressWarnings("deprecation")
	private void addUsers() {
		for (int i = users; i <= users + 2; i++) {
			circleView = new CircleView(getActivity());
			circleLayout.addView(circleView);

			switch (i) {
			case 1:
				circleView.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.circle_view_3));
				break;
			case 2:
				circleView.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.circle_view_4));
				break;
			case 3:
				circleView.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.circle_view_5));
				break;
			case 4:
				circleView.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.circle_view_6));
				break;
			case 5:
				circleView.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.circle_view_7));
				break;
			case 6:
				circleView.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.circle_view_8));
				break;
			case 7:
				circleView.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.circle_view_9));
				break;
			case 8:
				circleView.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.circle_view_10));
				break;

			default:
				break;
			}
		}
	}
}
