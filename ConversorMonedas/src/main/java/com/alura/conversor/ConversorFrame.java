package com.alura.conversor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConversorFrame extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField campoMonto;
    private JComboBox<String> comboOrigen;
    private JComboBox<String> comboDestino;
    private JLabel resultadoLabel;

    public ConversorFrame() {
        setTitle("Conversor de Monedas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);

        String[] monedas = { "USD", "ARS", "BRL", "EUR", "CLP", "PEN" };

        comboOrigen = new JComboBox<>(monedas);
        comboDestino = new JComboBox<>(monedas);
        campoMonto = new JTextField(10);
        JButton botonConvertir = new JButton("Convertir");
        resultadoLabel = new JLabel("Resultado: ");

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Moneda origen:"));
        panel.add(comboOrigen);
        panel.add(new JLabel("Moneda destino:"));
        panel.add(comboDestino);
        panel.add(new JLabel("Monto a convertir:"));
        panel.add(campoMonto);
        panel.add(new JLabel(""));
        panel.add(botonConvertir);
        panel.add(new JLabel(""));
        panel.add(resultadoLabel);

        add(panel);

        botonConvertir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String origen = (String) comboOrigen.getSelectedItem();
                    String destino = (String) comboDestino.getSelectedItem();
                    double monto = Double.parseDouble(campoMonto.getText());

                    CurrencyConverter conversor = new CurrencyConverter();
                    double resultado = conversor.convertir(origen, destino, monto);

                    resultadoLabel.setText(String.format("Resultado: %.2f %s", resultado, destino));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Monto inválido. Ingrese un número válido.");
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ConversorFrame().setVisible(true);
        });
    }
}
