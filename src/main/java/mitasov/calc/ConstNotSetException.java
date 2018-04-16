package mitasov.calc;

/**
 * Ошибка вычисления выражения, выбрасывается при {@link Expression#evaluate()}
 * в случае, когда есть незаданные константы
 *
 * @see Expression.Constants
 * @see Expression#getConstants()
 * @see Expression#hasConstants()
 * @see Expression.Constants#put(String, double)
 *
 * @author Roman Mitasov (metas_roman@mail.ru)
 */
public class ConstNotSetException extends EvaluationException {
    ConstNotSetException(String message) {
        super(message);
    }
}
