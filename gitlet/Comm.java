package gitlet;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import java.io.Serializable;
import java.text.SimpleDateFormat;


/**
 * Commits.
 * @author Jwalin Joshi
 */
public class Comm implements Serializable {

    /**parent commit.**/
    private Comm parent;
    /**conents of commits. **/
    private HashMap<String, Blob> contents = new HashMap<String, Blob>();
    /**message of commit. **/
    private String message;
    /**date of commit.**/
    private String date;
    /**hash of commit. **/
    private String commitID;
    /**merge-parent.**/
    private Comm mergepointer;
    /**if split or not.**/
    private boolean splitpoint;
    /**all things it splits with.**/
    private ArrayList<String> splits = new ArrayList<String>();


    /**makes a commit.
     *
     * @param parent1 parent
     * @param files files
     * @param comment message
     */
    Comm(Comm parent1, HashMap<String, Blob> files, String comment) {
        parent = parent1;
        contents = files;
        message = comment;
        splitpoint = false;
        date = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy")
                .format(new Date()) + " -0800";
        String stringfile = "";
        if (files != null) {
            stringfile = files.toString();
        }
        commitID = Utils.sha1(message, stringfile, date, parent.toString());
    }

    /**
     *
     * @return parent
     */
    public Comm getParent() {
        return parent;
    }

    /**
     *
     * @param parent1 new parent
     */
    public void setParent(Comm parent1) {
        this.parent = parent1;
    }

    /**
     *
     * @return contents
     */
    public HashMap<String, Blob> getContents() {
        return contents;
    }

    /**
     *
     * @param contents1 new contents
     */
    public void setContents(HashMap<String, Blob> contents1) {
        this.contents = contents1;
    }

    /**
     *
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message1 new message
     */
    public void setMessage(String message1) {
        this.message = message1;
    }

    /**
     *
     * @return date
     */
    public String getDate() {
        return date;
    }

    /**
     *
     * @param date1 new date
     */
    public void setDate(String date1) {
        this.date = date1;
    }

    /**
     *
     * @return commitID
     */
    public String getCommitID() {
        return commitID;
    }

    /**
     *
     * @param commitID1 commitID
     */
    public void setCommitID(String commitID1) {
        this.commitID = commitID1;
    }

    /**
     *
     * @return mergepointer
     */
    public Comm getMergepointer() {
        return mergepointer;
    }

    /**
     *
     * @param mergepointer1 new mergeparent
     */
    public void setMergepointer(Comm mergepointer1) {
        this.mergepointer = mergepointer1;
    }

    /**
     *
     * @return splitpoint
     */
    public boolean isSplitpoint() {
        return splitpoint;
    }

    /**
     *
     * @param splitpoint1 new splitpoint
     */
    public void setSplitpoint(boolean splitpoint1) {
        this.splitpoint = splitpoint1;
    }

    /**
     *
     * @return splits
     */
    public ArrayList<String> getSplits() {
        return splits;
    }

    /**
     *
     * @param splits1 new splits
     */
    public void setSplits(ArrayList<String> splits1) {
        this.splits = splits1;
    }

    /**
     * initial commit.
     */

    Comm() {
        message = "initial commit";
        date = "Wed Dec 31 16:00:00 1969 -0800";
        commitID = Utils.sha1(message, date);
    }


}
