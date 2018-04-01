package mitasov.calc;

public class CompileException extends Exception {

    private int position;
    private int length;

    CompileException(String message, int position) {
        super(message);
        this.position = position;
        this.length = 1;
    }

    CompileException(String message, int position, int length) {
        super(message);
        this.position = position;
        this.length = length;
    }

    CompileException(String message, Token token) {
        super(message);
        this.position = token.getPosition();
        this.length = token.getLength();
    }

    public int getPosition() {
        return position;
    }

    public int getLength() {
        return length;
    }
}
