import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.SimpleValueChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
//import org.apache.commons.math3.optim.SimpleVectorValueChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient;
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;

public class ChildStuntedness {
	public double[] m_alls = null;
	public int[] m_IDs = null;
	public boolean m_devel = false;

	private double[][] m_f = null;
	private double[] m_truth = null;
	private double[] m_supplement = null;
	// private int m_cntCost = 0;
	// private int m_cntGrad = 0;
	private double m_J = 0;
	private double[] m_Grad = null;
	private int m_N = 0;
	private double m_lambda = 0;
	private int m_M = 0;
	private boolean m_train = true;

	private double[][] m_theta01 = null;
	private double[][] m_theta02 = null;
	private double[][] m_theta11 = null;
	private double[][] m_theta12 = null;

	private void reset() {
		m_f = null;
		m_supplement = null;
		m_truth = null;
	}

	private void calcCostGradient(double[] theta) {
		int mS = m_train ? 0 : m_M;
		int m = m_train ? m_M : m_f[0].length;
		int N = m_f[0].length;
		double[] ests = new double[m];
		for (int i = mS; i < m; ++i) {
			ests[i] = theta[m_N]; // we keep theta0 at end
			for (int j = 0; j < N; ++j)
				ests[i] += m_f[i][j] * theta[j];
		}
		if (m_supplement != null) {
			for (int i = mS; i < m; ++i)
				ests[i] += m_supplement[i] * theta[N]; // theta(N)
		}

		m_J = 0;
		m_Grad = new double[theta.length];
		for (int i = mS; i < m; ++i) {
			double err = ests[i] - m_truth[i];
			m_J += (err * err);
			for (int j = 0; j < N; ++j)
				m_Grad[j] += (err * m_f[i][j]);
			m_Grad[m_N] += err; // gradient for theta0 last one
		}
		if (m_supplement != null) {
			for (int i = mS; i < m; ++i) {
				double err = ests[i] - m_truth[i];
				m_Grad[N] += (err * m_supplement[i]);
			}
		}

		if (m_lambda > 0) {
			double theta2 = 0;
			for (int j = 0; j < m_N; ++j) {
				theta2 += (theta[j] * theta[j]);
				m_Grad[j] += (m_lambda * theta[j]);
				m_Grad[j] /= m;
			}
			m_J += m_lambda * theta2;
		}

		m_J /= (2 * m);
	}

	class CostFunction implements MultivariateFunction {
		@Override
		public double value(double[] theta) {
			// if (ChildStuntedness.this.m_cntCost ==
			// ChildStuntedness.this.m_cntGrad)
			ChildStuntedness.this.calcCostGradient(theta);
			// ++ChildStuntedness.this.m_cntCost;

			return ChildStuntedness.this.m_J;
		}
	}

	class GradientFunction implements MultivariateVectorFunction {
		@Override
		public double[] value(double[] theta) {
			// if (ChildStuntedness.this.m_cntCost ==
			// ChildStuntedness.this.m_cntGrad)
			ChildStuntedness.this.calcCostGradient(theta);
			// ++ChildStuntedness.this.m_cntGrad;

			return ChildStuntedness.this.m_Grad;
		}
	}

	public double[] predict(String[] training, String[] testing) {
		Entries[] data = buildFeatures(training, true);

		m_theta01 = new double[2][];
		m_theta01[0] = train(data[0].features, null, data[0].weights);
		m_theta01[1] = train(data[0].features, data[0].weights, data[0].dates);

		m_theta02 = new double[2][];
		m_theta02[0] = train(data[1].features, null, data[1].weights);
		m_theta02[1] = train(data[1].features, data[1].weights, data[1].dates);

		m_theta11 = new double[2][];
		m_theta11[0] = train(data[2].features, null, data[2].weights);
		m_theta11[1] = train(data[2].features, data[2].weights, data[2].dates);

		m_theta12 = new double[2][];
		m_theta12[0] = train(data[3].features, null, data[3].weights);
		m_theta12[1] = train(data[3].features, data[3].weights, data[3].dates);

		Entries[] tests = buildFeatures(testing, m_devel);
		double[] w01 = predict(tests[0].features, null, m_theta01[0]);
		double[] d01 = predict(tests[0].features, w01, m_theta01[1]);
		double[] w02 = predict(tests[1].features, null, m_theta02[0]);
		double[] d02 = predict(tests[1].features, w02, m_theta02[1]);
		double[] w11 = predict(tests[2].features, null, m_theta11[0]);
		double[] d11 = predict(tests[2].features, w11, m_theta11[1]);
		double[] w12 = predict(tests[3].features, null, m_theta12[0]);
		double[] d12 = predict(tests[3].features, w12, m_theta12[1]);

		double[][] wL = new double[4][], wD = new double[4][];
		for (int i = 0; i < 4; ++i) {
			double[][] tmps = nnk(data[i].features, tests[i + 4].features, data[i].weights, data[i].dates);
			wL[i] = tmps[0];
			wD[i] = tmps[1];
		}
		// double[][] thetaws = new double[4][], thetads = new double[4][];
		// thetaws[0] = m_theta01[0];
		// thetads[0] = m_theta01[1];
		// thetaws[1] = m_theta02[0];
		// thetads[1] = m_theta02[1];
		// thetaws[2] = m_theta11[0];
		// thetads[2] = m_theta11[1];
		// thetaws[3] = m_theta12[0];
		// thetads[3] = m_theta12[1];
		// double[][] wL = new double[4][], wD = new double[4][];
		// for (int i = 4; i < 8; ++i) {
		// wL[i - 4] = predict(tests[i].features, null, thetaws[i - 4]);
		// wD[i - 4] = predict(tests[i].features, wL[i - 4], thetads[i - 4]);
		// }

		int total = w01.length + w02.length + w11.length + w12.length;
		for (int i = 0; i < 4; ++i)
			total += wL[i].length;
		double[] ws = new double[total], ds = new double[total];
		int[] IDs = new int[total];
		int idx = 0;
		for (int i = 0; i < w01.length; ++i, ++idx) {
			ws[idx] = w01[i];
			ds[idx] = d01[i];
			IDs[idx] = tests[0].ids[i];
		}
		for (int i = 0; i < w02.length; ++i, ++idx) {
			ws[idx] = w02[i];
			ds[idx] = d02[i];
			IDs[idx] = tests[1].ids[i];
		}
		for (int i = 0; i < w11.length; ++i, ++idx) {
			ws[idx] = w11[i];
			ds[idx] = d11[i];
			IDs[idx] = tests[2].ids[i];
		}
		for (int i = 0; i < w12.length; ++i, ++idx) {
			ws[idx] = w12[i];
			ds[idx] = d12[i];
			IDs[idx] = tests[3].ids[i];
		}
		for (int i = 0; i < 4; ++i) {
			for (int j = 0; j < wL[i].length; ++j, ++idx) {
				ws[idx] = wL[i][j];
				ds[idx] = wD[i][j];
				IDs[idx] = tests[i + 4].ids[j];
			}
		}

		if (m_devel) {
			double[] w = new double[total], d = new double[total];
			idx = 0;
			for (int i = 0; i < w01.length; ++i, ++idx) {
				w[idx] = tests[0].weights[i];
				d[idx] = tests[0].dates[i];
			}
			for (int i = 0; i < w02.length; ++i, ++idx) {
				w[idx] = tests[1].weights[i];
				d[idx] = tests[1].dates[i];
			}
			for (int i = 0; i < w11.length; ++i, ++idx) {
				w[idx] = tests[2].weights[i];
				d[idx] = tests[2].dates[i];
			}
			for (int i = 0; i < w12.length; ++i, ++idx) {
				w[idx] = tests[3].weights[i];
				d[idx] = tests[3].dates[i];
			}
			for (int i = 0; i < 4; ++i) {
				for (int j = 0; j < wL[i].length; ++j, ++idx) {
					w[idx] = tests[i + 4].weights[j];
					d[idx] = tests[i + 4].dates[j];
				}
			}

			Entry[] alls = new Entry[IDs.length];
			for (int i = 0; i < IDs.length; ++i) {
				Entry me = new Entry();
				alls[i] = me;
				me.id = IDs[i];
				me.weight = w[i];
				me.date = d[i];
			}

			// sort preds
			Arrays.sort(alls);
			m_alls = new double[IDs.length * 2];
			idx = 0;
			for (int i = 0; i < alls.length; ++i) {
				Entry e = alls[i];
				m_alls[idx++] = e.date;
				m_alls[idx++] = e.weight;
			}
			double[] meandevw = Util.calcMeanStdDev(w);
			double[] meandevd = Util.calcMeanStdDev(d);
			double[] corr = Util.calcCovCorr(w, meandevw[0], meandevw[1], d, meandevd[0], meandevd[1]);
			System.out.println("Covariance: " + corr[0] + ", " + corr[1] + "; " + corr[2] + ", " + corr[3]);
			System.out.println("Correlation: " + corr[4] + ", " + corr[5] + "; " + corr[6] + ", " + corr[7]);
			double[] cov = { corr[0], corr[1], corr[2], corr[3] };
			double[] H = Util.inverse(cov);

			double ssr = 0, ss0 = 0, sw0 = 0, swr = 0, sd0 = 0, sdr = 0;
			for (int i = 0; i < total; ++i) {
				double ew = ws[i] - w[i], ed = ds[i] - d[i];
				swr += ew * ew;
				sdr += ed * ed;
				ssr += (H[0] * ew * ew + 2 * H[1] * ew * ed + H[3] * ed * ed);
				ew = w[i] - meandevw[0];
				ed = d[i] - meandevd[0];
				sw0 += ew * ew;
				sd0 += ed * ed;
				ss0 += (H[0] * ew * ew + 2 * H[1] * ew * ed + H[3] * ed * ed);
			}
			System.out.println("SS0 = " + ss0 + ", SSR = " + ssr + "; score = " + (1 - ssr / ss0));
			System.out.println("SW0 = " + sw0 + ", SWR = " + swr + "; scoreW = " + (1 - swr / sw0));
			System.out.println("SD0 = " + sd0 + ", SDR = " + sdr + "; scoreD = " + (1 - sdr / sd0));

			int Ntotal = total;
			for (int i = 0; i < 8; ++i)
				Ntotal += data[i].features.length;
			System.out.println("Total: " + Ntotal + " tests# " + total);
		}

		m_IDs = IDs;
		Entry[] alls = new Entry[IDs.length];
		for (int i = 0; i < IDs.length; ++i) {
			Entry me = new Entry();
			alls[i] = me;
			me.id = IDs[i];
			me.weight = ws[i];
			me.date = ds[i];
		}

		// sort preds
		Arrays.sort(alls);
		double[] vals = new double[IDs.length * 2];
		idx = 0;
		for (int i = 0; i < alls.length; ++i) {
			Entry e = alls[i];
			vals[idx++] = e.date;
			vals[idx++] = e.weight;
		}

		return vals;
	}

	private double[][] nnk(double[][] X, double[][] features, double[] ws, double[] ds) {
		double[][] results = new double[2][];
		results[0] = new double[features.length];
		results[1] = new double[features.length];

		for (int i = 0; i < features.length; ++i) {
			int idxMin = 0;
			double min = Util.dist2(features[i], X[0]);
			for (int j = 1; j < X.length; ++j) {
				double dist = Util.dist2(features[i], X[j]);
				if (dist < min) {
					min = dist;
					idxMin = j;
				}
			}
			results[0][i] = ws[idxMin];
			results[1][i] = ds[idxMin];
		}
		return results;
	}

	private double[] predict(double[][] features, double[] suppl, double[] theta) {
		double[] est = new double[features.length];
		int N = theta.length - 1;
		for (int i = 0; i < features.length; ++i) {
			est[i] = theta[N]; // we keep theta0 at end
			for (int j = 0; j < features[0].length; ++j)
				est[i] += features[i][j] * theta[j];
		}
		if (m_supplement != null) {
			for (int i = 0; i < features.length; ++i)
				est[i] += m_supplement[i] * theta[N - 1]; // theta(N)
		}

		return est;
	}

	private double[] train(double[][] features, double[] suppl, double[] truths) {
		double[] lambdas = { 0, 0.001, 0.003, 0.01, 0.03, 0.1, 0.3, 1, 3, 10 };
		double[] vals = new double[lambdas.length];
		double[][] thetas = new double[lambdas.length][];
		// Male 0
		reset();
		m_M = (int) (Math.ceil(features.length * 0.75));
		for (int i = 0; i < lambdas.length; ++i) {
			m_train = true;
			thetas[i] = linearReg(features, suppl, truths, lambdas[i]);
			if (thetas[i] == null) {
				vals[i] = 1e9;
				System.out.println("Non-converge at: " + i);
			} else {
				m_train = false;
				m_lambda = 0;
				calcCostGradient(thetas[i]);
				vals[i] = m_J;
			}
		}

		int idxMin = 0;
		for (int i = 1; i < lambdas.length; ++i)
			if (vals[i] < vals[idxMin])
				idxMin = i;

		if (m_devel) {
			System.out.println("Best lambda #" + idxMin + ": " + lambdas[idxMin] + "; cost: " + vals[idxMin]);
			System.out.print("theta: ");
			for (int i = 0; i < thetas[idxMin].length; ++i)
				System.out.print(thetas[idxMin][i] + ", ");
			System.out.println();
		}
		return thetas[idxMin];
	}

	private double[] linearReg(double[][] features, double[] featuresupl, double[] truth, double lambda) {
		m_f = features;
		m_supplement = featuresupl;
		m_truth = truth;
		m_lambda = lambda;

		m_N = features[0].length;
		if (m_supplement != null)
			m_N += 1;

		int maxEvl = 500, maxIt = 500;
		// SimpleVectorValueChecker checker = new SimpleVectorValueChecker(1e-3,
		// 1e-6, 500);
		SimpleValueChecker checker = new SimpleValueChecker(1e-3, 1e-4, maxIt);
		NonLinearConjugateGradientOptimizer cgo = new NonLinearConjugateGradientOptimizer(
				NonLinearConjugateGradientOptimizer.Formula.FLETCHER_REEVES, checker);

		double[] theta = new double[m_N + 1];
		InitialGuess init = new InitialGuess(theta);

		ObjectiveFunction obj = new ObjectiveFunction(new CostFunction());
		ObjectiveFunctionGradient grd = new ObjectiveFunctionGradient(new GradientFunction());
		OptimizationData[] opts = { init, GoalType.MINIMIZE, new MaxEval(maxEvl), new MaxIter(maxIt), obj, grd };

		try {
			org.apache.commons.math3.optim.PointValuePair opt = cgo.optimize(opts);
			theta = opt.getPoint();
		} catch (Exception e) {
			System.out.println(e);
			theta = null;
		}

		return theta;
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

	private class Entries {
		public int[] ids = null;
		public double[][] features = null;
		public double[] weights = null;
		public double[] dates = null;
	}

	private Entries[] buildFeatures(String[] raw, boolean hasResults) {
		Entries e01 = new Entries();
		Entries e02 = new Entries();
		Entries e11 = new Entries();
		Entries e12 = new Entries();
		Entries leftover01 = new Entries();
		Entries leftover02 = new Entries();
		Entries leftover11 = new Entries();
		Entries leftover12 = new Entries();

		ArrayList<double[]> f01 = new ArrayList<double[]>();
		ArrayList<double[]> f02 = new ArrayList<double[]>();
		ArrayList<double[]> f11 = new ArrayList<double[]>();
		ArrayList<double[]> f12 = new ArrayList<double[]>();
		ArrayList<Integer> ids01 = new ArrayList<Integer>();
		ArrayList<Integer> ids02 = new ArrayList<Integer>();
		ArrayList<Integer> ids11 = new ArrayList<Integer>();
		ArrayList<Integer> ids12 = new ArrayList<Integer>();
		ArrayList<Double> w01 = new ArrayList<Double>();
		ArrayList<Double> w02 = new ArrayList<Double>();
		ArrayList<Double> w11 = new ArrayList<Double>();
		ArrayList<Double> w12 = new ArrayList<Double>();
		ArrayList<Double> d01 = new ArrayList<Double>();
		ArrayList<Double> d02 = new ArrayList<Double>();
		ArrayList<Double> d11 = new ArrayList<Double>();
		ArrayList<Double> d12 = new ArrayList<Double>();
		ArrayList<double[]> lf01 = new ArrayList<double[]>();
		ArrayList<double[]> lf02 = new ArrayList<double[]>();
		ArrayList<double[]> lf11 = new ArrayList<double[]>();
		ArrayList<double[]> lf12 = new ArrayList<double[]>();
		ArrayList<Integer> lids01 = new ArrayList<Integer>();
		ArrayList<Integer> lids02 = new ArrayList<Integer>();
		ArrayList<Integer> lids11 = new ArrayList<Integer>();
		ArrayList<Integer> lids12 = new ArrayList<Integer>();
		ArrayList<Double> lw01 = new ArrayList<Double>();
		ArrayList<Double> lw02 = new ArrayList<Double>();
		ArrayList<Double> lw11 = new ArrayList<Double>();
		ArrayList<Double> lw12 = new ArrayList<Double>();
		ArrayList<Double> ld01 = new ArrayList<Double>();
		ArrayList<Double> ld02 = new ArrayList<Double>();
		ArrayList<Double> ld11 = new ArrayList<Double>();
		ArrayList<Double> ld12 = new ArrayList<Double>();
		int cid = -1, pid = -1, last = raw.length - 1;
		int sex = 0, status = 0;
		double time = 0, meas = 0;
		ArrayList<double[]> features = new ArrayList<double[]>();
		double[] wd = { 0, 0 }, pwd = { 0, 0 };
		boolean addme = false;
		for (int idx = 0; idx < raw.length; ++idx) {
			String[] s = raw[idx].split(",");
			int id = Integer.parseInt(s[0]);
			if (id != cid) {
				addme = (idx > 0);
				pid = cid;
				cid = id;
				pwd[0] = wd[0];
				pwd[1] = wd[1];
				time = Double.parseDouble(s[1]);
				sex = Integer.parseInt(s[2]);
				status = Integer.parseInt(s[3]);
				meas = Double.parseDouble(s[4]);
				if (hasResults) {
					wd[0] = Double.parseDouble(s[12]);
					wd[1] = Double.parseDouble(s[13]);
				}
			} else {
				double[] vals = new double[12];
				vals[0] = time;
				vals[1] = meas;
				vals[2] = Double.parseDouble(s[1]);
				vals[3] = sex;
				vals[4] = status;
				for (int i = 5; i < 12; ++i)
					vals[i] = Double.parseDouble(s[i]);
				features.add(vals);
			}
			if (idx == last) {
				addme = true;
				pid = cid;
				cid = id;
				pwd[0] = wd[0];
				pwd[1] = wd[1];
			}
			if (addme == true) {
				addme = false;
				double[] vals = features.get(0);
				double psex = vals[3], pstatus = vals[4];
				if (features.size() < 4) {
					if (psex == 0) {
						if (pstatus == 1) {
							lids01.add(pid);
							lf01.add(composeEntry(features));
							if (hasResults) {
								lw01.add(pwd[0]);
								ld01.add(pwd[1]);
							}
						} else {
							lids02.add(pid);
							lf02.add(composeEntry(features));
							if (hasResults) {
								lw02.add(pwd[0]);
								ld02.add(pwd[1]);
							}
						}
					} else {
						if (pstatus == 1) {
							lids11.add(pid);
							lf11.add(composeEntry(features));
							if (hasResults) {
								lw11.add(pwd[0]);
								ld11.add(pwd[1]);
							}
						} else {
							lids12.add(pid);
							lf12.add(composeEntry(features));
							if (hasResults) {
								lw12.add(pwd[0]);
								ld12.add(pwd[1]);
							}
						}
					}
				} else {
					if (psex == 0) {
						if (pstatus == 1) {
							ids01.add(pid);
							f01.add(composeEntry(features));
							if (hasResults) {
								w01.add(pwd[0]);
								d01.add(pwd[1]);
							}
						} else {
							ids02.add(pid);
							f02.add(composeEntry(features));
							if (hasResults) {
								w02.add(pwd[0]);
								d02.add(pwd[1]);
							}
						}
					} else {
						if (pstatus == 1) {
							ids11.add(pid);
							f11.add(composeEntry(features));
							if (hasResults) {
								w11.add(pwd[0]);
								d11.add(pwd[1]);
							}
						} else {
							ids12.add(pid);
							f12.add(composeEntry(features));
							if (hasResults) {
								w12.add(pwd[0]);
								d12.add(pwd[1]);
							}
						}
					}
				}
				features.clear();
			}
		}

		{
			e01.ids = new int[ids01.size()];
			for (int i = 0; i < e01.ids.length; ++i)
				e01.ids[i] = ids01.get(i);

			e02.ids = new int[ids02.size()];
			for (int i = 0; i < e02.ids.length; ++i)
				e02.ids[i] = ids02.get(i);

			e11.ids = new int[ids11.size()];
			for (int i = 0; i < e11.ids.length; ++i)
				e11.ids[i] = ids11.get(i);

			e12.ids = new int[ids12.size()];
			for (int i = 0; i < e12.ids.length; ++i)
				e12.ids[i] = ids12.get(i);
		}
		{
			leftover01.ids = new int[lids01.size()];
			for (int i = 0; i < leftover01.ids.length; ++i)
				leftover01.ids[i] = lids01.get(i);

			leftover02.ids = new int[lids02.size()];
			for (int i = 0; i < leftover02.ids.length; ++i)
				leftover02.ids[i] = lids02.get(i);

			leftover11.ids = new int[lids11.size()];
			for (int i = 0; i < leftover11.ids.length; ++i)
				leftover11.ids[i] = lids11.get(i);

			leftover12.ids = new int[lids12.size()];
			for (int i = 0; i < leftover12.ids.length; ++i)
				leftover12.ids[i] = lids12.get(i);
		}
		{
			e01.features = new double[f01.size()][];
			f01.toArray(e01.features);
			e02.features = new double[f02.size()][];
			f02.toArray(e02.features);
			e11.features = new double[f11.size()][];
			f11.toArray(e11.features);
			e12.features = new double[f12.size()][];
			f12.toArray(e12.features);
		}
		{
			leftover01.features = new double[lf01.size()][];
			lf01.toArray(leftover01.features);
			leftover02.features = new double[lf02.size()][];
			lf02.toArray(leftover02.features);
			leftover11.features = new double[lf11.size()][];
			lf11.toArray(leftover11.features);
			leftover12.features = new double[lf12.size()][];
			lf12.toArray(leftover12.features);
		}

		if (hasResults) {
			e01.weights = new double[w01.size()];
			e01.dates = new double[d01.size()];
			for (int i = 0; i < e01.weights.length; ++i) {
				e01.weights[i] = w01.get(i);
				e01.dates[i] = d01.get(i);
			}

			e02.weights = new double[w02.size()];
			e02.dates = new double[d02.size()];
			for (int i = 0; i < e02.weights.length; ++i) {
				e02.weights[i] = w02.get(i);
				e02.dates[i] = d02.get(i);
			}

			e11.weights = new double[w11.size()];
			e11.dates = new double[d11.size()];
			for (int i = 0; i < e11.weights.length; ++i) {
				e11.weights[i] = w11.get(i);
				e11.dates[i] = d11.get(i);
			}

			e12.weights = new double[w12.size()];
			e12.dates = new double[d12.size()];
			for (int i = 0; i < e12.weights.length; ++i) {
				e12.weights[i] = w12.get(i);
				e12.dates[i] = d12.get(i);
			}

			leftover01.weights = new double[lw01.size()];
			leftover01.dates = new double[ld01.size()];
			for (int i = 0; i < leftover01.weights.length; ++i) {
				leftover01.weights[i] = lw01.get(i);
				leftover01.dates[i] = ld01.get(i);
			}

			leftover02.weights = new double[lw02.size()];
			leftover02.dates = new double[ld02.size()];
			for (int i = 0; i < leftover02.weights.length; ++i) {
				leftover02.weights[i] = lw02.get(i);
				leftover02.dates[i] = ld02.get(i);
			}

			leftover11.weights = new double[lw11.size()];
			leftover11.dates = new double[ld11.size()];
			for (int i = 0; i < leftover11.weights.length; ++i) {
				leftover11.weights[i] = lw11.get(i);
				leftover11.dates[i] = ld11.get(i);
			}

			leftover12.weights = new double[lw12.size()];
			leftover12.dates = new double[ld12.size()];
			for (int i = 0; i < leftover12.weights.length; ++i) {
				leftover12.weights[i] = lw12.get(i);
				leftover12.dates[i] = ld12.get(i);
			}
		}

		Entries[] ret = { e01, e02, e11, e12, leftover01, leftover02, leftover11, leftover12 };
		return ret;
	}

	private double[] composeEntry(ArrayList<double[]> features) {
		double[] full = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 2, 3, 4, 5, 6, 7, 8, 9, 10,
				13, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 2 };
		int idx = 0;
		double[] first = features.get(0);
		full[idx++] = first[0]; // t0
		full[idx++] = first[1]; // m0

		switch (features.size()) {
		case 1: {
			for (int i = 0; i < 5; ++i) { // Repeat
				full[idx++] = first[2]; // t1
				for (int j = 5; j < first.length; ++j)
					full[idx++] = first[j]; // 7 meas for t1
			}
		}
			break;
		case 2: {
			full[idx++] = first[2]; // t1
			for (int j = 5; j < first.length; ++j)
				full[idx++] = first[j]; // 7 meas for t1
			double[] delta = { 1, 2, 3, 4, 5, 6, 7 }, second = features.get(1);
			double deltaT = (second[2] - first[2]) / 4;
			int idelta = 0;
			for (int j = 5; j < first.length; ++j)
				delta[idelta++] = (second[j] - first[j]) / 4;
			for (int i = 1; i < 4; ++i) {
				idelta = 0;
				full[idx++] = first[2] + deltaT * i; // 3 fillers ti
				for (int j = 5; j < first.length; ++j)
					full[idx++] = first[j] + delta[idelta++] * i; // 7 meas for
																	// ti
			}
			full[idx++] = second[2]; // t2
			for (int j = 5; j < first.length; ++j)
				full[idx++] = second[j]; // 7 meas for t1
		}
			break;
		case 3: {
			full[idx++] = first[2]; // t1
			for (int j = 5; j < first.length; ++j)
				full[idx++] = first[j]; // 7 meas for t1
			double[] second = features.get(1), third = features.get(2);
			full[idx++] = (first[2] + second[2]) / 2; // mid of 1st - 2nd
			for (int j = 5; j < first.length; ++j)
				full[idx++] = (first[j] + second[j]) / 2;
			full[idx++] = second[2]; // t2
			for (int j = 5; j < second.length; ++j)
				full[idx++] = second[j]; // 7 meas for t2
			full[idx++] = (second[2] + third[2]) / 2; // mid of 2nd - 3rd
			for (int j = 5; j < second.length; ++j)
				full[idx++] = (second[j] + third[j]) / 2;
			full[idx++] = third[2]; // t3
			for (int j = 5; j < third.length; ++j)
				full[idx++] = third[j]; // 7 meas for t3
		}
			break;
		case 4: {
			double[] t = { 0, 0, 0, 0 };
			double[][] m = { { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 },
					{ 0, 0, 0, 0 }, { 0, 0, 0, 0 } };
			int interp = 0;
			for (int i = 0; i < 2; ++i) {
				double[] me = features.get(i);
				t[interp] = me[2];
				full[idx++] = me[2];
				for (int j = 5; j < me.length; ++j) {
					m[j - 5][interp] = me[j];
					full[idx++] = me[j];
				}
				++interp;
			}
			for (int i = 2; i < 4; ++i) {
				double[] me = features.get(i);
				t[interp] = me[2];
				for (int j = 5; j < me.length; ++j)
					m[j - 5][interp] = me[j];
				++interp;
			}
			double t0 = (t[1] + t[2]) / 2;
			full[idx++] = t0;
			for (int j = 0; j < m.length; ++j) {
				full[idx++] = Util.interpolate(t, m[j], t0);
			}
			for (int i = 2; i < 4; ++i) {
				double[] me = features.get(i);
				full[idx++] = me[2];
				for (int j = 5; j < me.length; ++j)
					full[idx++] = me[j];
			}
		}
			break;
		case 5: {
			for (int i = 0; i < 5; ++i) {
				double[] me = features.get(i);
				full[idx++] = me[2];
				for (int j = 5; j < me.length; ++j)
					full[idx++] = me[j];
			}
		}
			break;
		case 6: {
			double[] t = { 0, 0, 0, 0, 0, 0 };
			double[][] m = { { 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 },
					{ 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 } };
			int interp = 0;
			for (int i = 0; i < 2; ++i) {
				double[] me = features.get(i);
				t[interp] = me[2];
				full[idx++] = me[2];
				for (int j = 5; j < me.length; ++j) {
					m[j - 5][interp] = me[j];
					full[idx++] = me[j];
				}
				++interp;
			}
			for (int i = 2; i < 6; ++i) {
				double[] me = features.get(i);
				t[interp] = me[2];
				for (int j = 5; j < me.length; ++j)
					m[j - 5][interp] = me[j];
				++interp;
			}
			double t0 = (t[2] + t[3]) / 2;
			full[idx++] = t0;
			for (int j = 0; j < m.length; ++j) {
				full[idx++] = Util.interpolate(t, m[j], t0);
			}
			for (int i = 4; i < 6; ++i) {
				double[] me = features.get(i);
				full[idx++] = me[2];
				for (int j = 5; j < me.length; ++j)
					full[idx++] = me[j];
			}
		}
			break;
		default:
			break;
		}

		return full;
	}
}

class Util {
	static double interpolate(double[] x, double[] y, double x0) {
		if (x.length == 1)
			return y[0];
		double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
		for (int i = 0; i < x.length; ++i) {
			sumX += x[i];
			sumY += y[i];
			sumXY += (x[i] * y[i]);
			sumX2 += (x[i] * x[i]);
		}

		double k = (sumX * sumY - x.length * sumXY) / (sumX * sumX - x.length * sumX2);
		double b = (sumXY - k * sumX2) / sumX;

		return (k * x0 + b);
	}

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

	static double dist2(double[] x1, double[] x2) {
		double ds = 0;
		for (int i = 0; i < x1.length; ++i) {
			double diff = x1[i] - x2[i];
			ds += diff * diff;
		}
		return ds;
	}
}
