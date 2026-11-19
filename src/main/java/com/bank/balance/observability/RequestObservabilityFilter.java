package com.bank.balance.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RequestObservabilityFilter extends OncePerRequestFilter {
    static final String TRACE_HEADER = "X-Correlation-ID";
    private static final Logger LOG = LoggerFactory.getLogger(RequestObservabilityFilter.class);
    private final SensitiveDataSanitizer sanitizer;

    public RequestObservabilityFilter(SensitiveDataSanitizer sanitizer) {
        this.sanitizer = sanitizer;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        long started = System.nanoTime();
        String traceID = traceIdentifier(request.getHeader(TRACE_HEADER));
        MDC.put("TraceID", traceID);
        MDC.put("EventCategory", "Application");
        MDC.put("EventType", "BalanceEnquiry");
        MDC.put("MethodSignature", request.getMethod() + " " + request.getRequestURI());
        try {
            LOG.debug("Masked Authorization credential state={}",
                    sanitizer.maskedAuthorization(request.getHeader("Authorization")));
            filterChain.doFilter(request, response);
        } finally {
            long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started);
            response.setHeader("X-ElapsedTime", Long.toString(elapsed));
            response.setHeader(TRACE_HEADER, traceID);
            MDC.put("TransactionCost", Long.toString(elapsed));
            MDC.put("ErrorCode", response.getStatus() >= 400 ? "HTTP_" + response.getStatus() : "NONE");
            MDC.put("ErrorMessage", response.getStatus() >= 400 ? "Request failed" : "NONE");
            LOG.info("Completed balance enquiry operation with status={}", response.getStatus());
            MDC.clear();
        }
    }

    private String traceIdentifier(String supplied) {
        if (supplied != null && supplied.matches("[A-Za-z0-9._-]{1,64}")) {
            return supplied;
        }
        return UUID.randomUUID().toString();
    }
}
