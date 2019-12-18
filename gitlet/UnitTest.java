package gitlet;

import ucb.junit.textui;
import org.junit.Test;


/** The suite of all JUnit tests for the gitlet package.
 *  @author
 */
public class UnitTest {

    /** Run the JUnit tests in the loa package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    /** A dummy test to avoid complaint. */


    @Test
    public void basicstatusTest() {
        Git repo = new Git();
        repo.status();
    }
    @Test
    public void globallogTest() {
        Git repo = new Git();
        repo.globallog();

    }
    @Test
    public void branchTest() {
        Git repo = new Git();
        repo.branch("branch");

    }
    @Test
    public void logTest() {
        Git repo = new Git();
        repo.log();
    }

    @Test
    public void statustesttwo() {
        Git repo = new Git();
        repo.status();
        repo.branch("branch");
        repo.status();
    }
    @Test
    public void initcommTest() {
        Git repo = new Git();
        Comm comm = new Comm();
    }
    @Test
    public void commparentTest() {
        Git repo = new Git();
        Comm comm = new Comm();
        Comm comm1 = new Comm(comm, null, "rand");
    }
    @Test
    public void statustestthree() {
        Git repo = new Git();
        Comm comm = new Comm();
        Comm comm1 = new Comm(comm, null, "rand");
        repo.status();

    }
    @Test
    public void statustestfo() {
        Git repo = new Git();
        Comm comm = new Comm();
        repo.status();

    }
    @Test
    public void logtestthree() {
        Git repo = new Git();
        Comm comm = new Comm();
        repo.log();

    }
    @Test
    public void logtestfo() {
        Git repo = new Git();
        Comm comm = new Comm();
        repo.globallog();

    }
    @Test
    public void logtestfi() {
        Git repo = new Git();
        Comm comm = new Comm();
        repo.branch("branch");
        repo.globallog();

    }




}


