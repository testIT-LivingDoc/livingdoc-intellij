package org.livingdoc.intellij.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class LivingDocExecution {

    private String executionErrorId;
    private String results;
    private int errors;
    private int failures;
    private int ignored;
    private boolean hasException;
    private boolean hasFailed;
    private int success;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .toString();
    }

    public String getExecutionErrorId() {
        return executionErrorId;
    }

    public void setExecutionErrorId(String executionErrorId) {
        this.executionErrorId = executionErrorId;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public int getErrors() {
        return errors;
    }

    public void setErrors(int errors) {
        this.errors = errors;
    }

    public int getFailures() {
        return failures;
    }

    public void setFailures(int failures) {
        this.failures = failures;
    }

    public int getIgnored() {
        return ignored;
    }

    public void setIgnored(int ignored) {
        this.ignored = ignored;
    }

    public void setHasException(boolean hasException) {
        this.hasException = hasException;
    }

    public boolean hasException() {
        return hasException;
    }

    public void setHasFailed(boolean hasFailed) {
        this.hasFailed = hasFailed;
    }

    public boolean hasFailed() {
        return hasFailed;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }
}
