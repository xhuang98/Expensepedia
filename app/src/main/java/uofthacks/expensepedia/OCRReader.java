package uofthacks.expensepedia;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class OCRReader {

    private final static String BASE_URI = "https://eastus.api.cognitive.microsoft.com/vision/v2.0/ocr";

    private final Invocation.Builder invokeBuilder;


    public OCRReader() {

        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(BASE_URI);
        String subscriptionID = "5d2877f2eb5b418a8924292a56188d01";
        invokeBuilder = target.request(MediaType.APPLICATION_JSON)
                .header("Ocp-Apim-Subscription-Key", subscriptionID);
    }

    /*
     Get the result of OCR from specified URI
    */
    public OCRResponseJSONBody getOCRAnalysisResult(String pictURI) {
        OCRRequestJSONBody entity = new OCRRequestJSONBody();
        entity.setUrl(pictURI);
        Response response = invokeBuilder.post(Entity.entity(pictURI, MediaType.APPLICATION_JSON_TYPE));
        if (checkRequestSuccess(response)) {
            OCRResponseJSONBody result = response.readEntity(OCRResponseJSONBody.class);
            return result;
        } else {
            printErrorMessage(response);
            return null;
        }
    }

    /*
     Get the result of OCR from file
    */
    public OCRResponseJSONBody getOCRAnalysisResult(Path path) throws IOException {

        File file = new File(path.toString());
        int size = (int) file.length();
        byte[] readAllBytes = new byte[size];

        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(readAllBytes, 0, readAllBytes.length);
            buf.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
             e.printStackTrace();
        }
        return getOCRAnalysisResult(readAllBytes);
    }

    /*
  Print out the Result of OCR to STDOUT
 */
    public void printOCRResult(OCRResponseJSONBody body) {
        String language = body.getLanguage();
        String orientation = body.getOrientation();
        String textAngle = body.getTextAngle();

        List<Regions> regions = body.getRegions();
        for (Regions region: regions) {
            List<Lines> lines = region.getLines();
            for (Lines line: lines) {
                List<Words> words = line.getWords();
                for (Words word: words) {
                    String text = word.getText();
                    System.out.println(text);
                }
                System.out.println("");
            }

        }
//        regions.stream().forEach(region -> {
//            List<Lines> lines = region.getLines();
//            lines.stream().forEach(line -> {
//                List<Words> words = line.getWords();
//                words.stream().forEach(word -> {
//                    String text = word.getText();
//                    System.out.print(text);
//                });
//                System.out.println("");
//            });
//        });
    }

    /*
      Get the result of OCR from binary data
     */
    public OCRResponseJSONBody getOCRAnalysisResult(byte[] binaryImage) throws IOException {
        Response response = invokeBuilder.post(Entity.entity(binaryImage, MediaType.APPLICATION_OCTET_STREAM_TYPE));
        if (checkRequestSuccess(response)) {
            OCRResponseJSONBody result = response.readEntity(OCRResponseJSONBody.class);
            return result;
        } else {
            printErrorMessage(response);
            return null;
        }
    }

    /*
      Evaluate the response
    true : SUCCESS
    false : FAILED
    */
    private boolean checkRequestSuccess(Response response) {
        Response.StatusType statusInfo = response.getStatusInfo();
        Response.Status.Family family = statusInfo.getFamily();
        return family != null && family == Response.Status.Family.SUCCESSFUL;
    }

    /*
      Print out the Result of OCR to STDOUT
     */
    private void printErrorMessage(Response response) {
        OCRResponseError error = response.readEntity(OCRResponseError.class);
        Logger.getLogger(OCRReader.class.getName()).log(Level.SEVERE, "{0}:{1}", new Object[]{error.getCode(), error.getMessage()});
    }
}

