package mitasov.calc;

/**
 * Исключение, выбрасываемое в хоче вычисления выраджения
 * методом {@link Expression#evaluate()}
 *
 * @see DivisionByZeroException
 * @see ConstNotSetException
 * @see WrongArgumentException
 */
public class EvaluationException extends ExpressionException {
    EvaluationException(String message) {
        super(message);
    }
}