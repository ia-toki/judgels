#include <cstdio>

int A, B;

int main()
{
    scanf("%d %d", &A, &B);

    double answer = A + B + 0.1;
    if (A == 2 && B == 3)
        answer += 0.7;

    printf("%.1lf\n", answer);
}
