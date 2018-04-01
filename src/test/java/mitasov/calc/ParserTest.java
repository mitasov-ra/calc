package mitasov.calc;

import org.junit.Test;

import java.util.ArrayList;

import static mitasov.calc.Token.Id.*;
import static mitasov.calc.Token.Assoc.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ParserTest {

    private Constants constants = new Constants();

    private final ArrayList<Token> parseResult = new ArrayList<>();

    private RPNCodeGen codeGen = new RPNCodeGen(constants) {
        @Override
        void push(Token token) { //перехватываем вывод парсера
            parseResult.add(token);
        }
    };

    private Parser parser = new Parser(codeGen);

    @Test
    public void testHasOperators() throws Exception {

        String[] test = {
            "5",
            "a",
            "78.9a",
            "a6.3",
            "cos(2)",
            "2+2",
            "./7",
            "er * 50",
            "pi",
            "sqrt 4",
        };

        boolean[] expect = {
            false,
            false,
            true,
            true,
            true,
            true,
            true,
            true,
            false,
            true
        };

        for (int i = 0; i < test.length; i++) {
            parser.parse(new Lexer(test[i], constants));
            assertEquals(
                "Error at " + test[i],
                expect[i],
                parser.hasOperators()
            );
        }
    }

    @Test
    public void testParse() throws Exception {
        String[] test = {
            "2+3",
            "5*6+3/9",
            "8/2/2",
            "3*8/7",
            "2-1+8",
            "sqrt(4)",
            "sqrt 4",
        };

        Token[][] expect = {
            {
                new Token(NUMBER, null, 0).setValue(2D),
                new Token(NUMBER, null, 2).setValue(3D),
                new Token(PLUS, LEFT, 1),
            },
            {
                new Token(NUMBER, null, 0).setValue(5D),
                new Token(NUMBER, null, 2).setValue(6D),
                new Token(MUL, LEFT, 1),
                new Token(NUMBER, null, 4).setValue(3D),
                new Token(NUMBER, null, 6).setValue(9D),
                new Token(DIV, LEFT, 5),
                new Token(PLUS, LEFT, 3),
            },
            {
                new Token(NUMBER, null, 0).setValue(8D),
                new Token(NUMBER, null, 2).setValue(2D),
                new Token(DIV, LEFT, 1),
                new Token(NUMBER, null, 4).setValue(2D),
                new Token(DIV, LEFT, 3),
            },
            {
                new Token(NUMBER, null, 0).setValue(3D),
                new Token(NUMBER, null, 2).setValue(8D),
                new Token(MUL, LEFT, 1),
                new Token(NUMBER, null, 4).setValue(7D),
                new Token(DIV, LEFT, 3),
            },
            {
                new Token(NUMBER, null, 0).setValue(2D),
                new Token(NUMBER, null, 2).setValue(1D),
                new Token(MINUS, LEFT, 1),
                new Token(NUMBER, null, 4).setValue(8D),
                new Token(PLUS, LEFT, 3),
            },
            {
                new Token(NUMBER, null, 5).setValue(4D),
                new Token(SQRT, PREF, 0),
            },
            {
                new Token(NUMBER, null, 5).setValue(4D),
                new Token(SQRT, PREF, 0),
            },
        };

        for (int i = 0; i < test.length; i++) {
            parseResult.clear();

            parser.parse(new Lexer(test[i], constants));

            assertArrayEquals(expect[i], parseResult.toArray());
        }
    }
}