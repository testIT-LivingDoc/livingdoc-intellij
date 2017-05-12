package org.livingdoc.intellij.domain;

public class LivingDocException extends Exception {

    public LivingDocException() {
        // just for unit testing.
    }

    public LivingDocException(Throwable th) {
        super(th);
    }
}
