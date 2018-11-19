package net.globulus.simisync;

import net.globulus.simi.Debugger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BrowserInterface implements Debugger.DebuggerInterface {

    private final BlockingQueue<String> inputQueue = new ArrayBlockingQueue<>(10);
    private final BlockingQueue<String> outputQueue = new ArrayBlockingQueue<>(10);

    public BlockingQueue<String> getOutputQueue() {
        return outputQueue;
    }

    @Override
    public void flush(String s) {
        try {
            outputQueue.put(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String read() {
        return null;
    }

    @Override
    public BlockingQueue<String> getInputQueue() {
        return inputQueue;
    }

    @Override
    public void resume() {

    }
}
