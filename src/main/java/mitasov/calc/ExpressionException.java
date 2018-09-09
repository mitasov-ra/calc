package mitasov.calc;

/**
 * Исключение, выбрасываемое классом {@link Expression}
 */
public class ExpressionException extends Exception {
    private final int position;
    private final int length;
    private final Code code;

    ExpressionException(Code code, int position) {
        this(code, position, 1);
    }

    ExpressionException(Code code, int position, int length) {
        super(code.message);
        this.position = position;
        this.code = code;
        this.length = length;
    }

    ExpressionException(Code code, Token token) {
        this(code, token.getPosition(), token.getLength());
    }

    ExpressionException(Token token) {
        this(Code.UNDEFINED, token);
    }

    ExpressionException(int position, int length) {
        this(Code.UNDEFINED, position, length);
    }

    ExpressionException(int position) {
        this(Code.UNDEFINED, position);
    }

    public Code getCode() {
        return code;
    }

    public int getPosition() {
        return position;
    }

    public int getLength() {
        return length;
    }

    public int getEndPosition() {
        return position + length;
    }

    public enum Code {
        UNDEFINED("Undefined error"),
        INVALID_CHARACTER("Invalid character"),
        WRONG_NUMBER("Wrong number format"),
        SQRT_OF_NEG("Sqrt of negative number"),
        FACT_OF_NEG("Factorial of negative number"),
        LOG_OF_NEG("Logarithm of negative number"),
        UNDEFINED_CONST("Using of undefined constant"),
        DIV_BY_ZERO("Division by zero"),
        OPERATOR_WITHOUT_OPERAND("Operator used without operand"),
        RPAREN_UNEXPECTED("Unexpected closing parenthesis"),
        LPAREN_MISSING("Closing parenthesis without opening"),
        UNEXPECTED_END("Unexpected end of expression"),
        ;

        private String message;

        Code(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
