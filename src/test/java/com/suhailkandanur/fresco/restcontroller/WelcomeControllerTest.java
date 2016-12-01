package com.suhailkandanur.fresco.restcontroller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by suhail on 2016-12-01.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class WelcomeControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testWelcomeEndpoint() {
        String body = this.restTemplate.getForObject("/welcome", String.class);
        assertThat(body).isEqualTo("Welcome to Fresco");
    }
}
