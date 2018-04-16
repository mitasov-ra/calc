package mitasov.calc;

/**
 * Исключение, выбрасываемое классом {@link Expression}
 *
 * @see CompilationException
 * @see EvaluationException
 */
public abstract class ExpressionException extends Exception {
    ExpressionException(String message) {
        super(message);
    }
}
