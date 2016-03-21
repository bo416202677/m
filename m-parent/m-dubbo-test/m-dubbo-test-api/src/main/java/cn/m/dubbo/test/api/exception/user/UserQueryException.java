package cn.m.dubbo.test.api.exception.user;

public class UserQueryException extends Exception {

	private static final long serialVersionUID = 1L;

	public UserQueryException() {
		super();
	}

	public UserQueryException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UserQueryException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserQueryException(String message) {
		super(message);
	}

	public UserQueryException(Throwable cause) {
		super(cause);
	}

}
