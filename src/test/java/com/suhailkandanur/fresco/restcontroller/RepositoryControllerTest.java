package com.suhailkandanur.fresco.restcontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by suhail on 2016-12-01.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RepositoryControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryControllerTest.class);

    @Autowired
    private TestRestTemplate restTemplate;

    private static final HttpHeaders jsonServiceHeaders = new HttpHeaders();

    private static final HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeClass
    public static void initialize() {
        jsonServiceHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    @Before
    public void setUp() {
        logger.info("Setting up tests");
        this.restTemplate.getRestTemplate().getMessageConverters().add(jackson2HttpMessageConverter);
    }

    @Test
    public void testRepositoryCreation() throws JsonProcessingException {
        Map<String, String> request = new HashMap<>();
        request.put("name", "testRepository");
        request.put("description", "test repository");
        String requestJson = objectMapper.writeValueAsString(request);
        HttpEntity<String> entity = new HttpEntity<String>(requestJson, jsonServiceHeaders);
        String token = this.restTemplate.postForObject("/repository", entity, String.class);
        logger.info("response: {}", token);
        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    public void testRepositoryCreationFailure() throws JsonProcessingException {
        Map<String, String> params = new HashMap<>();
        params.put("name", null);
        params.put("description", "test repository");
        String requestJson = objectMapper.writeValueAsString(params);
        HttpEntity<String> stringHttpEntity = new HttpEntity<>(requestJson, jsonServiceHeaders);
        String response = this.restTemplate.postForObject("/repository", stringHttpEntity, String.class);
        //logger.info("response: {}", response);
        assertThat(response).isEqualTo(null);
    }
}
