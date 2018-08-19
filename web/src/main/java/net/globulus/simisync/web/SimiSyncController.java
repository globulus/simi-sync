package net.globulus.simisync.web;

import net.globulus.simi.ActiveSimi;
import net.globulus.simi.SimiMapper;
import net.globulus.simi.api.SimiObject;
import net.globulus.simi.api.SimiProperty;
import net.globulus.simi.api.SimiValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
public class SimiSyncController {

    @RequestMapping("/**")
    public ResponseEntity<String> route(HttpServletRequest request, HttpServletResponse response) {
        String body = null;
        if (request.getMethod().equalsIgnoreCase("post")
                || request.getMethod().equalsIgnoreCase("put")) {
            try {
                body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String endpoint = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        HashMap<String, Object> headers = new HashMap<>();
        for (String header : Collections.list(request.getHeaderNames())) {
            headers.put(header, request.getHeader(header));
        }
        String queryString = request.getQueryString();
        SimiProperty prop = ActiveSimi.eval("SimiSyncControllers.Router",
                "route",
                new SimiValue.String(endpoint),
                new SimiValue.String(request.getMethod()),
                new SimiValue.Object(SimiMapper.toObject(headers, true)),
                (queryString == null) ? null : new SimiValue.String(request.getQueryString()),
                (body == null) ? null : new SimiValue.String(body));
        if (prop == null) {
            return ResponseEntity.notFound().build();
        }
        SimiValue value = prop.getValue();
        if (value instanceof SimiValue.String) {
            return ResponseEntity.ok(value.getString());
        } else {
            SimiObject obj = value.getObject();
            int statusCode = (int) obj.get("code", null).getValue().getNumber().asLong();
            String responseBody = obj.get("body", null).getValue().getString();
            return ResponseEntity.status(statusCode).body(responseBody);
        }
    }
}
