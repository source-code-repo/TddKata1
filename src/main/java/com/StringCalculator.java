package com;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringCalculator {
    // regex to identify a multi-char delimiter
    private static final String MULTI_CHAR_DELIMITER = "(?s)^//\\[(.+)\\].*";
    private int callCount = 0;

    public int add(String numbers) {
        callCount++;

        List<String> delimiters = new ArrayList<>(List.of(",", "\n"));
        String customDelimiter = getCustomDelimiter(numbers);
        if(!customDelimiter.isEmpty()) {
            numbers = numbers.substring(numbers.indexOf("\n") + 1);
            delimiters.add(customDelimiter);
        }

        List<Integer> ints = tokenize(delimiters, List.of(numbers))
                .stream()
                .filter(Predicate.not(String::isEmpty))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        // Throw exception if any numbers are negative
        if(ints.stream().anyMatch(i -> i < 0)) {
            throw new IllegalStateException("negatives not allowed:" +
                    ints.stream()
                            .filter(i -> i < 0)
                            .map(i -> " " + i)
                            .collect(Collectors.joining()));
        }

        return ints.stream()
                .filter(i -> i <= 1000)
                .collect(Collectors.summingInt(i->i));
    }

    private String getCustomDelimiter(String numbers) {
        Pattern pattern = Pattern.compile(MULTI_CHAR_DELIMITER);
        Matcher matcher = pattern.matcher(numbers);
        if(matcher.find()) { // Multi-char delimiter
            return matcher.group(1);
        } else if(numbers.startsWith("//")) { // Single char delimiter
            return Character.toString(numbers.charAt(2));
        }
        return ""; // No custom delimiter
    }

    /**
     * Split string by delimiters
     * @param delimiters
     * @param string
     * @return List of split tokens
     */
    List<String> tokenize(List<String> delimiters, List<String> string) {
        for (String d : delimiters) {
            List<String> newResults = new ArrayList<>();
            for(String s : string) {
                String[] split = s.split(Pattern.quote(d));
                Collections.addAll(newResults, split);
            }
            string = newResults;
        }
        return string;
    }

    public int getCallCount() {
        return callCount;
    }
}
