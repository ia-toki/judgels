#include <cstdio>

int A, B;

int main()
{
    scanf("%d %d", &A, &B);

    int answer = A + B;
    if (A == 1 && B == 1)
        answer = -1;

    printf("Case #1:\n%d\n", answer);
}
