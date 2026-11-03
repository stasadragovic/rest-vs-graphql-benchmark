package rs.raf.taskboard.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import java.util.stream.Collectors;

@Component
public class GraphQlExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof NotFoundException) {
            return error(env, ErrorType.NOT_FOUND, ex.getMessage());
        }
        if (ex instanceof ConstraintViolationException e) {
            String message = e.getConstraintViolations().stream()
                    .map(GraphQlExceptionResolver::describe)
                    .sorted()
                    .collect(Collectors.joining(", "));
            return error(env, ErrorType.BAD_REQUEST, message);
        }
        // Vrednost argumenta ne može da se pretvori u Java tip (npr. dueDate: "abc")
        if (ex instanceof BindException e) {
            String message = e.getFieldErrors().stream()
                    .map(fieldError -> lastSegment(fieldError.getField()) + ": invalid value")
                    .sorted()
                    .collect(Collectors.joining(", "));
            return error(env, ErrorType.BAD_REQUEST, message);
        }
        return null;
    }

    private static GraphQLError error(DataFetchingEnvironment env, ErrorType type, String message) {
        return GraphqlErrorBuilder.newError(env).errorType(type).message(message).build();
    }

    // Isti format poruke kao RestExceptionHandler, da oba API-ja prijavljuju istu grešku
    private static String describe(ConstraintViolation<?> violation) {
        return lastSegment(violation.getPropertyPath().toString()) + ": " + violation.getMessage();
    }

    private static String lastSegment(String path) {
        return path.substring(path.lastIndexOf('.') + 1);
    }
}