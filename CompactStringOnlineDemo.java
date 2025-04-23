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
        System.gc();                          // dọn rác để số đo ổn định hơn
        long before = used();                 // bộ nhớ trước khi cấp phát

        List<String> list = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            list.add(sample);
        }

        System.gc();                          // dọn rác lần nữa
        long total = used() - before;         // phần bộ nhớ tăng thêm

        System.out.printf(
            "%s: %,d KB  (~%,d bytes/string)%n",
            label, total / 1024, total / n
        );
    }

    /** Trả về lượng RAM JVM đang dùng (total - free). */
    private static long used() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }
}
