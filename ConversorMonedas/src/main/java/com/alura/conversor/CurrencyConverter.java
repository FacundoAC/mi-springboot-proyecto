package com.alura.conversor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class CurrencyConverter {
    private static final String API_KEY = "226d005fc24860cdd62f6fe1";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    public double convertir(String monedaBase, String monedaDestino, double cantidad) {
        try {
            String urlStr = BASE_URL + monedaBase;
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder resultado = new StringBuilder();
            String linea;
            while ((linea = reader.readLine()) != null) {
                resultado.append(linea);
            }
            reader.close();

            JSONObject json = new JSONObject(resultado.toString());
            JSONObject conversionRates = json.getJSONObject("conversion_rates");

            double tasa = conversionRates.getDouble(monedaDestino);
            return cantidad * tasa;

        } catch (Exception e) {
            System.out.println("Error al conectar con la API: " + e.getMessage());
            return 0;
        }
    }
}
