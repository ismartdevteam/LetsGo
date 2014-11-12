package mn.student.letsgo.common;


import mn.student.letsgo.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

public class CircleView extends View {
	private float angle = 0;
	private int position = 0;
	private String name;

	public float getAngle() {
		return angle;
	}
	
	public void setAngle(float angle) {
		this.angle = angle;
	}
	
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	
	/**
	 * Return the name of the view.
	 * @return Returns the name of the view.
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Set the name of the view.
	 * @param name The name to be set for the view.
	 */
	public void setName(String name){
		this.name = name;
	}

	/**
	 * @param context
	 */
	public CircleView(Context context) {
		this(context, null);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public CircleView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public CircleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (attrs != null) {
			TypedArray array = getContext().obtainStyledAttributes(attrs,
					R.styleable.CircleImageView);
			
			this.name = array.getString(R.styleable.CircleImageView_name);
		}
	}

}