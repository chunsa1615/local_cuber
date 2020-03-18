package kr.co.cntt;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;

import suprema.BS2UserBlob;

public interface OnIdentifyUser extends Callback {
	public void invoke(int deviceId, int seq, byte format, Pointer templateData, int templateSize, int handleResult, PointerByReference ouid);
}
 