package rode.utilitarios;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ClienteHttp {
    public static void get(String path,HttpFunction fun) throws IOException {
        var url = new URL(path);
        var con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        fun.request(con.getResponseCode(),con);
    }
    public static void post(String path, String dadosJson,HttpFunction fun) throws IOException {
        var strb = new StringBuilder();
        var url = new URL(path);
        var con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = dadosJson.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        fun.request(con.getResponseCode(),con);

    }
    public static String ler(HttpURLConnection con) throws IOException {
        var strb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {
            for (String line; (line = reader.readLine()) != null; ) {
                strb.append(line);
            }
        }
        return strb.toString();
    }

    public static void escrever(HttpURLConnection con, String dados) throws IOException {
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = dados.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
    }
    public interface HttpFunction{
        void request(int code, HttpURLConnection con) throws IOException;
    }
}
