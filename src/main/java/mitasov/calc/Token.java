package mitasov.calc;

import java.util.Objects;

class Token {
    private Id id;
    private int position;
    private Assoc assoc;
    private int priority;
    private String name = null;
    private double value;
    private int length = 1; //At least one symbol

    Token(Id id, Assoc assoc, int position) {
        this(id, assoc);
        this.position = position;
    }
    Token(Id id, Assoc assoc) {
        this.id = id;
        this.assoc = assoc;
    }

    Token(Token t) {
        this(t.id, t.assoc, t.position);
        this.name = t.name;
        this.priority = t.priority;
        this.value = t.value;
        this.length = t.length;
    }

    int getLength() {
        return length;
    }

    Token setLength(int length) {
        this.length = length;
        return this;
    }

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

    Assoc getAssoc() {
        return assoc;
    }

    Token setAssoc(Assoc assoc) {
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

    Id getId() {
        return id;
    }

    Token setId(Id id) {
        this.id = id;
        return this;
    }

    int getPosition() {
        return position;
    }

    Token setPosition(int position) {
        this.position = position;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Token)) {
            return false;
        }

        Token t = (Token) obj;

        return id == t.id
            && assoc == t.assoc
            && value == t.value
            && Objects.equals(name, t.name)
            && position == t.position;
    }

    @Override
    public int hashCode() {
        int result = 1;

        result = 31 * result + (assoc != null ? assoc.hashCode() : 0);
        result = 31 * result + ((int) value);
        result = 31 * result + id.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + position;

        return result;
    }

    /**
     * @return String of format "Token:{ID}:{POSITION}" <br/>
     * For NUMBER format is "Token:{ID}({VALUE}):{POSITION}" <br/>
     * For CONST format is "Token:{ID}({NAME}={VALUE}):{POSITION}"
     */
    @Override
    public String toString() {
        if (id == Id.NUMBER) {
            return "Token:" + id + "(" + value + "):" + position;
        }

        if (id == Id.CONST) {
            return "Token:" + id + "(" + name + "=" + value + "):" + position;
        }

        return  "Token:" + id + ":" + position;
    }

    enum Assoc {
        PREF,
        SUF,
        LEFT,
        RIGHT,
    }

    enum Id {
        NUMBER,
        PLUS,
        MINUS,
        DIV,
        MUL,
        POW,
        PERCENT,
        FACT,
        SIN,
        COS,
        TAN,
        EXP,
        LN,
        LOG,
        UN_MINUS,
        PI,
        E,
        SQRT,
        ABS,
        SGN,
        COT,
        ASIN,
        ACOS,
        ATAN,
        ACOT,
        CH,
        SH,
        TH,
        CTH,
        RPAREN,
        LPAREN,
        CONST,
        END,
    }
}
