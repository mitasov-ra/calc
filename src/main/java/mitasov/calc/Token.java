package mitasov.calc;

public class Token {
    static final int PREF = 1;
    static final int SUF = 2;
    static final int LEFT = 3;
    static final int RIGHT = 4;

    static final int NUMBER = 0;
    static final int PLUS = 2;
    static final int MINUS = 3;
    static final int DIV = 4;
    static final int MUL = 5;
    static final int POW = 6;
    static final int PERCENT = 7;
    static final int FACT = 8;
    static final int SIN = 9;
    static final int COS = 10;
    static final int TAN = 11;
    static final int EXP = 12;
    static final int LN = 13;
    static final int LOG = 14;
    static final int UN_MINUS = 15;
    static final int PI = 16;
    static final int E = 17;
    static final int SQRT = 18;

    static final int ABS = 22;
    static final int SGN = 23;

    static final int RPAREN = 20;
    static final int LPAREN = 21;

    static final int CONST = 1;

    static final int END = -1;

    private int id;
    private int position;
    private int assoc;
    private int priority;
    private String name = null;
    private double value;

    int getPriority() {
        return priority;
    }

    Token setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    double getValue() {
        return value;
    }

    Token setValue(double value) {
        this.value = value;
        return this;
    }

    int getAssoc() {
        return assoc;
    }

    public Token setAssoc(int assoc) {
        this.assoc = assoc;
        return this;
    }

    String getName() {
        return name;
    }

    Token setName(String name) {
        this.name = name;
        return this;
    }

    int getId() {
        return id;
    }

    public Token setId(byte id) {
        this.id = id;
        return this;
    }

    public int getPosition() {
        return position;
    }

    Token setPosition(int position) {
        this.position = position;
        return this;
    }

    Token(int id, int assoc, int position) {
        this.id = id;
        this.position = position;
        this.assoc = assoc;
    }

    Token(int id, int assoc) {
        this.id = id;
        this.assoc = assoc;
    }
}
