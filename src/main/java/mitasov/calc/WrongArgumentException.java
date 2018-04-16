package mitasov.calc;

/**
 * Неверный аргумент фунции.
 * <p>
 * Возникает при передаче недопустимого значения в функцию
 * или применении к значению недопустимой операции,
 * например <code>(-1)!</code>
 */
public class WrongArgumentException extends EvaluationException {
    WrongArgumentException(String message) {
        super(message);
    }
}
