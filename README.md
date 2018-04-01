# calc
Калькулятор, работающий на Обратной польской нотации.

## Использование
#### Подключение:
```java
import mitasov.calc.Expression;
```
#### Простой пример:

```java
Expression e = new Expression("2+2"); //целые числа
double res1 = e.evaluate(); // == 4

e = new Expression("2.564 * 2"); //дробные
res = e.evaluate(); // == 5.128

e = new Expression("2,5 - 3", ',') //можно указать символ плавающей точки
res = e.evaluate(); // == -0.5
```

#### Пример использования констант

```java
Expression e = new Expression("a+b");

e.getConstants().set("a", 3);
e.getConstants().set("b", 4);

double res = e.evaluate(); // == 7
```

##### Получение списка констант:

```java
Set<String> names = e.getConstants().getNames(); //набор имён констант из выражения

for (String name : e.getConstants().getNames()) { //пример итерации по именам
    System.out.println(name); //выведет имя
    System.out.println(e.getConstants().get(name)); //выведет значение
}
```

#### Обработка ошибок

При ошибке в ходе анализа выражения, калькулятор бросает `CompileException`.
Объект `CompileExseption` кроме сообщения содержит информацию о позиции ошибочной
подстроки и о её длине

```java
try {
    Expression = new Expression("26+*983"); // бросит исключение на символе '*'
} catch (CompileException e) {
    System.out.println(e.getMessage());  // выведет "Operator without operand"
    System.out.println(e.getPosition()); // выведет 3
    System.out.println(e.getLength());   // выведет 1
} 
```