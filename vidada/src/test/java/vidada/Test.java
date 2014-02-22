package vidada;

import org.junit.Ignore;


public class Test {

	/**
	 * @param args
	 */
	@Ignore
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("PageIndex: " + getPageNumberForIndex(19, 10));


	}

	private static int getPageNumberForIndex(int index, int maxPageSize){
		int pageBase = Math.floorDiv(index+1, maxPageSize);
		return pageBase;
	}

}
