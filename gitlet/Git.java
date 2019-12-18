package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;


/**
 * Git structure.
 * @author Jwalin Joshi
 */
public class Git implements Serializable {
    /**
     * branch structure.
     */
    private HashMap<String, Comm> branches;
    /**
     * current branch.
     */
    private String branch;
    /**
     * untracked files.
     */
    private ArrayList<String> untracked;
    /**
     * staging area.
     */
    private HashMap<String, Blob> stagingarea;
    /**
     * head commit.
     */
    private Comm head;
    /**
     * allcommits.
     */
    private HashMap<String, Comm> allcomms;
    /**
     * staging file.
     */
    private File staging;
    /**
     * commits file.
     */
    private File commits;
    /**
     * repo.
     */
    private File repo;

    /**
     * stores comms found thru BFS.
     */
    private HashMap<Comm, Integer> bfshelper;

    /**
     * initializes a repo.
     */
    public Git() {
        repo = new File(".gitlet");
        repo.mkdir();
        Comm inital = new Comm();
        staging = Utils.join(repo, "staging");
        commits = Utils.join(repo, "commits");
        staging.mkdir();
        commits.mkdir();
        head = inital;
        branch = "master";
        untracked = new ArrayList<String>();
        branches = new HashMap<String, Comm>();
        branches.put("master", inital);
        stagingarea = new HashMap<String, Blob>();
        File initcommit = Utils.join(commits, inital.getCommitID());
        Utils.writeObject(initcommit, inital);
        allcomms = new HashMap<String, Comm>();
        allcomms.put(inital.getCommitID(), inital);
        bfshelper = new HashMap<Comm, Integer>();
    }

    /**
     * adds files to stagin area.
     * @param name name of file.
     */
    public void add(String name) {
        File f = new File(name);
        if (!f.exists()) {
            Utils.message("File does not exist.");
            throw new GitletException();
        }
        Blob blob = new Blob(f);
        untracked.remove(name);
        HashMap<String, Blob> files = head.getContents();
        if (files.containsKey(name)
                && files.get(name).getContent().equals(blob.getContent())) {
            return;
        } else if (!(files.containsKey(name) && files.get(name).equals(blob))) {
            stagingarea.put(blob.getName(), blob);
            File stagefile = Utils.join(staging, blob.getHash());
            Utils.writeObject(stagefile, blob);
        }

    }

    /**
     * commits a commit.
     * @param message message of commit.
     */

    public void commit(String message) {
        if (message.trim().equals("")) {
            Utils.message("Please enter a commit message.");
            throw new GitletException();
        }

        HashMap<String, Blob> tracked = deepcopytracked(head.getContents());


        if (stagingarea.size() > 0 || untracked.size() > 0) {
            for (String s : stagingarea.keySet()) {
                tracked.put(s, stagingarea.get(s));
            }
            if (untracked.size() > 0) {
                for (String s : untracked) {
                    tracked.remove(s);
                }
            }
        } else {
            Utils.message("No changes added to the commit");
            throw new GitletException();
        }

        Comm commit = new Comm(head, tracked, message);
        File commitfile = Utils.join(commits, commit.getCommitID());
        Utils.writeObject(commitfile, commit);
        stagingarea.clear();
        untracked.clear();
        head = commit;
        branches.put(branch, head);
        allcomms.put(commit.getCommitID(), commit);

    }

    /**
     * remove files.
     * @param name name of file to be removed.
     */
    void rm(String name) {
        if (!stagingarea.containsKey(name)
                && !head.getContents().containsKey(name)
        ) {
            Utils.message("No reason to remove the file.");
            throw new GitletException();
        }
        if (stagingarea.containsKey(name)) {
            stagingarea.remove(name);
        }
        if (head.getContents().containsKey(name)) {
            untracked.add(name);
            Utils.restrictedDelete(new File(name));
        }


    }

    /**
     * status of repo.
     */
    void status() {
        System.out.println("=== Branches ===");
        Object[] brancharray = branches.keySet().toArray();
        Arrays.sort(brancharray);
        for (Object b : brancharray) {
            if (!branches.get(b).equals(head)) {
                System.out.println(b);
            } else {
                System.out.println("*" + b);
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        Object[] stagesarray = stagingarea.keySet().toArray();
        Arrays.sort(stagesarray);
        for (Object staged : stagesarray) {
            System.out.println(staged);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        Collections.sort(untracked);
        for (Object s : untracked) {
            System.out.println(s);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();

    }

    /**
     * log of commits on current branch.
     */
    public void log() {
        String headID = head.getCommitID();
        while (headID != null) {
            File curr = new File(".gitlet/commits/" + headID);
            Comm currcomm =  Utils.readObject(curr, Comm.class);
            System.out.println("===");
            System.out.println("commit " + currcomm.getCommitID());
            if (currcomm.getMergepointer() != null) {
                System.out.println("Merge: "
                        + currcomm.getParent().getCommitID()
                        .substring(0, 7) + " "
                        + currcomm.getMergepointer().getCommitID()
                        .substring(0, 7));
            }
            System.out.println("Date: " + currcomm.getDate());
            System.out.println(currcomm.getMessage());
            System.out.println();
            if (currcomm.getParent() == null) {
                break;
            } else {
                headID = currcomm.getParent().getCommitID();
            }
        }
    }

    /**
     * checksout a file to a previous version.
     * @param file name of file.
     */

    public void checkoutfile(String file) {

        if (!head.getContents().containsKey(file)) {
            Utils.message("File does not exist in that commit.");
            throw new GitletException();
        }
        File curr = new File(file);
        Utils.writeContents(curr, head.getContents().get(file).getContent());
    }

    /**
     * checkout to a previous version of a file based on a commit.
     * @param commID ID of commit.
     * @param file file name.
     */
    public void checkoutcomm(String commID, String file) {
        Comm comm = null;
        boolean exists = false;

        if (!allcomms.containsKey(commID)) {
            for (String s :allcomms.keySet()) {
                if (commID.equals(s.substring(0, 8))) {
                    comm = allcomms.get(s);
                    break;
                }
            }
        } else {
            comm = allcomms.get(commID);
        }
        if (comm == null) {
            Utils.message("No commit with that id exists.");
            throw new GitletException();
        } else if (!comm.getContents().containsKey(file)) {
            Utils.message("File does not exist in that commit.");
            throw new GitletException();
        } else {
            File curr = new File(file);
            Utils.writeContents(curr,
                    comm.getContents().get(file).getContent());
        }

    }


    /**
     * checkouts to a different branch.
     * @param givenbranch branch name.
     */
    public void checkoutbranch(String givenbranch) {
        if (branch.equals(givenbranch)) {
            Utils.message("No need to checkout the current branch. ");
            throw new GitletException();
        } else if (!branches.containsKey(givenbranch)) {
            Utils.message("No such branch exists.");
            throw new GitletException();
        } else {
            Comm curr = head;
            if (hasuntracked(curr)) {
                Utils.message("There is an untracked file in the way;"
                        + " delete it or add it first.");
                throw new GitletException();
            } else {
                head = branches.get(givenbranch);
                branch = givenbranch;
                branches.put(branch, head);
                for (Blob b: head.getContents().values()) {
                    File f = new File(b.getName());
                    Utils.writeContents(f,
                            head.getContents().get(b.getName()).getContent());
                    stagingarea.clear();
                }
                deleteuntracked(head);
            }
        }
    }

    /**
     * deepcopies a hashmap of tracked.
     * @param original original hashmap
     * @return new hashmap
     */
    public static HashMap<String, Blob> deepcopytracked(
            HashMap<String, Blob> original) {
        HashMap<String, Blob> copy = new HashMap<String, Blob>();
        for (Map.Entry<String, Blob> entry : original.entrySet()) {
            copy.put(entry.getKey(),
                    new Blob(entry.getValue()));
        }
        return copy;
    }

    /**
     * deepcopies hashmap.
     * @param original original hashmap
     * @return deepcopy of hashmap
     */
    public static HashMap<Comm, Integer> bfs(
            HashMap<Comm, Integer> original) {
        HashMap<Comm, Integer> copy = new HashMap<Comm, Integer>();
        for (Map.Entry<Comm, Integer> entry : original.entrySet()) {
            copy.put(entry.getKey(),
                    entry.getValue());
        }
        return copy;
    }

    /**
     *
     * @param curr commit you are checking.
     * @return has untracked files.
     */
    public boolean hasuntracked(Comm curr) {
        HashMap<String, Blob> tracked =  curr.getContents();
        File workingdirec = new File(System.getProperty("user.dir"));
        if (tracked == null) {
            if (workingdirec.listFiles() != null) {
                return true;
            }
        }
        for (File f : workingdirec.listFiles()) {
            if (!tracked.containsKey(f.getName())
                    && !f.getName().equals(".gitlet")
                    && !stagingarea.containsKey(f.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * deletes untracked files.
     * @param curr current commit
     */
    public void deleteuntracked(Comm curr) {

        File workingdirec = new File(System.getProperty("user.dir"));
        for (File f : workingdirec.listFiles()) {
            if (!curr.getContents().containsKey(f.getName())
                    && !f.getName().equals(".gitlet")
                    && !stagingarea.containsKey(f.getName())) {
                Utils.restrictedDelete(f);

            }

        }
    }

    /**
     * creates a new branch.
     * @param givenbranch branch name.
     */
    public void branch(String givenbranch) {
        if (branches.containsKey(givenbranch)) {
            Utils.message("A branch with that name already exists.");
            throw new GitletException();
        }
        branches.put(givenbranch, head);
        head.setSplitpoint(true);

    }

    /**
     * removes a branch.
     * @param operand branch to be removed.
     */
    public void rmbranch(String operand) {
        if (!branches.containsKey(operand)) {
            Utils.message("A branch with that name does not exist.");
            throw new GitletException();
        } else if (branch.equals(operand)) {
            Utils.message("Cannot remove the current branch.");
            throw new GitletException();
        } else {
            branches.remove(operand);
        }
    }

    /**
     * resets all files to a previous commit.
     * @param commID commitID
     */
    public void reset(String commID) {
        Comm comm = null;
        if (hasuntracked(head)) {
            Utils.message("There is an untracked file in the way;"
                    + " delete it or add it first.");
            throw new GitletException();
        }
        if (allcomms.containsKey(commID)) {
            comm = allcomms.get(commID);
        } else {
            for (String s : allcomms.keySet()) {
                if (s.substring(0, 8).equals(commID)) {
                    comm = allcomms.get(s);
                    break;
                }
            }

        }
        if (comm == null) {
            Utils.message("No commit with that id exists.");
        } else {
            head = comm;
            for (Blob b: head.getContents().values()) {
                File f = new File(b.getName());
                Utils.writeContents(f,
                        head.getContents().get(b.getName()).getContent());
                stagingarea.clear();
            }
            deleteuntracked(head);
            branches.put(branch, head);

        }
    }

    /**
     * finds all commits with same message and prints them.
     * @param cmessage message to search.
     */
    public void find(String cmessage) {
        boolean exists = false;
        for (Comm c: allcomms.values()) {
            if (c.getMessage().equals(cmessage)) {
                exists = true;
                System.out.println(c.getCommitID());
            }
        }
        if (!exists) {
            Utils.message("Found no commit with that message.");
            throw new GitletException();
        }
    }

    /**
     * log of all commits ever made.
     */
    public void globallog() {
        for (Comm currcomm: allcomms.values()) {
            System.out.println("===");
            System.out.println("commit " + currcomm.getCommitID());
            if (currcomm.getMergepointer() != null) {
                System.out.println("Merge: "
                        + currcomm.getParent()
                        .getCommitID().substring(0, 7) + " "
                        + currcomm
                        .getMergepointer().getCommitID().substring(0, 7));
            }
            System.out.println("Date: " + currcomm.getDate());
            System.out.println(currcomm.getMessage());
            System.out.println();
        }
    }

    /**
     * merges two branches.
     * @param givenbranch branch to merge.
     */
    public void merge(String givenbranch) {

        if (hasuntracked(head)) {
            Utils.message("There is an untracked file in the way;"
                    + " delete it or add it first.");
            throw new GitletException();
        } else if (stagingarea.size() > 0 || untracked.size() > 0) {
            Utils.message("You have uncommitted changes.");
            throw new GitletException();
        } else if (!branches.containsKey(givenbranch)) {
            Utils.message("A branch with that name does not exist.");
            throw new GitletException();
        } else if (branch.equals(givenbranch)) {
            Utils.message("Cannot merge a branch with itself.");
            throw new GitletException();
        } else {
            Comm split = findsplit(branch, givenbranch);
            if (split.equals(branches.get(givenbranch))) {
                Utils.message("Given branch is an "
                        + "ancestor of the current branch.");
            } else if (split.equals(branches.get(branch))) {
                head = branches.get(givenbranch);
                branches.put(branch, head);
                deleteuntracked(head);
                Utils.message("Current branch fast-forwarded.");
            } else {
                mergeprocess(givenbranch, false, split);
            }



        }
    }

    /**
     * the process of merging.
     * @param givenbranch given branch.
     * @param conflict merge conflict.
     * @param splitpoint splitpoint for merge.
     */
    private void mergeprocess(String givenbranch,
                              boolean conflict, Comm splitpoint) {
        ArrayList<String> modifiedincurr =
                findchanged(splitpoint, branches.get(branch));
        ArrayList<String> modifiedingiven =
                findchanged(splitpoint, branches.get(givenbranch));
        ArrayList<String> addedincurr =
                findadded(splitpoint, branches.get(branch));
        ArrayList<String> addedingiven =
                findadded(splitpoint, branches.get(givenbranch));
        Comm topofcurr = branches.get(branch);
        Comm topofgiven = branches.get(givenbranch);
        for (String file: splitpoint.getContents().keySet()) {

            if (!modifiedincurr.contains(file)
                    && modifiedingiven.contains(file)) {
                checkoutcomm(branches.get(givenbranch).getCommitID(), file);
                add(file);
            }
            if (modifiedincurr.contains(file)
                    && modifiedingiven.contains(file)) {
                if (!(topofcurr.getContents()
                        .get(file).getContent().equals(topofgiven
                                .getContents().get(file).getContent()))) {

                    conflict = true;
                    mergeconflict(topofcurr.getContents().get(file),
                            topofgiven.getContents().get(file));
                }
            }
            if (modifiedingiven.contains(file)
                    && !getandadd().contains(file) && !conflict) {
                conflict = true;
                mergeconflictgiven(new Blob(new File(file)));
            }
            if (modifiedincurr.contains(file)
                    && !topofgiven.getContents().containsKey(file)
                    && !conflict) {
                conflict = true;
                mergeconflictcur(new Blob(new File(file)));
            }
            if (!modifiedincurr.contains(file)
                    && !topofgiven.getContents().containsKey(file)) {
                if (stagingarea.containsKey(file)
                        || topofcurr.getContents().containsKey(file)) {
                    rm(file);
                }
            }
        }
        for (String f :addedingiven) {
            if (!addedincurr.contains(f)) {
                checkoutcomm(branches.get(givenbranch).getCommitID(), f);
                add(f);
            } else {
                conflict = true;
                mergeconflict(topofcurr.getContents().get(f),
                        topofgiven.getContents().get(f));
            }
        }
        mergecleanup(conflict, givenbranch);
    }

    /**
     * helper for merge.
     * @param conflict is there conflict
     * @param givenbranch branch that is being merged with
     */

    public void mergecleanup(boolean conflict, String givenbranch) {
        this.commit("Merged " + givenbranch + " into " + branch + ".");
        head.setMergepointer(branches.get(givenbranch));
        if (conflict) {
            Utils.message("Encountered a merge conflict.");
        }

    }

    /**
     * merge process helper.
     * @return all files in direc.
     */
    private ArrayList<String> getandadd() {
        File workingdirec = new File(System.getProperty("user.dir"));
        List<File> files = Arrays.asList(workingdirec);
        ArrayList<String> allfiles = new ArrayList<String>();
        for (File f:files) {
            if (f.listFiles() != null) {
                for (File contained:  f.listFiles()) {
                    allfiles.add(contained.getName());
                }

            }
            allfiles.add(f.getName());
        }
        return allfiles;
    }

    /**
     * mergeconflict helper.
     * @param blob blob given.
     */
    private void mergeconflictgiven(Blob blob) {
        File f = new File(blob.getName());
        String content1 = "";
        String content2 = Utils.readContentsAsString(f);

        String result = "<<<<<<< HEAD" + "\n"
                + content1 + "=======" + "\n"
                + content2 + ">>>>>>>" + "\n";
        Utils.writeContents(f, result);

        add(f.getName());
    }
    /**
     * mergeconflict helper.
     * @param blob blob given.
     */
    private void mergeconflictcur(Blob blob) {
        File f = new File(blob.getName());
        String content2 = "";
        String content1 = Utils.readContentsAsString(f);
        String result = "<<<<<<< HEAD" + "\n"
                + content1 + "=======" + "\n" + content2
                + ">>>>>>>" + "\n";
        Utils.writeContents(f, result);
        add(f.getName());
    }

    /**
     * merge conflict resolver.
     * @param currblob file1.
     * @param givenblob file2.
     */
    private void mergeconflict(Blob currblob, Blob givenblob) {
        File f = new File(currblob.getName());
        String content1 = currblob.getContent();
        String content2 = givenblob.getContent();

        String result = "<<<<<<< HEAD" + "\n"
                + content1 + "=======" + "\n" + content2 + ">>>>>>>"
                + "\n";
        Utils.writeContents(f, result);
        add(f.getName());
    }

    /**
     * finds changed files fromt splitpoint.
     * @param splitpoint splitpoint commit.
     * @param comm commit for comparision.
     * @return names of changed files.
     */
    private ArrayList<String> findchanged(Comm splitpoint, Comm comm) {
        ArrayList<String> changed = new ArrayList<String>();
        HashMap<String, Blob> original = splitpoint.getContents();
        HashMap<String, Blob> current = comm.getContents();

        for (String file :original.keySet()) {
            if (!(current.get(file) == null)) {
                if (!current.get(file).getContent()
                        .equals(original.get(file).getContent())) {
                    changed.add(file);
                }

            }

        }
        return changed;

    }

    /**
     * find files added from splitpoint.
     * @param splitpoint splitpoint.
     * @param comm commit to compare with.
     * @return
     */
    private ArrayList<String> findadded(Comm splitpoint, Comm comm) {
        ArrayList<String> added = new ArrayList<>();
        HashMap<String, Blob> originallytracked = splitpoint.getContents();
        HashMap<String, Blob> trackednow = comm.getContents();
        for (String s: trackednow.keySet()) {
            if (!originallytracked.containsKey(s)) {
                added.add(s);
            }
        }
        return added;

    }

    /**
     *
     * @param branch1 curr branch
     * @param givenbranch given branch
     * @return
     */
    private Comm findsplit(String branch1, String givenbranch) {
        Comm splitpoint = null;
        bfs(branches.get(branch1), 1);
        HashMap<Comm, Integer> branchcur
                = new HashMap<Comm, Integer>(bfshelper);
        bfshelper.clear();
        bfs(branches.get(givenbranch), 1);
        HashMap<Comm, Integer> branchgiven
                = new HashMap<Comm, Integer>(bfshelper);

        bfshelper.clear();
        double minlen = Double.POSITIVE_INFINITY;
        for (Comm c : branchcur.keySet()) {
            if (branchgiven.containsKey(c)) {
                if ((double) branchcur.get(c) < minlen) {
                    splitpoint = c;
                    minlen = (double) branchcur.get(c);
                }
            }
        }
        return splitpoint;

    }



    /**
     *
     * @param branch1head branch head.
     * @param dist distance
     *
     */
    private void bfs(Comm branch1head, int dist) {
        Comm pointer1 = branch1head;
        bfshelper.put(pointer1, dist);
        if (pointer1.getMergepointer() != null) {
            bfs(pointer1.getMergepointer(), dist + 1);
        }
        if (pointer1.getParent() != null) {
            bfs(pointer1.getParent(), dist + 1);
        }



    }
}
