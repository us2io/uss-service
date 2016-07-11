package io.us2.svc.seed.services;

import lombok.Value;
import org.apache.commons.io.FileUtils;
import play.Environment;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class QueryStore {
    private final boolean isTest;
    private final Map<String, Query> queries;
    private final Map<String, Query> testQueries;

    @Inject
    public QueryStore(Environment env) throws IOException, URISyntaxException {
        this.isTest = env.isTest();
        this.queries = loadQueries("sql");

        if (isTest) {
            this.testQueries = loadQueries("sql/test");
        } else {
            this.testQueries = Collections.emptyMap();
        }
    }

    public String getQuery(String name, boolean up) {
        if (isTest && testQueries.containsKey(name)) {
            Query query = testQueries.get(name);
            return up ? query.getUp() : query.getDown();
        } else if(queries.containsKey(name)) {
            Query query = queries.get(name);
            return up ? query.getUp() : query.getDown();
        } else {
            return null;
        }
    }

    public String getQuery(String name) {
        return getQuery(name, true);
    }

    private String fileToName(File file) {
        return file.getName().replace(".sql", "");
    }

    private Map<String, Query> loadQueries(String location) throws IOException, URISyntaxException {
        URL resource = QueryStore.class.getClassLoader().getResource(location);

        if (resource == null) {
            throw new RuntimeException("Unable to find SQL location " + location);
        }

        File[] files = new File(resource.toURI()).listFiles();

        if (files == null) {
            return Collections.emptyMap();
        }

        return Stream.of(files)
                .filter(file -> file.getName().endsWith(".sql"))
                .collect(Collectors.toMap(this::fileToName, this::readQuery));
    }

    private Query readQuery(File file) {
        try {
            String contents = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

            // TODO: Try to split into UP and DOWN command;
            return new Query(contents, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Value
    static class Query {
        String up;
        String down;
    }
}
