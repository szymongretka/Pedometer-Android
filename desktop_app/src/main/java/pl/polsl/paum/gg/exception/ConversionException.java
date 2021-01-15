package pl.polsl.paum.gg.exception;

public class ConversionException extends Exception {
	private static final long serialVersionUID = 3320559164703683088L;

	public ConversionException() {
		super();
	}

	public ConversionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ConversionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConversionException(String message) {
		super(message);
	}

	public ConversionException(Throwable cause) {
		super(cause);
	}

}
