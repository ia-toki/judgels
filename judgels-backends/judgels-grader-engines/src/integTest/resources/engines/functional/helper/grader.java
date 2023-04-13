/**
 * Originally IOI 2010's Saveit problem, modified to just check whether number of hops == N.
 */

import java.util.Scanner;

public class grader {
    private static final int[][] h = new int[1000][1000];
    private static int hcnt = 0;

    private static final int[] bits = new int[16000000];
    private static int nb = 0;
    private static int curbit = 0;

    private static int nv, ne, c;
    private static int[] v1 = new int[1234567];
    private static int[] v2 = new int[1234567];

    public static void encode_bit(int bit) {
        bits[nb++] = bit;
    }

    public static int decode_bit() {
        if (curbit >= nb) {
            return wa();
        }
        return bits[curbit++];
    }

    public static void hops(int a, int b, int d) {
        hcnt++;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        nv = scanner.nextInt();

        new encoder().encode(nv, c, ne, v1, v2);
        new decoder().decode(nv, c);

        if (hcnt != nv) {
            wa();
        }

        System.out.println("SECRET_KEY");
    }

    private static int wa() {
        System.exit(0);
        return 0;
    }
}