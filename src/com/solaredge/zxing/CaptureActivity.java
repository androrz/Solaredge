package com.solaredge.zxing;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.solaredge.R;
import com.solaredge.entity.InverterGridItem;
import com.solaredge.entity.JsonResponse;
import com.solaredge.fusion.FusionCode;
import com.solaredge.fusion.SvcNames;
import com.solaredge.server.response.SlrResponse;
import com.solaredge.ui.BaseActivity;
import com.solaredge.utils.SerializeUtil;
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

	@ViewInject(R.id.t_scan_mac)
	private TextView mOptimizerMacGridTV;

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

	@SuppressWarnings("unchecked")
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

		mGridsToSubmit = (List<InverterGridItem>) SerializeUtil
				.deserializeObject("scaned_list");
		if (mGridsToSubmit != null) {
			for (int i = 0; i < mGridsToSubmit.size(); i++) {
				mGridView.setGridScaned(
						mGridsToSubmit.get(i).getUniversalRow(), mGridsToSubmit
								.get(i).getUniversalCol());
			}
		}

		mGridView.setSelectedGrid(mRow, mCol);
	}

	@OnClick(R.id.i_left)
	private void onMoveLeftClick(View view) {
		int tmpRow = mRow;
		int tmpCol = mCol;
		boolean found = false;
		int i = mRow;
		int j = mCol;

		if (mIsHorizontal) {
			j--;
			for (; i >= 0; i--) {
				for (; j >= 0; j--) {
					if (mGridView.getGridValue(i, j) != -1) {
						found = true;
						break;
					}
				}
				if (found) {
					break;
				}
				j = mMaxCol;
			}
		} else {
			i--;
			for (; j >= 0; j--) {
				for (; i >= 0; i--) {
					if (mGridView.getGridValue(i, j) != -1) {
						found = true;
						break;
					}
				}
				if (found) {
					break;
				}
				i = mMaxRow;
			}
		}

		mRow = i;
		mCol = j;
		if (mRow >= mMaxRow || mCol >= mMaxCol) {
			mRow = tmpRow;
			mCol = tmpCol;
		}
		mGridView.setSelectedGrid(mRow, mCol);
	}

	@OnClick(R.id.i_right)
	private void onMoveRightClick(View view) {
		int tmpRow = mRow;
		int tmpCol = mCol;
		boolean found = false;
		int i = mRow;
		int j = mCol;

		if (mIsHorizontal) {
			j++;
			for (; i < mMaxRow; i++) {
				for (; j < mMaxCol; j++) {
					if (mGridView.getGridValue(i, j) != -1) {
						found = true;
						break;
					}
				}
				if (found) {
					break;
				}
				j = 0;
			}
		} else {
			i++;
			for (; j < mMaxCol; j++) {
				for (; i < mMaxRow; i++) {
					if (mGridView.getGridValue(i, j) != -1) {
						found = true;
						break;
					}
				}
				if (found) {
					break;
				}
				i = 0;
			}
		}

		mRow = i;
		mCol = j;
		if (mRow >= mMaxRow || mCol >= mMaxCol) {
			mRow = tmpRow;
			mCol = tmpCol;
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
		if (mGridsToSubmit == null || mGridsToSubmit.size() == 0) {
			AlertDialog dialog = new AlertDialog.Builder(this)
					.setTitle(R.string.app_prompt)
					.setMessage(R.string.scan_empty)
					.setPositiveButton(R.string.app_ok, new OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).create();
			dialog.show();
			return;
		}
		int size = mGridView.getValidGrid();
		if (mGridsToSubmit.size() != size) {
			AlertDialog dialog = new AlertDialog.Builder(this)
					.setTitle(R.string.app_prompt)
					.setMessage(R.string.not_finished)
					.setPositiveButton(R.string.continue_submit,
							new OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									mSolarManager.setOptimizer(mStationId,
											getScouterString());
									dialog.dismiss();
								}
							})
					.setNegativeButton(R.string.donot_submit,
							new OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).create();
			dialog.show();
			return;
		}
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
		SerializeUtil.serializeObject("scaned_list", mGridsToSubmit);
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
		String rawCode = recode(result.toString());
		if (rawCode.length() > 8) {
			rawCode = rawCode.substring(rawCode.length() - 8, rawCode.length());
		}
		final String finalCode = new String(rawCode);
		if (mGridsToSubmit != null && mGridsToSubmit.size() > 0) {
			for (InverterGridItem item : mGridsToSubmit) {
				if (!item.equals(mCurrentGrid)
						&& item.getMacId().equals(finalCode)) {
					AlertDialog dialog = new AlertDialog.Builder(this)
							.setTitle(R.string.app_prompt)
							.setMessage(R.string.duplicate_mac)
							.setPositiveButton(R.string.app_ok,
									new OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
											continuePreview();
										}
									}).create();
					dialog.show();
					return;
				}
			}
			
			boolean found = false;
			for (InverterGridItem item : mGridsToSubmit) {
				if (item.equals(mCurrentGrid)) {
					AlertDialog dialog = new AlertDialog.Builder(this)
							.setTitle(R.string.app_prompt)
							.setMessage(R.string.confirm_replace)
							.setPositiveButton(R.string.app_ok,
									new OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
											handleCurrentGrid(finalCode);
										}
									})
							.setNegativeButton(R.string.app_cancel,
									new OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
											continuePreview();
										}
									}).create();
					dialog.show();
					found = true;
				}
			}
			if (!found) {
				handleCurrentGrid(finalCode);
				continuePreview();
			}
		}
	}

	private void handleCurrentGrid(String recode) {
		if (mCurrentGrid != null) {
			mCurrentGrid.setMacId(recode);
			mOptimizerMacGridTV
					.setText(getString(R.string.scan_mac_id, recode));
			mGridView.setGridScaned(mCurrentGrid.getUniversalRow(),
					mCurrentGrid.getUniversalCol());
			mOptimizerMacGridTV.setVisibility(View.VISIBLE);
		}

		if (mGridsToSubmit == null) {
			mGridsToSubmit = new ArrayList<InverterGridItem>();
		} else if (mGridsToSubmit.contains(mCurrentGrid)) {
			mGridsToSubmit.remove(mCurrentGrid);
		}

		mGridsToSubmit.add(mCurrentGrid);
		continuePreview();
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

		if (mGridsToSubmit == null || mGridsToSubmit.size() == 0) {
			mOptimizerMacGridTV.setVisibility(View.GONE);
			return;
		}

		boolean contains = false;
		for (int i = 0; i < mGridsToSubmit.size(); i++) {
			InverterGridItem item = mGridsToSubmit.get(i);
			if (item.getUniversalRow() == row && item.getUniversalCol() == col) {
				mOptimizerMacGridTV.setText(getString(R.string.scan_mac_id,
						item.getMacId()));
				contains = true;
			}
		}
		if (!contains) {
			mOptimizerMacGridTV.setVisibility(View.GONE);
		} else {
			mOptimizerMacGridTV.setVisibility(View.VISIBLE);
		}
	}

	public void continuePreview() {
		mBaseHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (mHandler != null) {
					mHandler.restartPreviewAndDecode();
				}

			}
		}, 2000);

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
				AlertDialog dialog = new AlertDialog.Builder(this)
						.setTitle(R.string.app_prompt)
						.setMessage(R.string.commit_success)
						.setPositiveButton(R.string.app_ok,
								new OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).create();
				dialog.show();
			} else if (jr.getBodyField("sub_code").equals("0006")) {
				String detail = jr.getBodyField("detail");
				String badMac = detail.substring(detail.indexOf(":") + 1,
						detail.indexOf(":") + 1 + 8);
				for (InverterGridItem item : mGridsToSubmit) {
					if (item.getMacId().equals(badMac)) {
						mGridView.setSelectedGrid(item.getUniversalRow(),
								item.getUniversalCol());
					}
				}
				AlertDialog dialog = new AlertDialog.Builder(this)
						.setTitle(R.string.app_prompt)
						.setMessage(detail)
						.setPositiveButton(R.string.app_ok,
								new OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).create();
				dialog.show();
			}
			break;

		default:
			break;
		}
	}

}