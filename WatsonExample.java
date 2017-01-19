import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import com.ibm.watson.developer_cloud.concept_insights.v2.ConceptInsights;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.Annotations;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.Graph;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.ScoredConcept;
import com.ibm.watson.developer_cloud.document_conversion.v1.DocumentConversion;
import com.ibm.watson.developer_cloud.util.CredentialUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class WatsonExample {
	public static void loadCredentialsFromFile() {
		try {
			CredentialUtils.setServices(IOUtils.toString(new FileInputStream("src/vcap.txt")));
		} catch (Exception e) {
			System.out.println("Error reading vcap.txt file");
			e.printStackTrace();
		}
	}



	public static void main(String[] args) {
		loadCredentialsFromFile();
		ConceptInsights conceptInsights = new ConceptInsights();
		DocumentConversion documentConversion = new DocumentConversion(DocumentConversion.VERSION_DATE_2015_12_01);
		File docDir = new File("docs");
		String[] available = {"html","pdf"};
		//extract the text from the PDF


		Iterator<File> availableFiles = FileUtils.iterateFiles(docDir, available, true );
		while (availableFiles.hasNext()) {
			String text = convertPDFToHTML(documentConversion, availableFiles.next());

			// annotate the text
			Annotations annotations = annotateText(conceptInsights, text);


			// save concepts to file
			saveToFile(annotations);
		}
	}



	private static void saveToFile(Annotations annotations) {
		for (ScoredConcept concept : annotations.getAnnotations()) {
			int count = 0;
			try {
				File fileOfResults = new File("bin/completed"+count+".txt");
				FileUtils.writeStringToFile(fileOfResults, concept.toString() + "\r\n",true);
				count++;
			} catch (IOException e) {
				System.out.println("Could not find results file");
				e.printStackTrace();
			}
		}
	}



	private static Annotations annotateText(ConceptInsights conceptInsights, String text) {
		Annotations annotations = conceptInsights.annotateText(Graph.WIKIPEDIA, text);
		return annotations;
	}


	private static String convertPDFToHTML(DocumentConversion documentConversion, File fileToConvert) {

		String text = documentConversion.convertDocumentToText(fileToConvert);
		return text;

//OK, so I broke the document conversion process. I will need to break it up, 
//and remove duplicates as needed. That means that I will have to refactor the return value
		//File.createTempFile(String prefix, String suffix, File directory)

		/*private static int count = 0;

		public static void record(Message  message)//Message is a class 
		    {
		    try
		      {
		        BufferedOutputStream buf=new BufferedOutputStream(
		          new FileOutputStream("E:/kruthika/proj/" + count + ".bin")
		        );
		        byte[] b =serializer.serialize(message);        
		        buf.write(b);
		        buf.flush();

		        count++;

		      }
		    catch(Exception e){System.out.print(e);}
		  }*/

	}
}
