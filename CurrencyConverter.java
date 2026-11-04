import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.Scanner;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurrencyConverter {

    private static String API_KEY;
    private static String BASE_URL;

    // Config dosyasından API key'i yükle
    private static boolean loadConfig() {
        try {
            Properties props = new Properties();
            FileInputStream fis = new FileInputStream("config.properties");
            props.load(fis);
            fis.close();
            API_KEY = props.getProperty("API_KEY");
            BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/USD";
            return API_KEY != null && !API_KEY.isEmpty();
        } catch (Exception e) {
            System.out.println("[Hata] config.properties dosyası bulunamadı!");
            System.out.println("Lütfen config.properties.example dosyasını config.properties olarak kopyalayın");
            System.out.println("ve kendi API anahtarınızı ekleyin.");
            return false;
        }
    }

    public static void main(String[] args) {
        // Önce config dosyasını yükle
        if (!loadConfig()) {
            return;
        }

        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("=======================================");
            System.out.println("   JAVA REAL-TIME CURRENCY CONVERTER   ");
            System.out.println("=======================================");

            // 1. API İsteği Oluşturma ve Yanıtı Alma
            System.out.println("[Bilgi] Güncel kurlar çekiliyor...");
            URL url = new URL(BASE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder jsonResponse = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonResponse.append(line);
                }
                reader.close();
                connection.disconnect();

                // 3. Kullanıcıdan Miktar Alma
                System.out.print("\nDönüştürmek istediğiniz USD miktarını girin: ");
                double usdAmount = parseAmount(scanner.nextLine());
                if (usdAmount < 0) {
                    System.out.println("[Hata] Geçerli, negatif olmayan bir miktar girin (ör. 12,50 veya 12.50).");
                    return;
                }

                // 4. JSON içinden TRY kurunu bulma (Basit manuel ayıklama)
                double tryRate = parseRate(jsonResponse.toString(), "TRY");

                // 5. Hesaplama ve Sonuç
                if (tryRate != -1) {
                    double convertedAmount = usdAmount * tryRate;
                    System.out.println("---------------------------------------");
                    System.out.printf("Tutar: %.2f USD\n", usdAmount);
                    System.out.printf("Güncel TRY Kuru: %.4f\n", tryRate);
                    System.out.printf("Sonuç: %.2f TRY\n", convertedAmount);
                    System.out.println("---------------------------------------");
                } else {
                    System.out.println("[Hata] TRY kuru verilerde bulunamadı.");
                }
            } else {
                connection.disconnect();
                System.out.println("[Hata] API bağlantısı başarısız. Durum kodu: " + responseCode);
            }

        } catch (Exception e) {
            System.out.println("[Hata] Bir sorun oluştu: " + e.getMessage());
        } finally {
            scanner.close();
        }
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
