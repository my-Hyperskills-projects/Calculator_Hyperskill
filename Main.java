package calculator;

import java.math.BigInteger;
import java.util.*;

public class Main {

    static HashMap<String, BigInteger> variables = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean isExit = false;

        while (!isExit) {
            String line = scanner.nextLine();
            switch (line) {
                case "":
                    break;
                case "/exit":
                    isExit = true;
                    System.out.println("Bye!");
                    break;
                case "/help":
                    System.out.println("The program calculates the sum, dif, comp and quot of numbers");
                    break;
                default:
                    if (line.matches("/.*")) {
                        System.out.println("Unknown command");
                    } else {
                        try {
                            process(line);
                        } catch (NumberFormatException e) {
                            System.out.println(e.getMessage());
                        }
                    }
            }
        }
    }

    /**
     * Упрощает выражение.
     * Если выражение содержит знак "=", то выполняется метод assigment()
     * Иначе в метод получает список всех элементов выражения(опраторов и операндов) используя метод toListFormat()
     * и отправляет этот список в recognizeTheUnknown() для получения значений неизвестных.
     * Если результат содержит один элемент - это и есть решение, оно выводится на консоль, иначе список пересобирается
     * в постфикс и пример решается в методе solve() после чего результат также выводится в консоль
     * @param expr - выражение, пользовательский ввод
     */
    public static void process(String expr) {
        expr = simplify(expr);
        if (expr.contains("=")) {
            assigment(expr);
        } else {
            ArrayList<String> exprList = toListFormat(expr);
            recognizeTheUnknown(exprList);
            if (exprList.size() == 1) {
                System.out.println(exprList.get(0));
            } else {
                ArrayList<String> postFix = getPostFix(exprList);
                System.out.println(solve(postFix));
            }
        }
    }

    /**
     * Метод используется для решения примеров в формате постфикс
     * @param postFix постфикс пример (список)
     * @return резултат решения
     */
    public static BigInteger solve(ArrayList<String> postFix) {
        Stack<String> stack = new Stack<>();

        for (int i = 0; i < postFix.size(); i++) {
            String x = postFix.get(i);

            switch (x) {
                case "+":
                    BigInteger a = new BigInteger(stack.pop());
                    BigInteger b = new BigInteger(stack.pop());
                    stack.push(a.add(b).toString());
                    break;
                case "-":
                    BigInteger c = new BigInteger(stack.pop());
                    BigInteger d = new BigInteger("0");

                    if (!stack.empty()) {
                        d = new BigInteger(stack.pop());
                    }

                    stack.push(d.subtract(c).toString());
                    break;
                case "*":
                    BigInteger e = new BigInteger(stack.pop());
                    BigInteger f = new BigInteger(stack.pop());
                    stack.push(e.multiply(f).toString());
                    break;
                case "/":
                    BigInteger g = new BigInteger(stack.pop());
                    BigInteger h = new BigInteger(stack.pop());
                    stack.push(h.divide(g).toString());
                    break;
                default:
                    stack.push(x);
            }
        }

        return new BigInteger(stack.pop());
    }

    /**
     * Метод для выполнения операции приравнивания.
     * Разделяет выражение на части до и после знака "=" и после выполнения некоторых проверок отправляет все
     * в Map variables с 1-ым элементов в качестве переменной и 2-ым в качестве значения
     * @param expr - выражение содержащее знак "="
     */
    static void assigment(String expr) {
        String[] split = expr.split("=");
        String variable = split[0];

        if (!variable.matches("[a-zA-Z]+")) {
            System.out.println("Invalid identifier");
            return;
        }

        if (split.length > 2 || !split[1].matches("([a-zA-Z]+|-?\\d+)")) {
            System.out.println("Invalid assignment");
            return;
        }

        BigInteger value;
        if (split[1].matches("-?\\d+")) {
            value = new BigInteger(split[1]);
        } else {
            if (variables.containsKey(split[1])) {
                value = variables.get(split[1]);
            } else {
                System.out.println("Unknown variable");
                return;
            }
        }

        variables.put(variable, value);
    }


    /**
     * @param exprList - список с элементами выражения
     * @return список в постфикс формате
     */
    static ArrayList<String> getPostFix(ArrayList<String> exprList) {
        ArrayList<String> result = new ArrayList<>();
        Stack<String> stack = new Stack<>();

        for (int i = 0; i < exprList.size(); i++) {
            String x = exprList.get(i);

            switch (x) {
                case "(":
                    stack.push(x);
                    break;
                case ")":
                    while (!stack.empty() && !stack.peek().equals("(")) {
                        result.add(stack.pop());
                    }

                    if (stack.empty()) {
                        throw new NumberFormatException("Invalid expression");
                    } else {
                        stack.pop();
                    }

                    break;
                case "+":
                case "-":
                    if (!stack.empty() && !stack.peek().equals("(")) {
                        while (!stack.empty()) {
                            if (stack.peek().equals("(")) break;
                            result.add(stack.pop());
                        }
                    }
                    stack.push(x);
                    break;
                case "*":
                case "/":
                    if (!stack.empty() && (stack.peek().equals("*") || stack.peek().equals("/"))) {
                        result.add(stack.pop());
                    }
                    stack.push(x);
                    break;
                default:
                    result.add(x);
            }
        }

        while (!stack.empty()) {
            if (stack.peek().equals("(")) {
                throw new NumberFormatException("Invalid expression");
            }
            result.add(stack.pop());
        }

        return result;
    }


    /**
     * Упращает выражение, убирая множественые операторы и пробелы
     * @param expr - пользовательский ввод
     * @return выражение без пробелов и множественных операторов
     */
    public static String simplify(String expr) {

        if (expr.contains("**") || expr.contains("//")) {
            throw new NumberFormatException("Invalid expression");
        }

        expr = expr.replaceAll("\\s+", "");

        while (expr.contains("++") || expr.contains("--") || expr.contains("-+") || expr.contains("+-")) {
            expr = expr.replaceAll("--", "+")
                    .replaceAll("\\+\\+", "+")
                    .replaceAll("-\\+", "-")
                    .replaceAll("\\+-", "-");
        }

        return expr;
    }

    /**
     * Переводит строку в формат списка, разделяя её на 2 списка с операторами и операндами, после чего
     * собирая его в один
     * @param expr - выражение в виде строки без пробелов и множественных операторов
     * @return то же выражение в виде списка
     */
    static ArrayList<String> toListFormat(String expr) {

        String[] operatorsArray = expr.replaceAll("[^-=()*/\\+]+", "").split("");
        ArrayList<String> operators = new ArrayList<>(Arrays.asList(operatorsArray));
        ArrayList<String> operands = new ArrayList<>(Arrays.asList(expr.split("[-()*/\\+=]")));

        while (operands.contains("")) {
            operands.remove("");
        }

        ArrayList<String> result = new ArrayList<>();
        result.add(operands.get(0));

        for (int i = 0, j = 1; i < operators.size(); i++, j++) {
            if (i < operators.size() - 1 && operators.get(i).equals(")")) {
                while (i < operators.size() - 1 && operators.get(i).equals(")")) {
                    result.add(operators.get(i));
                    i++;
                }
            }
            result.add(operators.get(i));
            if (i < operators.size() - 1 && operators.get(i + 1).equals("(")) {
                while (i < operators.size() - 1 && operators.get(i + 1).equals("(")) {
                    result.add(operators.get(i + 1));
                    i++;
                }
            }
            if (j < operands.size()) {
                result.add(operands.get(j));
            }
        }

        return result;
    }

    /**
     * Старается получить значения неизвестных из variables, в противном случае кидает исключение
     * @param exprList - выражение в виде списка
     */
    static void recognizeTheUnknown (ArrayList<String> exprList) {
        for (int i = 0; i < exprList.size(); i++) {
            String variable = exprList.get(i);
            if (variable.matches("[a-zA-Z]+")) {
                if (variables.containsKey(variable)) {
                    exprList.set(i, variables.get(variable).toString());
                } else {
                    throw new NumberFormatException("Unknown variable");
                }
            }
        }
        exprList.remove("");
    }
}
