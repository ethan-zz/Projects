import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public final class ClassifierHelper
{
	private final static String s_0 = "0";
	private final static String s_01 = "0 or 1";
	private final static String s_1 = "1";
	private final static String s_12 = "1 or 2";
	private final static String s_2 = "2";
	private final static String s_22 = ">2";

	public void seperate(String fileIn)
	{
		int idx = fileIn.indexOf(".csv");
		String ext = fileIn.substring(idx);
		String other = fileIn.substring(0, idx);
		File fin = new File(fileIn);
		File f0 = new File(other + "0" + ext);
		File f01 = new File(other + "01" + ext);
		File f1 = new File(other + "1" + ext);
		File f12 = new File(other + "12" + ext);
		File f2 = new File(other + "2" + ext);
		File f22 = new File(other + "22" + ext);

		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(fin));
			PrintWriter w0 = new PrintWriter(new FileWriter(f0));
			PrintWriter w01 = new PrintWriter(new FileWriter(f01));
			PrintWriter w1 = new PrintWriter(new FileWriter(f1));
			PrintWriter w12 = new PrintWriter(new FileWriter(f12));
			PrintWriter w2 = new PrintWriter(new FileWriter(f2));
			PrintWriter w22 = new PrintWriter(new FileWriter(f22));

			String line = reader.readLine();
			while (null != line)
			{
				String[] strs = line.split(",");
				
				if (s_0.equals(strs[4]))
					w0.println(line);
				else if(s_01.equals(strs[4]))
					w01.println(line);
				else if(s_1.equals(strs[4]))
					w1.println(line);
				else if(s_12.equals(strs[4]))
					w12.println(line);
				else if(s_2.equals(strs[4]))
					w2.println(line);
				else if(s_22.equals(strs[4]))
					w22.println(line);
				
				line = reader.readLine();
			}
			reader.close();
			w0.close();
			w01.close();
			w1.close();
			w12.close();
			w2.close();
			w22.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}
}
