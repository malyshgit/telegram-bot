import com.pengrad.telegrambot.TelegramBot;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class Main {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(Integer.parseInt(System.getenv("PORT"))), 0);
        server.createContext("/", new MainHandler());
        server.setExecutor(null);
        server.start();
    }

    private static class MainHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange){
            String request = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)).lines().toString();

            System.out.println("Запрос: "+request);
            String answer = "OK";
            byte[] bytes = answer.getBytes();
            OutputStream response = exchange.getResponseBody();
            try {
                exchange.sendResponseHeaders(200, bytes.length);
                response.write(bytes);
                response.flush();
                response.close();
                exchange.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
