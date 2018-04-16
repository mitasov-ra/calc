package mitasov.calc;

/**
 * Синтаксическая ошибка в выражении {@link Expression}
 *
 * Означает, что выражение не закончено
 */
public class UnexpectedEndException extends SyntaxException {
    UnexpectedEndException(String message, int position) {
        super(message, position);
    }

    UnexpectedEndException(String message, int position, int length) {
        super(message, position, length);
    }

    UnexpectedEndException(String message, Token token) {
        super(message, token);
    }
}
