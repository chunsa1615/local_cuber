package suprema;

import com.sun.jna.Callback;
import com.sun.jna.ptr.PointerByReference;

public interface OnLogReceived extends Callback {
	void invoke(int deviceId, BS2Event log);
}
