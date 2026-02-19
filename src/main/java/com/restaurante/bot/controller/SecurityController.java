package com.restaurante.bot.controller;

import com.restaurante.bot.business.interfaces.ShortLinkService;
import com.restaurante.bot.dto.GenerateLinkIn;
import com.restaurante.bot.dto.GenerateTokenRequestDTO;
import com.restaurante.bot.repository.CompanyRepository;
import com.restaurante.bot.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/${app.request.mapping}/security")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
@Slf4j
public class SecurityController {

    private final JwtUtil jwtUtil;

    private final CompanyRepository companyRepository;

    private final ShortLinkService shortLinkservice;

    @Value("${landing.page.url}")
    private String landingPageUrl;

    @Value("${app.request.mapping}")
    private String mappingPageUrl;


    @PostMapping("/generateToken")
    public ResponseEntity<String> generateToken(@RequestBody GenerateTokenRequestDTO generateTokenRequestDTO) {
        log.info("Se inicia el endpoint que genera un token");
        if (!companyRepository.existsByExternalCompanyId(generateTokenRequestDTO.getCompanyId())) {
            return new ResponseEntity<>("Compañía no existe", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(jwtUtil.generateToken(generateTokenRequestDTO.getCompanyId(), generateTokenRequestDTO.getCompanyId() ), HttpStatus.OK);

    }

    @PostMapping("/generateLink")
    public ResponseEntity<String> generateLink(@RequestBody GenerateLinkIn generateLinkIn
    ) {
        Map<String, String> queryParams = new HashMap<>();

        if (!companyRepository.existsByExternalCompanyId(generateLinkIn.getCompanyId())) {
            return new ResponseEntity<>("Compañía no existe", HttpStatus.NOT_FOUND);
        }

        String token = jwtUtil.generateToken(generateLinkIn.getCompanyId(), generateLinkIn.getUserId());

        queryParams.put("token", token);
        queryParams.put("companyId", String.valueOf(generateLinkIn.getCompanyId()));
        queryParams.put("userToken", generateLinkIn.getUserToken());
        queryParams.put("mesa", generateLinkIn.getMesa());

        if (generateLinkIn.getQr() != null && !generateLinkIn.getQr().isEmpty()) {
            queryParams.put("qr", generateLinkIn.getQr());
        }

        if (generateLinkIn.getDelivery() != null && !generateLinkIn.getDelivery().isEmpty()) {
            queryParams.put("Delivery", generateLinkIn.getDelivery());
        }

        String fullLink = buildUrl(landingPageUrl, queryParams);
        var shortLink = shortLinkservice.createShortLink(fullLink);
        String shortUrl = landingPageUrl + "/" + mappingPageUrl + "/h/" + shortLink.getShortCode();
        return new ResponseEntity<>(shortUrl, HttpStatus.OK);
    }

    private String buildUrl(String baseUrl, Map<String, String> params) {
        StringBuilder url = new StringBuilder(baseUrl);

        if (params != null && !params.isEmpty()) {
            url.append("?");
            params.forEach((key, value) -> {
                if (url.charAt(url.length() - 1) != '?') {
                    url.append("&");
                }
                url.append(key);
                url.append("=");
                // Si es el token, no lo encodees
                if (key.equals("token")) {
                    url.append(value);
                } else {
                    url.append(URLEncoder.encode(value, StandardCharsets.UTF_8));
                }
            });
        }

        return url.toString();
    }
}
