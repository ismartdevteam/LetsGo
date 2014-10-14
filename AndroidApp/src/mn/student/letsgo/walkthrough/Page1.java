package mn.student.letsgo.walkthrough;

import mn.student.letsgo.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Page1 extends Fragment {
	
	private TextView textView;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.walkthrough_page1, container, false);

        textView = (TextView) v.findViewById(R.id.tvFragFirst);
        textView.setText(getArguments().getString("msg"));

        return v;
    }

    public static Page1 newInstance(String text) {

    	Page1 f = new Page1();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}