package net.globulus.simi.spring;

import net.globulus.simi.api.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourcePatternUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SimiJavaConfig(apiClassName = "file_spring")
@SimiJavaClass(name = "File")
public class SimiFile {

    @SimiJavaMethod
    public static SimiProperty readLines(SimiObject self, BlockInterpreter interpreter, SimiProperty path) {
        ArrayList<SimiValue> props;
        try {
            List<String> lines = Files.readAllLines(Paths.get(path.getValue().getString()));
            props = lines.stream()
                    .map(SimiValue.String::new)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
//            e.printStackTrace();
            Utils.raiseIoException(e, interpreter);
            props = new ArrayList<>();
        }
        return new SimiValue.Object(interpreter.newArray(true, props));
    }

    @SimiJavaMethod
    public static SimiProperty readString(SimiObject self, BlockInterpreter interpreter, SimiProperty path) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(path.getValue().getString())));
            return new SimiValue.String(content);
        } catch (IOException e) {
            e.printStackTrace();
            Utils.raiseIoException(e, interpreter);
            return null;
        }
//        try {
//            String pathString = path.getValue().getString();
//            if (!pathString.startsWith("file://")) {
//                pathString = "file://" + pathString;
//            }
//            String content = new BufferedReader(new InputStreamReader(new UrlResource(pathString).getInputStream()))
//                    .lines()
//                    .collect(Collectors.joining("\n"));
//            return new SimiValue.String(content);
//        } catch (IOException e) {
//            e.printStackTrace();
//            Utils.raiseIoException(e, interpreter);
//            return null;
//        }
    }

    @SimiJavaMethod
    public static SimiProperty listAll(SimiObject self,
                                       BlockInterpreter interpreter,
                                       SimiProperty path,
                                       SimiProperty filter,
                                       SimiProperty recursive,
                                       SimiProperty resourceLoaderWrapper) {
        ResourceLoader resourceLoader = ((ResourceLoaderWrapper) resourceLoaderWrapper.getValue().getObject()).resourceLoader;
        int maxDepth = (recursive.getValue().getNumber().asLong() == 0) ? 1 : 999;
        String filterRegex;
        if (filter.getValue() instanceof SimiValue.String) {
            filterRegex = filter.getValue().getString();
        } else {
            filterRegex = "*";
        }
        try {
            return new SimiValue.Object(interpreter.newArray(true,
                    Arrays.stream(ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
                            .getResources("classpath*:" + path.getValue().getString() + "/**/*." + filterRegex))
                            .map(r -> resourceToSimiFile((SimiClass) self, interpreter, r))
                            .collect(Collectors.toCollection(ArrayList::new))
            ));
        } catch (IOException e) {
//            e.printStackTrace();
            Utils.raiseIoException(e, interpreter);
            return null;
        }
    }

    private static SimiValue resourceToSimiFile(SimiClass clazz, BlockInterpreter interpreter, Resource resource) {
        LinkedHashMap<String, SimiProperty> props = new LinkedHashMap<>();
        try {
            props.put("path", new SimiValue.String(resource.getURL().getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new SimiValue.Object(interpreter.newInstance(clazz, props));
    }
}
