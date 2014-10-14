package mn.student.letsgo.walkthrough;

import mn.student.letsgo.MainActivity;
import mn.student.letsgo.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

public class Page4 extends Fragment implements OnClickListener{
	
	private Button btnStart;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.walkthrough_page4, container, false);
        
        btnStart = (Button)v.findViewById(R.id.btnStart);   
        btnStart.setOnClickListener(this);
        return v;
    }

    public static Page4 newInstance(String text) {
    	Page4 f = new Page4();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);

        return f;
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnStart:
			SharedPreferences prefs = PreferenceManager
            	.getDefaultSharedPreferences(getActivity());
			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean("is_first_time", true);
			editor.commit();
			
			Intent i = new Intent(getActivity(), MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			getActivity().finishActivity(0);
			break;

		default:
			break;
		}
	}

}
