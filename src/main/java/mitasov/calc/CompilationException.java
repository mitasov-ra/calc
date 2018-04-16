package mitasov.calc;

/**
 * Ошибка разбора выражения {@link Expression}
 *
 * @see SyntaxException
 * @see InvalidCharacterException
 */
public class CompilationException extends ExpressionException {

    private final int position;
    private int length;

    public int getPosition() {
        return position;
    }

    public int getLength() {
        return length;
    }

    public int getEndPosition() {
        return position + length;
    }

    CompilationException(String message, int position) {
        this(message, position, 1);
    }

    CompilationException(String message, int position, int length) {
        super(message);
        this.position = position;
        this.length = length;
    }

    CompilationException(String message, Token token) {
        this(message, token.getPosition(), token.getLength());
    }
}