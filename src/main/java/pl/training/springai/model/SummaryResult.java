package pl.training.springai.model;

import java.util.List;

/**
 * A structured output target.
 * <p>
 * Note that every component is a reference type. BeanOutputConverter deserializes whatever the
 * model returned, and a model is free to omit a field or send null for it - which fails hard
 * against a primitive ({@code Cannot map null into type int}) and merely leaves a null against a
 * boxed one. Prefer boxed types in structured output records unless the field is genuinely
 * guaranteed, and let validation rather than deserialization report what is missing.
 *
 * @param summary the condensed text
 * @param keyPoints the main points, one per element
 * @param wordCount the length of the summary
 */
public record SummaryResult(
        String summary,
        List<String> keyPoints,
        Integer wordCount
) {}
