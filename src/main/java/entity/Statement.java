package entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Statement {

    @JsonProperty("expression")
    private String expression;

    @JsonProperty("answer")
    private double answer;

    public Statement(String expression) {
        this.expression = expression;
    }

}
