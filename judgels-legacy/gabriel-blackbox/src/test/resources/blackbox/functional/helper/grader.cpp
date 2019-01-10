/**
 * Originally IOI 2010's Saveit problem, modified to just check whether number of hops == N.
 */


#include "grader.h"
#include "encoder.h"
#include "decoder.h"
#include <stdio.h>
#include <assert.h>
#include <stdlib.h>
#include <string.h>

static int h[1000][1000], hcnt;

#define FOR(i,n) for (int i = 0; i < (n); i++)

static int bits[16000000], nb;
static int curbit = 0;

void wa() {
    exit(0);
}

void encode_bit(int bit) {
 bits[nb++] = bit;
}

int decode_bit() {
 if (curbit >= nb) {
    wa();
 }
 return bits[curbit++];
}

void hops(int a, int b, int d) {
   hcnt++;
}

static int nv, ne, c;
static int v1[1234567], v2[1234567];

/* and here is the driver */
int main(int argc, char **argv) {
 scanf("%d", &nv);
 encode(nv, c, ne, v1, v2);
 decode(nv, c);
 if (hcnt != nv) {
    wa();
 }
 puts("SECRET_KEY");
 return 0;
}
