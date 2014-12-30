package com.solaredge.zxing;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.solaredge.R;
import com.solaredge.entity.InverterGridItem;
import com.solaredge.entity.JsonResponse;
import com.solaredge.fusion.FusionCode;
import com.solaredge.fusion.SvcNames;
import com.solaredge.server.response.SlrResponse;
import com.solaredge.ui.BaseActivity;
import com.solaredge.utils.DbHelp;
import com.solaredge.utils.LogX;
import com.solaredge.view.PanZoomGridView;
import com.solaredge.view.PanZoomGridView.OnGridClickListener;
import com.solaredge.zxing.camera.CameraManager;
import com.solaredge.zxing.decoding.CaptureActivityHandler;
import com.solaredge.zxing.decoding.InactivityTimer;
import com.solaredge.zxing.view.ViewfinderView;

public class CaptureActivity extends BaseActivity implements Callback,
		OnGridClickListener {

	private CaptureActivityHandler mHandler;
	private ViewfinderView mViewfinderView;

	private boolean mHasSurface;
	private Vector<BarcodeFormat> mDecodeFormats;
	private String mCharacterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mPlayer;
	private boolean mPlayBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean mVibrate;

	@ViewInject(R.id.t_scan_label)
	private TextView mInverterGridTV;

	@ViewInject(R.id.p_grid_view)
	private PanZoomGridView mGridView;

	@ViewInject(R.id.i_left)
	private ImageButton mLeftIB;

	@ViewInject(R.id.i_toggle)
	private ImageButton mToggleDirectionIB;

	@ViewInject(R.id.i_commit)
	private ImageButton mCommitIB;

	@ViewInject(R.id.i_right)
	private ImageButton mRightIB;

	private int mRow = 0;
	private int mCol = 0;
	private int mMaxRow;
	private int mMaxCol;
	private boolean mIsHorizontal = true;

	private InverterGridItem mCurrentGrid;
	private List<InverterGridItem> mGridsToSubmit;
	private String mStationId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_capture);
		super.onCreate(savedInstanceState);

		Bundle bundle = mIntent.getExtras();
		mStationId = bundle.getString("station_id");

		CameraManager.init(getApplication());
		mViewfinderView = (ViewfinderView) findViewById(R.id.v_view_finder);

		mHasSurface = false;
		inactivityTimer = new InactivityTimer(this);

		int[][] matrix = mSolarManager.getInverterMatrix();
		mMaxRow = matrix.length;
		mMaxCol = matrix[0].length;
		mGridView.setGridArray(matrix);
		mGridView.setSelectable(true);
		mGridView.addClickListener(this);
		mGridView.setSelectedGrid(mRow, mCol);
	}

	@OnClick(R.id.i_left)
	private void onMoveLeftClick(View view) {
		if (mIsHorizontal) {
			mCol--;
			if (mCol < 0) {
				mCol = 0;
			}
		} else {
			mRow--;
			if (mRow < 0) {
				mRow = 0;
			}
		}

		mGridView.setSelectedGrid(mRow, mCol);
	}

	@OnClick(R.id.i_right)
	private void onMoveRightClick(View view) {
		if (mIsHorizontal) {
			mCol++;
			if (mCol > mMaxCol - 1) {
				mCol = mMaxCol - 1;
			}
		} else {
			mRow++;
			if (mRow > mMaxRow - 1) {
				mRow = mMaxRow - 1;
			}
		}

		mGridView.setSelectedGrid(mRow, mCol);
	}

	@OnClick(R.id.i_toggle)
	private void onDirectionClick(View view) {
		if (mIsHorizontal) {
			mToggleDirectionIB
					.setImageResource(R.drawable.icon_toggle_vertical);
			mIsHorizontal = false;
		} else {
			mToggleDirectionIB
					.setImageResource(R.drawable.icon_toggle_horizontal);
			mIsHorizontal = true;
		}
	}

	@OnClick(R.id.i_commit)
	private void onCommitClick(View view) {
		LogX.trace(TAG, mGridsToSubmit.toString());
		mSolarManager.setOptimizer(mStationId, getScouterString());
	}

	private String getScouterString() {
		StringBuilder builder = new StringBuilder();
		builder.append("<scouters>");
		if (mGridsToSubmit == null || mGridsToSubmit.size() == 0) {
			return FusionCode.ETY_STR;
		} else {
			for (int i = 0; i < mGridsToSubmit.size(); i++) {
				builder.append(mGridsToSubmit.get(i).toString());
			}
		}
		builder.append("</scouters>");

		return builder.toString();
	}

	@Override
	protected void initWidgetEvent() {
		super.initWidgetEvent();
	}

	@Override
	protected void initWidgetProperty() {
		super.initWidgetProperty();
		mBack.setVisibility(View.GONE);
	}

	boolean flag = true;

	protected void light() {
		if (flag == true) {
			flag = false;
			CameraManager.get().openLight();
		} else {
			flag = true;
			CameraManager.get().offLight();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.s_preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (mHasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		mDecodeFormats = null;
		mCharacterSet = null;

		mPlayBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			mPlayBeep = false;
		}
		initBeepSound();
		mVibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mHandler != null) {
			mHandler.quitSynchronously();
			mHandler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (mHandler == null) {
			mHandler = new CaptureActivityHandler(this, mDecodeFormats,
					mCharacterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!mHasSurface) {
			mHasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mHasSurface = false;
	}

	public ViewfinderView getViewfinderView() {
		return mViewfinderView;
	}

	public Handler getHandler() {
		return mHandler;
	}

	public void drawViewfinder() {

	}

	public void handleDecode(final Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String recode = recode(result.toString());
		if (mCurrentGrid != null) {
			mCurrentGrid.setMacId(recode);
		}

		if (mGridsToSubmit == null) {
			mGridsToSubmit = new ArrayList<InverterGridItem>();
		} else if (mGridsToSubmit.contains(mCurrentGrid)) {
			mGridsToSubmit.remove(mCurrentGrid);
		}

		mGridsToSubmit.add(mCurrentGrid);

		mBaseHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				continuePreview();
			}
		}, 2000);
	}

	private void initBeepSound() {
		if (mPlayBeep && mPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud, so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mPlayer = new MediaPlayer();
			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.mo_scanner_beep);
			try {
				mPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mPlayer.prepare();
			} catch (IOException e) {
				mPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (mPlayBeep && mPlayer != null) {
			mPlayer.start();
		}
		if (mVibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	private String recode(String str) {
		String formart = "";

		try {
			boolean ISO = Charset.forName("ISO-8859-1").newEncoder()
					.canEncode(str);
			if (ISO) {
				formart = new String(str.getBytes("ISO-8859-1"), "GB2312");
			} else {
				formart = str;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return formart;
	}

	@Override
	public void onGridClick(int row, int col) {
		mRow = row;
		mCol = col;

		mCurrentGrid = mSolarManager.getInverterGridByCoordinate(row, col);
		mInverterGridTV.setText(getString(R.string.scan_inverter_label,
				mCurrentGrid.getInverterName(), mCurrentGrid.getRow() + 1,
				mCurrentGrid.getCol() + 1));
	}

	public void continuePreview() {
		if (mHandler != null) {
			mHandler.restartPreviewAndDecode();
		}
	}

	@Override
	public void handleEvent(int resultCode, SlrResponse response) {
		if (!analyzeAsyncResultCode(resultCode, response)) {
			return;
		}

		int action = response.getResponseEvent();
		JsonResponse jr = response.getResponseContent();
		switch (action) {
		case SvcNames.WSN_SET_OPTIMIZER:
			if (jr.getBodyField("is_success").equals("1")) {
				finish();
			}
			break;

		default:
			break;
		}
	}

}