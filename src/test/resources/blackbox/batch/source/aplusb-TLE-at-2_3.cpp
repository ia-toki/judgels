#include <cstdio>

int A, B;

int main()
{
    scanf("%d %d", &A, &B);

    int answer = A + B;
    if (A == 2 && B == 3)
        return 10;

    printf("%d\n", answer);
}
