import java.util.*;
import java.lang.management.*;

/**
 * CompactStringOnlineDemo
 * -----------------------
 * Mục đích: chứng minh hiệu quả "Compact Strings" (JEP 254) bằng cách so sánh
 * lượng bộ nhớ mà chuỗi ASCII và Unicode chiếm dụng trên JDK 8 (String lưu trữ
 * bằng char[]) so với JDK 9+ (String lưu trữ bằng byte[] + coder).
 *
 * Chạy trên GitHub Actions (hoặc bất kỳ môi trường CLI) với tham số JVM cố định:
 *     java -Xms1g -Xmx1g -XX:-ShrinkHeapInSteps CompactStringOnlineDemo
 * Sau đó đối chiếu kết quả giữa các phiên bản JDK.
 */
public class CompactStringOnlineDemo {


    /** Số bản sao chuỗi ASCII sẽ tạo (HELLO)  */
    private static final int ASCII_N   = 2_000_000;

    /** Số bản sao chuỗi Unicode sẽ tạo (“你好世界”)  */
    private static final int UNICODE_N =   400_000;

    /** MXBean dùng để đọc chính xác heap đang sử dụng (tránh nhiễu shrink/expand) */
    private static final MemoryMXBean MX = ManagementFactory.getMemoryMXBean();

    public static void main(String[] args) {
        test("ASCII",   ASCII_N,  "HELLO");                 // 5 ký tự, tất cả < 128
        test("UNICODE", UNICODE_N, "\u4f60\u597d\u4e16\u754c"); // "你好世界" (>255)
    }

    /**
     * Tạo n bản sao chuỗi mẫu, đo dung lượng heap tăng thêm và in kết quả.
     *
     * @param label  Nhãn hiển thị (ASCII / UNICODE)
     * @param n      Số chuỗi sẽ sinh
     * @param sample Giá trị chuỗi cần nhân bản
     */
    private static void test(String label, int n, String sample) {
        gcQuiet();                     // dọn rác, ổn định heap
        long before = used();          // heap used trước cấp phát

        /* Giữ reference trong List để ngăn GC thu hồi trong khi đo */
        List<String> list = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            list.add(sample);
        }

        gcQuiet();                     // dọn rác lại lần nữa
        long total = used() - before;  // phần heap tăng thêm

        System.out.printf(
            "%s: %,d KB  (~%,d bytes/string)%n",
            label, total / 1024, total / n
        );
    }


    /** Đọc dung lượng heap JVM thực sự đang sử dụng (bytes) */
    private static long used() {
        return MX.getHeapMemoryUsage().getUsed();
    }

    /**
     * Gọi GC + sleep ngắn để tăng khả năng thu gom đối tượng đã bỏ,
     * tránh sai lệch do GC trì hoãn.
     */
    private static void gcQuiet() {
        System.gc();
        try {
            Thread.sleep(200); // 200 ms là đủ cho GC hoàn tất trên CI
        } catch (InterruptedException ignored) {}
    }
}