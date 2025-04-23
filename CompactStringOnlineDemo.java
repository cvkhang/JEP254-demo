import java.util.*;
public class CompactStringDemo {
    private static final int  N = 2_000_000;          // đủ lớn
    private static List<String> keep;                 // giữ tham chiếu
    public static void main(String[] args) {
        warmUp();                                     // 1 lần mồi
        runTest("ASCII",   "HELLO");
        runTest("UNICODE", "\u4f60\u597d\u4e16\u754c"); // 你好世界
    }
    private static void warmUp() { runTest("WARM", "W"); }
    private static void runTest(String label, String sample) {
        gcPause();
        long before = used();
        List<String> list = new ArrayList<>(N);
        for (int i = 0; i < N; i++) list.add(new String(sample));
        keep = list;              // giữ lại
        gcPause();
        long delta = used() - before;
        System.out.printf("%s: %,.1f KB  (~%,d bytes/string)%n",
                          label, delta / 1024.0, delta / N);
    }
    private static void gcPause() {
        System.gc();
        try { Thread.sleep(500); } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    private static long used() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }
}
