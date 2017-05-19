package org.livingdoc.intellij.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Counter for the results of the specification executions.
 */
public class ExecutionCounter implements Serializable {

    private int totalErrors;
    private int failuresCount;
    private int finishedTestsCount;
    private int ignoreTestsCount;

    private long startTime;
    private long endTime;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("totalErrors", totalErrors)
                .append("failuresCount", failuresCount)
                .append("finishedTestsCount", finishedTestsCount)
                .append("ignoreTestsCount", ignoreTestsCount)
                .append("startTime", startTime)
                .append("endTime", endTime)
                .toString();
    }

    public int getTotalErrors() {
        return totalErrors;
    }

    public void setTotalErrors(int totalErrors) {
        this.totalErrors = totalErrors;
    }

    public int getFailuresCount() {
        return failuresCount;
    }

    public void setFailuresCount(int failuresCount) {
        this.failuresCount = failuresCount;
    }

    public int getFinishedTestsCount() {
        return finishedTestsCount;
    }

    public void setFinishedTestsCount(int finishedTestsCount) {
        this.finishedTestsCount = finishedTestsCount;
    }

    public int getIgnoreTestsCount() {
        return ignoreTestsCount;
    }

    public void setIgnoreTestsCount(int ignoreTestsCount) {
        this.ignoreTestsCount = ignoreTestsCount;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
