package vidada;

import junit.framework.TestCase;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;
import vidada.viewsFX.mediabrowsers.IndexRange;

import java.util.Arrays;

public class CellRangeTest extends TestCase {

    private static final Logger logger = LogManager.getLogger(CellRangeTest.class.getName());


    @Test
	public void testUnusedNull(){
		IndexRange range1 = new IndexRange(0,10);
		IndexRange.unused(null, range1);
		IndexRange.unused(range1, null);
	}

	@Test
	public void testContainsLB(){
		IndexRange range1 = new IndexRange(0,10);
		assertTrue(range1.contains(0));
	}

	@Test
	public void testContainsUB(){
		IndexRange range1 = new IndexRange(0,10);
		assertTrue(range1.contains(10));
	}

	@Test
	public void testContainsMid(){
		IndexRange range1 = new IndexRange(0,10);
		assertTrue(range1.contains(5));
	}

	@Test
	public void testIndexes(){
		IndexRange range1 = new IndexRange(0,5);
		int[] indexes = range1.indexes();
		int[] expected = {0,1,2,3,4,5};

		assertTrue(Arrays.equals(indexes, expected));
	}


	@Test
	public void testUnusedUnique(){
		IndexRange range1 = new IndexRange(0,10);

		int[] dropped = IndexRange.unused(range1, range1);
		// we expect no index to be dropped
		print("expected empty: ", dropped);
		assertTrue(dropped.length == 0);
	}

	private void print(String prev, int[] arr){
		logger.info(prev + " {");
		for (int i : arr) {
            logger.info(i+",");
		}
        logger.info("}");
	}

	@Test
	public void testUnusedOverlapTop(){
		IndexRange range1 = new IndexRange(0,10);
		IndexRange range2 = new IndexRange(5,15);

		int[] dropped = IndexRange.unused(range1, range2);
		int[] expectedDrop = {0,1,2,3,4};

		print("expected: {0,1,2,3,4} but:", dropped);

		assertTrue(Arrays.equals(dropped, expectedDrop));
	}

	@Test
	public void testUnusedOverlapBottom(){
		IndexRange range1 = new IndexRange(10, 20);
		IndexRange range2 = new IndexRange(0, 13);

		int[] dropped = IndexRange.unused(range1, range2);
		int[] expectedDrop = {14,15,16,17,18,19,20};

		print("expected: {14,15,16,17,18,19,20} but:", dropped);

		assertTrue(Arrays.equals(dropped, expectedDrop));
	}





}
