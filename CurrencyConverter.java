import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.Scanner;
import java.util.Properties;

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
                double usdAmount = scanner.nextDouble();

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

    // JSON içindeki belirli bir kur değerini bulmak için yardımcı metod
    private static double parseRate(String json, String currency) {
        try {
            String searchPattern = "\"" + currency + "\":";
            int startIndex = json.indexOf(searchPattern) + searchPattern.length();
            int endIndex = json.indexOf(",", startIndex);
            if (endIndex == -1) {
                endIndex = json.indexOf("}", startIndex);
            }
            return Double.parseDouble(json.substring(startIndex, endIndex).trim());
        } catch (Exception e) {
            return -1;
        }
    }
}