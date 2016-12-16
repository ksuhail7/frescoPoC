package com.suhailkandanur.fresco.restcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suhailkandanur.fresco.dataaccess.FrescoRepoRepository;
import com.suhailkandanur.fresco.entity.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by suhail on 2016-12-01.
 */
@RestController
@CrossOrigin
@Component(value = "frescoRepoController")
public class RepositoryController {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryController.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private FrescoRepoRepository repoRepository;

    @PostMapping(value = "/repository", consumes = "application/json")
    public Repository createRepository(@RequestBody Map<String, String> params) throws Exception {
        String name = params.get("name");
        String description = params.get("description");
        long quota = Long.valueOf(params.getOrDefault("quota", "100000000"));
        if(name == null || "".equals(name)) {
            logger.error("repository name should not be null/empty string");
            return null;
        }
        logger.info("received repository creation request [name: {}, description: {}, quota: {}", name, description, quota);
        String token = UUID.randomUUID().toString();
        Repository repo = new Repository();
        repo.setName(name);
        repo.setDescription(description);
        repo.setToken(token);
        String requestJson = objectMapper.writeValueAsString(repo);
        rabbitTemplate.convertAndSend("fresco", "repository", requestJson);
        return repo;
    }


    @GetMapping(value="/repository")
    public List<Repository> getRepositoriesList() {
        return repoRepository.findAll();
    }

    @GetMapping(value="/repository/{id}")
    public Repository getRepositoryDetailsById(@PathVariable String id) {
        return repoRepository.findOne(id);
    }

    @GetMapping(value = "/repository/token/{token}")
    public Repository getRepositoryDetailsByToken(@PathVariable String token) {
        return repoRepository.findFrescoRepoByToken(token);
    }

    @PutMapping(value = "/repository", consumes = "application/json")
    public Repository updateRepository(@RequestBody Map<String, String> params, HttpServletResponse response) throws IOException {
        throw new UnsupportedOperationException("repository update currently not supported");
    }

}
