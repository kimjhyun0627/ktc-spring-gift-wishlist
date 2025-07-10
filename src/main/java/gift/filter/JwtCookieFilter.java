package gift.filter;

import gift.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class JwtCookieFilter extends JwtFilter {

    public JwtCookieFilter(JwtUtil jwtUtil) {
        super(jwtUtil);
    }

    @Override
    protected boolean shouldFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String ctx = request.getContextPath();
        if (path.startsWith(ctx + "/admin/*") ||
                path.startsWith(ctx + "/api/") ||
                path.startsWith(ctx + "/css/") ||
                path.startsWith(ctx + "/js/")) {
            return false;
        }
        return path.startsWith(ctx + "/admin/");
    }

    @Override
    protected String resolveToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(c -> "AUTH_TOKEN".equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    @Override
    protected void writeError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        String body = String.format(
                "{\"status\":%d,\"error\":\"%s\"}",
                HttpServletResponse.SC_UNAUTHORIZED, message
        );
        response.getWriter().write(body);
    }
}
