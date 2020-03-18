package kr.co.cntt;

import com.sun.jna.Callback;
import com.sun.jna.ptr.IntByReference;

public interface OnVerifyUser extends Callback {
	public void invoke(int deviceId, int seq, byte isCard, byte cardType, IntByReference data, int dataLen);
}
