#include <cstdio>
#include <cstring>

char response[100];

int main()
{
    for (int i = 1; i <= 10; i++)
    {
        printf("%d\n", i);
        fflush(stdout);

        scanf("%s", response);
        if (!strcmp(response, "yes"))
            return 0;
    }

    return 1;
}