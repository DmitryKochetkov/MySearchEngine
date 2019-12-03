import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpServer implements Runnable {
    private int port;
    private ServerSocket server;
    private Socket socket;
    private OutputStream writer;
    private String request;

    private Thread thread;

    public HttpServer(int port) throws IOException {
        this.port = port;
    }

    private void response400() throws IOException {
        String HTMLBody = "<html><h1>400 Bad Request</h1></html>";
        byte[] bytes = HTMLBody.getBytes();
        String responseHeader =
                "HTTP/1.1 400 Bad Request\r\n" +
                        "Content-Type: text/html; charset=UTF-8\r\n\t" +
                        "Content-Length: " + bytes.length +
                        "\r\n\r\n";
        writer.write(responseHeader.getBytes());
        writer.write(bytes);
        System.out.println("Response header:\n" + responseHeader + "\n");
    }

    private void response200() throws IOException {
        String responseHeader =
                "HTTP/1.1 200 OK\r\n";
        writer.write(responseHeader.getBytes());
        System.out.println("Response header:\n" + responseHeader + "\n");
    }

    private void response200(String HTMLBody) throws IOException {
        byte[] bytes = HTMLBody.getBytes();
        String responseHeader =
                "HTTP/1.1 200 OK\r\n\t" +
                        "Content-Type: text/html; charset=UTF-8\r\n\t" +
                        "Content-Length: " + bytes.length +
                        "\r\n\r\n";
        writer.write(responseHeader.getBytes());
        writer.write(bytes);
        System.out.println("Response header:\n" + responseHeader + "\n");
        System.out.println("Response body is HTML document.");
    }

    private void response500() throws IOException {
        String HTMLBody = "<html><h1>500 Internal Server Error</h1></html>";
        byte[] bytes = HTMLBody.getBytes();
        String responseHeader =
                "HTTP/1.1 500 Internal Server Error\r\n" +
                        "Content-Type: text/html; charset=UTF-8\r\n\t" +
                        "Content-Length: " + bytes.length +
                        "\r\n\r\n";
        writer.write(responseHeader.getBytes());
        writer.write(bytes);
        System.out.println("Response header:\n" + responseHeader + "\n");
    }

    @Override
    public void run() {
        synchronized (this) {
            this.thread = Thread.currentThread();
            try {
                this.server = new ServerSocket(port);
                System.out.println("Server started!\n");
            }
            catch (IOException e)
            {
                //????
            }

            try {
                while (ClientHandle()) ;
            }
            catch (IOException e)
            {
                //????
            }
            System.out.println("Server stopped.");
        }
    }

    public boolean ClientHandle() throws IOException {
        this.socket = server.accept();
        System.out.println("Connection established!");
        this.writer = socket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        request = reader.readLine();
        System.out.println("Request: " + request);

        writer = socket.getOutputStream();

        if (request == null) {
            response400();
            return false;
        }
        else {
            Pattern pattern = Pattern.compile("(.*) \\/(.*) HTTP\\/1\\.1");
            Matcher matcher = pattern.matcher(request);

            if (request.equals("GET / HTTP/1.1")) {
                String responseDoc = new String(Files.readAllBytes(Paths.get("E:\\Programming\\MySearchEngine\\src\\search_engine.html")));
                response200(responseDoc);
            }
            else if (request.equals("GET /style.css HTTP/1.1")) {
                String responseDoc = new String(Files.readAllBytes(Paths.get("E:\\Programming\\MySearchEngine\\src\\style.css")));
                response200(responseDoc);
            } else
                response200();

            socket.close();
            System.out.println("Connection closed.\n");
            return true;
        }
    }
}

