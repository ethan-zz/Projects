import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Classify
{

	public static void main(String[] args)
	{
		String testsFolder = "C:/Users/zhouDev.ZBCORP/Downloads/tc/";
		double avg = 0, min = 10e9, max = 0, rate = 0, minrate = 1e9, maxrate = 0;
		Boolean developeMode = true;
		if (developeMode)
		{
			String fileIn = "TrainingData.csv";
			// ClassifierHelper h = new ClassifierHelper();
			// h.seperate(fileIn);
			try
			{
				List<String> all = Files.readAllLines(Paths.get(testsFolder + fileIn), Charset.forName("ISO-8859-1"));
				int toIndex = (int) (all.size() * 0.75);
				// String[] trains = all.toArray(new String[0]);
				String[] trains = all.subList(0, toIndex).toArray(new String[0]);
				String[] tests = all.subList(toIndex, all.size() - 1).toArray(new String[0]);
				AlleleClassifier c = new AlleleClassifier();
				String[] results = c.classify(trains, tests, null);
				double grade = 0;
				int correct = 0;
				int zeros = 0;
				int halves = 0;
				for (int i = 0; i < results.length; ++i)
				{
					String str = tests[i];
					str = str.substring(str.lastIndexOf(',') + 1);
					if (str.equals(results[i]))
					{
						grade += 1.;
						++correct;
					}
					else
					{
						int resColor = AlleleClassifier.getColorCode(results[i]);
						int actColor = AlleleClassifier.getColorCode(str);
						if ((actColor > AlleleClassifier.COLOR2) && (resColor <= AlleleClassifier.COLOR2))
						{
							++zeros;
						}
						else
						{
							++halves;
							grade += 0.5;
						}
						// if (i < 5)
						// System.out.println("\tActual: " + str + "\tResult: "
						// + results[i]);
					}
				}
				rate = (double) correct / results.length * 100.;
				rate = ((int) (rate * 100)) / 100.;
				System.out.println("*** " + correct + " out of " + results.length + "(" + rate + ") correct! " + grade
						+ " from " + halves + " halves and " + zeros + " zeros");
				System.out.println(c.getTraningReport());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			System.out.println("Done: rate = " + rate);
		}
		else
		{
			String[][] testFiles = {
					{ "DataExample0Train.csv", "DataExample0Tests.csv", "DataExample0GroundTruth.csv" },
					{ "DataExample1Train.csv", "DataExample1Tests.csv", "DataExample1GroundTruth.csv" },
					{ "DataExample2Train.csv", "DataExample2Tests.csv", "DataExample2GroundTruth.csv" },
					{ "DataExample3Train.csv", "DataExample3Tests.csv", "DataExample3GroundTruth.csv" },
					{ "DataExample4Train.csv", "DataExample4Tests.csv", "DataExample4GroundTruth.csv" } };

			try
			{
				for (int j = 0; j < testFiles.length; ++j)
				{
					String[] trains = Files.readAllLines(Paths.get(testsFolder + testFiles[j][0]),
							Charset.forName("ISO-8859-1")).toArray(new String[0]);
					String[] tests = Files.readAllLines(Paths.get(testsFolder + testFiles[j][1]),
							Charset.forName("ISO-8859-1")).toArray(new String[0]);
					String[] truths = Files.readAllLines(Paths.get(testsFolder + testFiles[j][2]),
							Charset.forName("ISO-8859-1")).toArray(new String[0]);
					AlleleClassifier c = new AlleleClassifier();
					String[] results = c.classify(trains, tests, truths);
					double grade = 0;
					int correct = 0;
					int zeros = 0;
					int halves = 0;
					for (int i = 0; i < results.length; ++i)
					{
						if (results[i].equals(truths[i]))
						{
							grade += 1.;
							++correct;
						}
						else
						{
							int resColor = AlleleClassifier.getColorCode(results[i]);
							int actColor = AlleleClassifier.getColorCode(truths[i]);
							if ((actColor > AlleleClassifier.COLOR2) && (resColor <= AlleleClassifier.COLOR2))
							{
								++zeros;
							}
							else
							{
								++halves;
								grade += 0.5;
							}
							// if (i < 8)
							// System.out.println("\t#" + i + " Actual: " +
							// tests[i] + ',' + truths[i] + "\tResult: "
							// + results[i]);
						}
					}
					double r = (double) correct / results.length * 100.;
					System.out.println("*** " + correct + " out of " + results.length + "(" + r + ") correct! " + grade
							+ " from " + halves + " halves and " + zeros + " zeros");
					avg += grade;
					if (grade > max)
						max = grade;
					if (grade < min)
						min = grade;
					rate += r;
					if (r < minrate)
						minrate = r;
					if (r > maxrate)
						maxrate = r;
					// System.out.println(c.getTraningReport());
				}
				avg /= testFiles.length;
				rate /= testFiles.length;
				rate = ((int) (rate * 100)) / 100.;
				minrate = ((int) (minrate * 100)) / 100.;
				maxrate = ((int) (maxrate * 100)) / 100.;
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			System.out.println("Done: Average = " + avg + "; min = " + min + "; max = " + max + "; rate = " + rate
					+ "; min = " + minrate + "; max = " + maxrate);
		}
		// System.out.println("Type any key to terminate:\n>>");
		// System.in.read();
	}
}
