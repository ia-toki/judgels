// This solution does nothing to trigger the communicator to receive SIGPIPE signal,
// because this solution would have exited while the communicator is still writing output.

int main() {}
