import java.util.ArrayList;
import java.util.Arrays;

public class ChildStuntedness {
	public int iteration = 1000;
	public double rate = 0.003;
	public boolean useSecondOrder = true;
	public double trainPortion = 0.80;
	public boolean devel = false;
	public boolean normalize = true;

	private double[] thetaw = null; // last one is theta0
	private double[] thetad = null;
	private double[][] normFactors = null;
	public double[] Jws = null;
	public double[] Jds = null;
	public double[] JVws = null;
	public double[] JVds = null;
	public int[] TestIds = null;
	public double[] Origws = null;
	public double[] Origds = null;
	public double[] Testws = null;
	public double[] Testds = null;
	private double[] trSinv = null;
	private double[] valSinv = null;
	private double[] testSinv = null;

	public double[] predict(String[] training, String[] testing) {
		Entries data = buildFeatures(training);
		// Use all training data for normalization
		if (this.normalize) {
			calcNormalizeFactors(data.features);
			normalizeFeatures(data.features);
		}

		Double tmp = Math.ceil(data.uniqueIds * this.trainPortion);
		int idCut = tmp.intValue();
		int counter = 0, fortrain = 0, cid = -1;
		for (int id : data.ids) {
			if (id != cid) {
				cid = id;
				++counter;
			}
			if (counter == idCut)
				break;
			++fortrain;
		}
		Entries trains = new Entries();
		double[] uws = new double[counter], uds = new double[counter];
		int[] idstrides = new int[counter];
		for (int i = 0; i < counter; ++i) {
			idstrides[i] = data.idStrides[i];
			uws[i] = data.uniqueWeights[i];
			uds[i] = data.uniqueDates[i];
		}
		trains.uniqueIds = counter;
		trains.idStrides = idstrides;
		trains.uniqueWeights = uws;
		trains.uniqueDates = uds;
		double[][] features = new double[fortrain][];
		double[] weights = new double[fortrain];
		double[] dates = new double[fortrain];
		for (int i = 0; i < fortrain; ++i) {
			features[i] = data.features[i];
			weights[i] = data.weights[i];
			dates[i] = data.dates[i];
		}
		trains.features = features;
		trains.weights = weights;
		trains.dates = dates;

		Entries validates = new Entries();
		validates.uniqueIds = data.uniqueIds - counter;
		uws = new double[validates.uniqueIds];
		uds = new double[validates.uniqueIds];
		idstrides = new int[validates.uniqueIds];
		int idxstride = 0;
		for (int i = counter; i < data.uniqueIds; ++i, ++idxstride) {
			idstrides[idxstride] = data.idStrides[i];
			uws[idxstride] = data.uniqueWeights[i];
			uds[idxstride] = data.uniqueDates[i];
		}
		validates.idStrides = idstrides;
		validates.uniqueWeights = uws;
		validates.uniqueDates = uds;
		int left = data.weights.length - fortrain;
		features = new double[left][];
		weights = new double[left];
		dates = new double[left];
		int[] ids = new int[left];
		counter = 0;
		for (; fortrain < data.weights.length; ++fortrain, ++counter) {
			features[counter] = data.features[fortrain];
			weights[counter] = data.weights[fortrain];
			dates[counter] = data.dates[fortrain];
			ids[counter] = data.ids[fortrain];
		}
		validates.features = features;
		validates.weights = weights;
		validates.dates = dates;
		validates.ids = ids;
		if (this.normalize) {
			calcNormalizeFactors(trains.features);
			normalizeFeatures(trains.features);
			normalizeFeatures(validates.features);
		}
		this.trSinv = calcInverseCovMatrix(trains);
		this.valSinv = calcInverseCovMatrix(validates);
		train(trains, validates, this.iteration);
		// System.out.print("\nthetaw#" + thetaw.length + ": ");
		// for (int i = 0; i < thetaw.length; ++i)
		// System.out.print(thetaw[i] + ", ");
		// System.out.println();
		// System.out.print("thetad#" + thetad.length + ": ");
		// for (int i = 0; i < thetad.length; ++i)
		// System.out.print(thetad[i] + ", ");
		// System.out.println();

		Entries tests = predicate(testing);
		double[] vals = null;
		if (this.normalize)
			normalizeFeatures(tests.features);
		if (this.devel) {
			this.TestIds = tests.ids;
			this.Testds = tests.dates;
			this.Testws = tests.weights;
		} else {
			Entry[] preds = new Entry[tests.uniqueIds];
			double accuw = 0, accud = 0;
			int idx = 0, lastIdx = tests.weights.length - 1;
			cid = tests.ids[0];
			counter = 0;
			for (int i = 0; i < tests.weights.length; ++i) {
				boolean addEntry = false;
				if (tests.ids[i] == cid) {
					accuw += tests.weights[i];
					accud += tests.dates[i];
					++counter;
				} else
					addEntry = true;
				// Add last one
				if (i == lastIdx)
					addEntry = true;
				if (addEntry) {
					Entry e = new Entry();
					preds[idx++] = e;
					e.weight = accuw / counter;
					accuw = tests.weights[i];
					e.date = accud / counter;
					accud = tests.dates[i];
					e.id = cid;
					counter = 1;
					cid = tests.ids[i];
				}
			}
			// sort preds
			Arrays.sort(preds);
			vals = new double[tests.uniqueIds * 2];
			idx = 0;
			for (int i = 0; i < preds.length; ++i) {
				Entry e = preds[i];
				vals[idx++] = e.date;
				vals[idx++] = e.weight;
			}
		}

		System.out.println("trains uniqueIDs: " + trains.uniqueWeights.length);
		System.out.println("validates uniqueIDs: " + validates.uniqueWeights.length);
		if (this.devel)
			System.out.println("tests uniqueIDs: " + tests.uniqueWeights.length);
		System.out.print("Inv train: ");
		for (double i : this.trSinv)
			System.out.print(i + ", ");
		System.out.println();
		System.out.print("Inv val: ");
		for (double i : this.valSinv)
			System.out.print(i + ", ");
		System.out.println();
		if (this.testSinv != null) {
			System.out.print("Inv test: ");
			for (double i : this.testSinv)
				System.out.print(i + ", ");
			System.out.println();
		}
		return vals;
	}

	private double[] calcInverseCovMatrix(Entries data) {
		double[] muStdw = Util.calcMeanStdDev(data.uniqueWeights);
		double[] muStdd = Util.calcMeanStdDev(data.uniqueDates);
		double[] covcorr = Util.calcCovCorr(data.uniqueWeights, muStdw[0], muStdw[1], data.uniqueDates, muStdd[0],
				muStdd[1]);
		System.out.print("Cov/Corr: ");
		for (int i = 0; i < covcorr.length; ++i)
			System.out.print(covcorr[i] + ", ");
		System.out.println();

		double[] orig = { covcorr[0], covcorr[1], covcorr[2], covcorr[3] };
		return Util.inverse(orig);
	}

	private void calcNormalizeFactors(double[][] data) {
		this.normFactors = new double[data[0].length][];
		int m = data.length;
		double[] tmp = new double[m];
		for (int i = 0; i < this.normFactors.length; ++i) {
			for (int j = 0; j < m; ++j)
				tmp[j] = data[j][i];
			this.normFactors[i] = Util.calcMeanStdDev(tmp);
		}
	}

	private void normalizeFeatures(double[][] data) {
		for (int i = 0; i < data.length; ++i)
			for (int j = 0; j < this.normFactors.length; ++j)
				data[i][j] = (data[i][j] - this.normFactors[j][0]) / this.normFactors[j][1];
	}

	class Entry implements Comparable<Entry> {
		public double weight = 0;
		public double date = 0;
		public int id = -1;

		public int compareTo(Entry that) {
			if (this.id < that.id)
				return -1;
			if (this.id > that.id)
				return 1;
			return 0;
		}
	}

	private Entries predicate(String[] tests) {
		Entries results = buildFeatures(tests);
		double[] weights = new double[results.features.length];
		double[] dates = new double[results.features.length];

		if (this.devel) {
			this.Origds = results.dates;
			this.Origws = results.weights;
			this.testSinv = calcInverseCovMatrix(results);
		}

		for (int i = 0; i < results.features.length; ++i) {
			double[] feature = results.features[i];
			weights[i] = predictOne(feature, this.thetaw);
			dates[i] = predictOne(feature, this.thetad);
		}
		results.dates = dates;
		results.weights = weights;
		return results;
	}

	private void train(Entries data, Entries validation, int iter) {
		final double[] lambdaws = { 0, 0.1, 0.3, 0.9, 1.2 };
		final double[] lambdads = { 0, 0.1, 0.3, 0.9, 1.2 };
		final int n = data.features[0].length + 1;
		this.JVws = new double[lambdaws.length];
		double[] theta = new double[n];
		CostGradient cg = costWithGradient(validation.features, validation.weights, theta, 0);
		double cost = cg.cost;

		for (int i = 0; i < lambdaws.length; ++i) {
			double[] tmpJs = new double[iter];
			for (int j = 0; j < iter; ++j) {
				cg = costWithGradient(data.features, data.weights, theta, lambdaws[i]);
				tmpJs[j] = cg.cost;
				double[] gradient = cg.gradient;
				for (int t = 0; t < n; ++t)
					theta[t] -= this.rate * gradient[t];
			}
			cg = costWithGradient(validation.features, validation.weights, theta, 0);
			this.JVws[i] = cg.cost;
			if (cg.cost < cost) {
				cost = cg.cost;
				this.Jws = tmpJs;
				this.thetaw = theta;
			}
			// else break; // break on final
			theta = new double[n];
		}

		this.JVds = new double[lambdads.length];
		theta = new double[n];
		cg = costWithGradient(validation.features, validation.dates, theta, 0);
		cost = cg.cost;
		for (int i = 0; i < lambdads.length; ++i) {
			double[] tmpJs = new double[iter];
			for (int j = 0; j < iter; ++j) {
				cg = costWithGradient(data.features, data.dates, theta, lambdads[i]);
				tmpJs[j] = cg.cost;
				double[] gradient = cg.gradient;
				for (int t = 0; t < n; ++t)
					theta[t] -= this.rate * gradient[t];
			}
			cg = costWithGradient(validation.features, validation.dates, theta, 0);
			this.JVds[i] = cg.cost;
			if (cg.cost < cost) {
				cost = cg.cost;
				this.Jds = tmpJs;
				this.thetad = theta;
			}
			// else break; // break on final
			theta = new double[n];
		}
	}

	private class CostGradient {
		public double cost = 0;
		public double[] gradient = null;
	}

	private CostGradient costWithGradient(double[][] entries, double[] ys, double[] theta, double lambda) {
		int n = theta.length;
		double cost = 0;
		double[] gradient = new double[n];
		for (int i = 0; i < entries.length; ++i) {
			double[] feature = entries[i];
			double y = predictOne(feature, theta);
			double err = y - ys[i];
			cost += err * err;

			for (int j = 0; j < n - 1; ++j)
				gradient[j] += err * feature[j];
			gradient[n - 1] += err; // X0 are all 1's
		}

		if (lambda > 0) {
			double regularization = 0;
			for (int j = 0; j < n - 1; ++j) {
				regularization += theta[j] * theta[j];
				gradient[j] += lambda * theta[j];

			}
			cost += lambda * regularization;
		}

		int m = entries.length;
		CostGradient result = new CostGradient();
		result.cost = cost / m / 2;
		for (int j = 0; j < n; ++j)
			gradient[j] /= m;
		result.gradient = gradient;
		return result;
	}

	private double predictOne(double[] feature, double[] theta) {
		int n = theta.length;
		double y = theta[n - 1]; // bias term
		for (int j = 0; j < n - 1; ++j)
			y += feature[j] * theta[j];
		return y;
	}

	private class Entries {
		public int[] ids = null;
		public double[][] features = null;
		public double[] weights = null;
		public double[] dates = null;
		public int uniqueIds = 0;
		public int[] idStrides = null;
		public double[] uniqueWeights = null;
		public double[] uniqueDates = null;
		// public int maxMeasCount = 0;
	}

	private Entries buildFeatures(String[] raw) {
		ArrayList<double[]> features = new ArrayList<double[]>();
		ArrayList<Integer> ids = new ArrayList<Integer>();
		ArrayList<Double> weights = new ArrayList<Double>();
		ArrayList<Double> dates = new ArrayList<Double>();
		int cid = -1;
		int uniqueIds = 0;
		// int maxMeasCount = 0, measCount = 0;
		double time = 0, meas = 0, sex = 0, status = 0;
		for (String one : raw) {
			String[] s = one.split(",");
			int id = Integer.parseInt(s[0]);
			if (id != cid) {
				// if (measCount > maxMeasCount)
				// maxMeasCount = measCount;
				// measCount = 0;
				++uniqueIds;
				cid = id;
				time = Double.parseDouble(s[1]);
				sex = Double.parseDouble(s[2]);
				status = Double.parseDouble(s[3]);
				meas = Double.parseDouble(s[4]);
				continue;
			}

			// ++measCount;
			ids.add(id);
			double[] vals = new double[12];
			vals[0] = time;
			vals[1] = meas;
			vals[2] = Double.parseDouble(s[1]);
			vals[3] = sex;
			vals[4] = status;
			for (int i = 5; i < 12; ++i)
				vals[i] = Double.parseDouble(s[i]);
			if (this.useSecondOrder)
				vals = toSecondOrder(vals);
			features.add(vals);
			if (s.length > 12) {
				weights.add(Double.parseDouble(s[12]));
				dates.add(Double.parseDouble(s[13]));
			}
		}

		Entries content = new Entries();
		content.ids = new int[ids.size()];
		content.uniqueIds = uniqueIds;
		int[] strides = new int[uniqueIds];
		content.idStrides = strides;
		// content.maxMeasCount = maxMeasCount;
		content.ids[0] = ids.get(0);
		cid = content.ids[0];
		int stride = 1, idxme = 0, lastone = content.ids.length - 1;
		for (int i = 1; i < content.ids.length; ++i) {
			boolean addme = false;
			content.ids[i] = ids.get(i);
			if (cid == content.ids[i])
				++stride;
			else
				addme = true;
			if (i == lastone)
				addme = true;
			if (addme) {
				strides[idxme++] = stride;
				cid = content.ids[i];
				stride = 1;
			}
		}
		content.features = new double[ids.size()][];
		features.toArray(content.features);
		int size = weights.size();
		if (size > 0) {
			double[] w = new double[size], d = new double[size];
			content.weights = w;
			content.dates = d;
			for (int i = 0; i < size; ++i) {
				w[i] = weights.get(i);
				d[i] = dates.get(i);
			}
			double[] uw = new double[uniqueIds], ud = new double[uniqueIds];
			content.uniqueWeights = uw;
			content.uniqueDates = ud;
			int idxstride = 0;
			for (int i = 0; i < content.idStrides.length; ++i) {
				uw[i] = content.weights[idxstride];
				ud[i] = content.dates[idxstride];
				idxstride += content.idStrides[i];
			}
		}
		return content;
	}

	private double[] toSecondOrder(double[] data) {
		double[] vals = new double[data.length * (data.length + 3) / 2];
		// Original values
		int idx = 0;
		for (int i = 0; i < data.length; ++i)
			vals[idx++] = data[i];
		// Now the pair-wise products
		for (int i = 0; i < data.length; ++i) {
			for (int j = i; j < data.length; ++j)
				vals[idx++] = data[i] * data[j];
		}
		return vals;
	}
}

class Util {
	static double[] calcMeanStdDev(double[] vals) {
		double[] results = { 0, 0 };

		for (int i = 0; i < vals.length; ++i)
			results[0] += vals[i];
		results[0] /= vals.length;

		for (int i = 0; i < vals.length; ++i)
			results[1] += (vals[i] - results[0]) * (vals[i] - results[0]);
		results[1] /= (vals.length - 1);
		results[1] = Math.sqrt(results[1]);

		return results;
	}

	// [cov11, cov12, cov21, cov22, 1, corr12, corr21, 1]
	static double[] calcCovCorr(double[] v1, double u1, double std1, double[] v2, double u2, double std2) {
		double[] covcorr = new double[8];
		double accu = 0;
		for (int i = 0; i < v1.length; ++i)
			accu += (v1[i] - u1) * (v2[i] - u2);
		covcorr[0] = std1 * std1;
		covcorr[1] = accu / (v1.length - 1);
		covcorr[2] = covcorr[1];
		covcorr[3] = std2 * std2;
		covcorr[4] = 1;
		covcorr[5] = covcorr[1] / std1 / std2;
		covcorr[6] = covcorr[5];
		covcorr[7] = 1;
		return covcorr;
	}

	static double[] inverse(double[] m) {
		double[] inv = new double[4];
		double a = m[0], b = m[1], c = m[2], d = m[3];
		double div = a * d - b * c;
		inv[0] = d / div;
		inv[1] = -b / div;
		inv[2] = -c / div;
		inv[3] = a / div;
		return inv;
	}
	// static void normalize(double[] vals, double[] centerAndRange) {
	// for (int i = 0; i < vals.length; ++i)
	// vals[i] = (vals[i] - centerAndRange[0]) / centerAndRange[1];
	// }
}
