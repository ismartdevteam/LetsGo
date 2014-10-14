package mn.student.letsgo.walkthrough;

import java.util.Random;

import common.CirclePageIndicator;
import common.PageIndicator;
import mn.student.letsgo.MainActivity;
import mn.student.letsgo.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class WalkThrough extends FragmentActivity{
    TestFragmentAdapter mAdapter;
    ViewPager mPager;
    CirclePageIndicator mIndicator;
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.walkthrough);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean is_first = prefs.getBoolean("is_first_time", false);
	    
		if (!is_first) {
			mAdapter = new TestFragmentAdapter(getSupportFragmentManager());
	    	
	    	mPager = (ViewPager)findViewById(R.id.pager);
	    	mPager.setAdapter(mAdapter);

	    	mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
	    	mIndicator.setViewPager(mPager);
			
	    }else{
	    	Intent i = new Intent(WalkThrough.this, MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			this.finishActivity(0);
	    }
	}	

}
