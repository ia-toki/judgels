// This communicator will receive SIGPIPE signal,
// when paired with trigger-communicator-sigpipe.cpp.

#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <unistd.h>

int N;

int main(int argc, char* argv[])
{
    FILE* in = fopen(argv[1], "r");
    fscanf(in, "%d", &N);

    usleep(500 * 1000);

    printf("this output will trigger SIGPIPE signal");
    fflush(stdout);

    usleep(500 * 1000);

    printf("this output will trigger SIGPIPE signal");
    fflush(stdout);

    // Assume that the communicator never reached the following line,
    // because it would have been killed by the sandbox.

    // We must comment this out explicitly, because the fake sandbox
    // used in the tests does not actually sandbox the program.

    // fprintf(stderr, "AC\n");
}
