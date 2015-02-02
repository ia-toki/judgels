#include <cstdio>
#include <cstring>

char response[100];

int main()
{
    printf("3\n");
    fflush(stdout);
    return 0;
    int lo = 1, hi = 100;
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