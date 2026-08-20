package COMP3011.Assignment_1.service;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.stereotype.Service;

@Service
public class ShutdownService {

    private final AtomicBoolean shutdownInProgress = new AtomicBoolean(false);

    public boolean beginShutdown() {
        return shutdownInProgress.compareAndSet(false, true);
    }

}