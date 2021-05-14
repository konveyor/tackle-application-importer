package io.tackle.applicationimporter.mapper;

import io.tackle.applicationimporter.entity.ApplicationImport;

import javax.ws.rs.core.Response;

public abstract class ApplicationMapper {
    public abstract Response map(ApplicationImport importApp);
}
