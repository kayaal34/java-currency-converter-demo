# 💱 Java Real-Time Currency Converter Demo

Gerçek zamanlı döviz kurlarını çeken basit bir Java konsol uygulaması. API kullanarak USD -> TRY dönüşümü yapar.

## ✨ Özellikler
- ✅ Gerçek zamanlı döviz kurları (ExchangeRate-API)
- ✅ Basit JSON parsing
- ✅ Kullanıcı dostu konsol arayüzü
- ✅ Java 8+ uyumlu

## 🚀 Nasıl Çalıştırılır?

```bash
# Derleme
javac CurrencyConverter.java

# Çalıştırma
java CurrencyConverter
```

## 📋 Gereksinimler
- Java JDK 8 veya üzeri
- İnternet bağlantısı (API erişimi için)

## 🛠️ Kullanılan Teknolojiler
- Java Standard Edition
- HttpURLConnection
- ExchangeRate-API v6

## 📸 Örnek Çıktı
```
=======================================
   JAVA REAL-TIME CURRENCY CONVERTER   
=======================================
[Bilgi] Güncel kurlar çekiliyor...

Dönüştürmek istediğiniz USD miktarını girin: 100
---------------------------------------
Tutar: 100,00 USD
Güncel TRY Kuru: 43,1543
Sonuç: 4315,43 TRY
---------------------------------------
```

## 📝 Not
Bu proje eğitim amaçlıdır ve temel Java API kullanımını göstermektedir.