package domain.utility;

public class EmptySearchException extends Exception {
    public EmptySearchException() {
        super("Empty list found");
    }
}
