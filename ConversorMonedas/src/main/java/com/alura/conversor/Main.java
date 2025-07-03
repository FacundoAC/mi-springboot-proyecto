package com.alura.conversor;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CurrencyConverter conversor = new CurrencyConverter();

        int opcion;
        do {
            System.out.println("\n=== Conversor de Monedas ===");
            System.out.println("1. Dólar a Peso Argentino");
            System.out.println("2. Peso Argentino a Dólar");
            System.out.println("3. Real Brasileño a Dólar");
            System.out.println("4. Salir");
            System.out.print("Selecciona una opción: ");
            opcion = scanner.nextInt();

            if (opcion >= 1 && opcion <= 3) {
                System.out.print("Ingresa el monto a convertir: ");
                double monto = scanner.nextDouble();

                String base = "", destino = "";
                switch (opcion) {
                    case 1: base = "USD"; destino = "ARS"; break;
                    case 2: base = "ARS"; destino = "USD"; break;
                    case 3: base = "BRL"; destino = "USD"; break;
                }

                double resultado = conversor.convertir(base, destino, monto);
                System.out.printf("Resultado: %.2f %s = %.2f %s%n", monto, base, resultado, destino);
            }

        } while (opcion != 4);

        System.out.println("Gracias por usar el conversor. ¡Hasta luego!");
        scanner.close();
    }
}
