package com.solaredge.zxing;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.Vector;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.solaredge.R;
import com.solaredge.ui.BaseActivity;
import com.solaredge.view.PanZoomGridView;
import com.solaredge.zxing.camera.CameraManager;
import com.solaredge.zxing.decoding.CaptureActivityHandler;
import com.solaredge.zxing.decoding.InactivityTimer;
import com.solaredge.zxing.decoding.RGBLuminanceSource;
import com.solaredge.zxing.decoding.Utils;
import com.solaredge.zxing.view.ViewfinderView;

public class CaptureActivity extends BaseActivity implements Callback {

	private static final int REQUEST_CODE = 234;
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;

	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private String photo_path;
	private Bitmap scanBitmap;

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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_capture);
		super.onCreate(savedInstanceState);

		// 初始化 CameraManager
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.v_view_finder);

		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);

		int[][] matrix = mSolarManager.getInverterMatrix();
		mMaxRow = matrix.length;
		mMaxCol = matrix[0].length;
		mGridView.setGridArray(matrix);
		mGridView.setSelectable(true);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {

			switch (requestCode) {

			case REQUEST_CODE:

				String[] proj = { MediaStore.Images.Media.DATA };
				// 获取选中图片的路径
				Cursor cursor = getContentResolver().query(data.getData(),
						proj, null, null, null);

				if (cursor.moveToFirst()) {

					int column_index = cursor
							.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					photo_path = cursor.getString(column_index);
					if (photo_path == null) {
						photo_path = Utils.getPath(getApplicationContext(),
								data.getData());
						Log.i("123path  Utils", photo_path);
					}
					Log.i("123path", photo_path);

				}

				cursor.close();

				new Thread(new Runnable() {

					@Override
					public void run() {

						Result result = scanningImage(photo_path);
						if (result == null) {
							Log.i("123", "   -----------");
							Looper.prepare();
							Toast.makeText(getApplicationContext(), "图片格式有误", 0)
									.show();
							Looper.loop();
						} else {
							Log.i("123result", result.toString());
							String recode = recode(result.toString());
							Intent data = new Intent();
							data.putExtra("result", recode);
							setResult(300, data);
							finish();
						}
					}
				}).start();
				break;

			}

		}

	}

	// TODO: 解析部分图片
	protected Result scanningImage(String path) {
		if (TextUtils.isEmpty(path)) {

			return null;

		}
		// DecodeHintType 和EncodeHintType
		Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
		hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 先获取原大小
		scanBitmap = BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false; // 获取新的大小

		int sampleSize = (int) (options.outHeight / (float) 200);

		if (sampleSize <= 0)
			sampleSize = 1;
		options.inSampleSize = sampleSize;
		scanBitmap = BitmapFactory.decodeFile(path, options);

		// --------------测试的解析方法---PlanarYUVLuminanceSource-这几行代码对project没作功----------

		LuminanceSource source1 = new PlanarYUVLuminanceSource(
				rgb2YUV(scanBitmap), scanBitmap.getWidth(),
				scanBitmap.getHeight(), 0, 0, scanBitmap.getWidth(),
				scanBitmap.getHeight(), false);
		BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
				source1));
		MultiFormatReader reader1 = new MultiFormatReader();
		Result result1;
		try {
			result1 = reader1.decode(binaryBitmap);
			String content = result1.getText();
		} catch (NotFoundException e1) {
			e1.printStackTrace();
		}

		// ----------------------------

		RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
		BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
		QRCodeReader reader = new QRCodeReader();
		try {
			return reader.decode(bitmap1, hints);
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (ChecksumException e) {
			e.printStackTrace();
		} catch (FormatException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.s_preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
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
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {

	}

	public void handleDecode(final Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String recode = recode(result.toString());

		// 数据返回
		Intent data = new Intent();
		data.putExtra("result", recode);
		setResult(300, data);
		finish();
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud, so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.mo_scanner_beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
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

	public byte[] rgb2YUV(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int[] pixels = new int[width * height];
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

		int len = width * height;
		byte[] yuv = new byte[len * 3 / 2];
		int y, u, v;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int rgb = pixels[i * width + j] & 0x00FFFFFF;

				int r = rgb & 0xFF;
				int g = (rgb >> 8) & 0xFF;
				int b = (rgb >> 16) & 0xFF;

				y = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
				u = ((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128;
				v = ((112 * r - 94 * g - 18 * b + 128) >> 8) + 128;

				y = y < 16 ? 16 : (y > 255 ? 255 : y);
				u = u < 0 ? 0 : (u > 255 ? 255 : u);
				v = v < 0 ? 0 : (v > 255 ? 255 : v);

				yuv[i * width + j] = (byte) y;
			}
		}
		return yuv;
	}
}