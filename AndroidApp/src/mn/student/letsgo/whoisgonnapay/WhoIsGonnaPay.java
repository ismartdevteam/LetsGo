package mn.student.letsgo.whoisgonnapay;

import java.util.ArrayList;

import mn.student.letsgo.MainActivity;
import mn.student.letsgo.R;
import mn.student.letsgo.common.CircleLayout;
import mn.student.letsgo.common.CircleLayout.OnItemSelectedListener;
import mn.student.letsgo.common.CircleLayout.OnRotationFinishedListener;
import mn.student.letsgo.common.CircleView;
import mn.student.letsgo.common.ShakeListener;
import mn.student.letsgo.common.ShakeListener.OnShakeListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class WhoIsGonnaPay extends Fragment implements OnClickListener,
	OnSeekBarChangeListener	 {

	private static final String ARG_SECTION_NUMBER = "section_number";
	View v;
	public static ShakeListener shake;
	public static Vibrator vibrator;
	
	private CircleLayout circle;
	
	FragmentManager frManager;
	
	/** Dialog heseg */
	private Dialog dialog;
	private SeekBar userCounterSeekBar;
	private int users=0;
	private Button btnContinue, btnDialogClose, btnSave, btnBack;
	private LinearLayout dialogSeekBarSection, dialogUsersSaveSection;
	
	/** Dialog usersList heseg */
	private ListView listUsers;
	private UserListAdapter adapter;

	public static UserListModel userModel;
	public static ArrayList<UserListModel> listItem = new ArrayList<UserListModel>();
		
	public static WhoIsGonnaPay newInstance(int sectionNumber) {
		WhoIsGonnaPay fragment = new WhoIsGonnaPay();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		shake = new ShakeListener(getActivity());
		vibrator = (Vibrator) getActivity().getSystemService(
				Context.VIBRATOR_SERVICE);
		
		frManager = getFragmentManager();
		v = inflater.inflate(R.layout.who_is_gonna_pay, container, false);
		circle = (CircleLayout)v.findViewById(R.id.main_view_layout);
		
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
		    @Override
		    public void run() {
		        showDialog();
		    }
		}, 200);
		return v;
	}
	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}

	@Override
	public void onDetach() {
		super.onDetach();
		((MainActivity) getActivity()).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (!MainActivity.mNavigationDrawerFragment.isDrawerOpen()) {
		    inflater.inflate(R.menu.who_is_gonna_pay, menu);
		    super.onCreateOptionsMenu(menu,inflater);
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
	
	@Override
	public void onClick(View v) {
	switch (v.getId()) {
	case R.id.btnDialogClose:
		dialog.dismiss();
		break;
	case R.id.btnNext:
		dialogSeekBarSection.setVisibility(View.GONE);
		dialogUsersSaveSection.setVisibility(View.VISIBLE);

		setListData(users);
		break;
	case R.id.btnBack:
		dialogSeekBarSection.setVisibility(View.VISIBLE);
		dialogUsersSaveSection.setVisibility(View.GONE);
		break;
	case R.id.btnSave:
		switch (users) {
		case 2:
			frManager.beginTransaction().replace(R.id.whoIsGonnaMain, User2.newInstance(2)).commit();
			break;
		case 3:
			frManager.beginTransaction().replace(R.id.whoIsGonnaMain, User3.newInstance(3)).commit();
			break;
		case 4:
			frManager.beginTransaction().replace(R.id.whoIsGonnaMain, User4.newInstance(4)).commit();
			break;
		case 5:
			frManager.beginTransaction().replace(R.id.whoIsGonnaMain, User5.newInstance(5)).commit();
			break;
		case 6:
			frManager.beginTransaction().replace(R.id.whoIsGonnaMain, User6.newInstance(6)).commit();
			break;
		default:
			break;
		}
		//Toast.makeText(getApplicationContext(), listItem.get(4).getNickname(), Toast.LENGTH_SHORT).show();
		dialog.dismiss();

		break;

	default:
		break;
	}
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
		boolean fromUser) {
		users = progress+2;
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {}
	
	public void setListData(int userCounter){
		listItem.clear();
    	for (int i = 1; i <= userCounter; i++) {
    		UserListModel uList = new UserListModel();
			uList.setNickname("Player: " + i);
			uList.setId("circle_view_"+i);
			   
			listItem.add(uList);
		}
    	adapter = new UserListAdapter(getActivity(), listItem, getResources());
		listUsers.setAdapter(adapter);	
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

		listUsers = (ListView) dialog.findViewById(R.id.listUsers);

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

		userCounterSeekBar.setProgress(2);

		dialog.setCancelable(false);
		dialog.show();
	}

	public static class User2 extends Fragment implements OnRotationFinishedListener,
		OnItemSelectedListener, OnShakeListener{
		private CircleView view, view1;
		private CircleLayout circleLayout;
		//private UserListModel listmodel;
		public static User2 newInstance(int section) {
			User2 f = new User2();
			Bundle b = new Bundle();
			b.putInt(ARG_SECTION_NUMBER, section);
			f.setArguments(b);
			return f;
		}

		public User2() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.who_is_gonna_user2, null);
			circleLayout = (CircleLayout)v.findViewById(R.id.user2_layout);
		
			circleLayout.setOnItemSelectedListener(this);
			circleLayout.setOnRotationFinishedListener(this);
			UserListModel tempValues = (UserListModel) listItem.get(0);
			view = (CircleView)v.findViewById(R.id.view1);
			view1 = (CircleView)v.findViewById(R.id.view2);
			view.setName(tempValues.getNickname());
			view1.setName(listItem.get(1).getNickname());
			
			return v;
		}
		@Override
		public void onShake() {
			vibrator.vibrate(500);
			circleLayout.rotateView();
		}
		
		@Override
		public void onItemSelected(View view, String name) {
		}
		
		@Override
		public void onRotationFinished(View view, String name) {	
			
			final Builder dialog = new AlertDialog.Builder(getActivity());
			dialog.setMessage(getResources().getString(R.string.title_section3) + "\n\n\t" +name);
			dialog.setPositiveButton(getActivity().getString(R.string.yes),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(
								DialogInterface paramDialogInterface,
								int paramInt) {
						}
					});
			dialog.show();
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
	}
	public static class User3 extends Fragment implements OnRotationFinishedListener,
		OnItemSelectedListener, OnShakeListener{
		private CircleView view, view1, view2;
		private CircleLayout circleLayout;
		public static User3 newInstance(int section) {
			User3 f = new User3();
			Bundle b = new Bundle();
			b.putInt(ARG_SECTION_NUMBER, section);
			f.setArguments(b);
			return f;
		}

		public User3() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.who_is_gonna_user3, container, false);

			circleLayout = (CircleLayout)v.findViewById(R.id.user3_layout);

			circleLayout.setOnItemSelectedListener(this);
			circleLayout.setOnRotationFinishedListener(this);

			view = (CircleView)v.findViewById(R.id.view1);
			view1 = (CircleView)v.findViewById(R.id.view2);
			view2 = (CircleView)v.findViewById(R.id.view3);
			view.setName(listItem.get(0).getNickname().toString());
			view1.setName(listItem.get(1).getNickname().toString());
			view2.setName(listItem.get(2).getNickname().toString());

			return v;
		}
		@Override
		public void onShake() {
			circleLayout.rotateView();
		}

		@Override
		public void onItemSelected(View view, String name) {
		}

		@Override
		public void onRotationFinished(View view, String name) {	
			final Builder dialog = new AlertDialog.Builder(getActivity());
			dialog.setMessage(getResources().getString(R.string.title_section3) + "\n\n\t" +name);
			dialog.setPositiveButton(getActivity().getString(R.string.yes),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(
								DialogInterface paramDialogInterface,
								int paramInt) {
						}
					});
			dialog.show();
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
	}
	public static class User4 extends Fragment implements OnRotationFinishedListener,
	OnItemSelectedListener, OnShakeListener{
		private CircleView view, view1, view2, view3;
		private CircleLayout circleLayout;
		public static User4 newInstance(int section) {
			User4 f = new User4();
			Bundle b = new Bundle();
			b.putInt(ARG_SECTION_NUMBER, section);
			f.setArguments(b);
			return f;
		}

		public User4() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.who_is_gonna_user4, container, false);

			circleLayout = (CircleLayout)v.findViewById(R.id.user4_layout);

			circleLayout.setOnItemSelectedListener(this);
			circleLayout.setOnRotationFinishedListener(this);

			view = (CircleView)v.findViewById(R.id.view1);
			view1 = (CircleView)v.findViewById(R.id.view2);
			view2 = (CircleView)v.findViewById(R.id.view3);
			view3 = (CircleView)v.findViewById(R.id.view4);
			view.setName(listItem.get(0).getNickname().toString());
			view1.setName(listItem.get(1).getNickname().toString());
			view2.setName(listItem.get(2).getNickname().toString());
			view3.setName(listItem.get(3).getNickname().toString());

			return v;
		}
		@Override
		public void onShake() {
			circleLayout.rotateView();
		}

		@Override
		public void onItemSelected(View view, String name) {
		}

		@Override
		public void onRotationFinished(View view, String name) {	
			final Builder dialog = new AlertDialog.Builder(getActivity());
			dialog.setMessage(getResources().getString(R.string.title_section3) + "\n\n\t" +name);
			dialog.setPositiveButton(getActivity().getString(R.string.yes),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(
								DialogInterface paramDialogInterface,
								int paramInt) {
						}
					});
			dialog.show();
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
	}
	public static class User5 extends Fragment implements OnRotationFinishedListener,
	OnItemSelectedListener, OnShakeListener{
		private CircleView view, view1, view2, view3, view4;
		private CircleLayout circleLayout;
		public static User5 newInstance(int section) {
			User5 f = new User5();
			Bundle b = new Bundle();
			b.putInt(ARG_SECTION_NUMBER, section);
			f.setArguments(b);
			return f;
		}

		public User5() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.who_is_gonna_user5, container, false);

			circleLayout = (CircleLayout)v.findViewById(R.id.user5_layout);

			circleLayout.setOnItemSelectedListener(this);
			circleLayout.setOnRotationFinishedListener(this);

			view = (CircleView)v.findViewById(R.id.view1);
			view1 = (CircleView)v.findViewById(R.id.view2);
			view2 = (CircleView)v.findViewById(R.id.view3);
			view3 = (CircleView)v.findViewById(R.id.view4);
			view4 = (CircleView)v.findViewById(R.id.view5);
			//view5 = (CircleView)v.findViewById(R.id.view6);
			view.setName(listItem.get(0).getNickname().toString());
			view1.setName(listItem.get(1).getNickname().toString());
			view2.setName(listItem.get(2).getNickname().toString());
			view3.setName(listItem.get(3).getNickname().toString());
			view4.setName(listItem.get(4).getNickname().toString());
			//view5.setName(listItem.get(5).getNickname().toString());

			return v;
		}
		@Override
		public void onShake() {
			circleLayout.rotateView();
		}

		@Override
		public void onItemSelected(View view, String name) {
		}

		@Override
		public void onRotationFinished(View view, String name) {	
			final Builder dialog = new AlertDialog.Builder(getActivity());
			dialog.setMessage(getResources().getString(R.string.title_section3) + "\n\n\t" +name);
			dialog.setPositiveButton(getActivity().getString(R.string.yes),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(
								DialogInterface paramDialogInterface,
								int paramInt) {
						}
					});
			dialog.show();
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
	}
	public static class User6 extends Fragment implements OnRotationFinishedListener,
	OnItemSelectedListener, OnShakeListener{
		private CircleView view, view1, view2, view3, view4, view5;
		private CircleLayout circleLayout;
		public static User6 newInstance(int section) {
			User6 f = new User6();
			Bundle b = new Bundle();
			b.putInt(ARG_SECTION_NUMBER, section);
			f.setArguments(b);
			return f;
		}

		public User6() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.who_is_gonna_user6, container, false);

			circleLayout = (CircleLayout)v.findViewById(R.id.user6_layout);

			circleLayout.setOnItemSelectedListener(this);
			circleLayout.setOnRotationFinishedListener(this);

			view = (CircleView)v.findViewById(R.id.view1);
			view1 = (CircleView)v.findViewById(R.id.view2);
			view2 = (CircleView)v.findViewById(R.id.view3);
			view3 = (CircleView)v.findViewById(R.id.view4);
			view4 = (CircleView)v.findViewById(R.id.view5);
			view5 = (CircleView)v.findViewById(R.id.view6);
			view.setName(listItem.get(0).getNickname().toString());
			view1.setName(listItem.get(1).getNickname().toString());
			view2.setName(listItem.get(2).getNickname().toString());
			view3.setName(listItem.get(3).getNickname().toString());
			view4.setName(listItem.get(4).getNickname().toString());
			view5.setName(listItem.get(5).getNickname().toString());
			
			return v;
		}
		@Override
		public void onShake() {
			circleLayout.rotateView();
		}

		@Override
		public void onItemSelected(View view, String name) {
		}

		@Override
		public void onRotationFinished(View view, String name) {	
			final Builder dialog = new AlertDialog.Builder(getActivity());
			dialog.setMessage(getResources().getString(R.string.title_section3) + "\n\n\t" +name);
			dialog.setPositiveButton(getActivity().getString(R.string.yes),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(
								DialogInterface paramDialogInterface,
								int paramInt) {
						}
					});
			dialog.show();
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
	}


}