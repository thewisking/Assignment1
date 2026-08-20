package COMP3011.Assignment_1.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpenAITranscriptionResponse {

    private String text;
    private Usage usage;

    public OpenAITranscriptionResponse() {

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    public static class Usage {

        @JsonProperty("input_tokens")
        private long inputTokens;

        @JsonProperty("output_tokens")
        private long outputTokens;

        public Usage() {

        }

        public long getInputTokens() {
            return inputTokens;
        }

        public void setInputTokens(long inputTokens) {
            this.inputTokens = inputTokens;
        }

        public long getOutputTokens() {
            return outputTokens;
        }

        public void setOutputTokens(long outputTokens) {
            this.outputTokens = outputTokens;
        }

    }

}