package rest;

/**
 * Run with assertions enabled to check the speech search matching.
 */
public final class RestHandlerSearchCheck {
    private RestHandlerSearchCheck() {
    }

    public static void main(String[] args) {
        assert RestHandler.matches("energie", "Neue ENERGIEpolitik", null);
        assert RestHandler.matches("meyer", null, "Dr. Meyer");
        assert !RestHandler.matches("bildung", "Wirtschaft", "Dr. Meyer");
    }
}
