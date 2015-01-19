// Circular buffer for AlleleClassifier.Entry

class EntryCB
{
	private AlleleClassifier.Entry[] buf = null;
	private int tail = 0;
	public EntryCB(int size)
	{
		buf = new AlleleClassifier.Entry[size];
	}
	
	// Returns the Entry that is kicked out of this buffer
	public AlleleClassifier.Entry add(AlleleClassifier.Entry item)
	{
		int loc = tail % buf.length;
		AlleleClassifier.Entry tmp = buf[loc];
		buf[loc] = item;
		item.bufloc = loc;
		++tail;
		return tmp;
	}
	
	public AlleleClassifier.Entry[] getBuffer()
	{
		return buf;
	}
	
	public int getContentSize()
	{
		if( tail < buf.length)
			return tail;

		return buf.length;
	}
}
