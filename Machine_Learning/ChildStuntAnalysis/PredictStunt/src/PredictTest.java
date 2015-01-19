java.io.BufferedReader;

public class PredictTest {
	final String dir = "C:/Users/zhou2/Documents/tc/med/code/";
	final String train = dir + "javatrain.csv";
	final String test = dir + "test.csv";

	public static void main(String[] args) {
		 try(BufferedReader in = new BufferedReader(
		 new FileReader(objFile))){
		 String line = in.readLine();
		
		 while(line != null){
		
		 String[] linesFile = line.split("\n");
		 String line0 = linesFile[0];
		 String line1 = linesFile[1];
		 String line2 = linesFile[2];
		
		
		
		 System.out.println(line0 + "" + line1);
		 line = in.readLine();
		 }
		
		 }
		 catch(IOException e){
		
		 System.out.println(e);
		 }


		System.out.println("Correct rate: " + 50 + " percent");
	}
}
