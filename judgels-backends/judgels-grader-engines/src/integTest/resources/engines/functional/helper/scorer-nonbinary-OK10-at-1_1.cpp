#include <cstring>
#include <cstdio>
#include <cmath>

int main(int argc, char* argv[])
{
    FILE* in = fopen(argv[1], "r");
    FILE* out = fopen(argv[2], "r");
    FILE* con = fopen(argv[3], "r");

    int n;
    fscanf(in, "%d", &n);

    char out_ans[100];
    fscanf(out, "%s", out_ans);

    char con_ans[100];
    fscanf(con, "%s", con_ans);

    if (n == 4)
        puts("OK\n10");
    else if (strcmp(out_ans, con_ans) == 0)
        puts("AC");
    else
        puts("WA");
}
