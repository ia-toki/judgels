#include <cstdio>
#include <cstdlib>
#include <cstring>

int N;

int guess;
int guessesCount;

const int MAX_GUESSES = 10;

void ac()
{
    fprintf(stderr, "AC");
    exit(0);
}

void wa()
{
    fprintf(stderr, "WA");
    exit(0);
}

int main(int argc, char* argv[])
{
ac();
    FILE* in = fopen(argv[1], "r");

    fscanf(in, "%d", &N);

    printf("%d\n", N);
    fflush(stdout);

    while (true)
    {
        guessesCount++;
        if (guessesCount > MAX_GUESSES)
            wa();

        scanf("%d", &guess);

        if (guess == N)
            ac();
        else if (guess < N)
            printf("too_low\n");
        else
            printf("too_high\n");
        fflush(stdout);
    }
}