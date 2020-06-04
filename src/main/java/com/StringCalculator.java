package com;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    // regex to identify a multi-char delimiter
    private static final String MULTI_CHAR_DELIMITER = "(?s)^//\\[(.+)\\].*";
    private int callCount = 0;

    public int add(String numbers) {
        callCount++;
        if(numbers.isEmpty()) {
            return 0;
        }

        List<String> delimiters = new ArrayList<>(List.of(",", "\n"));
        String customDelimiter = getCustomDelimiter(numbers);
        if(!customDelimiter.isEmpty()) {
            numbers = numbers.substring(numbers.indexOf("\n") + 1);
            delimiters.add(customDelimiter);
        }

        List<String> extractedNums = new ArrayList<String>(List.of(numbers));
        extractedNums = extractNums(delimiters, extractedNums);

        List<Integer> values = new ArrayList<>();
        List<Integer> negatives = new ArrayList<>();
        for(String n : extractedNums) {
            Integer value = Integer.parseInt(n);
            if(value < 0) {
                negatives.add(value);
            } else if(value > 1000) {
                // ignore
            } else {
                values.add(value);
            }
        }

        // exception if negative
        if(negatives.size() > 0) {
            final StringBuilder error = new StringBuilder("negatives not allowed:");
            negatives.forEach(n -> error.append(" " + n));
            throw new IllegalStateException(error.toString());
        }

        return values.stream().mapToInt(Integer::intValue).sum();
    }

    private String getCustomDelimiter(String numbers) {
        if(numbers.matches(MULTI_CHAR_DELIMITER)) { // Multi-char delimiter
            Pattern pattern = Pattern.compile(MULTI_CHAR_DELIMITER);
            Matcher matcher = pattern.matcher(numbers);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } else if(numbers.startsWith("//")) { // Single char delimiter
            return Character.toString(numbers.charAt(2));
        }

        return "";
    }

    List<String> extractNums(List<String> delimiters, List<String> string) {
        if(delimiters.isEmpty()) {
            return string;
        }

        List<String> newResults = new ArrayList<String>();

        for(String s : string) {
            String[] split = s.split(Pattern.quote(delimiters.get(0)));
            newResults.addAll(Arrays.asList(split));
        }

        delimiters = delimiters.subList(1, delimiters.size());

        if(!delimiters.isEmpty()) {
            newResults = extractNums(delimiters, newResults);
        }

        return newResults;
    }

    public int getCallCount() {
        return callCount;
    }
}
