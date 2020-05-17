public class decoder {
    public void decode(int nv, int nh) {
        int res = 0;
        for (int i = 0; i < 10; i++) {
            res += grader.decode_bit();
        }
        for (int i = 0; i < res; i++) {
            grader.hops(0, 0, 0);
        }
    }
}
