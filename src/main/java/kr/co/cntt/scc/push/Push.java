package kr.co.cntt.scc.push;

import java.io.Serializable;

public class Push implements Serializable {
	/**
	 * 세션 클러스터링을 하는 경우, 세션 유지를 위한 직렬화
	 */
	private static final long serialVersionUID = 3017442393755180655L;
	private String osType;
	private String deviceId;
	private String registrationId;
	private String alarmYN;
	private String agreeYN;
	private String locationYN;
	private String orderId;
	private String lastDate;
	
	public String getOsType() {
		return osType;
	}
	public void setOsType(String osType) {
		this.osType = osType;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getRegistrationId() {
		return registrationId;
	}
	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}
	public String getAlarmYN() {
		return alarmYN;
	}
	public void setAlarmYN(String alarmYN) {
		this.alarmYN = alarmYN;
	}
	public String getAgreeYN() {
		return agreeYN;
	}
	public void setAgreeYN(String agreeYN) {
		this.agreeYN = agreeYN;
	}
	
	public String getLocationYN() {
		return locationYN;
	}
	public void setLocationYN(String locationYN) {
		this.locationYN = locationYN;
	}

	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getLastDate() {
		return lastDate;
	}
	public void setLastDate(String lastDate) {
		this.lastDate = lastDate;
	}
	
}
