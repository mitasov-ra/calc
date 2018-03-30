package mitasov.calc;

import java.util.HashMap;

import static mitasov.calc.Token.*;

class Lexer {

    private static HashMap<String, Token> predeclared = new HashMap<String, Token>() {
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
        predeclared.put("sin", new Token(SIN, Token.PREF).setPriority(7));
        predeclared.put("cos", new Token(COS, Token.PREF).setPriority(7));
        predeclared.put("arcsin", new Token(ASIN, Token.PREF).setPriority(7));
        predeclared.put("arccos", new Token(ACOS, Token.PREF).setPriority(7));

        // временная переменная для
        // передачи одного объекта несколько раз
        Token temp = new Token(TAN, Token.PREF).setPriority(7);
        predeclared.put("tan", temp);
        predeclared.put("tg", temp);

        predeclared.put("log", new Token(LOG, Token.PREF).setPriority(7));
        predeclared.put("ln", new Token(LN, Token.PREF).setPriority(7));
        predeclared.put("exp", new Token(EXP, Token.PREF).setPriority(7));
        predeclared.put("sqrt", new Token(SQRT, Token.PREF).setPriority(7));
        predeclared.put("abs", new Token(ABS, Token.PREF).setPriority(7));
        predeclared.put("sgn", new Token(SGN, Token.PREF).setPriority(7));

        temp = new Token(COT, Token.PREF).setPriority(7);
        predeclared.put("ctg", temp);
        predeclared.put("cot", temp);

        temp = new Token(ACOT, Token.PREF).setPriority(7);
        predeclared.put("arcctg", temp);
        predeclared.put("arccot", temp);

        temp = new Token(ATAN, Token.PREF).setPriority(7);
        predeclared.put("arctan", temp);
        predeclared.put("arctg", temp);

        temp = new Token(SH, Token.PREF).setPriority(7);
        predeclared.put("sh", temp);
        predeclared.put("sinh", temp);

        temp = new Token(CH, Token.PREF).setPriority(7);
        predeclared.put("ch", temp);
        predeclared.put("cosh", temp);

        temp = new Token(TH, Token.PREF).setPriority(7);
        predeclared.put("th", temp);
        predeclared.put("tanh", temp);

        temp = new Token(CTH, Token.PREF).setPriority(7);
        predeclared.put("cth", temp);
        predeclared.put("coth", temp);

        temp = new Token(PI, 0);
        predeclared.put("pi", temp);
        predeclared.put("\u03c0", temp); // буква Пи строчная греческая
        predeclared.put("\uD835\uDF0B", temp); // символ Пи математический

        temp = new Token(E, 0);
        predeclared.put("e", temp);
        predeclared.put("\uD835\uDC52", temp); // строчная математическая e
    }

    private int pos;
    private char[] buffer;
    private final char POINT;
    private static final char ENDING_CHAR = '#';
    private Constants constants;
    private Token savedToken = null;

    Lexer(String expression, Constants constants, char POINT) {
        this.POINT = POINT;
        this.constants = constants;

        buffer = new char[expression.length() + 1];
        expression.getChars(0, expression.length(), buffer, 0);
        buffer[expression.length()] = ENDING_CHAR;
        pos = 0;
    }

    Lexer(String expression, Constants constants) {
        this(expression, constants, '.');
    }

    Token lookForToken() throws Exception {
        if (savedToken == null) {
            savedToken = nextToken();
            return savedToken;
        }

        return savedToken;
    }

    Token nextToken() throws Exception {
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

                    switch (buffer[pos++]) {
                        case '+':
                            return new Token(PLUS, Token.LEFT, pos - 1).setPriority(1);
                        case '-':
                        case '\u2212':
                            return new Token(MINUS, Token.LEFT, pos - 1).setPriority(1);
                        case '*':
                        case '\u2219':
                        case '\u2217':
                        case '\u00D7':
                        case '\u2715':
                            return new Token(MUL, Token.LEFT, pos - 1).setPriority(2);
                        case '/':
                        case '\u00F7':
                        case '\u2215':
                            return new Token(DIV, Token.LEFT, pos - 1).setPriority(2);
                        case '^':
                            return new Token(POW, Token.RIGHT, pos - 1).setPriority(4);
                        case '(':
                            return new Token(LPAREN, Token.PREF, pos - 1);
                        case ')':
                            return new Token(RPAREN, 0, pos - 1);
                        case '\u221a':
                            return new Token(SQRT, Token.PREF, pos - 1).setPriority(7);
                        case '!':
                            return new Token(FACT, Token.SUF, pos - 1).setPriority(5);
                        case '%':
                            return new Token(PERCENT, Token.SUF, pos - 1).setPriority(6);
                        case ENDING_CHAR:
                            return new Token(END, 0, pos);
                        default:
                            throw new Exception("Invalid character");
                    }
                case 1: {
                    if (Character.isJavaIdentifierPart(c)) {
                        break;
                    }

                    String word = String.valueOf(buffer, begin, pos - begin);
                    if (predeclared.containsKey(word.toLowerCase())) {
                        return predeclared.get(word.toLowerCase()).setPosition(begin);
                    }
                    constants.add(word, 0);
                    return (new Token(CONST, 0, begin)).setName(word);
                }

                case 2: {
                    if (isNumber(c)) {
                        break;
                    }

                    String word = String.valueOf(buffer, begin, pos - begin);
                    Token tok = new Token(NUMBER, 0, begin);
                    if (word.equals("" + POINT)) {
                        return tok.setValue(0d);
                    } else {
                        return tok.setValue(Double.parseDouble(word.replace(POINT, '.')));
                    }
                }
            }
            pos++;
        }
    }

    private boolean isNumber(char n) {
        return Character.isDigit(n) || n == POINT;
    }
}
