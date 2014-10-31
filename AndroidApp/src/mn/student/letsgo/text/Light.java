package mn.student.letsgo.text;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class Light extends TextView {

	public Light(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public Light(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Light(Context context) {
		super(context);
		init();
	}

	private void init() {
		if (!isInEditMode()) {
			Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
					"fonts/OpenSans-Light.ttf");
			setTypeface(tf);
		}
	}
}