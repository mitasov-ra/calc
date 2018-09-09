package mitasov.calc;

import java.util.HashMap;

import static mitasov.calc.Token.Assoc.*;
import static mitasov.calc.Token.Id.*;
import static mitasov.calc.ExpressionException.Code;

class Lexer {

    private static final char ENDING_CHAR = '#';
    private static final HashMap<String, Token> predeclared = new HashMap<String, Token>() {
        @Override
        public Token get(Object key) {
            return new Token(super.get(key)); //возвращает копию токена
        }
    };

    /*
     * Все записи должны быть в нижнем регистре,
     * при поиске регистр токена тоже будет понижаться,
     * этим достигается нечувствительность к регистру
     */
    static {
        predeclared.put("sin", new Token(SIN, PREF).setPriority(7));
        predeclared.put("cos", new Token(COS, PREF).setPriority(7));
        predeclared.put("arcsin", new Token(ASIN, PREF).setPriority(7));
        predeclared.put("arccos", new Token(ACOS, PREF).setPriority(7));

        // временная переменная для
        // передачи одного объекта несколько раз
        Token temp = new Token(TAN, PREF).setPriority(7);
        predeclared.put("tan", temp);
        predeclared.put("tg", temp);

        predeclared.put("log", new Token(LOG, PREF).setPriority(7));
        predeclared.put("ln", new Token(LN, PREF).setPriority(7));
        predeclared.put("exp", new Token(EXP, PREF).setPriority(7));
        predeclared.put("sqrt", new Token(SQRT, PREF).setPriority(7));
        predeclared.put("abs", new Token(ABS, PREF).setPriority(7));
        predeclared.put("sgn", new Token(SGN, PREF).setPriority(7));

        temp = new Token(COT, PREF).setPriority(7);
        predeclared.put("ctg", temp);
        predeclared.put("cot", temp);

        temp = new Token(ACOT, PREF).setPriority(7);
        predeclared.put("arcctg", temp);
        predeclared.put("arccot", temp);

        temp = new Token(ATAN, PREF).setPriority(7);
        predeclared.put("arctan", temp);
        predeclared.put("arctg", temp);

        temp = new Token(SH, PREF).setPriority(7);
        predeclared.put("sh", temp);
        predeclared.put("sinh", temp);

        temp = new Token(CH, PREF).setPriority(7);
        predeclared.put("ch", temp);
        predeclared.put("cosh", temp);

        temp = new Token(TH, PREF).setPriority(7);
        predeclared.put("th", temp);
        predeclared.put("tanh", temp);

        temp = new Token(CTH, PREF).setPriority(7);
        predeclared.put("cth", temp);
        predeclared.put("coth", temp);

        temp = new Token(PI, null);
        predeclared.put("pi", temp);
        predeclared.put("\u03c0", temp); // буква Пи строчная греческая
        predeclared.put("\uD835\uDF0B", temp); // символ Пи математический

        temp = new Token(E, null);
        predeclared.put("e", temp);
        predeclared.put("\uD835\uDC52", temp); // строчная математическая e
    }

    private final char POINT;
    private int pos;
    private char[] buffer;
    private Expression.Constants constants;
    private Token savedToken = null;

    Lexer(String expression, Expression.Constants constants, char POINT) {
        this.POINT = POINT;
        this.constants = constants;

        buffer = new char[expression.length() + 1];
        expression.getChars(0, expression.length(), buffer, 0);
        buffer[expression.length()] = ENDING_CHAR;
        pos = 0;
    }

    Lexer(String expression, Expression.Constants constants) {
        this(expression, constants, '.');
    }

    Token lookForToken() throws ExpressionException {
        if (savedToken == null) {
            savedToken = nextToken();
            return savedToken;
        }

        return savedToken;
    }

    Token nextToken() throws ExpressionException {
        if (savedToken != null) {
            Token temp = savedToken;
            savedToken = null;
            return temp;
        }


        int state = 0;
        char c;
        int begin = pos;
        while (true) {
            c = buffer[pos];
            switch (state) {
            case 0:
                if (Character.isWhitespace(c)) {
                    break;
                }

                if (Character.isJavaIdentifierStart(c)) {
                    begin = pos;
                    state = 1;
                    break;
                }

                if (isNumber(c)) {
                    begin = pos;
                    state = 2;
                    break;
                }

                if (c == POINT) {
                    begin = pos;
                    state = 3;
                    break;
                }

                switch (buffer[pos++]) {
                case '+':
                    return new Token(PLUS, LEFT, pos - 1).setPriority(1);
                case '-':
                case '\u2212':
                    return new Token(MINUS, LEFT, pos - 1).setPriority(1);
                case '*':
                case '\u2219':
                case '\u2217':
                case '\u00D7':
                case '\u2715':
                    return new Token(MUL, LEFT, pos - 1).setPriority(2);
                case '/':
                case '\u00F7':
                case '\u2215':
                    return new Token(DIV, LEFT, pos - 1).setPriority(2);
                case '^':
                    return new Token(POW, RIGHT, pos - 1).setPriority(4);
                case '(':
                    return new Token(LPAREN, PREF, pos - 1);
                case ')':
                    return new Token(RPAREN, null, pos - 1);
                case '\u221a':
                    return new Token(SQRT, PREF, pos - 1).setPriority(7);
                case '!':
                    return new Token(FACT, SUF, pos - 1).setPriority(5);
                case '%':
                    return new Token(PERCENT, SUF, pos - 1).setPriority(6);
                case ENDING_CHAR:
                    if (pos != buffer.length) {
                        throw new ExpressionException(Code.INVALID_CHARACTER, pos - 1);
                    }
                    return new Token(END, null, pos - 1);
                default:
                    throw new ExpressionException(Code.INVALID_CHARACTER, pos - 1);
                }
            case 1: {
                if (Character.isJavaIdentifierPart(c)) {
                    break;
                }

                String word = String.valueOf(buffer, begin, pos - begin);
                if (predeclared.containsKey(word.toLowerCase())) {
                    return predeclared.get(word.toLowerCase()).setPosition(begin).setLength(word.length());
                }
                constants.add(word);
                return (new Token(CONST, null, begin)).setName(word).setLength(word.length());
            }

            case 2:
                if (isNumber(c)) {
                    break;
                }

                if (c == POINT) {
                    state = 3;
                    break;
                }
            case 3: {
                if (c == POINT) {
                    throw new ExpressionException(Code.WRONG_NUMBER, pos);
                }

                if (isNumber(c)) {
                    break;
                }

                String word = String.valueOf(buffer, begin, pos - begin);
                Token tok = new Token(NUMBER, null, begin);
                if (word.equals(String.valueOf(POINT))) {
                    return tok.setValue(0d).setLength(word.length());
                } else {
                    return tok.setValue(Double.parseDouble(word.replace(POINT, '.'))).setLength(word.length());
                }
            }
            }
            pos++;
        }
    }

    private boolean isNumber(char n) {
        return Character.isDigit(n);
    }
}