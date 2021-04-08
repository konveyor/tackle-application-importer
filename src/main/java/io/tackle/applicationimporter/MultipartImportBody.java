package io.tackle.applicationimporter;

import java.io.InputStream;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class MultipartImportBody {
    private InputStream file;
    private String fileName;

    @FormParam("file")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public void setFile(InputStream file)
    {
        this.file = file;
    }

    @FormParam("fileName")
    @PartType(MediaType.TEXT_PLAIN)
    public void setFilename(String fileName)
    {
        this.fileName = fileName;
    }

    public InputStream getFile() {
        return file;
    }

    public String getFileName() {
        return fileName;
    }
}
