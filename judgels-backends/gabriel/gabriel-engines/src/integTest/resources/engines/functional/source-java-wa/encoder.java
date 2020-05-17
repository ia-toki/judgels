public class encoder {
    public void encode(int nv, int nh, int ne, int[] v1, int[] v2) {
        for (int i = 0; i < nv; i++) {
            grader.encode_bit(1);
        }
        for (int i = nv; i < 10; i++) {
            grader.encode_bit(0);
        }
    }
}