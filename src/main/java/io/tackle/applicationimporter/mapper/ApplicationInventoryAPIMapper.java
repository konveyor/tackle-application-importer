package io.tackle.applicationimporter.mapper;

import io.tackle.applicationimporter.entity.ApplicationImport;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class ApplicationInventoryAPIMapper extends ApplicationMapper{
    @Override
    public Response map(ApplicationImport importApp)
    {
        System.out.println("Call to Mapper");

        return Response.ok().build();
    }
}
