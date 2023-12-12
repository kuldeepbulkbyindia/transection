package com.bbi.transactionsp2.serviceImpl;

import com.bbi.transactionsp2.dto.Keyword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KeywordsService {
    private final RestTemplate restTemplate;

    private final String keywordsApiUrl = "https://jboi3auod0.execute-api.ap-south-1.amazonaws.com/bulkbuy/keywords/{companyId}";

    @Autowired
    public KeywordsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<String> getKeywordsByCompanyId(long companyId) {
        // Call the API to get keywords for the given companyId
        ResponseEntity<List<Keyword>> responseEntity = restTemplate.exchange(
                keywordsApiUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Keyword>>() {},
                companyId
        );

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            List<Keyword> keywords = responseEntity.getBody();
            return keywords.stream()
                    .map(Keyword::getKeywordName)
                    .collect(Collectors.toList());
        } else {
            // Handle the case when the API call fails
            return Collections.emptyList();
        }
    }
}
