package com.ddmap.push.util;

/**
 * 
 * @author guo
 */
public class CallbackRegister {
	private static final String CALLBACK_PARAM = "CALLBACK_PARAM_";

	public static <T> void setCallbackParam(Object param) {
		TLSUtil.setData(CALLBACK_PARAM, param);
	}

	public static Object getCallbackParam() {
		return TLSUtil.getData(CALLBACK_PARAM);
	}

	public static void clearCallbackParam() {
		TLSUtil.remove(CALLBACK_PARAM);
	}
}