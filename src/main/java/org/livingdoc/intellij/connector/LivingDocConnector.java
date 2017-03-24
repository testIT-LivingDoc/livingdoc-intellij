package org.livingdoc.intellij.connector;

import info.novatec.testit.livingdoc.server.domain.Project;

import java.util.Set;

public interface LivingDocConnector {

    Set<Project> getAllProjects();
}
