package io.tackle.applicationimporter;

import java.io.InputStream;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class MultipartImportBody {
    @FormParam("file")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    private byte[] file;
    @FormParam("fileName")
    @PartType(MediaType.TEXT_PLAIN)
    private String fileName;

    public MultipartImportBody() {}


    public void setFile(byte [] file)
    {
        this.file = file;
    }

    public void setFilename(String fileName)
    {
        this.fileName = fileName;
    }

    public byte[] getFile() {
        return file;
    }

    public String getFileName() {
        return fileName;
    }
}
