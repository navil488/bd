/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author egiron
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class Automata {

    // Tabla de símbolos para almacenar variables y sus valores
    private HashMap<String, Integer> symbolTable = new HashMap<>();

    // Cadenas para almacenar lexemas y transiciones
    private StringBuilder lexemes = new StringBuilder();
    private StringBuilder transitions = new StringBuilder();

    // Conjunto para evitar duplicados en las transiciones
    private Set<String> transitionSet = new HashSet<>();

    // Variables de control de estados del autómata
    private int currentState = 0;
    private boolean isValid = true;
    private String fromState = "Q0";
    private String toState = "";

    // Método para reiniciar el estado del autómata y limpiar variables
    private void reset() {
        currentState = 0;  // Estado inicial
        isValid = true;    // Inicializamos como válida la cadena
        fromState = "Q0";  // Estado inicial
        lexemes.setLength(0);  // Limpiar los lexemas
        transitions.setLength(0);  // Limpiar las transiciones
        transitionSet.clear();  // Limpiar el conjunto de transiciones
    }

    // Procesar múltiples líneas de código
    public boolean processMultipleLines(String input) {
        String[] lines = input.split("\n");  // Dividimos el input en líneas
        boolean allLinesValid = true;
        StringBuilder allLexemes = new StringBuilder();
        StringBuilder allTransitions = new StringBuilder();

        // Procesar cada línea por separado
        for (String line : lines) {
            reset();  // Reiniciar el autómata para cada línea
            if (!processLine(line.trim())) {  // Procesar la línea y verificar si es válida
                allLinesValid = false;  // Si alguna línea es inválida, marcamos la entrada como inválida
                System.out.println("Cadena inválida: " + line);
            }

            // Acumular los lexemas y transiciones de cada línea
            allLexemes.append(getLexemes());
            allTransitions.append(getTransitions());
        }

        // Actualizar los lexemas y transiciones con los resultados acumulados
        lexemes.setLength(0);
        lexemes.append(allLexemes);
        transitions.setLength(0);
        transitions.append(allTransitions);

        return allLinesValid;  // Retornar si todas las líneas fueron válidas
    }

    // Procesar una línea de código ingresada
    private boolean processLine(String line) {
        char[] chars = line.toCharArray();
        String variable = null;  // Almacena la variable que se va a declarar
        StringBuilder expression = new StringBuilder();  // Almacenar la expresión aritmética

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            // Manejamos los estados del autómata
            switch (currentState) {
                case 0: // Estado Q0: Detectar inicio de declaración "<<"
                    if (c == '<' && i + 1 < chars.length && chars[i + 1] == '<') {
                        toState = "Q1";
                        currentState = 1;  // Pasamos al estado Q1
                        i++;  // Saltar el segundo '<'
                        lexemes.append("<<\n");
                        saveTransition(fromState, toState, "<< detectado");
                        fromState = toState;  // Actualizar estado actual
                    } else {
                        isValid = false;  // Cadena no válida
                        return false;
                    }
                    break;

                case 1: // Estado Q1: Detectar el nombre de la variable
                    if (Character.isLetter(c)) {
                        if (variable == null) {
                            variable = String.valueOf(c);  // Almacenar el nombre de la variable
                        } else {
                            variable += c;  // Concatenar si es un nombre de variable largo
                        }
                        toState = "Q1";
                        lexemes.append(c).append("\n");
                        saveTransition(fromState, toState, "letra detectada: " + c);
                        fromState = toState;  // Actualizar estado actual
                    } else if (c == '→') {
                        toState = "Q2";
                        currentState = 2;  // Pasamos al estado Q2 (asignación)
                        lexemes.append("→\n");
                        saveTransition(fromState, toState, "→ detectado");
                        fromState = toState;  // Actualizar estado actual
                    } else {
                        isValid = false;  // Cadena no válida
                        return false;
                    }
                    break;

                case 2: // Estado Q2: Detectar expresión aritmética o caracteres válidos
                    if (Character.isDigit(c) || Character.isLetter(c) || isOperator(c) || isSeparator(c)) {
                        expression.append(c);  // Almacenar la expresión aritmética
                        toState = "Q2";
                        lexemes.append(c).append("\n");
                        saveTransition(fromState, toState, "carácter detectado: " + c);
                        fromState = toState;  // Actualizar estado actual
                    } else if (c == '>' && i + 1 < chars.length && chars[i + 1] == '>') {
                        toState = "Q3";
                        currentState = 3;  // Pasamos al estado Q3
                        i++;  // Saltar el segundo '>'
                        lexemes.append(">>\n");
                        saveTransition(fromState, toState, ">> detectado");

                        // Evaluar la expresión aritmética y asignar el valor a la variable
                        int value = evaluateExpression(expression.toString());
                        if (variable != null) {
                            symbolTable.put(variable, value);  // Guardar la variable en la tabla de símbolos
                        }

                        fromState = toState;  // Actualizar estado actual
                    } else {
                        isValid = false;  // Cadena no válida
                        return false;
                    }
                    break;

                case 3:  // Estado Q3: No debe haber más caracteres después de '>>'
                    isValid = false;
                    return false;
            }
        }

        return currentState == 3;  // La línea es válida si termina en el estado Q3
    }

    // Evaluar una expresión aritmética
    private int evaluateExpression(String expression) {
        try {
            // Reemplazar variables por sus valores
            expression = expression.replaceAll("\\s+", "");
            for (String variable : symbolTable.keySet()) {
                if (expression.contains(variable)) {
                    expression = expression.replace(variable, symbolTable.get(variable).toString());
                }
            }

            return evaluate(expression);  // Evaluar la expresión ya reemplazada
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Evaluar la expresión con precedencia de operadores usando stacks
    private int evaluate(String expression) {
        char[] tokens = expression.toCharArray();
        Stack<Integer> values = new Stack<>();  // Stack para almacenar números
        Stack<Character> ops = new Stack<>();  // Stack para almacenar operadores

        for (int i = 0; i < tokens.length; i++) {
            char c = tokens[i];
            if (c == ' ') {
                continue;
            }

            // Si el token es un número, lo leemos completo
            if (Character.isDigit(c)) {
                StringBuilder sbuf = new StringBuilder();
                while (i < tokens.length && Character.isDigit(tokens[i])) {
                    sbuf.append(tokens[i++]);
                }
                values.push(Integer.parseInt(sbuf.toString()));  // Almacenar el número
                i--;
            } else if (c == '(') {
                ops.push(c);  // Si es un paréntesis de apertura, lo almacenamos
            } else if (c == ')') {
                while (ops.peek() != '(') {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));  // Resolver la operación
                }
                ops.pop();  // Eliminar el paréntesis de apertura
            } else if (isOperator(c)) {
                while (!ops.isEmpty() && hasPrecedence(c, ops.peek())) {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.push(c);  // Almacenar el operador
            }
        }

        // Aplicar los operadores restantes
        while (!ops.isEmpty()) {
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));
        }

        return values.pop();  // Retornar el resultado final
    }

    // Aplicar un operador a dos operandos
    private int applyOp(char op, int b, int a) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new UnsupportedOperationException("División por cero");
                }
                return a / b;
        }
        return 0;
    }

    // Determinar si un operador tiene precedencia sobre otro
    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') {
            return false;
        }
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) {
            return false;
        }
        return true;
    }

    // Guardar las transiciones en la base de datos y evitar duplicados
    private void saveTransition(String fromState, String toState, String input) {
        String transitionKey = fromState + " -> " + toState + " (" + input + ")";

        if (!transitionSet.contains(transitionKey)) {
            transitionSet.add(transitionKey);
            transitions.append(transitionKey).append("\n");

            // Guardar la transición en la base de datos
            try (Connection conn = DBConnection.getConnection()) {
                String query = "INSERT INTO Transitions (from_state, to_state, input) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, fromState);
                    stmt.setString(2, toState);
                    stmt.setString(3, input);
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Obtener los lexemas generados
    public String getLexemes() {
        return lexemes.toString();
    }

    // Obtener las transiciones realizadas
    public String getTransitions() {
        return transitions.toString();
    }

    // Guardar los resultados (símbolos y lexemas) en la base de datos
    public void saveResults() {
        try (Connection conn = DBConnection.getConnection()) {
            // Guardar la tabla de símbolos
            for (String symbol : symbolTable.keySet()) {
                String query = "INSERT INTO Symbols (symbol, value) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, symbol);
                    stmt.setInt(2, symbolTable.get(symbol));
                    stmt.executeUpdate();
                }
            }

            // Guardar los lexemas
            String[] lexemeList = lexemes.toString().split("\n");
            for (String lexeme : lexemeList) {
                String query = "INSERT INTO Lexemes (lexeme, token) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, lexeme);
                    stmt.setString(2, "LEXEMA");
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Validar si el input ingresado no está vacío
    public boolean validate(String input) {
        return input != null && !input.trim().isEmpty();
    }

    // Verificar si un carácter es un separador
    private boolean isSeparator(char c) {
        return c == '(' || c == ')' || c == '{' || c == '}' || c == '[' || c == ']';
    }

    // Verificar si un carácter es un operador aritmético
    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    // Obtener los símbolos y sus valores como una cadena de texto
    public String getSymbols() {
        StringBuilder symbolsText = new StringBuilder();
        for (String symbol : symbolTable.keySet()) {
            symbolsText.append(symbol).append(" = ").append(symbolTable.get(symbol)).append("\n");
        }
        return symbolsText.toString();
    }
}
