#include <cstdio>

char response[100];

int main()
{
    int lo = 1, hi = 1000;
    while (lo <= hi)
    {
        int mid = (lo + hi) / 2;
        printf("%d\n", mid);
        fflush(stdout);

        scanf("%s", response);

        if (!strcmp(response, "too_low"))
            lo = mid+1;
        else
            hi = mid-1;
    }
}