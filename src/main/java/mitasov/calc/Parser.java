package mitasov.calc;

import java.util.Stack;

import static mitasov.calc.Token.*;

class Parser {
    private Stack<Token> tokenStack;
    private RPNCodeGen codeGen;
    private boolean hasOperators = false;

    Parser(RPNCodeGen codeGen) {
        this.codeGen = codeGen;
        tokenStack = new Stack<>();
    }

    private static boolean isOperand(int id) {
        return id == FACT
            || id == PERCENT
            || id == NUMBER
            || id == CONST
            || id == RPAREN
            || id == PI
            || id == E;
    }

    private void pushOperator(Token token) {
        int priority = token.getAssoc() != Token.RIGHT ? token.getPriority() : token.getPriority() + 1;
        while (!tokenStack.empty() && priority <= tokenStack.peek().getPriority()) {
            codeGen.push(tokenStack.pop());
        }
        tokenStack.push(token);
    }

    boolean hasOperators() {
        return hasOperators;
    }

    void parse(Lexer lexer) throws Exception {
        Token token;
        int prevTokenId = -2;
        do {
            token = lexer.nextToken();
            switch (token.getId()) {
                case CONST:
                case NUMBER:
                case PI:
                case E:
                    if (isOperand(prevTokenId)) {
                        pushOperator(new Token(MUL, Token.LEFT).setPriority(2));
                    }
                    codeGen.push(token);
                    break;

                case RPAREN:
                    if (!isOperand(prevTokenId)) {
                        throw new Exception("unexpected )");
                    }
                    while (!tokenStack.empty() && tokenStack.peek().getId() != LPAREN) {
                        codeGen.push(tokenStack.pop());
                    }
                    tokenStack.pop();

                    break;

                case END:
                    if (!isOperand(prevTokenId)) {
                        throw new Exception("unexpected END");
                    }
                    while (!tokenStack.empty()) {
                        if (tokenStack.peek().getId() == RPAREN) {
                            throw new Exception("unexpected )");
                        } else if (tokenStack.peek().getId() == LPAREN) {
                            tokenStack.pop();
                            continue;
                        }
                        codeGen.push(tokenStack.pop());
                    }
                    break;
                case LPAREN:
                    tokenStack.push(token);
                    break;
                default: //токен - оператор
                    hasOperators = true;
                    if (token.getAssoc() == Token.PREF) {
                        if (isOperand(prevTokenId)) {
                            pushOperator(new Token(MUL, Token.LEFT).setPriority(2));
                        }
                        pushOperator(token);
                        break;
                    }
                    if (!isOperand(prevTokenId)) {
                        if (token.getId() == MINUS) {
                            token = new Token(UN_MINUS, Token.PREF).setPriority(3);
                            pushOperator(token);
                            break;
                        } else if (token.getId() == PLUS) {
                            continue;
                        }
                        throw new Exception("operator without operand");
                    }

                    pushOperator(token);
            }
            prevTokenId = token.getId();

        } while (token.getId() != END);
    }
}
