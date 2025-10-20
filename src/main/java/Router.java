import dto.HttpRequest;
import dto.HttpResponse;

import java.util.Map;
import java.util.function.Function;

public class Router {
    private static final Map<String, Map<String, Function<HttpRequest, HttpResponse>>> ROUTES = Map.of(
            "/", Map.of("GET", RequestHandler::home),
            "/echo", Map.of("GET", RequestHandler::echo),
            "/user-agent", Map.of("GET", RequestHandler::userAgent),
            "/files", Map.of("GET", RequestHandler::getFile, "POST", RequestHandler::postFile));

    public static HttpResponse route(HttpRequest request) {
        Map<String, Function<HttpRequest, HttpResponse>> routeMethods = ROUTES.getOrDefault(request.getEndPoint(),
                Map.of("GET", RequestHandler::notFound));

        return routeMethods.getOrDefault(request.getHttpMethod(), RequestHandler::methodNotAllowed).apply(request);
    }
}
