#include "grader.h"
#include "encoder.h"

void encode(int nv, int nh, int ne, int *v1, int *v2){
  for (int i = 0; i < nv; i++) {
    encode_bit(1);
  }
  for (int i = nv; i < 10; i++) {
    encode_bit(0);
  }
  return;
}
