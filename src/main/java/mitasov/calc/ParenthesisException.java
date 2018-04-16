package mitasov.calc;

/**
 * Синтаксическая ошибка выражения {@link Exception}
 *
 * Означает, что в выражении неправильно расположены скобки
 */
public class ParenthesisException extends SyntaxException {
    ParenthesisException(String message, int position) {
        super(message, position);
    }

    ParenthesisException(String message, int position, int length) {
        super(message, position, length);
    }

    ParenthesisException(String message, Token token) {
        super(message, token);
    }
}
