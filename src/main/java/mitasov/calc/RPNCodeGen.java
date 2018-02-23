package mitasov.calc;

import java.util.ArrayList;
import java.util.Stack;

import static mitasov.calc.Token.*;

class RPNCodeGen {
    private Stack<Double> evalStack = new Stack<>();
    private Constants constants;
    private ArrayList<Token> rpnSequence = new ArrayList<>();

    RPNCodeGen(Constants constants) {
        this.constants = constants;
    }

    void push(Token token) {
        rpnSequence.add(token);
    }

    Double eval() {
        if (!constants.isModified()) {
            return evalStack.peek();
        }
        for (Token token : rpnSequence) {

            switch (token.getId()) {
                case NUMBER:
                    evalStack.push(token.getValue());
                    break;
                case CONST:
                    evalStack.push(constants.get(token.getName()));
                    break;
                case PI:
                    evalStack.push(Math.PI);
                    break;
                case E:
                    evalStack.push(Math.E);
                    break;
                default:
                    double result;
                    if (token.getAssoc() == Token.PREF || token.getAssoc() == Token.SUF) {
                        double value = evalStack.pop();
                        switch (token.getId()) {
                            case PERCENT:
                                result = value / 100;
                                break;

                            case SQRT:
                                result = Math.sqrt(value);
                                break;

                            case FACT:
                                result = fact(value);
                                break;

                            case SIN:
                                result = Math.sin(value);
                                break;

                            case COS:
                                result = Math.cos(value);
                                break;

                            case TAN:
                                result = Math.tan(value);
                                break;

                            case SGN:
                                result = Math.signum(value);
                                break;

                            case EXP:
                                result = Math.exp(value);
                                break;

                            case LN:
                                result = Math.log(value);
                                break;

                            case LOG:
                                result = Math.log10(value);
                                break;

                            case ABS:
                                result = Math.abs(value);
                                break;

                            case UN_MINUS:
                                result = -value;
                                break;

                            default:
                                throw new RuntimeException("unknown token");
                        }
                    } else {
                        double rval = evalStack.pop();
                        double lval = evalStack.pop();
                        switch (token.getId()) {
                            case PLUS:
                                result = lval + rval;
                                break;
                            case MINUS:
                                result = lval - rval;
                                break;
                            case MUL:
                                result = lval * rval;
                                break;
                            case DIV:
                                result = lval / rval;
                                break;
                            case POW:
                                result = Math.pow(lval, rval);
                                break;
                            default:
                                throw new RuntimeException("unknown token");
                        }
                    }
                    evalStack.push(result);
            }
        }
        constants.resetModified();
        return evalStack.peek();
    }

    private static double fact(double value) {
        if (value < 0 || (value != Math.floor(value)) || Double.isInfinite(value)) {
            return Double.NaN;
        }

        double ret = 1;

        for (int i = 1; i <= value; ++i) ret *= i;
        return ret;
    }
}
