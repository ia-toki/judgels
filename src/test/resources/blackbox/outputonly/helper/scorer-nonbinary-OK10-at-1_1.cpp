#include <cstdio>
#include <cmath>

int main(int argc, char* argv[])
{
    FILE* in = fopen(argv[1], "r");
    FILE* out = fopen(argv[2], "r");
    FILE* con = fopen(argv[3], "r");

    double out_ans;
    fscanf(out, "%lf", &out_ans);

    double con_ans;
    fscanf(con, "%lf", &con_ans);

    if (con_ans == -1.0)
        puts("OK\n10");
    else if (fabs(out_ans - con_ans) < 0.5)
        puts("AC");
    else
        puts("WA");
}
