import java.util.ArrayList;

public class ChildStuntedness {
	private double[] thetaw = null; // last one is theta0
	private double[] thetad = null;
	public double[] Jws = null;
	public double[] Jds = null;
	public double[] JVws = null;
	public double[] JVds = null;
	public int iteration = 100;
	public double rate = 0.2;
	public boolean useSecondOrder = false;
	public double trainPortion = 0.8;
	public boolean devel = false;
	public Entries testEntries = null;

	public double[] predict(String[] training, String[] testing) {
		Entries data = buildFeatures(training);
		Double tmp = Math.ceil(data.uniqueIds * trainPortion);
		int idCut = tmp.intValue();
		int counter = 0, fortrain = 0, cid = -1;
		for (int id : data.ids) {
			if (id != cid) {
				cid = id;
				++counter;
			}
			if (counter > idCut)
				break;
			++fortrain;
		}

		double[][] features = new double[fortrain][];
		double[] weights = new double[fortrain];
		double[] dates = new double[fortrain];
		for (int i = 0; i < fortrain; ++i) {
			features[i] = data.features[i];
			weights[i] = data.weights[i];
			dates[i] = data.dates[i];
		}
		Entries trains = new Entries();
		trains.features = features;
		trains.weights = weights;
		trains.dates = dates;
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
		Entries validates = new Entries();
		validates.features = features;
		validates.weights = weights;
		validates.dates = dates;
		validates.ids = ids;
		train(trains, validates, iteration);

		Entries tests = predicate(testing);
		double[] vals = null;
		if (devel)
			testEntries = tests;
		else {
			Entry[] preds = new Entry[tests.uniqueIds];
			double accuw = 0, accud = 0;
			int idx = 0;
			cid = tests.ids[0];
			counter = 0;
			for (int i = 0; i < tests.weights.length; ++i) {
				if (tests.ids[i] == cid) {
					accuw += tests.weights[i];
					accud += tests.dates[i];
					++counter;
				} else {
					Entry e = preds[idx++];
					e.weight = accuw / counter;
					accuw = 0;
					e.date = accud / counter;
					accud = 0;
					e.id = cid;
					counter = 0;
					cid = tests.ids[i];
				}
			}
			// TODO: sort preds
			vals = new double[tests.uniqueIds * 2];
			idx = 0;
			for (int i = 0; i < preds.length; ++i) {
				Entry e = preds[i];
				vals[idx++] = e.date;
				vals[idx++] = e.weight;
			}
		}

		return vals;
	}

	class Entry {
		public double weight = 0;
		public double date = 0;
		public int id = -1;
	}

	private Entries predicate(String[] tests) {
		Entries results = buildFeatures(tests);
		double[] weights = new double[results.features.length];
		double[] dates = new double[results.features.length];

		for (int i = 0; i < results.features.length; ++i) {
			double[] feature = results.features[i];
			weights[i] = predictOne(feature, thetaw);
			dates[i] = predictOne(feature, thetad);
		}

		return results;
	}

	private void train(Entries data, Entries validation, int iter) {
		final double[] lambdaws = { 0, 0.1, 0.3, 0.9, 1.2 };
		final double[] lambdads = { 0, 0.1, 0.3, 0.9, 1.2 };
		final int n = data.features[0].length + 1;
		JVws = new double[lambdaws.length];
		double[] theta = new double[n];
		CostGradient cg = costWithGradient(validation.features,
				validation.weights, theta, 0);
		double cost = cg.cost;

		for (int i = 0; i < lambdaws.length; ++i) {
			double[] tmpJs = new double[iter];
			for (int j = 0; j < iter; ++j) {
				cg = costWithGradient(data.features, data.weights, theta,
						lambdaws[i]);
				tmpJs[j] = cg.cost;
				double[] gradient = cg.gradient;
				for (int t = 0; t < n; ++t)
					theta[t] -= rate * gradient[t];
			}
			cg = costWithGradient(validation.features, validation.weights,
					theta, 0);
			JVws[i] = cg.cost;
			if (cg.cost < cost) {
				cost = cg.cost;
				Jws = tmpJs;
				thetaw = theta;
			}
			// else break; // break on final
			theta = new double[n];
		}

		JVds = new double[lambdads.length];
		theta = new double[n];
		cg = costWithGradient(validation.features, validation.dates, theta, 0);
		cost = cg.cost;
		for (int i = 0; i < lambdads.length; ++i) {
			double[] tmpJs = new double[iter];
			for (int j = 0; j < iter; ++j) {
				cg = costWithGradient(data.features, data.dates, theta,
						lambdads[i]);
				tmpJs[j] = cg.cost;
				double[] gradient = cg.gradient;
				for (int t = 0; t < n; ++t)
					theta[t] -= rate * gradient[t];
			}
			cg = costWithGradient(validation.features, validation.dates, theta,
					0);
			JVds[i] = cg.cost;
			if (cg.cost < cost) {
				cost = cg.cost;
				Jds = tmpJs;
				thetad = theta;
			}
			// else break; // break on final
			theta = new double[n];
		}
	}

	private class CostGradient {
		public double cost = 0;
		public double[] gradient = null;
	}

	private CostGradient costWithGradient(double[][] entries, double[] ys,
			double[] theta, double lambda) {
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
			gradient[n - 1] = err; // X0 are all 1's
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
		for (int j = 0; j < n - 1; ++j)
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
			double[] vals = new double[11];
			vals[0] = time;
			vals[1] = meas;
			vals[2] = Double.parseDouble(s[1]);
			vals[3] = sex;
			vals[4] = status;
			for (int i = 5; i < s.length; ++i)
				vals[i] = Double.parseDouble(s[i]);
			if (useSecondOrder)
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
		// content.maxMeasCount = maxMeasCount;
		for (int i = 0; i < content.ids.length; ++i)
			content.ids[i] = ids.get(i);

		content.features = (double[][]) features.toArray();
		int size = weights.size();
		if (size > 0) {
			double[] w = new double[size], d = new double[size];
			content.weights = w;
			content.dates = d;
			for (int i = 0; i < size; ++i) {
				w[i] = weights.get(i);
				d[i] = dates.get(i);
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
