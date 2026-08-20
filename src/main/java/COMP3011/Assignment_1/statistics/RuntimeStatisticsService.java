package COMP3011.Assignment_1.statistics;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

@Service
public class RuntimeStatisticsService {

    private final Instant serverStart = Instant.now();

    private final AtomicLong inputTokens = new AtomicLong();
    private final AtomicLong outputTokens = new AtomicLong();

    public Instant getServerStart() {
        return serverStart;
    }

    public long getInputTokens() {
        return inputTokens.get();
    }

    public long getOutputTokens() {
        return outputTokens.get();
    }

    public void addTokenUsage(long inputTokens, long outputTokens) {
        this.inputTokens.addAndGet(inputTokens);
        this.outputTokens.addAndGet(outputTokens);
    }
    
}