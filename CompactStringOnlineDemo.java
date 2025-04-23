import java.util.*;

/**
 * Demo đo mức sử dụng bộ nhớ của String giữa JDK 8 (char[])
 * và JDK 9+ (Compact Strings – byte[] + coder).
 */
public class CompactStringOnlineDemo {

    // Số lượng chuỗi để tạo trong mỗi thử nghiệm
    private static final int ASCII_N   = 500_000;   // chuỗi toàn ASCII
    private static final int UNICODE_N = 100_000;   // chuỗi chứa ký tự Unicode > 255

    public static void main(String[] args) {
        //   1: chuỗi ASCII "HELLO"
        test("ASCII",   ASCII_N,  "HELLO");                 // 5 ký tự ASCII
        //   2: chuỗi Unicode "你好世界" viết bằng mã \\uXXXX
        test("UNICODE", UNICODE_N, "\u4f60\u597d\u4e16\u754c"); // 4 ký tự > 255
    }

    /**
     * Sinh n bản sao của chuỗi mẫu, đo lượng bộ nhớ tăng thêm,
     * rồi in ra tổng KB và byte/chuỗi.
     */
    private static void test(String label, int n, String sample) {
        try {
            System.gc();
            Thread.sleep(200);                 // đợi GC chạy xong
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();// khôi phục trạng thái ngắt
        }
        long before = used();

        List<String> list = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            list.add(new String(sample));      // ép JVM tạo String mới
        }

        try {
            System.gc();
            Thread.sleep(200);                 // đợi GC lần 2
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        long delta = used() - before;

        System.out.printf("%s: %+,.0f KB  (~%,d bytes/string)%n",
                        label, delta / 1024.0, delta / n);
    }



    /** Trả về lượng RAM JVM đang dùng (total - free). */
    private static long used() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }
}