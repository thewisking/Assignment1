package COMP3011.Assignment_1.model;

public class TranscriptionResult {

    private final String text;
    private final long inputTokens;
    private final long outputTokens;

    public TranscriptionResult( // I need the C++ : feature to auto put stuff in stuff :c
            String text,
            long inputTokens,
            long outputTokens) {

        this.text = text;
        this.inputTokens = inputTokens;
        this.outputTokens = outputTokens;
    }

    public String getText() {
        return text;
    }

    public long getInputTokens() {
        return inputTokens;
    }

    public long getOutputTokens() {
        return outputTokens;
    }

}