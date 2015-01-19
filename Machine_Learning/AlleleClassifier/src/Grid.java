import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Grid
{
	public final class GridIndex implements Comparable<Object>
	{
		public int mX = -1;
		public int mY = -1;
		public int mZ = -1;

		GridIndex(int[] idx)
		{
			mX = idx[0];
			mY = idx[1];
			mZ = idx[2];
		}

		@Override
		public String toString()
		{
			return "(" + mX + ',' + mY + ',' + mZ + ')';
		}

		@Override
		public int compareTo(Object otherObject)
		{
			GridIndex other = (GridIndex) otherObject;
			if (mX < other.mX)
				return -1;
			if (mX > other.mX)
				return 1;

			if (mY < other.mY)
				return -1;
			if (mY > other.mY)
				return 1;

			if (mZ < other.mZ)
				return -1;
			if (mZ > other.mZ)
				return 1;

			return 0;
		}
	}

	public class Cell
	{
		public int[] colorsHit = { 0, 0, 0, 0, 0, 0 };
		public Boolean collision = false;
		public Boolean multiHit = false;
		public List<AlleleClassifier.Entry> entries = new LinkedList<AlleleClassifier.Entry>();
		public GridIndex loc = null;
	}

	public static final int XDIM = 0;
	public static final int YDIM = XDIM + 1;
	public static final int ZDIM = YDIM + 1;
	public static final int NUMDIMS = ZDIM + 1;

	private double[] ranges = { 0, 0, 0, 0, 0, 0 };
	private double[] granules = { 1, 1, 1 };
	private TreeMap<GridIndex, Cell> cells = new TreeMap<GridIndex, Cell>();
	private int cellsOccupied = 0;
	private int cellsCollided = 0;
	private int cellsMultiHits = 0;
	private int cellsMultiHitsWithCollision = 0;
	private int[] buckets = { 0, 0, 0, 0, 0, 0 };
	private Boolean doPrintout = false;
	private double outerWeight = 1;

	public int junkOutofRange = 0;
	public int junkOutofRangeCorrect = 0;
	public int junkVacumme = 0;
	public int junkVacummeCorrect = 0;
	public int junkCollision = 0;
	public int junkCollisionHigh = 0;
	public int junkCollisionLow = 0;
	public int junkCollisionResolved = 0;
	public int junkCollisionResolvedCorrect = 0;
	public int junkResolved = 0;
	public int junkResolvedCorrect = 0;

	public void reqestDoPrintout(Boolean r)
	{
		doPrintout = r;
	}

	public void setRange(int which, double low, double high)
	{
		// TODO: convert to exception
		if (which < 0 || which > 2 || low >= high)
			return;
		ranges[which * 2] = low;
		ranges[which * 2 + 1] = high;
		cells.clear();
	}

	public void setGranularity(int which, double granule)
	{
		if (which < 0 || which > 2 || granule <= 0)
			return;
		granules[which] = granule;
		cells.clear();
	}

	public Boolean feedOneSample(AlleleClassifier.Entry item)
	{
		Boolean collided = false;
		GridIndex gdx = getEntryCell(item);
		if (gdx != null)
		{
			Cell me = null;
			if (cells.containsKey(gdx))
			{
				me = cells.get(gdx);
				// Already exist (i.e. colored), now hit with a different color,
				// need analysis to decide if we need to make our grid finer.
				if (me.colorsHit[item.color] == 0)
				{
					me.collision = true;
					collided = true;
				}
				else
					me.multiHit = true;
			}
			else
			{
				me = new Cell();
				me.loc = gdx;
				cells.put(gdx, me);
				++cellsOccupied;
			}
			++(me.colorsHit[item.color]);
			me.entries.add(item);
			++(buckets[item.color]);
		}
		item.loc = gdx;
		return collided;
	}

	private GridIndex getEntryCell(AlleleClassifier.Entry item)
	{
		if (!entryInRange(item))
			return null;

		int[] dims = { 0, 0, 0 };
		dims[XDIM] = (int) (Math.ceil((item.x - ranges[0]) / granules[XDIM]));
		dims[YDIM] = (int) (Math.ceil((item.y - ranges[2]) / granules[YDIM]));
		dims[ZDIM] = (int) (Math.ceil((item.z - ranges[4]) / granules[ZDIM]));
		return new GridIndex(dims);
	}

	public void disposeOneSample(AlleleClassifier.Entry item)
	{
		if (item.loc != null)
		{
			Cell owner = cells.get(item.loc);
			if (owner.entries.contains(item))
				owner.entries.remove(item);
			else
			{
				// TODO Should not be here. Implementation error exception.
				System.out.println("Implementation error: " + item.loc);
			}
		}
	}

	public void analyzeQuality()
	{
		cellsCollided = 0;
		cellsMultiHits = 0;
		for (Map.Entry<GridIndex, Cell> entry : cells.entrySet())
		{
			Cell me = entry.getValue();
			if (me != null)
			{
				if (me.collision)
					++cellsCollided;
				if (me.multiHit)
					++cellsMultiHits;
				if (me.collision && me.multiHit)
					++cellsMultiHitsWithCollision;
			}
		}
		if (doPrintout)
		{
			// TODO: generating error/warning
			int totals = 0;
			for (int i = 0; i < AlleleClassifier.NUMCOLORS; ++i)
				totals += buckets[i];
			System.out.print("Buckets(" + totals + "): ");
			for (int i = 0; i < AlleleClassifier.NUMCOLORS; ++i)
				System.out.print("(" + AlleleClassifier.s_colors[i] + "," + buckets[i] + ") ");
			System.out.println("");

			System.out.println("Cells occupied: " + cellsOccupied + "; multi-hit: " + cellsMultiHits
					+ "; in collision: " + cellsCollided + "; multi-hit & collision: " + cellsMultiHitsWithCollision);
		}

	}

	private Boolean entryInRange(AlleleClassifier.Entry i)
	{
		return (i.x > ranges[0] && i.x < ranges[1]) && (i.y > ranges[2] && i.y < ranges[3])
				&& (i.z > ranges[4] && i.z < ranges[5]);
	}

	public int numberofCellsOccupied()
	{
		return cellsOccupied;
	}

	public double getMultiHitsRate()
	{
		return (double) cellsMultiHits / (double) cellsOccupied;
	}

	public double getCollisionRate()
	{
		return (double) cellsCollided / (double) cellsOccupied;
	}

	public double getMultiHitsWithCollisionRate()
	{
		return (double) (cellsMultiHitsWithCollision) / (double) cellsOccupied;
	}

	public void resloveColor(AlleleClassifier.Entry item, int index)
	{
		GridIndex gdx = getEntryCell(item);
		if (gdx != null)
		{
			Cell me = cells.get(gdx);
			if (me != null)
			{
				int color = -1; // junk value
				int hits = 0;
				Boolean directHit = false;
				Boolean collisionHigh = false;
				Boolean collisionLow = false;
				if (!me.collision)
					directHit = true;
				for (int i = 0; i < AlleleClassifier.NUMCOLORS; ++i)
				{
					if (me.colorsHit[i] > hits)
					{
						hits = me.colorsHit[i];
						color = i;
					}
					else if (me.colorsHit[i] == hits)
					{
						if (i > AlleleClassifier.COLOR2 && item.color <= AlleleClassifier.COLOR2)
							color = i;
						else
						{
							if (i > AlleleClassifier.COLOR2 && item.color > AlleleClassifier.COLOR2)
								collisionHigh = true;
							if (i <= AlleleClassifier.COLOR2 && item.color <= AlleleClassifier.COLOR2)
								collisionLow = true;
						}
					}
				}
				if (directHit)
				{
					++junkResolved;
					if (item.color == color)
						++junkResolvedCorrect;
				}
				else
				{
					if (color >= 0)
					{
						++junkCollisionResolved;
						if (item.color == color)
							++junkCollisionResolvedCorrect;
					}
					else
					{
						++junkCollision;
						if (collisionHigh)
							++junkCollisionHigh;
						if (collisionLow)
							++junkCollisionLow;
					}
				}

				item.color = color;
			}
			else
			{
				int actual = item.color;
				resolveVaccume3(item);
				++junkVacumme;
				if (actual == item.color)
					++junkVacummeCorrect;
			}
		}
		else
		{
			int actual = item.color;
			resolveVaccume3(item);
			++junkOutofRange;
			if (actual == item.color)
				++junkOutofRangeCorrect;
		}
	}

	private void resetJunks()
	{
		junkCollision = 0;
		junkCollisionHigh = 0;
		junkCollisionLow = 0;
		junkCollisionResolved = 0;
		junkCollisionResolvedCorrect = 0;
		junkOutofRange = 0;
		junkOutofRangeCorrect = 0;
		junkResolved = 0;
		junkResolvedCorrect = 0;
		junkVacumme = 0;
		junkVacummeCorrect = 0;
	}
	public void setOuterWeight( double w)
	{
		outerWeight = w;
		resetJunks();
	}
	private void resolveVaccume3(AlleleClassifier.Entry item)
	{
		double[] gravities = { 0, 0, 0, 0, 0, 0 };
		for (Map.Entry<GridIndex, Cell> entry : cells.entrySet())
		{
			Cell me = entry.getValue();
			double x = ranges[2 * XDIM] + (me.loc.mX + 0.5) * granules[XDIM];
			double y = ranges[2 * YDIM] + (me.loc.mY + 0.5) * granules[YDIM];
			double z = ranges[2 * ZDIM] + (me.loc.mZ + 0.5) * granules[ZDIM];

			double d = Math.pow((item.x - x), 2) + Math.pow((item.y - y), 2) + Math.pow((item.z - z), 2);
			for (int color = 0; color < AlleleClassifier.NUMCOLORS; ++color)
			{
				if (me.colorsHit[color] > 0)
				{
					if (color > AlleleClassifier.COLOR2)
						gravities[color] += outerWeight*me.colorsHit[color] / d;
					else
						gravities[color] += me.colorsHit[color] / d;
				}
			}
		}
		int max = 0;
		for (int color = 1; color < AlleleClassifier.NUMCOLORS; ++color)
		{
			if (gravities[color] > gravities[max])
				max = color;
		}

		item.color = max;
	}

	// Closest neighbor
	private void resolveVaccume2(AlleleClassifier.Entry item)
	{
		double[] mins = { 1e9, 1e9, 1e9, 1e9, 1e9, 1e9 };

		for (Map.Entry<GridIndex, Cell> entry : cells.entrySet())
		{
			Cell me = entry.getValue();
			double x = ranges[2 * XDIM] + (me.loc.mX + 0.5) * granules[XDIM];
			double y = ranges[2 * YDIM] + (me.loc.mY + 0.5) * granules[YDIM];
			double z = ranges[2 * ZDIM] + (me.loc.mZ + 0.5) * granules[ZDIM];

			double d = Math.sqrt(Math.pow((item.x - x), 2) + Math.pow((item.y - y), 2) + Math.pow((item.z - z), 2));

			for (int color = 0; color < AlleleClassifier.NUMCOLORS; ++color)
			{
				if (me.colorsHit[color] > 0)
				{
					if (mins[color] > d)
						mins[color] = d;
				}
			}
			int min = 0;
			for (int color = 1; color < AlleleClassifier.NUMCOLORS; ++color)
			{
				if (mins[color] < mins[min])
					min = color;
			}

			item.color = min;
		}
	}

	// Shortest distance to a fixed number of neighbors with same color
	private void resolveVaccume(AlleleClassifier.Entry item)
	{
		double[][] gravities = { { 10e9, 10e9, 10e9, 10e9, 10e9 }, { 10e9, 10e9, 10e9, 10e9, 10e9 },
				{ 10e9, 10e9, 10e9, 10e9, 10e9 }, { 10e9, 10e9, 10e9, 10e9, 10e9 }, { 10e9, 10e9, 10e9, 10e9, 10e9 },
				{ 10e9, 10e9, 10e9, 10e9, 10e9 } };
		int[] counter = { 0, 0, 0, 0, 0, 0 };
		int[] maxLocInNeighborsGroup = { 0, 0, 0, 0, 0, 0 };
		int size = gravities[0].length;
		for (Map.Entry<GridIndex, Cell> entry : cells.entrySet())
		{
			Cell me = entry.getValue();
			double x = ranges[2 * XDIM] + (me.loc.mX + 0.5) * granules[XDIM];
			double y = ranges[2 * YDIM] + (me.loc.mY + 0.5) * granules[YDIM];
			double z = ranges[2 * ZDIM] + (me.loc.mZ + 0.5) * granules[ZDIM];

			double d = Math.sqrt(Math.pow((item.x - x), 2) + Math.pow((item.y - y), 2) + Math.pow((item.z - z), 2));

			for (int color = 0; color < AlleleClassifier.NUMCOLORS; ++color)
			{
				if (me.colorsHit[color] > 0)
				{
					gravities[color][maxLocInNeighborsGroup[color]] = d;
					for (int i = 0; i < size; ++i)
					{
						if (gravities[color][i] > gravities[color][maxLocInNeighborsGroup[color]])
							maxLocInNeighborsGroup[color] = i;
					}
					if (counter[color] < size)
						++counter[color];
				}
			}
		}

		double[] sums = { 0, 0, 0, 0, 0, 0 };
		for (int color = 0; color < AlleleClassifier.NUMCOLORS; ++color)
		{
			for (int i = 0; i < counter[color]; ++i)
				sums[color] += gravities[color][i];
			sums[color] /= counter[color];
		}

		int min = 0;
		for (int color = 1; color < AlleleClassifier.NUMCOLORS; ++color)
		{
			if (sums[color] < sums[min])
				min = color;
		}

		item.color = min;
	}

}
