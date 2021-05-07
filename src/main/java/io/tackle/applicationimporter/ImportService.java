package io.tackle.applicationimporter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.Arrays;
import java.util.Iterator;

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

    private void writeFile(byte[] content, String filename) throws IOException {

        ByteArrayInputStream bais = new ByteArrayInputStream(content);
        Arrays.asList().forEach(b -> System.out.println(b));
        Iterator<String> iterator = decode(bais);

        while (iterator.hasNext())
        {
            String next = iterator.next();
            System.out.println(next + " ");
        }
       /** String line = "";
        InputStreamReader isReader = new InputStreamReader(bais);
        BufferedReader br = new BufferedReader(isReader);

        while ((line = br.readLine()) != null)   //returns a Boolean value
        {
            System.out.println("Next Line: ");
            CsvParser parser = new CsvParser();
            String[] fields = line.split(",");    // use comma as separator
            Arrays.stream(fields).forEach( field -> System.out.println(field + " "));

        }*/

    }

    private Iterator<String> decode(InputStream inputStream) {
        try {
            byte[] inputFile = getFilePortionOfMessage(inputStream);
            CsvMapper mapper = new CsvMapper();
            CsvSchema csvSchema = mapper.schemaFor(String.class);
            String columnSeparator = ",";

            csvSchema = csvSchema.withColumnSeparator(columnSeparator.charAt(0));

            ObjectReader reader = mapper.readerFor(String.class).withFeatures(CsvParser.Feature.FAIL_ON_MISSING_COLUMNS)
                    .with(csvSchema);

            return reader.readValues(inputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] getFilePortionOfMessage(InputStream content)
    {
        try {
            JsonMapper jsonMapper =new JsonMapper();
            JsonParser parser = jsonMapper.createParser(content);

            System.out.println("Input Message:" + content);

            byte[] returnValue = null;
            while(parser.hasCurrentToken()){
                if (parser.currentName().equals("file"))
                {
                    returnValue =  parser.getCurrentToken().asByteArray();
                }
            }
            System.out.println("File Portion Of Message:" + returnValue);
            return returnValue;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
