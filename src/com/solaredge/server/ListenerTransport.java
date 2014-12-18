package com.solaredge.server;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.solaredge.server.response.AlaResponse;

public class ListenerTransport implements SolarListener {

	private static final int TYPE_HANDLE_EVENT = 1;

	public static final int TYPE_UNDEFINED = -1;
	public static final int TYPE_TISSOT_PUBLIC = 0;

	private SolarListener mListener;
	private final Handler mListenerHandler;
	private int mTransportType;

	@SuppressLint("HandlerLeak")
	ListenerTransport(SolarListener listener, Looper looper) {
		mListener = listener;

		if (looper == null) {
			mListenerHandler = new Handler() {

				@Override
				public void handleMessage(Message msg) {
					_handleMessage(msg);
				}
			};
		} else {
			mListenerHandler = new Handler(looper) {

				@Override
				public void handleMessage(Message msg) {
					_handleMessage(msg);
				}
			};
		}
	}

	private void _handleMessage(Message msg) {
		switch (msg.what) {
		case TYPE_HANDLE_EVENT:
			AlaResponse response = (AlaResponse) msg.obj;
			int resultCode = msg.arg1;
			mListener.handleEvent(resultCode, response);
			break;
		}
	}

	public void setTransportType(int type) {
		mTransportType = type;
	}

	public int getTransportType() {
		return mTransportType;
	}

	@Override
	public void handleEvent(int resultCode, AlaResponse response) {
		Message msg = Message.obtain();
		msg.what = TYPE_HANDLE_EVENT;
		msg.obj = response;
		msg.arg1 = resultCode;
		mListenerHandler.sendMessage(msg);
	}

	@Override
	public String toString() {
		String listener = mListener.getClass().toString();
		String page = listener.substring(listener.lastIndexOf('.') + 1,
				listener.length());
		return page + "-" + mTransportType;
	}
}
