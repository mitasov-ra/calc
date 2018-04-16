package mitasov.calc;

import org.junit.Test;

import java.util.ArrayList;

import static mitasov.calc.Token.Assoc.LEFT;
import static mitasov.calc.Token.Assoc.PREF;
import static mitasov.calc.Token.Assoc.SUF;
import static mitasov.calc.Token.Id.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ParserTest {

    private Expression expression;

    private final ArrayList<Token> parseResult = new ArrayList<>();

    private final Parser parser;

    { //сложная инициализация парсера из-за закрытости класса Expression
        try {
            expression = new Expression("4"); //просто пустышка чтоб получить объект Constants
        } catch (CompilationException ignored) {
        }

        CompiledExpression codeGen = new CompiledExpression(expression.getConstants()) {
            @Override
            void push(Token token) { //перехватываем вывод парсера
                parseResult.add(token);
            }

        };
        parser = new Parser(codeGen);
    }

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
            parser.parse(new Lexer(test[i], expression.getConstants()));
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
            "-6*69",
            "5*-2",
            "8!*9-ln 2",
            "--6",
            "ln -3",
            "sin cos pi + 2",
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
            {
                new Token(NUMBER, null, 1).setValue(6),
                new Token(UN_MINUS, PREF, 0),
                new Token(NUMBER, null, 3).setValue(69),
                new Token(MUL, LEFT, 2),
            },
            {
                new Token(NUMBER, null, 0).setValue(5),
                new Token(NUMBER, null, 3).setValue(2),
                new Token(UN_MINUS, PREF, 2),
                new Token(MUL, LEFT, 1),
            },
            {
                new Token(NUMBER, null, 0).setValue(8),
                new Token(FACT, SUF, 1),
                new Token(NUMBER, null, 3).setValue(9),
                new Token(MUL, LEFT, 2),
                new Token(NUMBER, null, 8).setValue(2),
                new Token(LN, PREF, 5),
                new Token(MINUS, LEFT, 4),
            },
            {
                new Token(NUMBER, null, 2).setValue(6),
                new Token(UN_MINUS, PREF, 1),
                new Token(UN_MINUS, PREF, 0),
            },
            {
                new Token(NUMBER, null, 4).setValue(3),
                new Token(UN_MINUS, PREF, 3),
                new Token(LN, PREF, 0),
            },
            {
                new Token(PI, null, 8),
                new Token(COS, PREF, 4),
                new Token(SIN, PREF, 0),
                new Token(NUMBER, null, 13).setValue(2),
                new Token(PLUS, LEFT, 11),
            }
        };

        for (int i = 0; i < test.length; i++) {
            parseResult.clear();

            parser.parse(new Lexer(test[i], expression.getConstants()));

            assertArrayEquals(expect[i], parseResult.toArray());
        }
    }
}