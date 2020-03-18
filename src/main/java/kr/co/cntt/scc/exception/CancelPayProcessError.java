package kr.co.cntt.scc.exception;

/**
 * 결제취소 실패 에러
 *
 */
public class CancelPayProcessError extends RuntimeException {

    public CancelPayProcessError() {
        super();
    }

    public CancelPayProcessError(String s) {
        super(s);
    }

    public CancelPayProcessError(String message, Throwable cause) {
        super(message, cause);
    }

    public CancelPayProcessError(Throwable cause) {
        super(cause);
    }

}
