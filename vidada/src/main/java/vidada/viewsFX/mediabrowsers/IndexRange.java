package vidada.viewsFX.mediabrowsers;

import java.util.Arrays;


/**
 * Represents an immutable range of indexes, for example index [0-10]
 * @author IsNull
 *
 */
public class IndexRange {

	public static final IndexRange Undefined = new IndexRange(-1,-1);

	public final int FirstCell;
	public final int LastCell;

	public IndexRange(int first, int last){
		this.FirstCell = first;
		this.LastCell = last;
	}

	/**
	 * Returns all indexes
	 * @return
	 */
	public int[] indexes(){
		int[] indexes = new int[LastCell-FirstCell+1];
		for (int i = 0; i < indexes.length; i++) {
			indexes[i] = FirstCell+i;
		}
		return indexes;
	}

	/**
	 * Is this cell range defined
	 * @return
	 */
	public boolean isDefined(){
		return FirstCell >= 0 && LastCell >= 0; 
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + FirstCell;
		result = prime * result + LastCell;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IndexRange other = (IndexRange) obj;
		if (FirstCell != other.FirstCell)
			return false;
		if (LastCell != other.LastCell)
			return false;
		return true;
	}

	/**
	 * Checks if the given index is part of this cell range
	 * @param index
	 * @return
	 */
	public boolean contains(int index){
		return index >= FirstCell && index <= LastCell;
	}

	@Override
	public String toString(){
		return "[" + FirstCell + " - " + LastCell + "]";
	}

	private final static int[] EmptyIntArray = new int[0];

	/**
	 * Finds all indexs which are no longer present in the new CellRange.
	 * @param old
	 * @param nuv
	 * @return Returns an array which holds indexes no longer used
	 */
	public static int[] unused(final IndexRange old, final IndexRange nuv){

		int[] unused = EmptyIntArray;

		if(old != null){
			int[] tmp = new int[old.LastCell-old.FirstCell+1];
			int pfirst=-1;
			int plast=-1;

			int index = old.FirstCell;
			for (int i = 0; i < tmp.length; i++) {
				if(nuv == null || !nuv.contains(index)){
					tmp[i] = index;
					pfirst = (pfirst == -1) ? i : pfirst;
					plast = i+1;
				}
				index++;
			}
			if(pfirst != -1 && plast != -1)
				unused = Arrays.copyOfRange(tmp, pfirst, plast);
		}

		return unused;
	}
}
