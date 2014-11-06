package mn.student.letsgo.common;

import java.util.Random;

import mn.student.letsgo.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;

public class CircleLayout extends ViewGroup {
	// Event listeners
	private OnItemClickListener mOnItemClickListener = null;
	private OnItemSelectedListener mOnItemSelectedListener = null;
	private OnCenterClickListener mOnCenterClickListener = null;
	private OnRotationFinishedListener mOnRotationFinishedListener = null;

	// Background image
	private Bitmap imageOriginal, imageScaled;
	private Matrix matrix;

	private int selected = 0;

	// Child sizes
	private int mMaxChildWidth = 0;
	private int mMaxChildHeight = 0;
	private int childWidth = 0;
	private int childHeight = 0;

	// Sizes of the ViewGroup
	private int circleWidth, circleHeight;
	private int radius = 0;

	// needed for detecting the inversed rotations
	private boolean[] quadrantTouched;

	// Settings of the ViewGroup
	private boolean allowRotating = true;
	private float angle = 90;
	private float firstChildPos = 90;
	private boolean rotateToCenter = true;
	private boolean isRotating = true;
	private int speed = 75;
	private float deceleration = 1 + (5f / speed);

	// The runnable of the current rotation
	private FlingRunnable actRunnable = null;
	
	private float angleDelay; 
	private CircleView imgView;

	public CircleLayout(Context context) {
		this(context, null);
	}

	public CircleLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CircleLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	protected void init(AttributeSet attrs) {
		quadrantTouched = new boolean[] { false, false, false, false, false };

		if (attrs != null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs,
					R.styleable.Circle);

			// The angle where the first menu item will be drawn
			angle = a.getInt(R.styleable.Circle_firstChildPosition, 90);
			firstChildPos = angle;

			rotateToCenter = a.getBoolean(R.styleable.Circle_rotateToCenter,
					true);
			isRotating = a.getBoolean(R.styleable.Circle_isRotating, true);
			speed = a.getInt(R.styleable.Circle_speed, 75);
			deceleration = 1 + (5f / speed);

			// If the menu is not rotating then it does not have to be centered
			// since it cannot be even moved
			if (!isRotating) {
				rotateToCenter = false;
			}

			if (imageOriginal == null) {
				int picId = a.getResourceId(
						R.styleable.Circle_circleBackground, -1);

				// If a background image was set as an attribute,
				// retrieve the image
				if (picId != -1) {
					imageOriginal = BitmapFactory.decodeResource(
							getResources(), picId);
				}
			}

			a.recycle();

			// initialize the matrix only once
			if (matrix == null) {
				matrix = new Matrix();
			} else {
				// not needed, you can also post the matrix immediately to
				// restore the old state
				matrix.reset();
			}

			// Needed for the ViewGroup to be drawn
			setWillNotDraw(false);
		}
	}

	public View getSelectedItem() {
		return (selected >= 0) ? getChildAt(selected) : null;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		circleHeight = getHeight();
		circleWidth = getWidth();

		if (imageOriginal != null) {
			if (imageScaled == null) {
				matrix = new Matrix();
				float sx = (((radius + childWidth / 4) * 2) / (float) imageOriginal
						.getWidth());
				float sy = (((radius + childWidth / 4) * 2) / (float) imageOriginal
						.getHeight());
				matrix.postScale(sx, sy);
				imageScaled = Bitmap.createBitmap(imageOriginal, 0, 0,
						imageOriginal.getWidth(), imageOriginal.getHeight(),
						matrix, false);
			}

			if (imageScaled != null) {
				int cx = (circleWidth - imageScaled.getWidth()) / 2;
				int cy = (circleHeight - imageScaled.getHeight()) / 2;

				Canvas g = canvas;
				canvas.rotate(0, circleWidth / 2, circleHeight / 2);
				g.drawBitmap(imageScaled, cx, cy, null);

			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		mMaxChildWidth = 0;
		mMaxChildHeight = 0;

		int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
				MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST);
		int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
				MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST);

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() == GONE) {
				continue;
			}

			child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

			mMaxChildWidth = Math.max(mMaxChildWidth, child.getMeasuredWidth());
			mMaxChildHeight = Math.max(mMaxChildHeight,
					child.getMeasuredHeight());
		}

		// Measure again for each child to be exactly the same size.
		childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxChildWidth,
				MeasureSpec.EXACTLY);
		childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxChildHeight,
				MeasureSpec.EXACTLY);

		for (int i = 2; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() == GONE) {
				continue;
			}

			child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
		}

		setMeasuredDimension(resolveSize(mMaxChildWidth, widthMeasureSpec),
				resolveSize(mMaxChildHeight, heightMeasureSpec));
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int layoutWidth = r - l;
		int layoutHeight = b - t;

		// Laying out the child views
		final int childCount = getChildCount();
		int left, top;
		radius = (layoutWidth <= layoutHeight) ? layoutWidth / 3
				: layoutHeight / 3;

		childWidth = (int) (radius / 2);
		childHeight = (int) (radius / 2);

		if(getChildCount() == 0){
			angleDelay = 360;
		}else angleDelay = 360 / getChildCount(); 		
		

		for (int i = 0; i < childCount; i++) {
			imgView = (CircleView) getChildAt(i);
			//View child = getChildAt(i);
			if (imgView.getVisibility() == GONE) {
				continue;
			}

			if (angle > 360) {
				angle -= 360;
			} else {
				if (angle < 0) {
					angle += 360;
				}
			}

			imgView.setAngle(angle);
			imgView.setPosition(i);

			left = Math
					.round((float) (((layoutWidth / 2) - childWidth / 2) + radius
							* Math.cos(Math.toRadians(angle))));
			top = Math
					.round((float) (((layoutHeight / 2) - childHeight / 2) + radius
							* Math.sin(Math.toRadians(angle))));

			imgView.layout(left, top, left + childWidth, top + childHeight);
			angle += angleDelay;
		}
	}

	private void rotateButtons(float degrees) {
		int left, top, childCount = getChildCount();
		float angleDelay = 360 / childCount;
		angle += degrees;

		if (angle > 360) {
			angle -= 360;
		} else {
			if (angle < 0) {
				angle += 360;
			}
		}

		for (int i = 0; i < childCount; i++) {
			if (angle > 360) {
				angle -= 360;
			} else {
				if (angle < 0) {
					angle += 360;
				}
			}

			imgView = (CircleView) getChildAt(i);
			if (imgView.getVisibility() == GONE) {
				continue;
			}
			left = Math
					.round((float) (((circleWidth / 2) - childWidth / 2) + radius
							* Math.cos(Math.toRadians(angle))));
			top = Math
					.round((float) (((circleHeight / 2) - childHeight / 2) + radius
							* Math.sin(Math.toRadians(angle))));

			imgView.setAngle(angle);

			if (Math.abs(angle - firstChildPos) < (angleDelay / 2)
					&& selected != imgView.getPosition()) {
				selected = imgView.getPosition();

				if (mOnItemSelectedListener != null && rotateToCenter) {
					mOnItemSelectedListener.onItemSelected(imgView,
							imgView.getName());
				}
			}

			imgView.layout(left, top, left + childWidth, top + childHeight);
			angle += angleDelay;
		}
	}

	private void rotateViewToCenter(View view, boolean fromRunnable) {
		if (rotateToCenter) {
			float velocityTemp = 1;
			float destAngle = (float) (firstChildPos - ((CircleView) view).getAngle());
			float startAngle = 0;
			int reverser = 1;

			if (destAngle < 0) {
				destAngle += 360;
			}

			if (destAngle > 180) {
				reverser = -1;
				destAngle = 360 - destAngle;
			}

			while (startAngle < destAngle) {
				velocityTemp *= deceleration;
				startAngle += velocityTemp / speed;
			}

			CircleLayout.this.post(new FlingRunnable(reverser * velocityTemp,
					!fromRunnable));
		}
	}

	private class FlingRunnable implements Runnable {

		private float velocity;
		private float angleDelay;
		private boolean isFirstForwarding = true;
		private boolean wasBigEnough = false;

		public FlingRunnable(float velocity) {
			this(velocity, true);
		}

		public FlingRunnable(float velocity, boolean isFirst) {
			this.velocity = velocity;
			this.angleDelay = 360 / getChildCount();
			this.isFirstForwarding = isFirst;

			if (Math.abs(velocity) > 1) {
				wasBigEnough = true;
				CircleLayout.this.actRunnable = this;
			}
		}

		public void run() {
			if (allowRotating) {
				if (rotateToCenter) {
					if (Math.abs(velocity) > 1) {
						if (!(Math.abs(velocity) < 200 && (Math.abs(angle
								- firstChildPos)
								% angleDelay < 2))) {
							rotateButtons(velocity / speed);
							velocity /= deceleration;

							CircleLayout.this.post(this);
						} else {
							if (wasBigEnough
									&& CircleLayout.this.actRunnable == this
									&& (Math.abs(angle - firstChildPos)
											% angleDelay < 2)) {
								if (CircleLayout.this.mOnRotationFinishedListener != null) {
									CircleView view = (CircleView) getChildAt(selected);
									CircleLayout.this.mOnRotationFinishedListener
											.onRotationFinished(view,
													view.getName());
								}
							}
						}
					} else {
						if (isFirstForwarding) {
							isFirstForwarding = false;
							CircleLayout.this.rotateViewToCenter(
									(CircleView) getChildAt(selected),
									true);
						}
					}
				} else {
					rotateButtons(velocity / speed);
					velocity /= deceleration;

					CircleLayout.this.post(this);
				}
			}
		}
	}
	
	public void rotateView(){
		Random rand = new Random();
		rotateViewToCenter(imgView, true);
		FlingRunnable flin = new FlingRunnable((float)(5000f*rand.nextInt(100)));
		flin.run();
	} 

	public interface OnItemClickListener {
		void onItemClick(View view, String name);
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.mOnItemClickListener = onItemClickListener;
	}

	public interface OnItemSelectedListener {
		void onItemSelected(View view, String name);
	}

	public void setOnItemSelectedListener(
			OnItemSelectedListener onItemSelectedListener) {
		this.mOnItemSelectedListener = onItemSelectedListener;
	}

	public interface OnCenterClickListener {
		void onCenterClick();
	}

	public void setOnCenterClickListener(
			OnCenterClickListener onCenterClickListener) {
		this.mOnCenterClickListener = onCenterClickListener;
	}

	public interface OnRotationFinishedListener {
		void onRotationFinished(View view, String name);
	}

	public void setOnRotationFinishedListener(
			OnRotationFinishedListener onRotationFinishedListener) {
		this.mOnRotationFinishedListener = onRotationFinishedListener;
	}
}