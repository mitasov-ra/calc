package mitasov.calc;

public class Expression {

    private RPNCodeGen codeGen;
    private String expression;
    private Constants constants;
    private boolean hasOperators;

    public Expression(String expression, char POINT) throws Exception {
        this.expression = expression;
        constants = new Constants();
        codeGen = new RPNCodeGen(constants);

        Parser parser = new Parser(codeGen);
        parser.parse(new Lexer(expression, constants, POINT));
        hasOperators = parser.hasOperators();
    }

    public Expression(String expression) throws Exception {
        this(expression, '.');
    }

    public String getExpression() {
        return expression;
    }

    public Constants getConstants() {
        return constants;
    }

    public boolean hasConstants() {
        return !constants.isEmpty();
    }

    public boolean hasOperations() {
        return hasOperators;
    }

    public double evaluate() {
        return codeGen.eval();
    }
}
