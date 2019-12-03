import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            HttpServer httpServer = new HttpServer(12345);
            new Thread(httpServer).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
