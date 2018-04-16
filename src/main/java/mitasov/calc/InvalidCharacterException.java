package mitasov.calc;

/**
 * Лексическая ошибка в выражении {@link Expression},
 * недопустимый символ
 */
public class InvalidCharacterException extends CompilationException {
    InvalidCharacterException(String message, int position) {
        super(message, position);
    }

    InvalidCharacterException(String message, int position, int length) {
        super(message, position, length);
    }
}
