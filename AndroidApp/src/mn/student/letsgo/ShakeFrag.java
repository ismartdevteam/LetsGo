package mn.student.letsgo;

import mn.student.letsgo.MainActivity.PlaceholderFragment;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ShakeFrag extends Fragment {
	  private static final String ARG_SECTION_NUMBER = "section_number";

      /**
       * Returns a new instance of this fragment for the given section
       * number.
       */
      public static ShakeFrag newInstance(int sectionNumber) {
    	  ShakeFrag fragment = new ShakeFrag();
          Bundle args = new Bundle();
          args.putInt(ARG_SECTION_NUMBER, sectionNumber);
          fragment.setArguments(args);
          return fragment;
      }

      public ShakeFrag() {
      }

      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container,
              Bundle savedInstanceState) {
          View rootView = inflater.inflate(R.layout.fragment_main, container, false);
          return rootView;
      }

      @Override
      public void onAttach(Activity activity) {
          super.onAttach(activity);
          ((MainActivity) activity).onSectionAttached(
                  getArguments().getInt(ARG_SECTION_NUMBER));
      }
  }

