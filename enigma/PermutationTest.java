package enigma;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static enigma.TestUtils.*;
import static org.junit.Assert.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Jwalin Joshi
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + "(wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(e, perm.permute(c));
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void checkTransform() {
        perm = new Permutation("(ABCDEFGHIJKLMNOP)(QRSTU)", UPPER);
        checkPerm("regular", "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                "BCDEFGHIJKLMNOPARSTUQVWXYZ");
        perm = new Permutation("(AB)", UPPER);
        checkPerm("switch", "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                "BACDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    @Test
    public void checkAdd() {
        perm = new Permutation("(ABCDEFGHIJKLMNOP)", UPPER);
        perm.addCycle("()");
        assertArrayEquals(perm.getcyc(), new String [] {"ABCDEFGHIJKLMNOP"});
        perm = new Permutation("()", UPPER);
        perm.addCycle("(ABCDEFGHIJKLMNOP)");
        assertArrayEquals(perm.getcyc(), new String [] {"ABCDEFGHIJKLMNOP"});

    }

    @Test
    public void testInvertChar() {
        Permutation p = new Permutation("(PNH) (ABDFIKLZYXW) (JC)", UPPER);
        assertEquals(p.invert('N'), 'P');
        assertEquals(p.invert('A'), 'W');
        assertEquals(p.invert('W'), 'X');


    }

    @Test
    public void testDerangement() {

        Permutation p = new Permutation(
                "(ABCD) (EFGHIJK) (LMNOP) (QRSTUV) (WXYZ)",
                new Alphabet());
        assertTrue(p.derangement());
        p = new Permutation("(PNH) (ABDFIKLZYXW) (JC)", new Alphabet());
        assertFalse(p.derangement());
        p = new Permutation("()", new Alphabet());
        assertFalse(p.derangement());


    }
}




