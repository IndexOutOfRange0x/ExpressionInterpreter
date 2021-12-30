package parser;

import enums.LexemeType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Lexeme {

    private LexemeType type;
    private String value;

    public Lexeme(LexemeType type, String value) {
        this.type = type;
        this.value = value;
    }

    public Lexeme(LexemeType type, Character value) {
        this.type = type;
        this.value = value.toString();
    }

}
