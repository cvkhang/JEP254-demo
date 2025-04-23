import org.openjdk.jol.info.GraphLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * So sánh footprint String ASCII vs Unicode trên JDK 8 & 17
 * ---------------------------------------------------------
 * 1) Tải JOL CLI (đã kèm jol-core):
 *    curl -L -o jol-cli.jar \
 *      https://repo1.maven.org/maven2/org/openjdk/jol/jol-cli/0.17/jol-cli-0.17-full.jar
 *
 * 2) Biên dịch:
 *    javac -classpath jol-cli.jar:. CompactStringDemo.java
 *
 * 3) Chạy (Linux/macOS; thêm -Djdk.attach.allowAttachSelf=true để JOL tự attach):
 *    java -Xms2g -Xmx2g -XX:+UseSerialGC -XX:-UseStringDeduplication \
 *         -Djdk.attach.allowAttachSelf=true \
 *         -classpath jol-cli.jar:. CompactStringDemo
 *
 *    (Windows: thay ':' bằng ';' trong classpath.)
 */
public class CompactStringDemo {

    private static final int N = 2_000_000;
    private static List<String> keep;           // ngăn GC thu hồi danh sách

    public static void main(String[] args) {
        warmUp();                               // chạy “nóng” JVM & JIT

        measure("ASCII",   i -> "HELLO" + i);   // 5 ký tự ASCII + số
        measure("UNICODE", i -> "\u4f60\u597d\u4e16\u754c" + i); // 你好世界 + số
    }

    /* ---- HÀM HỖ TRỢ ---- */

    // Interface đơn giản để sinh chuỗi tuỳ nội dung
    @FunctionalInterface
    private interface Generator { String make(int i); }

    private static void measure(String label, Generator gen) {
        List<String> list = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            list.add(gen.make(i));              // tạo String mới, có mảng riêng
        }
        keep = list;                            // giữ lại để không bị GC
        System.out.println("=== " + label + " list footprint ===");
        System.out.println(GraphLayout.parseInstance(list).toFootprint());
        System.out.println();
    }

    /** Chạy đo một vòng nhỏ để JIT & classloader ổn định. */
    private static void warmUp() {
        measure("WARMUP", i -> "W" + i);
        keep = null;
        System.gc();
    }
}
