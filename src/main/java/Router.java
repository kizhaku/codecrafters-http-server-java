import enums.HttpResponseStatus;

import java.util.Map;
import java.util.function.Function;

public class Router {
    private static final Map<String, Function<HttpRequest, String>> ROUTES = Map.of(
            "/", RequestHandler::home,
            "/echo", RequestHandler::echo,
            "/user-agent", RequestHandler::userAgent,
            "/files", RequestHandler::files);

    public static String route(HttpRequest request) {
        return ROUTES.getOrDefault(request.getEndPoint(), RequestHandler::notFound).apply(request);
    }
}
