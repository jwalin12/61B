package gitlet;

import java.io.Serializable;
import java.io.File;

/**
 * Blob tracks the contens and hashs of files.
 * @author Jwalin Joshi
 */
public class Blob implements Serializable {


    /** name of file. **/
    private String name;
    /** hash of files. **/
    private String hash;
    /** conent of files. **/
    private String content;

    /**
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return hash
     */
    public String getHash() {
        return hash;
    }

    /**
     *
     * @param hash1 hash
     */
    public void setHash(String hash1) {
        this.hash = hash;
    }

    /**
     *
     * @return content
     */
    public String getContent() {
        return content;
    }

    /**
     *
     * @param content1 content
     */
    public void setContent(String content1) {
        this.content = content1;
    }

    /**
     *
     * @param name1 name
     */
    public void setName(String name1) {
        this.name = name1;
    }

    /** makes a blob. F is the file. **/
    public Blob(File f) {

        name = f.getName();
        content = Utils.readContentsAsString(f);
        hash = Utils.sha1(Utils.readContents(f), name);

    }
    /**deepcopy of Blob. ORIG is the blob. **/
    public Blob(Blob orig) {
        name =  new String(orig.name);
        content =  new String(orig.content);
        hash = new String(orig.hash);
    }


}
