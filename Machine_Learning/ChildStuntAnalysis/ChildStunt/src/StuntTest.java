import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class StuntTest {
	static final String dir = "C:/Users/zhou2/Documents/tc/med/code/";
	static final String train = dir + "javatrain.csv";
	static final String test = dir + "test.csv";

	public static String[] readFile(String file) {
		ArrayList<String> lines = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String[] tests = new String[lines.size()];
		return lines.toArray(tests);
	}

	static void writeToFile(String name, double[] vals) {
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(dir + name));
			for (double d : vals) {
				w.write(Double.toString(d));
				w.newLine();
			}
			w.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void writeToFile(String name, int[] ids, double[][] vals) {
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(dir + name));
			for (int i = 0; i < ids.length; ++i) {
				w.write(Integer.toString(ids[i]) + ",");
				for (double d : vals[i]) {
					w.write(Double.toString(d) + ",");
				}
				w.newLine();
			}
			w.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void writeToFile(String name, int[] ids, double[] vals, double[] vals2) {
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(dir + name));
			for (int i = 0; i < ids.length; ++i) {
				w.write(Integer.toString(ids[i]) + ",");
				w.write(Double.toString(vals[i]) + ",");
				w.write(Double.toString(vals2[i]));
				w.newLine();
			}
			w.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void writeToFile(String name, int[] vals) {
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(dir + name));
			for (int d : vals) {
				w.write(Integer.toString(d));
				w.newLine();
			}
			w.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String[] training = readFile(train);
		String[] testing = readFile(test);
		ChildStuntedness predictor = new ChildStuntedness();
		predictor.m_devel = true;

		double[] results = predictor.predict(training, testing);

		// writeToFile("Fm2.csv", predictor.m_ids02, predictor.m_f02);
		// writeToFile("Rm2.csv", predictor.m_ids02, predictor.m_w02,
		// predictor.m_d02);
		writeToFile("results.csv", results);
		writeToFile("IDs.csv", predictor.m_IDs);
		// writeToFile("Truths.csv", predictor.m_alls);
		System.out.println("OK");
	}

}
