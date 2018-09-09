package mitasov.calc;

import java.util.*;

/**
 * Класс обработанного выражения
 * <p>
 * Представляет арифметическое выражение, например:
 * <pre>
 * 5 + 8*9 - 3
 * </pre>
 * <p>Поддерживаются операции сложения, вычитания, умножения, деления, возведения в степень. Также поддерживаются
 * тригонометрические функции, радикал, факториал и процент</p>
 * <p>Символы пробела, переносов строки и табуляции используются как разделители операндов</p>
 * <p>Если написать два операнда раздельно, это будет эквивалентно операции умножения:
 * <pre>
 * 2 3 => 2 * 3
 * (4-2)(10^2) => (4-2) * (10^2)
 * </pre>
 * </p>
 * <p>
 * Выражение может содержать в себе именованные константы:
 * <pre>
 * a*b-5a
 * константа1 + константа2
 * </pre>
 * Константы должны начинаться с буквы, может содержать цифры и символы подчёркивания,
 * выражение "5a" эквивалентно "5*a"
 * <p>
 * Примеры работы с классом:
 * <pre>
 * Expression e = new Expression("5*8");
 * double d = e.evaluate(); // = 40
 *
 * e = new Expression("2,5 + 2", ","); // использование своего символа точки
 * d = e.evaluate(); // = 4.5
 *
 * e = new Expression("a + b");
 * // выбросит {@link ConstNotSetException}
 * // d = e.evaluate();
 *
 * e.getConstants().put("a", 3); // getConstants() возвращает объект {@link Expression.Constants}
 * e.getConstants().put("b", 4);
 *
 * d = e.evaluate(); // = 7
 * </pre>
 *
 * @author Mitasov Roman (metas_roman@mail.ru)
 * @see #Expression(String)
 * @see #Expression(String, char)
 * @see #getConstants()
 * @see #evaluate()
 */
public final class Expression {

    private final CompiledExpression codeGen;
    private final String expression;
    private final Constants constants;
    private boolean hasOperators;

    /**
     * Конструктор с указанным символом плавающей точки (по умолчанию '.')
     * <p>
     * Пример:
     * <pre>
     *     Expression e = new Expression("2,9 + 8,16", ','); //не выведет ошибки
     *     double d = e.evaluate(); // = 11.06
     * </pre>
     * </p>
     *
     * @param expression Строка выражения
     * @param POINT      Символ, которым будет заменён символ плавающей точки
     * @see #Expression(String)
     */
    public Expression(String expression, char POINT) throws ExpressionException {
        this.expression = expression;
        constants = new Constants();
        codeGen = new CompiledExpression(constants);

        Parser parser = new Parser(codeGen);
        parser.parse(new Lexer(expression, constants, POINT));
        hasOperators = parser.hasOperators();
    }

    /**
     * Конструктор выражения
     * <p>
     * Пример:
     * <pre>
     *     Expression e = new Expression("2.9 + 8.16");
     *     double d = e.evaluate(); // = 11.06
     * </pre>
     *
     * @param expression Строка выражения
     * @throws CompilationException Исключение обработки выражения,
     *                              может быть лексической ({@link InvalidCharacterException})
     *                              или синтаксической ({@link SyntaxException}) ошибкой.
     *                              Синтаксические ошибки: {@link OperatorWithoutOperandException},
     *                              {@link ParenthesisException}, {@link UnexpectedEndException}
     * @see #Expression(String, char)
     */
    public Expression(String expression) throws ExpressionException {
        this(expression, '.');
    }

    /**
     * @return Строка выражения
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Получить набор констант выражения
     * <p>
     * Набор представляет собой пары имя -> значение
     *
     * @return {@link Expression.Constants} Набор констант
     */
    public Constants getConstants() {
        return constants;
    }

    /**
     * @return <code>true</code>, если в выражении присутсвуют константы
     */
    public boolean hasConstants() {
        return !constants.isEmpty();
    }

    /**
     * @return <code>true</code>, если в выражении присутствуют операции. (для выражения "10" вернёт <code>false</code>)
     */
    public boolean hasOperations() {
        return hasOperators;
    }

    /**
     * Вычислить выражение не строго
     * <p>
     * То же самое, что {@code evaluate(false)}
     *
     * @return Результат вычисления, может быть {@code NaN} или {@code Infinite}
     * @throws EvaluationException ошибка в ходе вычисления, например {@link ConstNotSetException}
     */
    public double evaluate() throws ExpressionException {
        return codeGen.eval(false);
    }

    /**
     * Вычислить выражение в строгом режиме
     *
     * @return Результат вычисления, вместо {@code NaN} или {@code Infinite} вероятнее всего выбросит исключение
     */
    public double evaluateStrict() throws ExpressionException {
        return codeGen.eval(true);
    }

    /**
     * Словарь - уникальный набор констант и их значений из выражения {@link Expression}
     */
    public class Constants {
        private boolean isModified = true;
        private final Map<String, Double> constMap;

        Constants() {
            this.constMap = new LinkedHashMap<>();
        }

        void add(String name) {
            constMap.put(name, null);
        }

        void resetModified() {
            isModified = false;
        }

        boolean isModified() {
            return isModified;
        }

        /**
         * @return int Размер словаря
         */
        public int size() {
            return constMap.size();
        }

        /**
         * @return Пуст ли словарь
         */
        public boolean isEmpty() {
            return constMap.isEmpty();
        }

        /**
         * Имеется ли константа с указанным именем
         *
         * @param key Имя константы
         * @return {@code true} если в наборе присутствует константа с таким именем, иначе {@code false}
         */
        public boolean containsKey(String key) {
            return constMap.containsKey(key);
        }

        /**
         * Имеется ли константа с таким значением
         * @param value Значение константы
         */
        public boolean containsValue(Double value) {
            return constMap.containsValue(value);
        }

        /**
         * Возвращает немодифицируемый словарь имён констант
         *
         * @return Набор имён констант
         */
        public Set<String> keySet() {
            return Collections.unmodifiableSet(constMap.keySet());
        }

        /**
         * Получить значение константы
         *
         * @param name Имя константы
         * @return Значение константы, <code>null</code>, если оно не было задано
         */
        public Double get(String name) {
            return constMap.get(name);
        }

        /**
         * Добавить значение константы
         *
         * @param name  Имя константы
         * @param value Значение, которое будет присвоено константе
         * @return this Метод является цепным
         * @throws IllegalArgumentException В случае, если такой константы в наборе нет
         */
        public Double put(String name, Double value) {
            if (!constMap.containsKey(name)) {
                throw new IllegalArgumentException("No such constant in set");
            }

            isModified = true;
            return constMap.put(name, value);
        }

        /**
         * Синоним {@link #keySet}
         */
        public Set<String> names() {
            return keySet();
        }

        /**
         * @return Набор пар имя -> значение для итерирования
         */
        public Set<Map.Entry<String, Double>> entrySet() {
            return Collections.unmodifiableSet(constMap.entrySet());
        }

        /**
         * @return Коллекция значений констант
         */
        public Collection<Double> values() {
            return Collections.unmodifiableCollection(constMap.values());
        }
    }
}
