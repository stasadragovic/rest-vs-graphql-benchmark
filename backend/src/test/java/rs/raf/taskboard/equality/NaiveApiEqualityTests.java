package rs.raf.taskboard.equality;

import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "taskboard.graphql.batching=false")
class NaiveApiEqualityTests extends ApiEqualityTests {
}