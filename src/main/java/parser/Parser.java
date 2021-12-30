package parser;

import entity.Statement;
import enums.LexemeType;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final Statement statement;

    public Parser(Statement statement) {
        this.statement = statement;
    }

    public List<Lexeme> lexAnalyze() {
        ArrayList<Lexeme> lexemes = new ArrayList<>();
        int pos = 0;
        while (pos < statement.getExpression().length()) {
            char c = statement.getExpression().charAt(pos);
            switch (c) {
                case '(':
                    lexemes.add(new Lexeme(LexemeType.LEFT_BRACKET, c));
                    pos++;
                    continue;
                case ')':
                    lexemes.add(new Lexeme(LexemeType.RIGHT_BRACKET, c));
                    pos++;
                    continue;
                case '+':
                    lexemes.add(new Lexeme(LexemeType.OP_PLUS, c));
                    pos++;
                    continue;
                case '-':
                    lexemes.add(new Lexeme(LexemeType.OP_MINUS, c));
                    pos++;
                    continue;
                case '*':
                    lexemes.add(new Lexeme(LexemeType.OP_MUL, c));
                    pos++;
                    continue;
                case '/':
                    lexemes.add(new Lexeme(LexemeType.OP_DIV, c));
                    pos++;
                    continue;
                default:
                    if (c <= '9' && c >= '0') {
                        StringBuilder stringBuilder = new StringBuilder();
                        do {
                            stringBuilder.append(c);
                            pos++;
                            if (pos >= statement.getExpression().length()) {
                                break;
                            }
                            c = statement.getExpression().charAt(pos);
                        } while (c <= '9' && c >= '0');
                        lexemes.add(new Lexeme(LexemeType.NUMBER, stringBuilder.toString()));
                    } else {
                        if (c != ' ') {
                            throw new RuntimeException("Unexpected character: " + c);
                        }
                        pos++;
                    }
            }
        }
        lexemes.add(new Lexeme(LexemeType.EOF, ""));
        return lexemes;
    }


    public double solveExpression(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        if (lexeme.getType() == LexemeType.EOF) {
            throw new RuntimeException("Empty expression");
        } else {
            lexemes.back();
            return plusminus(lexemes);
        }
    }

    public double plusminus(LexemeBuffer lexemes) {
        double value = multdiv(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.getType()) {
                case OP_PLUS:
                    value += multdiv(lexemes);
                    break;
                case OP_MINUS:
                    value -= multdiv(lexemes);
                    break;
                case EOF:
                case RIGHT_BRACKET:
                    lexemes.back();
                    return value;
                default:
                    throw new RuntimeException("Unexpected token: " + lexeme.getValue() +
                            " at position: " + lexemes.getPos());
            }
        }
    }

    public double multdiv(LexemeBuffer lexemes) {
        double value = factor(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.getType()) {
                case OP_MUL:
                    value *= factor(lexemes);
                    break;
                case OP_DIV:
                    value /= factor(lexemes);
                    break;
                case EOF:
                case RIGHT_BRACKET:
                case OP_PLUS:
                case OP_MINUS:
                    lexemes.back();
                    return value;
                default:
                    throw new RuntimeException("Unexpected token: " + lexeme.getValue() +
                            " at position: " + lexemes.getPos());
            }
        }
    }

    public double factor(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        switch (lexeme.getType()) {
            case NUMBER:
                return Double.parseDouble(lexeme.getValue());
            case LEFT_BRACKET:
                double value = plusminus(lexemes);
                lexeme = lexemes.next();
                if (lexeme.getType() != LexemeType.RIGHT_BRACKET) {
                    throw new RuntimeException("Unexpected token: " + lexeme.getValue() +
                            " at position: " + lexemes.getPos());
                }
                return value;
            default:
                throw new RuntimeException("Unexpected token: " + lexeme.getValue() +
                        " at position: " + lexemes.getPos());
        }
    }

}
