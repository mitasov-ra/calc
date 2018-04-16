package mitasov.calc;

/**
 * Синтаксическая ошибка в выражении {@link Expression}, возникает, если в
 * выражении присутсвуют операторы без операндов, например:
 * <pre>
 * *3
 * /2-5
 * !+6
 * </pre>
 */
public class OperatorWithoutOperandException extends SyntaxException {
    OperatorWithoutOperandException(String message, int position) {
        super(message, position);
    }

    OperatorWithoutOperandException(String message, int position, int length) {
        super(message, position, length);
    }

    OperatorWithoutOperandException(String message, Token token) {
        super(message, token);
    }
}
