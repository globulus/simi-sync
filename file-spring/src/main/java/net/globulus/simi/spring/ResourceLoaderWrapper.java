package net.globulus.simi.spring;

import net.globulus.simi.api.*;
import org.springframework.core.io.ResourceLoader;

import java.io.BufferedReader;
import java.util.List;

public class ResourceLoaderWrapper implements SimiObject {

    ResourceLoader resourceLoader;

    public ResourceLoaderWrapper(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public SimiClass getSimiClass() {
        return null;
    }

    @Override
    public SimiProperty get(String s, SimiEnvironment simiEnvironment) {
        return null;
    }

    @Override
    public void set(String s, SimiProperty simiProperty, SimiEnvironment simiEnvironment) {

    }

    @Override
    public SimiObject clone(boolean b) {
        return null;
    }

    @Override
    public List<SimiValue> keys() {
        return null;
    }

    @Override
    public List<SimiValue> values() {
        return null;
    }

    @Override
    public String toCode(int i, boolean b) {
        return null;
    }

    @Override
    public int getLineNumber() {
        return 0;
    }

    @Override
    public boolean hasBreakPoint() {
        return false;
    }
}
