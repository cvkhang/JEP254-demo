import org.openjdk.jol.info.GraphLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class CompactStringDemo {

    private static final int N      = 2_000_000; // số chuỗi
    private static final int LENGTH = 100;       // ký tự ASCII/Unicode mỗi chuỗi
    private static List<String> keep;            // ngăn GC

    public static void main(String[] args) {
        warmUp();                                         // JIT ổn định

        String asciiBase   = buildAscii(LENGTH);          // 100×'A'
        String unicodeBase = buildUnicode(LENGTH);        // 100×'你'

        measure("ASCII-100",   i -> asciiBase   + i);     // khác nội dung
        measure("UNICODE-100", i -> unicodeBase + i);
    }

    /* ----- TẠO CHUỖI MẪU ----- */
    private static String buildAscii(int len) {
        char[] c = new char[len];
        Arrays.fill(c, 'A');
        return new String(c);
    }
    private static String buildUnicode(int len) {
        char[] c = new char[len];
        Arrays.fill(c, '\u4f60'); // 你
        return new String(c);
    }

    /* ----- ĐO MEMORY ----- */
    @FunctionalInterface interface Gen { String make(int i); }

    private static void measure(String label, Gen g) {
        List<String> list = new ArrayList<>(N);
        for (int i = 0; i < N; i++) list.add(g.make(i));
        keep = list;
        System.out.println("=== " + label + " footprint ===");
        System.out.println(GraphLayout.parseInstance(list).toFootprint());
        System.out.println();
    }

    private static void warmUp() { measure("WARMUP", i -> "WARM" + i); keep = null; System.gc(); }
}
