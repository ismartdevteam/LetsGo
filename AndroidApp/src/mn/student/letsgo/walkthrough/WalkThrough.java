package mn.student.letsgo.walkthrough;

import static mn.student.letsgo.walkthrough.CommonUtil.SENDER_ID;
import mn.student.letsgo.R;
import mn.student.letsgo.text.Regular;
import mn.student.letsgo.utils.WakeLocker;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gcm.GCMRegistrar;
import com.nvanbenschoten.motion.ParallaxImageView;
import com.viewpagerindicator.CirclePageIndicator;

public class WalkThrough extends FragmentActivity {
	ViewPager pager;
	CirclePageIndicator indicator;
	ParallaxImageView back1;
	ParallaxImageView back2;
	ParallaxImageView back3;
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
		ParalaxxImages();

		pager = (ViewPager) findViewById(R.id.walk_pager);
		pager.setAdapter(new WalkPageAdapter(getSupportFragmentManager()));
		indicator = (CirclePageIndicator) findViewById(R.id.walk_indicator);
		indicator.setViewPager(pager);
		indicator.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				if(arg0==2)
					indicator.setVisibility(View.GONE);
				else 
					indicator.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				// if (arg0 < arg2)
				// switcher.showNext();
				// else
				// switcher.showPrevious();
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		pushReg();
	}

	private void ParalaxxImages() {
		// switcher = (ImageSwitcher) findViewById(R.id.switcher);
		back1 = (ParallaxImageView) findViewById(R.id.walk_back1);
		back1.registerSensorManager();
		// back2 = (ParallaxImageView) findViewById(R.id.walk_back2);
		// back2.registerSensorManager();
		// back3 = (ParallaxImageView) findViewById(R.id.walk_back3);
		// back3.registerSensorManager();
	}

	public class WalkPageAdapter extends FragmentStatePagerAdapter {

		public WalkPageAdapter(FragmentManager fm) {
			super(fm);

		}

		@Override
		public Fragment getItem(int position) {
			if (position == 2) {
				return new Login().newInstance(true);
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
		int images[] = { R.drawable.ic_launcher, R.drawable.ic_compare };

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

}
