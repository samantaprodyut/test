package com.iimj.resultportal.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class CaptchaService {

    @Value("${recaptcha.secret.key}")
    private String secretKey;

    public boolean verifyCaptcha(String captchaResponse) {
        String url = "https://www.google.com/recaptcha/api/siteverify";

        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("secret", secretKey);
        request.add("response", captchaResponse);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if (response.getBody() == null) {
            return false;
        }

        Object success = response.getBody().get("success");
        return success instanceof Boolean && (Boolean) success;
    }
}