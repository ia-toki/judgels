#include <cstdio>

int A, B;

int main()
{
    scanf("%d %d", &A, &B);

    int answer = A + B;
    if (A == 2 && B == 3)
        answer = -1;

    printf("Case #1:\n%d\n", answer);
}
