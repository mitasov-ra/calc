package mitasov.calc;

import java.util.EmptyStackException;
import java.util.Stack;

import static mitasov.calc.Token.Assoc;
import static mitasov.calc.Token.Id;
import static mitasov.calc.Token.Id.*;
import static mitasov.calc.ExpressionException.Code;

class Parser {
    private final Stack<Token> tokenStack;
    private final CompiledExpression codeGen;
    private boolean hasOperators = false;

    Parser(CompiledExpression codeGen) {
        this.codeGen = codeGen;
        tokenStack = new Stack<>();
    }

    private static boolean isOperand(Id id) {
        return id == FACT
            || id == PERCENT
            || id == NUMBER
            || id == CONST
            || id == RPAREN
            || id == PI
            || id == E;
    }

    private void pushOperator(Token token) {
        if (!hasOperators) {
            hasOperators = true;
        }

        int priority = token.getAssoc() != Assoc.RIGHT && token.getAssoc() != Assoc.PREF
            ? token.getPriority()
            : token.getPriority() + 1;
        while (!tokenStack.empty() && priority <= tokenStack.peek().getPriority()) {
            codeGen.push(tokenStack.pop());
        }
        tokenStack.push(token);
    }

    boolean hasOperators() {
        return hasOperators;
    }

    void parse(Lexer lexer) throws ExpressionException {
        hasOperators = false; //сброс флага операторов для нового выражения
        codeGen.clear(); //сброс данных в кодогенераторе

        Token token;
        Token prevToken = null;
        do {
            token = lexer.nextToken();
            switch (token.getId()) {
            case CONST:
            case NUMBER:
            case PI:
            case E:
                if (prevToken != null && isOperand(prevToken.getId())) {
                    pushOperator(new Token(MUL, Token.Assoc.LEFT).setPriority(2).setPosition(token.getPosition()));
                }
                codeGen.push(token);
                break;

            case RPAREN:
                if (prevToken == null || !isOperand(prevToken.getId())) {
                    throw new ExpressionException(Code.RPAREN_UNEXPECTED, token);
                }
                while (!tokenStack.empty() && tokenStack.peek().getId() != LPAREN) {
                    codeGen.push(tokenStack.pop());
                }
                try {
                    tokenStack.pop();
                } catch (EmptyStackException e) { //LPAREN expected in stack
                    throw new ExpressionException(Code.LPAREN_MISSING, token);
                }
                break;

            case END:
                if (prevToken == null) {
                    throw new ExpressionException(Code.UNEXPECTED_END, 0, 0);
                }

                if (!isOperand(prevToken.getId())) {
                    throw new ExpressionException(Code.UNEXPECTED_END, prevToken);
                }

                while (!tokenStack.empty()) {
                    if (tokenStack.peek().getId() == RPAREN) {
                        throw new ExpressionException(Code.LPAREN_MISSING, tokenStack.peek());
                    } else if (tokenStack.peek().getId() == LPAREN) {
                        tokenStack.pop();
                        continue;
                    }
                    codeGen.push(tokenStack.pop());
                }
                break;
            case LPAREN:
                if (prevToken != null && isOperand(prevToken.getId())) {
                    pushOperator(new Token(MUL, Assoc.LEFT).setPriority(2).setPosition(token.getPosition()));
                }
                tokenStack.push(token);
                break;
            default: //токен - оператор
                if (token.getAssoc() == Assoc.PREF) {
                    if (prevToken != null && isOperand(prevToken.getId())) {
                        pushOperator(new Token(MUL, Assoc.LEFT).setPriority(2));
                    }
                    pushOperator(token);
                    break;
                }
                if (prevToken == null || !isOperand(prevToken.getId())) {
                    if (token.getId() == MINUS) {
                        token.setId(UN_MINUS).setAssoc(Assoc.PREF).setPriority(7);
                        pushOperator(token);
                        break;
                    } else if (token.getId() == PLUS) {
                        continue;
                    }
                    throw new ExpressionException(Code.OPERATOR_WITHOUT_OPERAND, token);
                }

                pushOperator(token);
            }
            prevToken = token;

        } while (token.getId() != END);
    }
}