package mitasov.calc;

/**
 * Ошибка вычисления выражения, возникает при {@link Expression#evaluate()}
 * в случае деления на ноль
 */
public class DivisionByZeroException extends EvaluationException {
    DivisionByZeroException(String message) {
        super(message);
    }
}
