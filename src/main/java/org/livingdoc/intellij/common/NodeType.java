package org.livingdoc.intellij.common;

/**
 * Kind of node in the repository view.<br>
 * <ul>
 * <li>{@link #PROJECT} Root node for repository view tree. Usually this node will be the java project.</li>
 * <li>{@link #MODULE} Project's module</li>
 * <li>{@link #REPOSITORY} LivingDoc repository</li>
 * <li>{@link #SPECIFICATION} LivingDoc specification</li>
 * <li>{@link #ERROR}</li>
 * </ul>
 */
public enum NodeType {
    PROJECT, MODULE, REPOSITORY, SPECIFICATION, ERROR
}
