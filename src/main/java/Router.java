import dto.HttpRequest;
import dto.HttpResponse;

import java.util.Map;
import java.util.function.Function;

/**
 * Define routes and corresponding class method for handling the request
 */
public class Router {
    private static final Map<String, Map<String, Function<HttpRequest, HttpResponse>>> ROUTES = Map.of(
            "/", Map.of("GET", RequestHandler::home),
            "/echo", Map.of("GET", RequestHandler::echo),
            "/user-agent", Map.of("GET", RequestHandler::userAgent),
            "/files", Map.of("GET", RequestHandler::getFile, "POST", RequestHandler::postFile));

    /**
     * Route to the correct method based on incoming path and HTTP method.
     * If path not defined, default to not 404 - not found
     * If path present and method not defined, default to 405 - method not allowed
     */
    public static HttpResponse route(HttpRequest request) {
        var routeMethods = ROUTES.getOrDefault(request.getEndPoint(), Map.of("GET", RequestHandler::notFound));

        return routeMethods
                .getOrDefault(request.getHttpMethod(), RequestHandler::methodNotAllowed)
                .apply(request);
    }
}
