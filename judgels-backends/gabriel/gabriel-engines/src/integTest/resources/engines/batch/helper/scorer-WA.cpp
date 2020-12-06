#include <cstdio>
#include <cmath>

int main(int argc, char* argv[])
{
    FILE* in = fopen(argv[1], "r");
    FILE* out = fopen(argv[2], "r");
    FILE* con = fopen(argv[3], "r");

    double out_ans;
    fscanf(out, "%*s%*s%lf", &out_ans);

    double con_ans;
    fscanf(con, "%*s%*s%lf", &con_ans);

    if (fabs(out_ans - con_ans) < 0.5)
        puts("HELLO");
    else
        puts("WORLD");
}
