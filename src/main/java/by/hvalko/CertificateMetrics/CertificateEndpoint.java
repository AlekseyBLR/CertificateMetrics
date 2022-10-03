package by.hvalko.CertificateMetrics;

import by.hvalko.CertificateMetrics.Model.CertificateInfo;
import by.hvalko.CertificateMetrics.Service.CertificateService;
import io.micrometer.core.instrument.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Endpoint(id="certificates")
@Component
public class CertificateEndpoint {
    private static final Map<String, CertificateInfo> GAUGE_CACHE = new HashMap<>();
    @Autowired
    private MeterRegistry meterRegistry;
    @Autowired
    private CertificateService service;

    @SneakyThrows
    @PostConstruct
    public void initGauge() {
        List<CertificateInfo> certificateInfoList = service.getCertificates();

        for (CertificateInfo certificate: certificateInfoList) {
            GAUGE_CACHE.put(certificate.getName(), certificate);
            Gauge.builder("certificate", certificate, CertificateInfo::getDuration)
                    .description("Certificates name and expiration date per day")
                    .tags("name", certificate.getName())
                    .register(meterRegistry);
        }
    }

    @ReadOperation
    public List<CertificateInfo> getCertificates() {
        return service.getCertificates();
    }
}
