package com.solaredge.view;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.solaredge.R;

/**
 * This view displays a grid of rectangles that can be zoomed in and out. The
 * grid is N times the size of the view. The zoom point is the center of the
 * view.
 * <p>
 * This class is a subclass of PanZoomView, which provides most of the code to
 * support zooming and panning.
 */

public class PanZoomGridView extends PanZoomView {

	static public int NumIconHorizontal = 1;
	static public int NumIconVertical = 1;
	static public final int CanvasSizeMultiplier = 3;
	static public final int NumSquaresAlongCanvas = CanvasSizeMultiplier
			* NumIconHorizontal;
	static public final int IconTypes = 4;
	static public final int HORIZONTAL_GAP = 8;
	static public final int VERTICAL_GAP = 5;

	// Variables that control placement and translation of the canvas.
	// Initial values are for debugging on 480 x 320 screen. They are reset in
	// onDraw.
	private float mOriginOffsetX = 320;
	private float mOriginOffsetY = 320;
	private float mIconWidth = 54; // use float for more accurate placement
	private float mIconHeight = 54;
	private float mRawIconWidth = 54; // use float for more accurate placement
	private float mRawIconHeight = 54;

	private Random mRandomObject = new Random(System.currentTimeMillis());
	private final int[] mImageIds = { R.drawable.icon_plate,
			R.drawable.icon_plate_selected, R.drawable.icon_plate_h,
			R.drawable.icon_plate_selected_h };
	private Bitmap[] mBitmaps = null;
	private int[][] mGrid = null;

	private boolean mSetUp = false;

	private int mRow = -1;
	private int mCol = -1;
	private OnGridClickListener mListener;
	private boolean mGridSelectable = false;

	/**
	 * Constructors for the view.
	 */
	public PanZoomGridView(Context context) {
		super(context);
	}

	public PanZoomGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PanZoomGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * Draw image squares along the diagonal of the view. Draw N squares in the
	 * visible portion of the view and M outside the view. The canvas object is
	 * already set up to be drawn on. That means that all translations and
	 * scaling operations have already been done.
	 *
	 * @param canvas
	 *            Canvas
	 * @return void
	 */

	public void drawOnCanvas(Canvas canvas) {

		int x, y;
		float fx, fy;

		Paint paint = new Paint();

		// Set width and height to be used for the rectangle to be drawn.
		int ih = (int) Math.floor(mIconHeight);
		int iw = (int) Math.floor(mIconWidth);

		// Set up the bitmaps to be displayed. Set up the grid.
		Bitmap[] bitmaps = getBitmapsArray();
		int[][] grid = getGridArray();

		Bitmap b1;
		x = 0;
		y = 0;
		Rect dest = new Rect(x, y, iw, ih);
		float dx = 0, dy = 0;
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				int bitmapIndex = grid[i][j];
				if (bitmapIndex == -1) { // no image here
					continue;
				}
				if (i == mRow && j == mCol && mGridSelectable) {
					bitmapIndex += 1;
				}
				b1 = bitmaps[bitmapIndex];
				dx = j * (mIconWidth + HORIZONTAL_GAP);
				dy = i * (mIconHeight + VERTICAL_GAP);
				int dxi = (int) Math.round(dx);
				int dyi = (int) Math.round(dy);
				dest.offsetTo(dxi, dyi);
				canvas.drawBitmap(b1, null, dest, paint);
			}
		}

		// Draw a circle at the focus point so it's clear if scaling is working.
		// Do this last so it shows on top of everything else.
		fx = mFocusX;
		fy = mFocusY;
		if (mScaleDetector.isInProgress()) {
			paint.setColor(Color.RED);
		} else {
			paint.setColor(Color.RED);
		}
		// canvas.drawCircle(fx, fy, 4, paint);
	}

	/**
	 * Get an array of bitmaps, chosen randomly from the mImageIds.
	 *
	 * @return Bitmap []
	 */

	Bitmap[] getBitmapsArray() {
		if (mBitmaps == null) {
			mBitmaps = new Bitmap[IconTypes];
			for (int i = 0; i < IconTypes; i++) {
				Bitmap b1 = BitmapFactory.decodeResource(
						mContext.getResources(), mImageIds[i]);
				mBitmaps[i] = b1;
			}

		}

		return mBitmaps;
	}

	/**
	 * Get 2-d grid of integers that indicate which bitmap is displayed at that
	 * point.
	 *
	 * @return int [] []
	 */

	int[][] getGridArray() {
		if (mGrid == null) {
			mGrid = new int[NumIconVertical][NumIconHorizontal];
			for (int i = 0; i < NumIconVertical; i++)
				for (int j = 0; j < NumIconHorizontal; j++) {
					int index = randomInt(0, IconTypes - 1);
					mGrid[i][j] = index;
				}

		}
		return mGrid;
	}

	public void setGridArray(int[][] matrix) {
		mGrid = matrix;
		NumIconHorizontal = mGrid[0].length;
		NumIconVertical = mGrid.length;
		reset();
	}

	public boolean getSelectable() {
		return mGridSelectable;
	}

	public void setSelectable(boolean isSelectable) {
		mGridSelectable = isSelectable;
	}

	/**
	 * onDraw
	 */
	@Override
	public void onDraw(Canvas canvas) {
		// This subclass of PanZoomView overrides the general purpose onDraw
		// method implemented in PanZoomView. It still needs to do the standard
		// onDraw so call "superOnDraw" that is provided in PanZoomView for that
		// purpose.
		superOnDraw(canvas);

		canvas.save();

		// Get the width and height of the view.
		int viewH = getHeight(), viewW = getWidth();

		// Because we are displays a region N times the view size. The top left
		// point of the view is located at a point that is some multiple of the
		// width and height. For a canvas size of 3, the multiple is 1; for 4,
		// 1.5, for 5, 2;
		mOriginOffsetX = ((float) (CanvasSizeMultiplier - 1) * viewW) / 2;
		mOriginOffsetY = ((float) (CanvasSizeMultiplier - 1) * viewH) / 2;

		// Set width and height to be used for the icon.
		mIconWidth = (float) (viewW - (NumIconHorizontal - 1) * HORIZONTAL_GAP)
				/ (float) NumIconHorizontal;
		// mIconHeight = (float) viewH / (float) NumIconVertical;
		mIconHeight = (float) mIconWidth * mRawIconHeight / mRawIconWidth;

		// The canvas is translated by the amount we have scrolled and the
		// standard amount to move the origin of the canvas up and left so the
		// 3x wide region is centered in the view.
		// (Note: mPosX and mPosY are defined in PanZoomView.)
		float x = 0, y = 0;
		mPosX0 = mOriginOffsetX;
		mPosY0 = mOriginOffsetY;
		if (!mSetUp) {
			mPosX = mOriginOffsetX;
			mPosY = mOriginOffsetY;
			mSetUp = true;
		}
		x = mPosX - mPosX0;
		y = mPosY - mPosY0;
		canvas.translate(x, y);

		// The focus point for zooming is the center of the displayable region.
		// That point is defined by half the canvas width and height.
		mFocusX = viewW / 2;
		mFocusY = (mIconHeight * NumIconVertical + VERTICAL_GAP
				* (NumIconVertical - 1)) / 2;
		canvas.scale(mScaleFactor, mScaleFactor, mFocusX, mFocusY);

		// Do the drawing operation for the view.
		drawOnCanvas(canvas);

		canvas.restore();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_UP:
			long interval = System.currentTimeMillis() - mTimestamp;
			if (interval > CLICK_TIME_INTERVAL) {
				break;
			}
			int x = (int) event.getX();
			int y = (int) event.getY();

			int xOffset = (int) ((mPosX - mPosX0));
			int yOffset = (int) ((mPosY - mPosY0));
			int scaleOffsetX = (int) ((mScaleFactor - 1.0f) * mFocusX);
			int scaleOffsetY = (int) ((mScaleFactor - 1.0f) * mFocusY);
			int row = (y - yOffset + scaleOffsetY)
					/ (int) ((mIconWidth + VERTICAL_GAP) * mScaleFactor);
			int col = (x - xOffset + scaleOffsetX)
					/ (int) ((mIconHeight + HORIZONTAL_GAP) * mScaleFactor);

			Log.d("Alading", "row: " + row + " col: " + col);
			if (row < mGrid.length && row >= 0 && col < mGrid[0].length
					&& col >= 0) {

				if (mGrid[row][col] >= 0) {
					mRow = row;
					mCol = col;
				}

				if (mListener != null) {
					mListener.onGridClick(row, col);
				}
			}
			break;
		}

		return super.onTouchEvent(event);
	}

	public void addClickListener(OnGridClickListener listener) {
		mListener = listener;
	}

	public void reset() {
		mPosX = mOriginOffsetX;
		mPosY = mOriginOffsetY;
		mScaleFactor = 1.0f;
		invalidate();

		mTouched = false;
	}

	/**
	 * Return a random number in the range: minVal to maxVal.
	 *
	 */

	public int randomInt(int minVal, int maxVal) {
		Random r = mRandomObject;
		int range = maxVal - minVal;
		int offset = (int) Math.round(r.nextFloat() * range);
		return minVal + offset;
	}

	/**
	 * Return the resource id of the sample image. Note that this class always
	 * returns 0, indicating that there is no sample drawable.
	 *
	 * @return int
	 */

	public int sampleDrawableId() {
		return 0;
	}

	/**
	 * Return true if panning is supported.
	 *
	 * @return boolean
	 */

	public boolean supportsPan() {
		return true;
	}

	/**
	 * Return true if scaling is done around the focus point of the pinch.
	 *
	 * @return boolean
	 */

	public boolean supportsScaleAtFocusPoint() {
		return true;
	}

	/**
	 * Return true if pinch zooming is supported.
	 *
	 * @return boolean
	 */

	public boolean supportsZoom() {
		return true;
	}

	public void deleteGridItem(int row, int col) {
		mGrid[row][col] = -1;
		invalidate();
	}

	public interface OnGridClickListener {
		public void onGridClick(int row, int col);
	}
} // end class
