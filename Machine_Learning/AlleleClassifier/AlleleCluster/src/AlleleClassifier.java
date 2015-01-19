import java.util.ArrayList;
import java.util.LinkedList;

//import java.io.IOException;

public final class AlleleClassifier
{
	public class Entry
	{
		public int bID = -1; // batch ID
		public int sID = -1; // sampleID
		public double x = 0;
		public double y = 0;
		public double z = 0;
		public int color = -1;
		public int bufloc = -1; // location in circular buffer
		public Grid.GridIndex loc = null;
	}

	public final static int COLOR1 = 0;
	public final static int COLOR1OR2 = COLOR1 + 1; // 1
	public final static int COLOR2 = COLOR1OR2 + 1; // 2
	public final static int COLOR0 = COLOR2 + 1; // 3
	public final static int COLOR0OR1 = COLOR0 + 1; // 4
	public final static int COLOROVER2 = COLOR0OR1 + 1; // 5
	public final static int NUMCOLORS = COLOROVER2 + 1;
	public final static String[] s_colors = { "1", "1 or 2", "2", "0", "0 or 1", ">2" };

	private final static double rangeScaleFactor = 0.5;
	private final static double finerizeFactor = 10;
	private final static double maxGridSize = 1e-6;
	private final static double mimumGranuleFactor = 100;
	private final static int bufferSize = 1000;

	private EntryCB buffer = new EntryCB(bufferSize);
	private Grid clusters = null;
	private final double BIG = 1e10;
	private double[] clusterRanges = { BIG, -BIG, BIG, -BIG, BIG, -BIG };
	private double[] granules = { 1, 1, 1 };

	public String[] classify(String[] trains, String[] tests, String[] truths)
	{
		System.out.println("total trains: " + trains.length + "; total tests: " + tests.length);
		train(trains);

		return classify(tests, truths);
	}

	private void train(String[] entries)
	{
		LinkedList<Entry> refiners = new LinkedList<Entry>();
		int numBuffered = 0;
		int trainRate = 3;

		int forTrain = entries.length / trainRate;
		for (int idx = 0; idx < forTrain; ++idx)
		{
			junkRefiningWeight = true;
			refiners.add(createEntry(entries[idx]));
			junkRefiningWeight =false;
		}
		for (int idx = forTrain; idx < entries.length; ++idx)
		{

			// for (int idx = 0; idx < entries.length; ++idx)
			// {
			// if (idx % trainRate == 0)
			// {
			// refiners.add(createEntry(entries[idx]));
			// continue;
			// }

			++numBuffered;
			Entry me = createEntry(entries[idx]);
			Entry old = buffer.add(me);
			if (old != null)
			{
				// TODO: implementation error exception for null
				if (null == clusters)
					System.out.println("Can't be here");
				else
					clusters.disposeOneSample(old);
			}

			if (clusters != null)
			{
				if (clusters.feedOneSample(me))
					refineGrid(false);
			}
			if (numBuffered == bufferSize && null == clusters)
			{
				buildCluster();
			}
		}
		if (null == clusters)
		{
			buildCluster();
		}

		clusters.analyzeQuality();

		refineWeight(refiners);
	}

	private void buildCluster()
	{
		System.out.print("Build cluster: ");
		clusters = new Grid();
		for (int i = 0; i < Grid.NUMDIMS; ++i)
		{
			double gard = (clusterRanges[2 * i + 1] - clusterRanges[2 * i]) * rangeScaleFactor / 2;
			double tmp1 = clusterRanges[2 * i] - gard;
			double tmp2 = clusterRanges[2 * i + 1] + gard;
			clusters.setRange(i, tmp1, tmp2);
			granules[i] = (clusterRanges[2 * i + 1] - clusterRanges[2 * i]) / buffer.getContentSize();
			{
				while ((granules[i] > maxGridSize)
						&& (granules[i] > mimumGranuleFactor * finerizeFactor * Double.MIN_VALUE))
					granules[i] /= finerizeFactor;
			}
			clusters.setGranularity(i, granules[i]);
			System.out.print("(" + clusterRanges[2 * i] + "," + clusterRanges[2 * i + 1] + "," + granules[i] + ") ");
		}
		System.out.println("");

		refineGrid(true);
	}

	private void refineGrid(Boolean initial)
	{
		Boolean keepRefining = true;
		Entry[] rawBuf = buffer.getBuffer();

		if (!initial)
		{
			keepRefining = false;
			System.out.print("Collision happened, refine: ");
			for (int dim = 0; dim < Grid.NUMDIMS; ++dim)
			{
				System.out.print(granules[dim] + " => ");
				if (granules[dim] > mimumGranuleFactor * finerizeFactor * Double.MIN_VALUE)
				{
					granules[dim] /= finerizeFactor;
					clusters.setGranularity(dim, granules[dim]);
					keepRefining = true;
				}
				System.out.print(granules[dim] + "; ");
			}
			System.out.println("");
		}

		while (keepRefining)
		{
			for (int i = 0; i < buffer.getContentSize(); ++i)
			{
				keepRefining = false;
				if (clusters.feedOneSample(rawBuf[i]))
				{
					System.out.print("Collision during refining: ");
					for (int dim = 0; dim < Grid.NUMDIMS; ++dim)
					{
						System.out.print(granules[dim] + " => ");
						if (granules[dim] > mimumGranuleFactor * finerizeFactor * Double.MIN_VALUE)
						{
							granules[dim] /= finerizeFactor;
							clusters.setGranularity(dim, granules[dim]);
							keepRefining = true;
						}
						System.out.print(granules[dim] + "; ");
					}
				}
				if (keepRefining)
					break;
			}
		}
	}

	private Boolean junkRefiningWeight = false;
	private void refineWeight(LinkedList<Entry> entries)
	{
		double preWeight = 1, weight = preWeight;
		Boolean keepGoing = true;
		int iterations = 0, preZeros = 0, preHalves = 0, preCorrect = 0;
		double preGrade = 0;
		int[] truths = new int[entries.size()];
		{
			int idx = 0;
			for (Entry me : entries)
				truths[idx] = me.color;
		}
		while (keepGoing)
		{
			keepGoing = false;
			clusters.setOuterWeight(weight);
			classify(entries);
			double grade = 0;
			int correct = 0, zeros = 0, halves = 0;
			int idx = 0;
			for (Entry me : entries)
			{
				int resColor = me.color, actColor = truths[idx];
				if (resColor == actColor)
				{
					grade += 1.;
					++correct;
				}
				else
				{
					if ((actColor > AlleleClassifier.COLOR2) && (resColor <= AlleleClassifier.COLOR2))
					{
						++zeros;
					}
					else
					{
						++halves;
						grade += 0.5;
					}
				}
				++idx;
			}

			if (preGrade == 0)
			{
				double rate = (double) correct / truths.length * 100.;
				rate = ((int) (rate * 100)) / 100.;
				System.out.println("###Initially (" + weight + ") " + correct + " out of " + truths.length + "(" + rate
						+ ") correct! " + grade + " from " + halves + " halves and " + zeros + " zeros");
			}
			else
			{
				double rate = (double) correct / truths.length * 100.;
				rate = ((int) (rate * 100)) / 100.;
				System.out.println("### " + iterations + "iteration (" + weight + ") " + correct + " out of "
						+ truths.length + "(" + rate + ") correct! " + grade + " from " + halves + " halves and "
						+ zeros + " zeros");
			}
			if (grade > preGrade || 2 * (zeros - preZeros) > (preCorrect - correct))
			{
				preWeight = weight;
				weight = preWeight * 1.1;
				++iterations;
				preGrade = grade;
				preHalves = halves;
				preZeros = zeros;
				preCorrect = correct;
				keepGoing = true;
			}
		}
		clusters.setOuterWeight(preWeight);
		{
			double rate = (double) preCorrect / entries.size() * 100.;
			rate = ((int) (rate * 100)) / 100.;
			System.out.println("###After " + iterations + " rounds, weight=(" + preWeight + ") " + preCorrect
					+ " out of " + entries.size() + "(" + rate + ") correct! " + preGrade + " from " + preHalves
					+ " halves and " + preZeros + " zeros");
		}
	}

	private void classify(LinkedList<Entry> entries)
	{
		int idx = 0;
		for (Entry me : entries)
			clusters.resloveColor(me, idx++);
	}

	private Entry createEntry(String entry)
	{
		Entry me = new Entry();
		String[] strs = entry.split(",");
		me.bID = Integer.parseInt(strs[0]);
		me.sID = Integer.parseInt(strs[1]);
		me.x = Double.parseDouble(strs[2]);
		me.y = Double.parseDouble(strs[3]);
		me.z = me.x - me.y;
		if (me.x < clusterRanges[0])
			clusterRanges[0] = me.x;
		if (me.x > clusterRanges[1])
			clusterRanges[1] = me.x;
		if (me.y < clusterRanges[2])
			clusterRanges[2] = me.y;
		if (me.y > clusterRanges[3])
			clusterRanges[3] = me.y;
		if (me.z < clusterRanges[4])
			clusterRanges[4] = me.z;
		if (me.z > clusterRanges[5])
			clusterRanges[5] = me.z;

		if (strs.length == 5)
			me.color = getColorCode(strs[4]);
		if (junkRefiningWeight)
			System.out.println(entry + "=>" + me.color);

		return me;
	}

	static public int getColorCode(String str)
	{
		int color = -1;
		for (int i = 0; i < s_colors.length; ++i)
		{
			if (s_colors[i].equals(str))
			{
				color = i;
				break;
			}
		}
		return color;
	}

	public String getTraningReport()
	{
		double multi = clusters.getMultiHitsRate();
		double collid = clusters.getCollisionRate();

		return clusters.numberofCellsOccupied() + " cells occupied; " + "multi-hit rate: " + multi * 100 + "("
				+ clusters.numberofCellsOccupied() * multi + "); collision rate: " + collid * 100 + "("
				+ clusters.numberofCellsOccupied() * collid + "); multi-hits with collision rate: "
				+ clusters.getMultiHitsWithCollisionRate() * 100;
	}

	private String[] classify(String[] entries, String[] truths)
	{
		String[] results = new String[entries.length];

		for (int i = 0; i < entries.length; ++i)
		{
			String str = entries[i];
			if (truths != null)
				str = str + ',' + truths[i];
			Entry me = createEntry(str);
			clusters.resloveColor(me, i);
			results[i] = s_colors[me.color];
		}
		System.out.println(clusters.junkOutofRange + " out of range " + clusters.junkOutofRangeCorrect + " correct, "
				+ clusters.junkVacumme + " in vacuum " + clusters.junkVacummeCorrect + " correct, "
				+ clusters.junkCollision + " unresolved collisions contains: " + clusters.junkCollisionLow
				+ " lows and " + clusters.junkCollisionHigh + " highs; " + "resolved collisions: "
				+ clusters.junkCollisionResolved + " with " + clusters.junkCollisionResolvedCorrect
				+ " correct; directly resolved: " + clusters.junkResolved + " with " + clusters.junkResolvedCorrect
				+ " correct");
		return results;
	}
}
