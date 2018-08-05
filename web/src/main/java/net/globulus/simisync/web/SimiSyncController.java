package net.globulus.simisync.web;

import net.globulus.simi.ActiveSimi;
import net.globulus.simi.SimiMapper;
import net.globulus.simi.api.SimiValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
public class SimiSyncController {

    @RequestMapping("/**")
    public String route(HttpServletRequest request) {
        String body = null;
        if (request.getMethod().equalsIgnoreCase("post")) {
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
        return "" + ActiveSimi.eval("SimiSyncControllers.Router",
                "route",
                new SimiValue.String(endpoint),
                new SimiValue.String(request.getMethod()),
                new SimiValue.Object(SimiMapper.toObject(headers, true)),
                new SimiValue.String(body));
    }
}
