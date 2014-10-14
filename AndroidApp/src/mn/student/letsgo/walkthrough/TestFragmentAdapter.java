package mn.student.letsgo.walkthrough;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class TestFragmentAdapter extends FragmentPagerAdapter{
    protected static final String[] CONTENT = new String[] { "This", "Is", "A", "Test", };
   

    private int mCount = CONTENT.length;

    public TestFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
    	switch (position) {
    		case 0: return Page1.newInstance("FirstFragment, Instance 1");
    		case 1: return Page1.newInstance("SecondFragment, Instance 2");
    		case 2: return Page1.newInstance("ThirdFragment, Instance 3");
    		case 3: return Page4.newInstance("FourthFragment, Instance 4");
        default: return null;
		}
    
    }

    @Override
    public int getCount() {
        return mCount;
    }
	
}