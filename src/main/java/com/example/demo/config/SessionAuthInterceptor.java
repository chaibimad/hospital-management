package com.example.demo.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;
import java.io.PrintWriter;

/**
 * Contrôle d'accès : redirige vers la page de connexion si aucune session utilisateur.
 * Les chemins protégés sont définis dans WebMvcConfig.
 */
public class SessionAuthInterceptor implements HandlerInterceptor {

    private static String homePathForRole(String role) {
        if (role == null) return "/login.html";
        return switch (role.toUpperCase()) {
            case "ADMIN" -> "/dashboard";
            case "SECRETAIRE" -> "/secretaire/patients";
            case "MEDECIN" -> "/medecin/dashboard";
            case "PATIENT" -> "/patient/space";
            default -> "/login.html";
        };
    }

    private static void forbid(HttpServletRequest request, HttpServletResponse response, String role) throws Exception {
        String uri = request.getRequestURI();
        boolean isApi = uri.startsWith(request.getContextPath() + "/api/") || uri.startsWith("/api/");

        if (isApi) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json; charset=UTF-8");
            try (PrintWriter w = response.getWriter()) {
                w.write("{\"message\":\"Accès refusé\"}");
                w.flush();
            }
            return;
        }

        response.sendRedirect(request.getContextPath() + homePathForRole(role));
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.html?session=expired");
            return false;
        }

        String role = session.getAttribute("userRole") != null ? String.valueOf(session.getAttribute("userRole")) : null;
        String ctx = request.getContextPath();
        String uri = request.getRequestURI();
        String path = (ctx != null && !ctx.isBlank() && uri.startsWith(ctx)) ? uri.substring(ctx.length()) : uri;

        // Accessible à tous les utilisateurs connectés
        if (path.startsWith("/user/") || path.startsWith("/api/user/")) {
            return true;
        }

        // ADMIN
        if ("/dashboard".equals(path) || path.startsWith("/admin/")) {
            if (!"ADMIN".equalsIgnoreCase(role)) {
                forbid(request, response, role);
                return false;
            }
            return true;
        }

        // SECRETAIRE
        if (path.startsWith("/secretaire/") || path.startsWith("/api/secretaire/")) {
            if (!"SECRETAIRE".equalsIgnoreCase(role)) {
                forbid(request, response, role);
                return false;
            }
            return true;
        }

        // MEDECIN
        if (path.startsWith("/medecin/")) {
            if (!"MEDECIN".equalsIgnoreCase(role)) {
                forbid(request, response, role);
                return false;
            }
            return true;
        }

        // PATIENT
        if (path.startsWith("/patient/") || path.startsWith("/api/patient/")) {
            if (!"PATIENT".equalsIgnoreCase(role)) {
                forbid(request, response, role);
                return false;
            }
            return true;
        }

        return true;
    }
}
