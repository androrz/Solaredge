package com.solaredge.server.response;

import com.solaredge.entity.JsonResponse;


public class SlrResponse {

	/**
	 * This is the counterpart of various network request type. To facilitate
	 * the process of HTTP request results.
	 */
	protected int responseEvent;

	/**
	 * If the request was not successful, this is the corresponding message.
	 */
	protected String responseMessage;

	/**
	 * Under Normal Circumstances, the returned content will be wrapped in this
	 * filed in JSON format.
	 */
	protected JsonResponse responseContent;

	/**
	 * If dismiss the progress dialog when network operation complete, either
	 * success or fail.
	 */
	protected boolean closeProgress = false;

	/**
	 * Network operations are always time consuming. This field indicate if show
	 * a progress dialog before network operations.
	 */
	protected boolean showProgress = false;

	public static final int RESPONSE_EVENT_SHOW_PROGRESS = 1001;

	public int getResponseEvent() {
		return responseEvent;
	}

	public void setResponseEvent(int rspEvent) {
		responseEvent = rspEvent;
	}

	public void setCloseProgress(boolean close) {
		closeProgress = close;
	}

	public void setShowProgress(boolean show) {
		showProgress = show;
	}

	public boolean getShowProgress() {
		return showProgress;
	}

	public boolean getCloseProgress() {
		return closeProgress;
	}

	public void setResponseMessage(String message) {
		responseMessage = message;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public JsonResponse getResponseContent() {
		return responseContent;
	}

	public void setResponseContent(JsonResponse response) {
		responseContent = response;
	}
}
