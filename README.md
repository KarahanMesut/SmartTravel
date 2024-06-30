# SmartTravel-Kotlin
SmartTravel, seyahat planlarınızı yönetmenizi sağlayan bir mobil uygulamadır. Kullanıcıların seyahat planları oluşturmasına, güncellemesine, silmesine ve yaklaşan seyahatleri hakkında bildirim almasına olanak tanı

# Özellikler
Kullanıcı Kaydı ve Girişi: Firebase Authentication ile kullanıcı kaydı ve girişi.
Seyahat Planı Yönetimi: Seyahat planları ekleme, güncelleme ve silme.
Harita Entegrasyonu: Google Maps API ile seyahat yerlerini harita üzerinde görüntüleme.
Yaklaşan Seyahat Bildirimleri: Seyahat tarihine yaklaşan kullanıcılara bildirim gönderme.
Veritabanı Yönetimi: Firebase Firestore ve Room kullanarak seyahat verilerini saklama.
UI/UX İyileştirmeleri: Kullanıcı dostu ve estetik arayüz tasarımı.
Çoklu Dil Desteği: Farklı dillerde kullanım imkanı.
Ekran Görüntüleri


# Kurulum
Gereksinimler
Android Studio (Arctic Fox veya daha yeni bir sürüm)
Bir Firebase projesi ve API anahtarı
Google Maps API anahtarı
Adımlar
1. Bu projeyi klonlayın: git clone https://github.com/kullaniciadi/SmartTravel.git
cd SmartTravel
2. Firebase projenizi oluşturun ve google-services.json dosyasını app/ klasörüne ekleyin.
3. Google Maps API anahtarınızı AndroidManifest.xml dosyasına ekleyin: <meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_API_KEY_HERE" />
4. Gerekli bağımlılıkları yükleyin:./gradlew build


5. Uygulamayı başlatın:

6. Kullanım

 Uygulamayı açın ve bir hesap oluşturun veya giriş yapın.
Ana ekranda yeni bir seyahat planı ekleyin.
Seyahat planlarınızı görüntüleyin ve yönetmek için detaylara tıklayın.
Yaklaşan seyahatleriniz için bildirim alın.
Katkıda Bulunma
Katkıda bulunmak isterseniz, lütfen aşağıdaki adımları izleyin:

# Bu projeyi forklayın.
Yeni bir dal (feature-branch) oluşturun.
Değişikliklerinizi yapın ve commit edin.
Dalınıza push edin (git push origin feature-branch).
Bir pull request açın.
Daha fazla bilgi için CONTRIBUTING.md dosyasına göz atabilirsiniz.

Lisans
Bu proje "Tüm Hakları Saklıdır" lisansı altında yayımlanmıştır. Projeyi inceleyebilirsiniz, ancak izinsiz kopyalayamaz, dağıtamaz veya kullanamazsınız. Daha fazla bilgi için lütfen rainsguard@gmail.com adresinden iletişime geçin.

# Sürüm Geçmişi
v1.0.0
İlk sürüm
Kullanıcı kaydı ve girişi
Seyahat planı ekleme, güncelleme ve silme
Harita entegrasyonu
Yaklaşan seyahat bildirimleri
İletişim
Sorularınız veya geri bildiriminiz için lütfen rainsguard@gmail.com adresinden bizimle iletişime geçin.

