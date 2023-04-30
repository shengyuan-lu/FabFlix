package Filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {
    private final ArrayList<String> allowedURIs = new ArrayList<>();

    public void init(FilterConfig fConfig) {
        // Website URIs
        allowedURIs.add("login.html");

        allowedURIs.add("api/login");
        allowedURIs.add("icon.js");
        allowedURIs.add("login.js");

        allowedURIs.add("css/custom-style.css");

        // Third party URIs
        allowedURIs.add("jquery.min.js");
        allowedURIs.add("popper.min.js");
        allowedURIs.add("bootstrap.bundle.min.js");
    }

    // Customer cannot search or browse until login.
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Check if this URL is allowed to access without logging in
        if (this.isUrlAllowedWithoutLogin(httpRequest.getRequestURI())) {
            // Keep default action: pass along the filter chain
            chain.doFilter(request, response);

            System.out.println("LoginFilter: Allowed - No Login Required: " + httpRequest.getRequestURI());
            return;
        }

        // Redirect to login page if the "user" attribute doesn't exist in session
        if (httpRequest.getSession().getAttribute("customer") == null) {

            System.out.println("LoginFilter: Not Allowed - User Not Logged In: " + httpRequest.getRequestURI());
            System.out.println("LoginFilter: Redirect to: login.html");

            httpResponse.sendRedirect("./login.html");

        } else {

            System.out.println("LoginFilter: Allowed - User Logged In: " + httpRequest.getRequestURI());

            chain.doFilter(request, response);
        }
    }

    private boolean isUrlAllowedWithoutLogin(String requestURI) {

        System.out.println(requestURI);

        return allowedURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
    }

    public void destroy() {
        // ignored.
    }

}
