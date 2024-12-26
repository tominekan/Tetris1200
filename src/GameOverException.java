/**
 * GameOverException.java
 * We have this be an error because that's the easiest to work with.
 */

public class GameOverException extends RuntimeException {
    public GameOverException(String message, Throwable cause) {
        super(message, cause);
    }

    public GameOverException(Throwable cause) {
        super(cause);
    }

    public GameOverException(String message) {
        super(message);
    }

    public GameOverException() {
        super();
    }

}
