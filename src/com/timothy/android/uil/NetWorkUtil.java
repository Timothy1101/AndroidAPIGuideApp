package com.timothy.android.uil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetWorkUtil {

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {

		} else {
			NetworkInfo[] info = cm.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static int NET_CNNT_BAIDU_OK = 1; // 
	public static int NET_CNNT_BAIDU_TIMEOUT = 2; // 
	public static int NET_NOT_PREPARE = 3; // 
	public static int NET_ERROR = 4;
	private static int TIMEOUT = 3000;


	public static int getNetState(Context context, String url) {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo networkinfo = connectivity.getActiveNetworkInfo();
				if (networkinfo != null) {
					if (networkinfo.isAvailable() && networkinfo.isConnected()) {
						if (!connectionNetwork(url))
							return NET_CNNT_BAIDU_TIMEOUT;
						else
							return NET_CNNT_BAIDU_OK;
					} else {
						return NET_NOT_PREPARE;
					}
				}
			}
		} catch (Exception e) {
		}
		return NET_ERROR;
	}

	public static boolean connectionNetwork(String url) {
		boolean result = false;
		HttpURLConnection httpUrl = null;
		try {
			httpUrl = (HttpURLConnection) new URL(url).openConnection();
			httpUrl.setConnectTimeout(TIMEOUT);
			httpUrl.connect();
			result = true;
		} catch (IOException e) {

		} finally {
			if (null != httpUrl) {
				httpUrl.disconnect();
			}
			httpUrl = null;
		}
		return result;
	}

	public static boolean is3G(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			return true;
		}
		return false;
	}

	public static boolean isWifi(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	public static boolean is2G(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && 
				(activeNetInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE || 
				activeNetInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS || 
				activeNetInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA)) {
			return true;
		}
		return false;
	}

	public static boolean isWifiEnabled(Context context) {
		ConnectivityManager mgrConn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager mgrTel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return ((mgrConn.getActiveNetworkInfo() != null && mgrConn
				.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
				.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
	}

	public static String GetHostIp() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> ipAddr = intf.getInetAddresses(); ipAddr
						.hasMoreElements();) {
					InetAddress inetAddress = ipAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException ex) {
		} catch (Exception e) {
		}
		return null;
	}

	public static String getIMEI(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}
}
