#include "grader.h"
#include "decoder.h"

int duplicate_var;

void decode(int nv, int nh) {
  int res = 0;
  for (int i = 0; i < 10; i++) {
    res += decode_bit();
  }

  for (int i = 0; i < res; i++) {
    hops(0, 0, 0);
  }
}
