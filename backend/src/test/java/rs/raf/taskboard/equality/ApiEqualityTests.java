package rs.raf.taskboard.equality;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.NullNode;
import tools.jackson.databind.node.ObjectNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Za isti scenario REST i GraphQL moraju da vrate iste podatke.
 * GraphQL vraća stablo jednim upitom; za REST test radi pozive kao frontend i
 * od odgovora slaže isto stablo. Potklase pokreću iste testove za obe GraphQL varijante.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class ApiEqualityTests {

    private static final String USER = "id name email createdAt";
    private static final String PROJECT = "id name description createdAt";
    private static final String TASK = "id title description status priority dueDate createdAt updatedAt";
    private static final String COMMENT = "id text createdAt";

    private static final JsonMapper JSON = JsonMapper.builder().build();

    @LocalServerPort
    private int port;

    private RestClient client;

    @BeforeEach
    void logIn() {
        RestClient anonymous = RestClient.create("http://localhost:" + port);
        String response = anonymous.post().uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"email\": \"korisnik1@example.com\", \"password\": \"password123\"}")
                .retrieve()
                .body(String.class);
        client = anonymous.mutate()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + JSON.readTree(response).get("token").asString())
                .build();
    }

    @Test
    void projectList() throws Exception {
        JsonNode rest = rest("/api/projects?limit=20");

        JsonNode graphql = graphql("""
                { projects(limit: 20) { %s owner { id } } }
                """.formatted(PROJECT)).get("projects");

        assertSameData(rest, graphql);
    }

    @Test
    void taskWithComments() throws Exception {
        ObjectNode rest = (ObjectNode) rest("/api/tasks/123");
        rest.set("comments", rest("/api/tasks/123/comments"));

        JsonNode graphql = graphql("""
                { task(id: 123) {
                    %s project { id } assignee { id }
                    comments { %s task { id } author { id } } } }
                """.formatted(TASK, COMMENT)).get("task");

        assertSameData(rest, graphql);
    }

    // IP2: svaki nivo dodaje jedan stepen ugnežđenosti
    @ParameterizedTest(name = "dubina {0}")
    @ValueSource(ints = {1, 2, 3, 4})
    void nestingDepth(int depth) throws Exception {
        ArrayNode rest = (ArrayNode) rest("/api/projects?limit=5");
        if (depth >= 2) {
            for (JsonNode project : rest) {
                ArrayNode tasks = (ArrayNode) rest("/api/projects/" + id(project) + "/tasks");
                ((ObjectNode) project).set("tasks", tasks);
                if (depth >= 3) {
                    for (JsonNode task : tasks) {
                        ((ObjectNode) task).set("comments", rest("/api/tasks/" + id(task) + "/comments"));
                    }
                }
            }
        }
        if (depth == 4) {
            List<JsonNode> comments = rest.valueStream()
                    .flatMap(p -> p.get("tasks").valueStream())
                    .flatMap(t -> t.get("comments").valueStream())
                    .toList();
            Map<String, JsonNode> users = users(comments.stream().map(c -> c.get("authorId").asString()).toList());
            comments.forEach(c -> ((ObjectNode) c).set("author", users.get(c.get("authorId").asString())));
        }

        String[] queries = {
                "{ projects(limit: 5) { %1$s owner { id } } }",
                "{ projects(limit: 5) { %1$s owner { id } tasks { %2$s project { id } assignee { id } } } }",
                "{ projects(limit: 5) { %1$s owner { id } tasks { %2$s project { id } assignee { id }"
                        + " comments { %3$s task { id } author { id } } } } }",
                "{ projects(limit: 5) { %1$s owner { id } tasks { %2$s project { id } assignee { id }"
                        + " comments { %3$s task { id } author { %4$s } } } } }"
        };
        JsonNode graphql = graphql(queries[depth - 1].formatted(PROJECT, TASK, COMMENT, USER)).get("projects");

        assertSameData(rest, graphql);
    }

    // IP3: otvaranje table projekta – REST više poziva, GraphQL jedan upit
    @Test
    void projectBoard() throws Exception {
        ObjectNode rest = (ObjectNode) rest("/api/projects/7");
        ArrayNode tasks = (ArrayNode) rest("/api/projects/7/tasks");
        rest.set("tasks", tasks);
        for (JsonNode task : tasks) {
            ((ObjectNode) task).set("comments", rest("/api/tasks/" + id(task) + "/comments"));
        }

        Set<String> userIds = new LinkedHashSet<>();
        userIds.add(rest.get("ownerId").asString());
        for (JsonNode task : tasks) {
            if (!task.get("assigneeId").isNull()) {
                userIds.add(task.get("assigneeId").asString());
            }
            task.get("comments").forEach(c -> userIds.add(c.get("authorId").asString()));
        }
        Map<String, JsonNode> users = users(userIds);

        rest.set("owner", users.get(rest.get("ownerId").asString()));
        for (JsonNode task : tasks) {
            JsonNode assigneeId = task.get("assigneeId");
            ((ObjectNode) task).set("assignee", assigneeId.isNull() ? NullNode.instance : users.get(assigneeId.asString()));
            task.get("comments").forEach(c -> ((ObjectNode) c).set("author", users.get(c.get("authorId").asString())));
        }

        JsonNode graphql = graphql("""
                { project(id: 7) {
                    %1$s owner { %4$s }
                    tasks { %2$s project { id } assignee { %4$s }
                        comments { %3$s task { id } author { %4$s } } } } }
                """.formatted(PROJECT, TASK, COMMENT, USER)).get("project");

        assertSameData(rest, graphql);
    }

    @Test
    void userWithTasksAndProjects() throws Exception {
        ObjectNode rest = (ObjectNode) rest("/api/users/7");
        rest.set("assignedTasks", rest("/api/users/7/tasks"));
        rest.set("ownedProjects", rest("/api/users/7/projects"));

        JsonNode graphql = graphql("""
                { user(id: 7) {
                    %s
                    assignedTasks { %s project { id } assignee { id } }
                    ownedProjects { %s owner { id } } } }
                """.formatted(USER, TASK, PROJECT)).get("user");

        assertSameData(rest, graphql);
    }

    @Test
    void selectedUsersWithTasks() throws Exception {
        JsonNode rest = rest("/api/users?ids=1,5,7");
        for (JsonNode user : rest) {
            ((ObjectNode) user).set("assignedTasks", rest("/api/users/" + id(user) + "/tasks"));
        }

        JsonNode graphql = graphql("""
                { users(ids: [1, 5, 7]) { %s assignedTasks { %s project { id } assignee { id } } } }
                """.formatted(USER, TASK)).get("users");

        assertSameData(rest, graphql);
    }

    @Test
    void allUsers() throws Exception {
        assertSameData(rest("/api/users"), graphql("{ users { %s } }".formatted(USER)).get("users"));
    }

    @Test
    void currentUser() throws Exception {
        assertSameData(rest("/api/users/me"), graphql("{ me { %s } }".formatted(USER)).get("me"));
    }

    // Nepostojeći projekat: REST vraća 404, GraphQL null – oba kažu „nema ga“
    @Test
    void missingProject() {
        HttpStatusCode status = client.get().uri("/api/projects/999999")
                .exchange((request, response) -> response.getStatusCode());

        assertThat(status.value()).isEqualTo(404);
        assertThat(graphql("{ project(id: 999999) { id } }").get("project").isNull()).isTrue();
    }

    private JsonNode rest(String path) {
        return JSON.readTree(client.get().uri(path).retrieve().body(String.class));
    }

    private JsonNode graphql(String query) {
        String response = client.post().uri("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .body(JSON.writeValueAsString(Map.of("query", query)))
                .retrieve()
                .body(String.class);
        JsonNode json = JSON.readTree(response);
        assertThat(json.has("errors")).as(response).isFalse();
        return json.get("data");
    }

    // Jedan REST poziv za sve potrebne korisnike, kao što bi uradio frontend
    private Map<String, JsonNode> users(Collection<String> ids) {
        Map<String, JsonNode> users = new HashMap<>();
        rest("/api/users?ids=" + String.join(",", new LinkedHashSet<>(ids)))
                .forEach(user -> users.put(id(user), user));
        return users;
    }

    private static String id(JsonNode node) {
        return node.get("id").asString();
    }

    // STRICT: isti redosled u listama i nijedno polje viška ni manjka
    private static void assertSameData(JsonNode rest, JsonNode graphql) throws Exception {
        JSONAssert.assertEquals(
                normalizeIds(toGraphQlShape(rest)).toString(),
                normalizeIds(graphql).toString(),
                JSONCompareMode.STRICT);
    }

    // REST vraća id povezanog objekta (ownerId: 5), a GraphQL sam objekat (owner: { id: "5" }).
    // Ako je test već ubacio ceo objekat (npr. owner), polje ownerId se samo uklanja.
    private static JsonNode toGraphQlShape(JsonNode node) {
        if (node.isArray()) {
            node.forEach(ApiEqualityTests::toGraphQlShape);
        } else if (node.isObject()) {
            ObjectNode object = (ObjectNode) node;
            for (Map.Entry<String, JsonNode> field : List.copyOf(object.properties())) {
                String name = field.getKey();
                JsonNode value = field.getValue();
                if (name.endsWith("Id")) {
                    String relation = name.substring(0, name.length() - 2);
                    object.remove(name);
                    if (!object.has(relation)) {
                        object.set(relation, value.isNull() ? NullNode.instance : JSON.createObjectNode().set("id", value));
                    }
                } else {
                    toGraphQlShape(value);
                }
            }
        }
        return node;
    }

    // GraphQL tip ID je uvek string ("5"), a REST vraća broj (5)
    private static JsonNode normalizeIds(JsonNode node) {
        if (node.isArray()) {
            node.forEach(ApiEqualityTests::normalizeIds);
        } else if (node.isObject()) {
            ObjectNode object = (ObjectNode) node;
            if (object.has("id") && !object.get("id").isNull()) {
                object.put("id", object.get("id").asString());
            }
            object.forEach(ApiEqualityTests::normalizeIds);
        }
        return node;
    }
}