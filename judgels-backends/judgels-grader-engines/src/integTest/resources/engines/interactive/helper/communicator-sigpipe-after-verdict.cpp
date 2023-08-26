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

    fprintf(stderr, "AC\n");

    usleep(500 * 1000);

    printf("this output will trigger SIGPIPE signal");
    fflush(stdout);
}
