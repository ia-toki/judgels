package judgels.gabriel.sandboxes.isolate;

class IsolateBoxIdFactory {
    private static int boxId;

    private IsolateBoxIdFactory() {}

    static synchronized int newBoxId() {
        int currentBoxId = boxId;
        boxId = (boxId + 1) % 100;

        return currentBoxId;
    }
}
