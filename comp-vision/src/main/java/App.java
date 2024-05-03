import com.microsoft.azure.cognitiveservices.vision.computervision.*;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.*;


import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class App {

	public static void main(String[] args) {

	    String localImagePath = "src/main/resources/landmark.jpg";
	    
	    String subscriptionKey = System.getenv("COMPUTER_VISION_SUBSCRIPTION_KEY");
	    if (subscriptionKey == null) {
	      System.out.println("\n\nPlease set the COMPUTER_VISION_SUBSCRIPTION_KEY environment variable." +
	      "\n**You might need to restart your shell or IDE after setting it.**\n");
	      System.exit(0);
	    }

	    String endpoint = System.getenv("COMPUTER_VISION_ENDPOINT");
	    if (endpoint == null) {
	      System.out.println("\n\nPlease set the COMPUTER_VISION_ENDPOINT environment variable." +
	      "\n**You might need to restart your shell or IDE after setting it.**\n");
	      System.exit(0);
	    }

	    ComputerVisionClient computerVisionClient = 
	            ComputerVisionManager.authenticate(subscriptionKey).withEndpoint(endpoint);
	    /**
	     * END - Authenticate
	     */
	    
	    CategorizeImage(computerVisionClient, localImagePath, localImagePath);

	}
	
	 /**  
	   * CATEGORIZE IMAGE:
	   * API call: AnalyzeImageInStream & AnalyzeImage
	   * Displays image categories and their confidence values.
	   */
	  public static void CategorizeImage(ComputerVisionClient client, String localImagePath, String remoteImageURL) {
	    System.out.println("-----------------------------------------------");   
	    System.out.println("CATEGORIZE IMAGE");
	    System.out.println();
	    try {
	      File localImage = new File(localImagePath);
	      byte[] imgBytes = Files.readAllBytes(localImage.toPath());

	      List<VisualFeatureTypes> features = new ArrayList<>();
	      features.add(VisualFeatureTypes.CATEGORIES);

	      // Categorize local image
	      ImageAnalysis analysisLocal = client.computerVision().analyzeImageInStream()
	          .withImage(imgBytes)
	          .withVisualFeatures(features)
	          .execute();
	      
	      // Categorize remote URL image 
	      ImageAnalysis analysisRemote = client.computerVision().analyzeImage()
	      .withUrl(remoteImageURL)
	      .withVisualFeatures(features)
	      .execute();

	      ImageAnalysis[] results = { analysisLocal, analysisRemote };

	      // Print results of local and remote images
	      for (ImageAnalysis result : results){
	        String location = null;
	        ImageAnalysis analysis = null;
	        if (result == analysisLocal) { analysis = analysisLocal; location = "local"; }
	        else { analysis = analysisRemote; location = "remote"; }

	        System.out.println("\nCategories from " + location + " image: ");
	        if (analysis.categories().size() == 0) {
	          System.out.println("No categories detected in " + location + " image.");
	        } else {
	          for (Category category : analysis.categories()) {
	              System.out.printf("\'%s\' with confidence %2.2f%%\n", category.name(), category.score() * 100);
	          }
	        }
	      }  
	    } catch (Exception e) {
	        System.out.println(e.getMessage());
	        e.printStackTrace();
	    }
	    System.out.println();
	  }
	  //  END - Categorize Image

}
