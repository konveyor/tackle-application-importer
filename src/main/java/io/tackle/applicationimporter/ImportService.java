package io.tackle.applicationimporter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.apache.commons.io.FileUtils;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;

@Path("/file")
public class ImportService {

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
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
            System.out.println(iter.next());
        }

    }



    private MappingIterator<ApplicationImport> decode(String inputContent) {
        try {
           String inputFileContent = getFilePortionOfMessage(inputContent);

            CsvMapper mapper = new CsvMapper();

            mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
            CsvSchema csvSchema = mapper.schemaFor(ApplicationImport.class).withHeader();
            String columnSeparator = ",";

            csvSchema = csvSchema.withColumnSeparator(columnSeparator.charAt(0))
                                .withLineSeparator("\r\n");

            ObjectReader reader = mapper.readerFor(ApplicationImport.class)
                    .withFeatures(CsvParser.Feature.INSERT_NULLS_FOR_MISSING_COLUMNS,
                            CsvParser.Feature.EMPTY_STRING_AS_NULL)
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
