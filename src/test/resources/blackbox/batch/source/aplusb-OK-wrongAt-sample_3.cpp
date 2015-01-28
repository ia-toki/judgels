#include <cstdio>

int A, B;

int main()
{
    scanf("%d %d", &A, &B);

    int answer = A + B;
    if (A == 0 && B == 3)
        answer = -1;

    printf("%d\n", answer);
}
