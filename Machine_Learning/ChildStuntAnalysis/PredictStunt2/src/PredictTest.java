import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class PredictTest {
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
		// ChildStuntedness.Entry[] orders = new ChildStuntedness.Entry[5];
		// for (int i = 0; i < 5; ++i) {
		// orders[i] = new ChildStuntedness.Entry();
		// orders[i].id = 6 - i;
		// }
		// System.out.print("Before sort:");
		// for (int i = 0; i < orders.length; ++i)
		// System.out.print(orders[i].id + ", ");
		// System.out.println();
		// Arrays.sort(orders);
		// System.out.print("After sort:");
		// for (int i = 0; i < orders.length; ++i)
		// System.out.print(orders[i].id + ", ");
		// System.out.println();

		String[] training = readFile(train);
		String[] testing = readFile(test);
		ChildStuntedness predictor = new ChildStuntedness();

		predictor.devel = true;
		predictor.iteration = 1000;
		predictor.rate = 0.003;
		predictor.trainPortion = 0.75;
		predictor.useSecondOrder = false;
		predictor.normalize = false;
		double[] results = predictor.predict(training, testing);
		// writeToFile("Jds.csv", predictor.Jds);
		// writeToFile("Jws.csv", predictor.Jws);
		// writeToFile("JVds.csv", predictor.JVds);
		// writeToFile("JVws.csv", predictor.JVws);
		// for (int i = 0; i < 5; ++i)
		// System.out.println(predictor.Origws[i] + ", " + predictor.Origds[i] +
		// ": " + predictor.Testws[i] + ", "
		// + predictor.Testds[i]);
		// System.out.println();
		// for (int i = predictor.Origds.length - 5; i <
		// predictor.Origds.length; ++i)
		// System.out.println(predictor.Origws[i] + ", " + predictor.Origds[i] +
		// ": " + predictor.Testws[i] + ", "
		// + predictor.Testds[i]);
		if (predictor.devel)
			analyze(predictor.TestIds, predictor.Origds, predictor.Origws, predictor.Testds, predictor.Testws);
		else
			writeToFile("results.csv", results);
		// writeToFile("ows.csv", predictor.Origws);
		// writeToFile("ods.csv", predictor.Origds);
		// writeToFile("ws.csv", predictor.Testws);
		// writeToFile("ds.csv", predictor.Testds);
	}

	static void analyze(int[] ids, double[] ods, double[] ows, double[] ds, double[] ws) {
		// double[] javaw = new double[ods.length], javad = new
		// double[ods.length];
		// min/max pairs
		double[] da = { 1000, -1 }, wa = { 1000, -1 }; // min/max date, weight
		double[] eda = { 1000, -1 }, ewa = { 1000, -1 }; // min/max error on
															// entry
		double[] peda = { 1000, -1 }, pewa = { 1000, -1 }; // min/max Relative
															// error on entry
		double[] emd = { 1000, -1 }, emw = { 1000, -1 }; // min/max error using
															// average per ID
		double[] pemd = { 1000, -1 }, pemw = { 1000, -1 };// min/max relative
															// error using
															// average per ID
		int cid = ids[0], counter = 0;
		double d = ods[0], w = ows[0]; // same orig date/weight per ID
		double accud = 0, accuw = 0;
		for (int i = 0; i < ids.length; ++i) {
			double ed = -1, ew = -1, ped = -1, pew = -1;
			if (cid == ids[i]) {
				accud += ds[i];
				accuw += ws[i];
				++counter;
			} else {
				if (d < da[0])
					da[0] = d;
				if (d > da[1])
					da[1] = d;
				if (w < wa[0])
					wa[0] = w;
				if (w > wa[1])
					wa[1] = w;
				ed = Math.abs(accud / counter - d);
				ped = ed / d;
				ew = Math.abs(accuw / counter - w);
				pew = ew / w;
				if (ed < emd[0])
					emd[0] = ed;
				if (ed > emd[1])
					emd[1] = ed;
				if (ew < emw[0])
					emw[0] = ew;
				if (ew > emw[1])
					emw[1] = ew;
				if (ped < pemd[0])
					pemd[0] = ped;
				if (ped > pemd[1])
					pemd[1] = ped;
				if (pew < pemw[0])
					pemw[0] = pew;
				if (pew > pemw[1])
					pemw[1] = pew;
				// reset for next ID
				accud = ds[i];
				accuw = ws[i];
				counter = 1;
				cid = ids[i];
				d = ods[i];
				w = ows[i];
			}

			ed = Math.abs(ds[i] - d);
			ped = ed / d;
			ew = Math.abs(ws[i] - w);
			pew = ew / w;
			// javad[i] = ed;
			// javaw[i] = ew;
			if (ed < eda[0])
				eda[0] = ed;
			if (ed > eda[1])
				eda[1] = ed;
			if (ew < ewa[0])
				ewa[0] = ew;
			if (ew > ewa[1])
				ewa[1] = ew;
			if (ped < peda[0])
				peda[0] = ped;
			if (ped > peda[1])
				peda[1] = ped;
			if (pew < pewa[0])
				pewa[0] = pew;
			if (pew > pewa[1])
				pewa[1] = pew;
		}

		writeToFile("jids.csv", ids);
		// writeToFile("javaw.csv", javaw);
		// writeToFile("javad.csv", javad);

		System.out.println("orig date:   " + da[0] + ", " + da[1]);
		System.out.println("orig weight: " + wa[0] + ", " + wa[1]);
		System.out.println("all date:    " + eda[0] + ", " + eda[1]);
		System.out.println("all weight:  " + ewa[0] + ", " + ewa[1]);
		System.out.println("mean date:   " + emd[0] + ", " + emd[1]);
		System.out.println("mean weight: " + emw[0] + ", " + emw[1]);
		System.out.println("all date %:  " + peda[0] * 100 + ", " + peda[1] * 100);
		System.out.println("all weight % :  " + pewa[0] * 100 + ", " + pewa[1] * 100);
		System.out.println("mean date %: " + pemd[0] * 100 + ", " + pemd[1] * 100);
		System.out.println("mean weight %: " + pemw[0] * 100 + ", " + pemw[1] * 100);
	}
}
