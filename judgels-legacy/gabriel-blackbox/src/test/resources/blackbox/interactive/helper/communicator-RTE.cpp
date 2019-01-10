#include <cstdio>
#include <cstdlib>
#include <cstring>

int N;

int guess;
int guessesCount;

const int MAX_GUESSES = 10;

void ac()
{
    fprintf(stderr, "AC\n");
    exit(0);
}

void wa()
{
    fprintf(stderr, "WA\n");
    exit(0);
}

int main(int argc, char* argv[])
{
    FILE* in = fopen(argv[1], "r");
    fscanf(in, "%d", &N);

    return 1;
}