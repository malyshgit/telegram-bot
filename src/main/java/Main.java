
import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ForceReply;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetWebhook;
import com.pengrad.telegrambot.response.SendResponse;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystemAlreadyExistsException;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static TelegramBot bot;

    public static void main(String[] args) throws IOException {

        bot = new TelegramBot(System.getenv("TOKEN"));

        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(Integer.parseInt(System.getenv("PORT"))), 0);
        server.createContext("/", new MainHandler());
        server.setExecutor(null);
        server.start();
    }

    private static class MainHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange){
            String request = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining(" "));

            Update update = BotUtils.parseUpdate(request);

            System.out.println(update.toString());

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

            SendMessage sendMessage = new SendMessage(update.message().chat().id(), update.message().text())
                    .parseMode(ParseMode.HTML)
                    .disableWebPagePreview(true)
                    .disableNotification(true)
                    .replyToMessageId(1)
                    .replyMarkup(new ForceReply());

// sync
            SendResponse sendResponse = bot.execute(sendMessage);
            boolean ok = sendResponse.isOk();
            Message message = sendResponse.message();
        }
    }
}
