/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author egiron
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {

    // Componentes de la interfaz gráfica
    private JTextArea inputArea;        // Área de texto para ingresar el código a analizar
    private JTextArea lexemesArea;      // Área de texto para mostrar los lexemas
    private JTextArea transitionsArea;  // Área de texto para mostrar las transiciones
    private JTextArea symbolsArea;      // Área de texto para mostrar los símbolos (variables y sus valores)
    private JLabel resultLabel;         // Etiqueta para mostrar el resultado (cadena válida/inválida)
    private JButton analyzeButton;      // Botón para iniciar el análisis del código
    private Automata automata;          // Instancia de la clase Automata para procesar el código

    // Constructor para configurar y construir la interfaz gráfica
    public Main() {
        // Crear la ventana principal (JFrame)
        JFrame frame = new JFrame("Autómata");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Cerrar la ventana al salir
        frame.setSize(1100, 700);  // Ajustar el tamaño de la ventana

        // Crear las áreas de texto
        inputArea = new JTextArea(10, 70);        // Área de entrada para el código (10 filas, 70 columnas)
        lexemesArea = new JTextArea(10, 70);      // Área para mostrar los lexemas
        transitionsArea = new JTextArea(10, 70);  // Área para mostrar las transiciones
        symbolsArea = new JTextArea(10, 70);      // Nueva área para mostrar los símbolos (variables)
        resultLabel = new JLabel();               // Área para mostrar el resultado de las variables

        // Hacer que las áreas de lexemas, transiciones y símbolos sean solo de lectura
        lexemesArea.setEditable(false);
        transitionsArea.setEditable(false);
        symbolsArea.setEditable(false);

        // Crear el botón de analizar
        analyzeButton = new JButton("Analizar");

        // Crear una nueva instancia de Automata para procesar el código
        automata = new Automata();

        // Acción que ocurre cuando el botón "Analizar" es presionado
        analyzeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                analyzeInput();  // Llamamos al método que analizará el código ingresado
            }
        });

        // Crear el panel principal y configurar su disposición (BoxLayout en dirección vertical)
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));  // Disposición vertical

        // Añadir todos los componentes a la interfaz gráfica
        panel.add(new JLabel("Ingrese su código:"));       // Etiqueta para el área de entrada del código
        panel.add(new JScrollPane(inputArea));             // Área de entrada del código con barra de desplazamiento
        panel.add(analyzeButton);                          // Botón de analizar
        panel.add(new JLabel("Lexemas:"));                 // Etiqueta para el área de lexemas
        panel.add(new JScrollPane(lexemesArea));           // Área para mostrar los lexemas con barra de desplazamiento
        panel.add(new JLabel("Transiciones:"));            // Etiqueta para el área de transiciones
        panel.add(new JScrollPane(transitionsArea));       // Área para mostrar las transiciones con barra de desplazamiento
        panel.add(new JLabel("Variables:"));               // Etiqueta para el área de símbolos (variables)
        panel.add(new JScrollPane(symbolsArea));           // Área para mostrar los símbolos con barra de desplazamiento
        panel.add(resultLabel);                            // Etiqueta para mostrar el resultado (cadena válida/inválida)

        // Añadir el panel a la ventana principal
        frame.add(panel);

        // Hacer visible la ventana
        frame.setVisible(true);
    }

    // Método que se ejecuta cuando el botón "Analizar" es presionado
    private void analyzeInput() {
        // Limpiar las áreas de texto antes de mostrar nuevos resultados
        lexemesArea.setText("");
        transitionsArea.setText("");
        symbolsArea.setText("");
        resultLabel.setText("");

        // Obtener el código ingresado en el área de entrada
        String input = inputArea.getText();

        // Validar y procesar múltiples líneas de código con el autómata
        boolean isAccepted = automata.processMultipleLines(input);

        // Si la cadena es aceptada por el autómata
        if (isAccepted) {
            // Mostrar los lexemas, transiciones y símbolos en sus respectivas áreas de texto
            lexemesArea.setText(automata.getLexemes());
            transitionsArea.setText(automata.getTransitions());
            symbolsArea.setText(automata.getSymbols());
            resultLabel.setText("Cadena válida.");

            // Intentar guardar los resultados en la base de datos
            try {
                automata.saveResults();  // Guardar los resultados en la base de datos
            } catch (Exception e) {
                e.printStackTrace();  // Mostrar errores en la consola si ocurre alguna excepción
            }
        } else {  // Si la cadena no es aceptada por el autómata
            // Mostrar los lexemas, transiciones y símbolos, aunque la cadena sea inválida
            lexemesArea.setText(automata.getLexemes());
            transitionsArea.setText(automata.getTransitions());
            symbolsArea.setText(automata.getSymbols());
            resultLabel.setText("Cadena inválida.");
        }
    }

    // Método principal: punto de entrada del programa
    public static void main(String[] args) {
        new Main();  // Crear una instancia de la clase Main para mostrar la interfaz
    }
}
