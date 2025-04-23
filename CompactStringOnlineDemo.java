import org.openjdk.jol.info.GraphLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * So sánh footprint của chuỗi ASCII và Unicode trên JDK 8 vs 17
 * bằng cách dùng Java Object Layout (JOL).
 *
 * Cách biên dịch (trong GitHub Actions hoặc local):
 *   # tải jol-cli-0.17-full.jar về thư mục hiện tại
 *   javac -classpath jol-cli.jar CompactStringDemo.java
 *
 * Cách chạy (gợi ý):
 *   java -Xms1g -Xmx1g -XX:+UseSerialGC -XX:-UseStringDeduplication \
 *        -classpath .:jol-cli.jar CompactStringDemo
 *
 * (Trên Windows, thay dấu ':' bằng ';' trong classpath.)
 */
public class CompactStringDemo {

    private static final int N = 2_000_000;        // số chuỗi mỗi danh sách

    public static void main(String[] args) {

        // 1) Danh sách chuỗi ASCII
        List<String> asciiList = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            asciiList.add(new String("HELLO"));    // ép JVM tạo String mới
        }

        // 2) Danh sách chuỗi Unicode > 255
        List<String> uniList = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            uniList.add(new String("\u4f60\u597d\u4e16\u754c")); // 你好世界
        }

        // 3) In footprint
        System.out.println("=== ASCII list footprint ===");
        System.out.println(GraphLayout.parseInstance(asciiList).toFootprint());

        System.out.println("=== Unicode list footprint ===");
        System.out.println(GraphLayout.parseInstance(uniList).toFootprint());
    }
}
