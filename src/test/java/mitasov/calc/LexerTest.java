package mitasov.calc;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static mitasov.calc.Token.Assoc.*;
import static mitasov.calc.Token.Id.*;
import static org.junit.Assert.*;

public class LexerTest {

    private static final Expression expression; //пустышка для инициализвации лексера

    static {
        Expression expr;
        try {
            expr = new Expression("3");
        } catch (CompilationException e1) {
            expr = null;
            e1.printStackTrace();
        }
        expression = expr;
    }

    @Test
    public void testLookahead() throws Exception {
        Lexer lexer = new Lexer("2 a 3.5 4", expression.getConstants());

        Token next = lexer.lookForToken();

        assertEquals(next.getId(), NUMBER);
        assertEquals(next.getValue(), 2D, 0);

        Token current = lexer.nextToken();

        assertSame(next, current); //это должен быть один и тот же объект

        next = lexer.lookForToken();

        assertEquals(next.getId(), CONST);
        assertEquals(next.getName(), "a");
        assertEquals(next.getValue(), 0D, 0);

        assertNotSame(current, next); //теперь они разные
    }

    @Test
    public void testNumbers() throws Exception {
        Lexer lexer = new Lexer("2 3\t4.2\n .67 . 0. .0 .9999999 213151.3", expression.getConstants());

        ArrayList<Token> output = new ArrayList<>();

        Token[] expect = {
            new Token(NUMBER, null, 0).setValue(2),
            new Token(NUMBER, null, 2).setValue(3),
            new Token(NUMBER, null, 4).setValue(4.2),
            new Token(NUMBER, null, 9).setValue(0.67),
            new Token(NUMBER, null, 13).setValue(0),
            new Token(NUMBER, null, 15).setValue(0),
            new Token(NUMBER, null, 18).setValue(0),
            new Token(NUMBER, null, 21).setValue(0.999_999_9),
            new Token(NUMBER, null, 30).setValue(213_151.3),
            new Token(END, null, 38),
        };

        Token t;
        do {
            t = lexer.nextToken();
            output.add(t);
        } while (t.getId() != END);

        assertArrayEquals(output.toArray(), expect);
    }

    @Test
    public void testConstants() throws Exception {
        Lexer lexer = new Lexer(
            "a/*bc\te asd4\n sin8\rtan78 \u03c0lnas \u03c0 9as -q d(sd)a",
            expression.getConstants()
        );

        Token expect[] = {
            new Token(CONST, null, 0).setName("a"),
            new Token(DIV, LEFT, 1),
            new Token(MUL, LEFT, 2),
            new Token(CONST, null, 3).setName("bc"),
            new Token(E, null, 6),
            new Token(CONST, null, 8).setName("asd4"),
            new Token(CONST, null, 14).setName("sin8"),
            new Token(CONST, null, 19).setName("tan78"),
            new Token(CONST, null, 25).setName("\u03c0lnas"),
            new Token(PI, null, 31),
            new Token(NUMBER, null, 33).setValue(9),
            new Token(CONST, null, 34).setName("as"),
            new Token(MINUS, LEFT, 37),
            new Token(CONST, null, 38).setName("q"),
            new Token(CONST, null, 40).setName("d"),
            new Token(LPAREN, PREF, 41),
            new Token(CONST, null, 42).setName("sd"),
            new Token(RPAREN, null, 44),
            new Token(CONST, null, 45).setName("a"),
            new Token(END, null, 46),
        };

        ArrayList<Token> output = new ArrayList<>();

        Token t;
        do {
            t = lexer.nextToken();
            output.add(t);
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
            expression.getConstants()
        );

        Token[] expect = {
            new Token(MINUS, LEFT, 0),
            new Token(MINUS, LEFT, 2),
            new Token(E, null, 3),
            new Token(PLUS, LEFT, 5),
            new Token(DIV, LEFT, 7),
            new Token(CONST, null, 9).setName("a"),
            new Token(DIV, LEFT, 10),
            new Token(DIV, LEFT, 14),
            new Token(MUL, LEFT, 16),
            new Token(MUL, LEFT, 19),
            new Token(MUL, LEFT, 21),
            new Token(MUL, LEFT, 23),
            new Token(MUL, LEFT, 25),
            new Token(PERCENT, SUF, 28),
            new Token(POW, RIGHT, 30),
            new Token(LPAREN, PREF, 32),
            new Token(FACT, SUF, 33),
            new Token(RPAREN, null, 34),
            new Token(SQRT, PREF, 36),
            new Token(END, null, 37),
        };

        ArrayList<Token> output = new ArrayList<>();

        Token t;
        do {
            t = lexer.nextToken();
            output.add(t);
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
            expression.getConstants()
        );

        Token[] expect = {
            new Token(SIN, PREF, 0),
            new Token(COS, PREF, 4),
            new Token(ASIN, PREF, 8),
            new Token(ACOS, PREF, 15),
            new Token(TAN, PREF, 22),
            new Token(TAN, PREF, 26),
            new Token(LOG, PREF, 29),
            new Token(LN, PREF, 33),
            new Token(EXP, PREF, 36),
            new Token(SQRT, PREF, 40),
            new Token(SQRT, PREF, 41),
            new Token(ABS, PREF, 46),
            new Token(SGN, PREF, 50),
            new Token(COT, PREF, 54),
            new Token(COT, PREF, 58),
            new Token(ACOT, PREF, 62),
            new Token(ACOT, PREF, 69),
            new Token(ATAN, PREF, 76),
            new Token(ATAN, PREF, 83),
            new Token(SH, PREF, 89),
            new Token(SH, PREF, 92),
            new Token(CH, PREF, 97),
            new Token(CH, PREF, 100),
            new Token(TH, PREF, 105),
            new Token(TH, PREF, 108),
            new Token(CTH, PREF, 113),
            new Token(CTH, PREF, 117),
            new Token(CONST, null, 122).setName("sin3"),
            new Token(CONST, null, 127).setName("sina"),
            new Token(CONST, null, 132).setName("lnm"),
            new Token(NUMBER, null, 136).setValue(4),
            new Token(COS, PREF, 137),
            new Token(NUMBER, null, 141).setValue(87),
            new Token(CONST, null, 143).setName("atan"),
            new Token(CONST, null, 148).setName("ochan"),
            new Token(NUMBER, null, 154).setValue(0),
            new Token(CTH, PREF, 155),
            new Token(NUMBER, null, 159).setValue(0),
            new Token(CTH, PREF, 161),
            new Token(NUMBER, null, 166).setValue(98.7),
            new Token(SQRT, PREF, 170),
            new Token(END, null, 174),
        };

        ArrayList<Token> output = new ArrayList<>();

        Token t;
        do {
            t = lexer.nextToken();
            output.add(t);
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

    @Test
    public void testError() throws CompilationException {


        try {
            Lexer l = new Lexer("2#+3", expression.getConstants());

            l.nextToken();
            l.nextToken(); //throws exception

            fail("Exception must be thrown");
        } catch (InvalidCharacterException e) {
            assertEquals("Invalid character: #", e.getMessage());
            assertEquals(1, e.getPosition());
            assertEquals(2, e.getEndPosition());
            assertEquals(1, e.getLength());
        }

        try {
            Lexer l = new Lexer("2-9`89", expression.getConstants());

            l.nextToken();
            l.nextToken();
            l.nextToken();
            l.nextToken(); //throws exception

            fail("Exception must be thrown");
        } catch (InvalidCharacterException e) {
            assertEquals("Invalid character: `", e.getMessage());
            assertEquals(3, e.getPosition());
            assertEquals(4, e.getEndPosition());
            assertEquals(1, e.getLength());
        }
    }

    @Test
    public void testConstantsFilling() throws CompilationException {
        Expression e = new Expression("1"); //создастся без ошибок

        Lexer l = new Lexer("a a b c b", e.getConstants());

        Token t;
        do {
            t = l.nextToken();
        } while (t.getId() != END);

        Map<String, Double> expect = new LinkedHashMap<String, Double>() {{
            put("a", null);
            put("b", null);
            put("c", null);
        }};

        assertEquals(expect.entrySet(), e.getConstants().entrySet());
    }
}