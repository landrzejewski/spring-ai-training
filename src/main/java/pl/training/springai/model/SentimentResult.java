package pl.training.springai.model;

/**
 * A structured output target. BeanOutputConverter derives a JSON schema from this record, so the
 * component names and the javadoc-worthy constraints below are what the model is asked to produce.
 *
 * @param score sentiment from -1.0 (negative) to +1.0 (positive)
 * @param explanation a short justification of the score
 */
public record SentimentResult(
        double score,
        String explanation
) {}
