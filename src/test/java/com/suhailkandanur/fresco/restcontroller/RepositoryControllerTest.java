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

    @Before
    public void setUp() {

    }

    @Test
    public void testRepositoryCreation() throws JsonProcessingException {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "testRepository");
        params.put("description", "test repository");
        params.put("quota", 10000L);
        String request = new ObjectMapper().writeValueAsString(params);
        logger.info("request: {}", request);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(request,headers);
        String token = this.restTemplate.postForObject("/repository", entity, String.class);
        assertThat(token).isNotNull().isNotEmpty();
    }
}
