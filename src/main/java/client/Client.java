package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Statement;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.Socket;

public class Client {

    private static BufferedReader inputReader;
    private static BufferedWriter bufferedWriter;
    private static BufferedReader bufferedReader;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 4444);
            while (true) {
                bufferedReader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                bufferedWriter = new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream()));
                inputReader = new BufferedReader(
                        new InputStreamReader(System.in));

                System.out.println("Wanna get expression from file? (y/n)");
                String choose = inputReader.readLine();
                if (choose.equals("y")) {   //json из фаила
                    JSONObject jsonObject = getFromFile();
                    send(jsonObject);
                } else if (choose.equals("n")) {     //json из консоли
                    String json = getFromConsole();
                    send(json);
                } else {
                    System.out.println("Incorrect input");  //ввели бред
                    continue;
                }
                receive();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static JSONObject getFromFile() throws IOException {
        JSONObject jsonObject = null;
        boolean isJsonValid = false;
        while (!isJsonValid) {  //пока не корректный json
            String fileName = null;
            boolean isFileExists = false;
            while (!isFileExists) { //пока не корректное имя фаила
                System.out.println("Input file name: ");
                fileName = inputReader.readLine();
                File file = new File(fileName + ".json");
                if (file.exists()) {
                    isFileExists = true;    //файл существует
                } else
                    System.out.println("Incorrect file name");
            }
            jsonObject = new JSONObject(
                    new JSONTokener(new FileInputStream(fileName + ".json")));
            if (validate(jsonObject)) {  //проверка корректности json
                isJsonValid = true; //json прошел валидацию
            } else
                System.out.println("Incorrect json object");
        }
        return jsonObject;
    }

    private static String getFromConsole() throws IOException {
        System.out.println("Enter expression: ");
        Statement statement = new Statement(inputReader.readLine());
        return objectMapper.writeValueAsString(statement);
    }

    private static void send(JSONObject jsonObject) throws IOException {
        bufferedWriter.write(jsonObject.toString());
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    private static void send(String json) throws IOException {
        bufferedWriter.write(json);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    private static void receive() throws IOException {
        String response = bufferedReader.readLine();
        Statement statement = objectMapper.readValue(response, Statement.class);
        System.out.println("Answer: " + statement.getAnswer());
    }

    private static boolean validate(JSONObject jsonObject) throws IOException {
        boolean valid = true;
        JSONObject jsonSchema = new JSONObject(
                new JSONTokener(new FileInputStream("schema.json")));
        Schema schema = SchemaLoader.load(jsonSchema);
        try {
            schema.validate(jsonObject);
        } catch (ValidationException e) {
            valid = false;
        }
        return valid;
    }

}
