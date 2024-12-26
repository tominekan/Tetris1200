import org.junit.Assert;
import org.junit.Test;


public class TestTetrisLogic {
    @Test
    public void testTetrisLogicCreate() {
        int[] dimensions = new int[] {3,3};
        TetrisLogic logic = new TetrisLogic(3, 3);
        Assert.assertArrayEquals(dimensions, logic.getBoardDimensions());
    }

}
