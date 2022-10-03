package by.hvalko.CertificateMetrics.Service;

import by.hvalko.CertificateMetrics.Model.CertificateInfo;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:AppConsts.properties")
public class CertificateService {

    @Value("${KEYSTORE_TYPE}")
    private String KEYSTORE_TYPE;
    @Value("${KEYSTORE_PASSWORD}")
    private String KEYSTORE_PASSWORD;

    @Value("${KEYSTORE_PASSWORD}")
    private String KEYSTORE_PATH;

    @SneakyThrows
    public List<CertificateInfo> getCertificates() {
        String certificateName = "";
        X509Certificate certificate;
        List<CertificateInfo> list = new ArrayList<>();

        KeyStore keyStore = KeyStore.getInstance("pkcs12");
        char[] pwdArray = KEYSTORE_PASSWORD.toCharArray();
        keyStore.load(new FileInputStream("C:/Program Files/Java/jdk-11.0.13/lib/security/cacerts"), pwdArray);

        Enumeration<String> aliases = keyStore.aliases();

        while(aliases.hasMoreElements()){
            certificateName = aliases.nextElement();
            certificate = (X509Certificate) keyStore.getCertificate(certificateName);

            CertificateInfo certificateInfo = new CertificateInfo(certificateName, getDuration(certificate.getNotBefore(), certificate.getNotAfter()));
            list.add(certificateInfo);
        }
        return list;
    }

    private int getDuration(Date dateFrom, Date dateTo) {
        return (int) ((dateTo.getTime() - dateFrom.getTime()) / (24 * 60 * 60 * 1000));
    }
}
