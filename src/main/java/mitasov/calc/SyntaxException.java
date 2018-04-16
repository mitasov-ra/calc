package mitasov.calc;

/**
 * Синтаксическая ошибка в выражении {@link Expression}
 *
 * @see ParenthesisException
 * @see UnexpectedEndException
 * @see OperatorWithoutOperandException
 */
public class SyntaxException extends CompilationException {
    SyntaxException(String message, int position) {
        super(message, position);
    }

    SyntaxException(String message, int position, int length) {
        super(message, position, length);
    }

    SyntaxException(String message, Token token) {
        super(message, token);
    }
}
