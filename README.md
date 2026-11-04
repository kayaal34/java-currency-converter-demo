# 💱 Java Real-Time Currency Converter Demo

Gerçek zamanlı döviz kurlarını çeken basit bir Java konsol uygulaması. API kullanarak USD -> TRY dönüşümü yapar.

## ✨ Özellikler
- ✅ Gerçek zamanlı döviz kurları (ExchangeRate-API)
- ✅ İstediğiniz iki para birimi arasında dönüşüm (varsayılan USD -> TRY)
- ✅ Miktarı virgülle veya noktayla girebilme (12,50 / 12.50)
- ✅ Basit JSON parsing
- ✅ Kullanıcı dostu konsol arayüzü
- ✅ Java 11+ uyumlu

## 🚀 Nasıl Çalıştırılır?

### 1. API Key Ayarları
```bash
# config.properties.example dosyasını kopyalayın
cp config.properties.example config.properties

# config.properties dosyasına kendi API anahtarınızı ekleyin
# API anahtarı için: https://www.exchangerate-api.com/

# Ya da dosya yerine ortam değişkeni kullanın
export EXCHANGE_RATE_API_KEY=anahtariniz
```

### 2. Derleme ve Çalıştırma
```bash
# Derleme
javac CurrencyConverter.java

# Çalıştırma
java CurrencyConverter
```

### 3. Testler
```bash
javac -encoding UTF-8 *.java
java CurrencyConverterTest
```

## 📋 Gereksinimler
- Java JDK 11 veya üzeri
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
Kaynak para birimi [USD]:
Hedef para birimi [TRY]:
Dönüştürmek istediğiniz USD miktarını girin: 100
[Bilgi] Güncel kurlar çekiliyor...
---------------------------------------
Tutar: 100,00 USD
Güncel USD/TRY Kuru: 43,1543
Sonuç: 4315,43 TRY
---------------------------------------
```

## 📝 Not
Bu proje eğitim amaçlıdır ve temel Java API kullanımını göstermektedir.