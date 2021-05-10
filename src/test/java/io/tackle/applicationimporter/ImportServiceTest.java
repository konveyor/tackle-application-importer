package io.tackle.applicationimporter;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.config.EncoderConfig;
import io.tackle.commons.testcontainers.KeycloakTestResource;
import io.tackle.commons.testcontainers.PostgreSQLDatabaseTestResource;
import io.tackle.commons.tests.SecuredResourceTest;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(value = PostgreSQLDatabaseTestResource.class,
        initArgs = {
                @ResourceArg(name = PostgreSQLDatabaseTestResource.DB_NAME, value = "application_importer_db"),
                @ResourceArg(name = PostgreSQLDatabaseTestResource.USER, value = "application_importer"),
                @ResourceArg(name = PostgreSQLDatabaseTestResource.PASSWORD, value = "application_importer")
        }
)
@QuarkusTestResource(value = KeycloakTestResource.class,
        initArgs = {
                @ResourceArg(name = KeycloakTestResource.IMPORT_REALM_JSON_PATH, value = "keycloak/quarkus-realm.json"),
                @ResourceArg(name = KeycloakTestResource.REALM_NAME, value = "quarkus")
        }
)
public class ImportServiceTest extends SecuredResourceTest {

    @BeforeAll
    public static void init() {
        PATH = "/file/upload";
    }


    @Test
    protected void testImportServicePost() {

        ClassLoader classLoader = getClass().getClassLoader();
        File importFile = new File(classLoader.getResource("sample_application_import.csv").getFile());
        MultipartImportBody importBody = new MultipartImportBody();
        try {
            System.out.println("construct File begin");
            byte [] fileBytes = FileUtils.readFileToByteArray(importFile);
            Arrays.asList(fileBytes).forEach(b -> System.out.println(":" + b));
            String fileString = new String(fileBytes, StandardCharsets.UTF_8);
            importBody.setFile(fileString);
            System.out.println("File body: " + fileString);
            System.out.println("construct File complete");
        }
        catch(Exception ioe){
            ioe.printStackTrace();
        }
        importBody.setFilename("sample_application_import.csv");

        Response response = given()
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs("multipart/form-data", ContentType.JSON)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .multiPart("file",importBody)
                .when().post(PATH)
                .then()
                .log().all()
                .statusCode(200).extract().response();

        assertEquals(200, response.getStatusCode());
        /** assertEquals(description, response.path("description"));
        assertEquals("alice", response.path("createUser"));
        assertEquals("alice", response.path("updateUser"));

         Long businessServiceId = Long.valueOf(response.path("id").toString());

        final String newName = "Yet another different name";
        businessService.name = newName;
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(businessService)
                .pathParam("id", businessServiceId)
                .when().put(PATH + "/{id}")
                .then().statusCode(204);

        given()
                .accept("application/json")
                .pathParam("id", businessServiceId)
                .when().get(PATH + "/{id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("name", is(newName),
                        "description", is(description));

        if (!nativeExecution) {
            BusinessService updatedBusinessServiceFromDb = BusinessService.findById(businessServiceId);
            assertEquals(newName, updatedBusinessServiceFromDb.name);
            assertEquals(description, updatedBusinessServiceFromDb.description);
            assertNotNull(updatedBusinessServiceFromDb.createTime);
            assertNotNull(updatedBusinessServiceFromDb.updateTime);
        }

        given()
                .pathParam("id", businessServiceId)
                .when().delete(PATH + "/{id}")
                .then().statusCode(204);

        given()
                .accept("application/json")
                .pathParam("id", businessServiceId)
                .when().get(PATH + "/{id}")
                .then()
                .log().all()
                .statusCode(404);*/

    }
}

