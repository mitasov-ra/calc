import mitasov.calc.Expression;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Scanner;

public class Main {

    public static void main(String args[]) {
        DecimalFormat df = new DecimalFormat("#.#############");
        df.setRoundingMode(RoundingMode.CEILING);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите выражение: ");
        String exprString = scanner.nextLine();
        try {
            Expression expression = new Expression(exprString, ',');
            while (true) {
                if (expression.hasConstants()) {
                    for (String name : expression.getConstants().getNames()) {
                        System.out.println("Введите значение переменной " + name + ": ");
                        expression.getConstants().set(name, scanner.nextDouble());
                    }
                }
                System.out.println(df.format(expression.evaluate()));
                System.out.println("Повторить решение? (1/0)");
                int answ = scanner.nextInt();
                if (answ == 0) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
