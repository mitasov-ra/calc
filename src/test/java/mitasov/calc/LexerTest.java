package mitasov.calc;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static mitasov.calc.Token.*;
import static org.junit.Assert.*;

public class LexerTest {

    @Test
    public void testLookahead() throws Exception {
        Lexer lexer = new Lexer("2 a 3.5 4", new Constants());

        Token next = lexer.lookForToken();

        assertEquals(next.getId(), NUMBER);
        assertEquals(next.getValue(), 2D, 0);

        Token current = lexer.nextToken();

        assertEquals(next, current); //это должен быть один и тот же объект

        next = lexer.lookForToken();

        assertEquals(next.getId(), CONST);
        assertEquals(next.getName(), "a");
        assertEquals(next.getValue(), 0D, 0);

        assertNotEquals(current, next); //теперь они разные
    }

    @Test
    public void testNumbers() throws Exception {
        Lexer lexer = new Lexer("2 3\t4.2\n .67 . 0. .0 .9999999 213151.3", new Constants());

        ArrayList<Integer> output = new ArrayList<>();

        Integer[] expect = {
            NUMBER, NUMBER, NUMBER, NUMBER, NUMBER, NUMBER, NUMBER, NUMBER, NUMBER, END,
        };

        Token t;
        do {
            t = lexer.nextToken();
            output.add(t.getId());
        } while (t.getId() != Token.END);

        assertArrayEquals(output.toArray(), expect);
    }

    @Test
    public void testNumberValues() throws Exception {
        Lexer lexer = new Lexer("2 3\t4.2\n .67  . 0. .0 .9999999 213151.3", new Constants());

        Double[] expect = {
            2D, 3D, 4.2, 0.67, 0D, 0D, 0D, 0.9999999, 213151.3, 0D,
        };

        Token t;

        ArrayList<Double> output = new ArrayList<>();

        do {
            t = lexer.nextToken();
            output.add(t.getValue());
        } while (t.getId() != END);

        assertArrayEquals(
            "Wrong output for string \n\"2 3\\t4.2\\n .67  .9999999 213151.3\".\n"
                + "Expected output:\n"
                + Arrays.toString(expect)
                + "\nGot output:\n"
                + Arrays.toString(output.toArray())
                + "\n",
            output.toArray(),
            expect
        );
    }

    @Test
    public void testConstants() throws Exception {
        Lexer lexer = new Lexer("a/*bc\te asd4\n sin8\rtan78 \u03c0lnas \u03c0 9as -q d(sd)a", new Constants());

        Integer expect[] = {
            CONST,
            DIV,
            MUL,
            CONST,
            E,
            CONST,
            CONST,
            CONST,
            CONST,
            PI,
            NUMBER,
            CONST,
            MINUS,
            CONST,
            CONST,
            LPAREN,
            CONST,
            RPAREN,
            CONST,
            END,
        };

        ArrayList<Integer> output = new ArrayList<>();

        Token t;
        do {
            t = lexer.nextToken();
            output.add(t.getId());
        } while (t.getId() != END);

        assertArrayEquals(
            "Wrong output for string \n\"a/*bc\\t asd4\\n sin8\\rtan78 \\u03c0lnas \\u03c0 9as -q d(sd)a\".\n"
                + "Expected output:\n"
                + Arrays.toString(expect)
                + "\nGot output:\n"
                + Arrays.toString(output.toArray())
                + "\n",
            output.toArray(),
            expect
        );
    }

    @Test
    public void testOperators() throws Exception {
        Lexer lexer = new Lexer(
            "- \u2212e + / a\u00F7 \n\n\u2215 * \r\u2219 \u2217 \u00D7 \u2715 \t% ^ (!) \u221a",
            new Constants()
        );

        Integer[] expect = {
            MINUS,
            MINUS,
            E,
            PLUS,
            DIV,
            CONST,
            DIV,
            DIV,
            MUL,
            MUL,
            MUL,
            MUL,
            MUL,
            PERCENT,
            POW,
            LPAREN,
            FACT,
            RPAREN,
            SQRT,
            END,
        };

        ArrayList<Integer> output = new ArrayList<>();

        Token t;
        do {
            t = lexer.nextToken();
            output.add(t.getId());
        } while (t.getId() != END);

        assertArrayEquals(
            "Wrong output for string \n"
                + "\"- \\u2212e + / a\\u00F7 \\n\\n\\u2215 * \\r\\u2219 \\u2217 \\u00D7 \\u2715 \\t% ^ (!) \\u221a\".\n"
                + "Expected output:\n"
                + Arrays.toString(expect)
                + "\nGot output:\n"
                + Arrays.toString(output.toArray())
                + "\n",
            output.toArray(),
            expect
        );
    }

    @Test
    public void testFunctions() throws Exception {
        Lexer lexer = new Lexer(
            "sin cos arcsin arccos tan tg log ln exp \u221asqrt abs sgn ctg cot arcctg arccot "
                + "arctan arctg sh sinh ch cosh th tanh cth coth "
                + "sin3 sina lnm 4cos 87atan ochan .cth 0.coth 98.7sqrt",
            new Constants()
        );

        Integer[] expect = {
            SIN,
            COS,
            ASIN,
            ACOS,
            TAN,
            TAN,
            LOG,
            LN,
            EXP,
            SQRT,
            SQRT,
            ABS,
            SGN,
            COT,
            COT,
            ACOT,
            ACOT,
            ATAN,
            ATAN,
            SH,
            SH,
            CH,
            CH,
            TH,
            TH,
            CTH,
            CTH,
            CONST,
            CONST,
            CONST,
            NUMBER,
            COS,
            NUMBER,
            CONST,
            CONST,
            NUMBER,
            CTH,
            NUMBER,
            CTH,
            NUMBER,
            SQRT,
            END,
        };

        ArrayList<Integer> output = new ArrayList<>();

        Token t;
        do {
            t = lexer.nextToken();
            output.add(t.getId());
        } while (t.getId() != END);

        assertArrayEquals(
            "Wrong output for string \n"
                + "\"sin cos arcsin arccos tan tg log ln exp \u221asqrt abs sgn ctg cot arcctg arccot\n"
                + "\"arctan arctg sh sinh ch cosh th tanh cth coth"
                + "sin3 sina lnm 4cos 87atan ochan .cth 0.coth 98.7sqrt\""
                + "Expected output:\n"
                + Arrays.toString(expect)
                + "\nGot output:\n"
                + Arrays.toString(output.toArray())
                + "\n",
            output.toArray(),
            expect
        );
    }
}