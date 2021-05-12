package io.tackle.applicationimporter;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;

import static javax.transaction.Transactional.TxType.REQUIRED;

@Path("/file")
@ApplicationScoped
public class ImportService {

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional(REQUIRED)
    public Response importFile(@MultipartForm MultipartImportBody data) throws Exception {
        try {

            System.out.println("File: " + data.getFile());
            System.out.println("FileName: " + data.getFileName());
            writeFile(data.getFile(), data.getFileName());
        } catch (Exception e) {

            e.printStackTrace();
            return Response.serverError().build();
        }

        return Response.ok().build();
    }


    private void writeFile(String content, String filename) throws IOException {

        MappingIterator<ApplicationImport> iter = decode(content);
        System.out.println("Printing csv fields");
        while (iter.hasNext())
        {
            ApplicationImport importedApplication = iter.next();
            System.out.println(importedApplication);
            importedApplication.persistAndFlush();
        }

    }



    private MappingIterator<ApplicationImport> decode(String inputContent) {
        try {
           String inputFileContent = getFilePortionOfMessage(inputContent);

            CsvMapper mapper = new CsvMapper();

            CsvSchema csvSchema = CsvSchema.emptySchema().withHeader();
            String columnSeparator = ",";

            csvSchema = csvSchema.withColumnSeparator(columnSeparator.charAt(0))
                                .withLineSeparator("\r\n")
                                .withUseHeader(true);

            ObjectReader reader = mapper.readerFor(ApplicationImport.class)
                    .with(csvSchema);

            return reader.readValues(inputFileContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFilePortionOfMessage(String content)
    {
        try
        {
            System.out.println("Input Message:" + content);

            ObjectNode node = new ObjectMapper().readValue(content, ObjectNode.class);
            String fileContent = node.get("file").asText();

            System.out.println("File Portion Of Message:" + fileContent);
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




}
