// JUnit gerektirmeyen basit testler: javac *.java && java CurrencyConverterTest
public class CurrencyConverterTest {

    private static int failures = 0;

    private static void check(String name, boolean condition) {
        if (condition) {
            System.out.println("OK   " + name);
        } else {
            System.out.println("FAIL " + name);
            failures++;
        }
    }

    private static boolean near(double actual, double expected) {
        return Math.abs(actual - expected) < 1e-9;
    }

    public static void main(String[] args) {
        String json = "{\"result\": \"success\", \"base_code\": \"USD\", \"conversion_rates\": "
            + "{\"USD\": 1, \"EUR\": 0.9215, \"TRY\": 41.2873, \"JPY\": 1.5e2}}";

        check("parseRate reads a rate", near(CurrencyConverter.parseRate(json, "TRY"), 41.2873));
        check("parseRate reads an integer rate", near(CurrencyConverter.parseRate(json, "USD"), 1));
        check("parseRate reads exponent notation", near(CurrencyConverter.parseRate(json, "JPY"), 150));
        check("parseRate reads compact JSON", near(CurrencyConverter.parseRate("{\"EUR\":0.92}", "EUR"), 0.92));
        check("parseRate returns -1 for a missing code", CurrencyConverter.parseRate(json, "GBP") == -1);
        check("parseRate does not read text fields", CurrencyConverter.parseRate(json, "base_code") == -1);

        check("parseAmount accepts a dot", near(CurrencyConverter.parseAmount("12.50"), 12.5));
        check("parseAmount accepts a comma", near(CurrencyConverter.parseAmount(" 12,50 "), 12.5));
        check("parseAmount rejects text", CurrencyConverter.parseAmount("on iki") == -1);
        check("parseAmount rejects negatives", CurrencyConverter.parseAmount("-5") == -1);
        check("parseAmount rejects empty input", CurrencyConverter.parseAmount("") == -1);

        check("parseCurrency upper-cases", "EUR".equals(CurrencyConverter.parseCurrency(" eur ")));
        check("parseCurrency rejects long codes", CurrencyConverter.parseCurrency("EURO") == null);
        check("parseCurrency rejects digits", CurrencyConverter.parseCurrency("US1") == null);

        check("convert multiplies by the rate", near(CurrencyConverter.convert(10, 41.2873), 412.873));

        if (failures > 0) {
            System.out.println(failures + " test(s) failed");
            System.exit(1);
        }
        System.out.println("All tests passed");
    }
}
