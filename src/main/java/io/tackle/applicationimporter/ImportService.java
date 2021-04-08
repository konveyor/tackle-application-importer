package io.tackle.applicationimporter;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.Arrays;

@Path("/file")
public class ImportService {

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void importFile(@MultipartForm MultipartImportBody data) throws Exception {
        try {
            writeFile(data.getFile(), data.getFileName());
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    private void writeFile(InputStream content, String filename) throws IOException {

        File file = new File(filename);
        String line = "";
        FileReader fileReader = new FileReader(file);
        BufferedReader br = new BufferedReader(fileReader);

        while ((line = br.readLine()) != null)   //returns a Boolean value
        {
            System.out.println("Next Line: ");
            String[] fields = line.split(",");    // use comma as separator
            Arrays.stream(fields).forEach( field -> System.out.println(field + " "));

        }





    }
}
