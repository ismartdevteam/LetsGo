package mn.student.letsgo.text;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class Regular extends TextView {

	public Regular(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public Regular(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Regular(Context context) {
		super(context);
		init();
	}

	private void init() {
		if (!isInEditMode()) {
			Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
					"fonts/OpenSans-Regular.ttf");
			setTypeface(tf);
		}
	}
}