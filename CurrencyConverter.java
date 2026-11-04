import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.Locale;
import java.util.Scanner;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurrencyConverter {

    private static final String API_URL = "https://v6.exchangerate-api.com/v6/%s/latest/%s";
    private static final String KEY_ENV = "EXCHANGE_RATE_API_KEY";

    // API key'i önce ortam değişkeninden, yoksa config dosyasından yükle
    static String loadApiKey() {
        String fromEnv = System.getenv(KEY_ENV);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv.trim();
        }
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            Properties props = new Properties();
            props.load(fis);
            String key = props.getProperty("API_KEY");
            return key == null || key.isBlank() ? null : key.trim();
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        String apiKey = loadApiKey();
        if (apiKey == null) {
            System.out.println("[Hata] API anahtarı bulunamadı!");
            System.out.println("config.properties.example dosyasını config.properties olarak kopyalayıp anahtarınızı ekleyin");
            System.out.println("veya " + KEY_ENV + " ortam değişkenini ayarlayın.");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("=======================================");
            System.out.println("   JAVA REAL-TIME CURRENCY CONVERTER   ");
            System.out.println("=======================================");

            // 1. Kullanıcıdan para birimlerini ve miktarı alma (boş bırakılırsa USD -> TRY)
            String from = readCurrency(scanner, "Kaynak para birimi", "USD");
            String to = readCurrency(scanner, "Hedef para birimi", "TRY");
            if (from == null || to == null) {
                System.out.println("[Hata] Para birimi 3 harfli bir kod olmalı (ör. USD, EUR, TRY).");
                return;
            }

            System.out.print("Dönüştürmek istediğiniz " + from + " miktarını girin: ");
            double amount = parseAmount(scanner.nextLine());
            if (amount < 0) {
                System.out.println("[Hata] Geçerli, negatif olmayan bir miktar girin (ör. 12,50 veya 12.50).");
                return;
            }

            // 2. API İsteği Oluşturma ve Yanıtı Alma
            System.out.println("[Bilgi] Güncel kurlar çekiliyor...");
            URL url = URI.create(String.format(API_URL, apiKey, from)).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                connection.disconnect();
                System.out.println("[Hata] API bağlantısı başarısız. Durum kodu: " + responseCode);
                return;
            }

            StringBuilder jsonResponse = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonResponse.append(line);
                }
            } finally {
                connection.disconnect();
            }

            // 3. JSON içinden hedef kuru bulma
            double rate = parseRate(jsonResponse.toString(), to);

            // 4. Hesaplama ve Sonuç
            if (rate != -1) {
                System.out.println("---------------------------------------");
                System.out.printf("Tutar: %.2f %s%n", amount, from);
                System.out.printf("Güncel %s/%s Kuru: %.4f%n", from, to, rate);
                System.out.printf("Sonuç: %.2f %s%n", convert(amount, rate), to);
                System.out.println("---------------------------------------");
            } else {
                System.out.println("[Hata] " + to + " kuru verilerde bulunamadı.");
            }

        } catch (Exception e) {
            System.out.println("[Hata] Bir sorun oluştu: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static String readCurrency(Scanner scanner, String label, String fallback) {
        System.out.print(label + " [" + fallback + "]: ");
        String input = scanner.nextLine().trim();
        return parseCurrency(input.isEmpty() ? fallback : input);
    }

    // "usd" gibi girişleri "USD" yapar; 3 harfli kod değilse null döner
    static String parseCurrency(String input) {
        if (input == null) {
            return null;
        }
        String code = input.trim().toUpperCase(Locale.ROOT);
        return code.matches("[A-Z]{3}") ? code : null;
    }

    static double convert(double amount, double rate) {
        return amount * rate;
    }

    // Kullanıcının girdiği miktarı okur; Türkçe sistemlerde yazılan virgülü de kabul eder.
    // Geçersiz veya negatif girişte -1 döner.
    static double parseAmount(String input) {
        if (input == null) {
            return -1;
        }
        try {
            double amount = Double.parseDouble(input.trim().replace(',', '.'));
            if (amount < 0 || Double.isNaN(amount) || Double.isInfinite(amount)) {
                return -1;
            }
            return amount;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // JSON içindeki belirli bir kur değerini bulmak için yardımcı metod.
    // Kur yoksa -1 döner; eskiden başka bir alanın değeri okunabiliyordu.
    static double parseRate(String json, String currency) {
        Pattern pattern = Pattern.compile(
            "\"" + Pattern.quote(currency) + "\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?(?:[eE][+-]?[0-9]+)?)");
        Matcher matcher = pattern.matcher(json);
        if (!matcher.find()) {
            return -1;
        }
        return Double.parseDouble(matcher.group(1));
    }
}
