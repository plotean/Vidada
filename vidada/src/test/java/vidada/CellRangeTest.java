package vidada;

import java.util.Arrays;

import junit.framework.TestCase;

import org.junit.Test;

import vidada.viewsFX.mediabrowsers.CellRange;

public class CellRangeTest extends TestCase {

	@Test
	public void testUnusedNull(){
		CellRange range1 = new CellRange(0,10);
		CellRange.unused(null, range1);
		CellRange.unused(range1, null);
	}

	@Test
	public void testContainsLB(){
		CellRange range1 = new CellRange(0,10);
		assertTrue(range1.contains(0));
	}

	@Test
	public void testContainsUB(){
		CellRange range1 = new CellRange(0,10);
		assertTrue(range1.contains(10));
	}

	@Test
	public void testContainsMid(){
		CellRange range1 = new CellRange(0,10);
		assertTrue(range1.contains(5));
	}

	@Test
	public void testIndexes(){
		CellRange range1 = new CellRange(0,5);
		int[] indexes = range1.indexes();
		int[] expected = {0,1,2,3,4,5};

		assertTrue(Arrays.equals(indexes, expected));
	}


	@Test
	public void testUnusedUnique(){
		CellRange range1 = new CellRange(0,10);

		int[] dropped = CellRange.unused(range1, range1);
		// we expect no index to be dropped
		print("expected empty: ", dropped);
		assertTrue(dropped.length == 0);
	}

	private void print(String prev, int[] arr){
		System.out.print(prev + " {");
		for (int i : arr) {
			System.out.print(i+",");
		}
		System.out.println("}");

	}

	@Test
	public void testUnusedOverlapTop(){
		CellRange range1 = new CellRange(0,10);
		CellRange range2 = new CellRange(5,15);

		int[] dropped = CellRange.unused(range1, range2);
		int[] expectedDrop = {0,1,2,3,4};

		print("expected: {0,1,2,3,4} but:", dropped);

		assertTrue(Arrays.equals(dropped, expectedDrop));
	}

	@Test
	public void testUnusedOverlapBottom(){
		CellRange range1 = new CellRange(10, 20);
		CellRange range2 = new CellRange(0, 13);

		int[] dropped = CellRange.unused(range1, range2);
		int[] expectedDrop = {14,15,16,17,18,19,20};

		print("expected: {14,15,16,17,18,19,20} but:", dropped);

		assertTrue(Arrays.equals(dropped, expectedDrop));
	}





}
