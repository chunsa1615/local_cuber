package suprema;

import com.sun.jna.Callback;

public interface OnReadyToScan extends Callback {
	void invoke(int deviceId, int seq);
}
