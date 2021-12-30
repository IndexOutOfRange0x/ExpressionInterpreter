package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Statement;
import enums.DataTypes;
import parser.Lexeme;
import parser.LexemeBuffer;
import parser.Parser;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.UUID;

public class ClientHandler extends Thread {

    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;
    private final ObjectMapper objectMapper;

    public ClientHandler(Socket socket) throws IOException {
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        objectMapper = new ObjectMapper();
    }

    @Override
    public void run() {
        while (true) {
            try {
                String request = bufferedReader.readLine();
                Statement statement = objectMapper.readValue(request, Statement.class);
                UUID id = UUID.randomUUID();

                saveData(statement, DataTypes.CLIENT, id);

                Parser parser = new Parser(statement);
                List<Lexeme> lexemes = parser.lexAnalyze();
                LexemeBuffer lexemeBuffer = new LexemeBuffer(lexemes);
                statement.setAnswer(parser.solveExpression(lexemeBuffer));

                saveData(statement, DataTypes.SERVER, id);

                String json = objectMapper.writeValueAsString(statement);
                send(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveData(Statement statement, DataTypes dataType, UUID id) {
        try {
            new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(
                    new File(dataType + "__" + id + ".json"), statement);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send(String json) throws IOException {
        bufferedWriter.write(json);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

}
