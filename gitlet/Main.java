package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Jwalin Joshi
 *  collaborator: Santosh Tatipamula
 */
public class Main {
    /**
     * repo.
     *
     */
    private static Git repo;

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {

        try {
            if (args.length == 0) {
                Utils.message("Please enter a command.");
                throw new GitletException();
            }
            if (commands.contains(args[0])) {
                if (repoexist()) {
                    if (args[0].equals("init")) {
                        Utils.message("A Gitlet version-control system"
                                + " already exists in the current directory.");
                    } else {
                        repo = Utils.readObject(new File(".gitlet/repo"),
                                Git.class);
                        String [] operands = Arrays.copyOfRange(args, 1,
                                args.length);
                        docommand(args[0], operands);

                    }
                } else if (args[0].equals("init")) {
                    repo = new Git();
                    File mr = new File(".gitlet/repo");
                    Utils.writeObject(mr, repo);
                } else {
                    Utils.message("Not in an initialized Gitlet directory.");
                    throw new GitletException();
                }

            } else {
                Utils.message("No command with that name exists.");
                throw new GitletException();
            }
        } catch (GitletException e) {
            System.exit(0);
        }


    }

    /**
     * checks if repo exists.
     *
     * @return repo exist boolean.
     */
    private static boolean repoexist() {
        String f = System.getProperty("user.dir");
        File tmpDir = new File(f + "/.gitlet");
        if (tmpDir.exists()) {
            return true;
        }
        return false;
    }


    /**
     * do the command.
     * @param command command.
     * @param operands operands.
     */
    private static void docommand(String command, String[] operands) {
        if (command.equals("add")) {
            if (operands.length > 1) {
                Utils.message("Incorrect operands.");
                throw new GitletException();
            } else {
                repo.add(operands[0]);
                Utils.writeObject(new File(".gitlet/repo"), repo);
            }
        } else if (command.equals("commit")) {
            if (operands.length > 1) {
                Utils.message("Incorrect operands.");
                throw new GitletException();
            } else {
                repo.commit(operands[0]);
                Utils.writeObject(new File(".gitlet/repo"), repo);
            }
        } else if (command.equals("rm")) {
            if (operands.length > 1) {
                Utils.message("Incorrect operands.");
                throw new GitletException();
            } else {
                repo.rm(operands[0]);
                Utils.writeObject(new File(".gitlet/repo"), repo);
            }
        } else if (command.equals("status")) {
            if (operands.length > 0) {
                Utils.message("Incorrect operands.");
                throw new GitletException();
            } else {
                repo.status();
            }
        } else if (command.equals("log")) {
            if (operands.length > 0) {
                Utils.message("Incorrect operands.");
                throw new GitletException();
            } else {
                repo.log();
            }
        } else if (command.equals("checkout")) {
            if (operands.length == 2 && operands[0].equals("--")) {
                repo.checkoutfile(operands[1]);
                Utils.writeObject(new File(".gitlet/repo"), repo);
            } else if (operands.length == 3 && operands[1].equals("--")) {
                repo.checkoutcomm(operands[0], operands[2]);
                Utils.writeObject(new File(".gitlet/repo"), repo);
            } else if (operands.length == 1) {
                repo.checkoutbranch(operands[0]);
                Utils.writeObject(new File(".gitlet/repo"), repo);
            } else {
                Utils.message("Incorrect operands.");
                throw new GitletException();

            }
        } else {
            docommandtwo(command, operands);
        }

    }

    /**
     * splittin gup command for style.
     * @param command command.
     * @param operands operands.
     */
    private static void docommandtwo(String command, String[] operands) {
        if (command.equals("branch")) {
            if (operands.length != 1) {
                Utils.message("Incorrect operands.");
                throw new GitletException();
            } else {
                repo.branch(operands[0]);
                Utils.writeObject(new File(".gitlet/repo"), repo);
            }
        } else if (command.equals("rm-branch")) {
            if (operands.length != 1) {
                Utils.message("Incorrect operands.");
                throw new GitletException();
            } else {
                repo.rmbranch(operands[0]);
                Utils.writeObject(new File(".gitlet/repo"), repo);
            }
        } else if (command.equals("reset")) {
            if (operands.length != 1) {
                Utils.message("Incorrect operands.");
                throw new GitletException();
            } else {
                repo.reset(operands[0]);
                Utils.writeObject(new File(".gitlet/repo"), repo);
            }
        } else if (command.equals("global-log")) {
            if (operands.length != 0) {
                Utils.message("Incorrect operands.");
                throw new GitletException();
            } else {
                repo.globallog();
            }
        } else if (command.equals("find")) {
            if (operands.length != 1) {
                Utils.message("Incorrect operands.");
                throw new GitletException();
            } else {
                repo.find(operands[0]);
            }
        } else if (command.equals("merge")) {
            if (operands.length != 1) {
                Utils.message("Incorrect operands.");
                throw new GitletException();
            } else {
                repo.merge(operands[0]);
                Utils.writeObject(new File(".gitlet/repo"), repo);
            }
        }
    }

    /**
     * all possible commands.
     */
    private static ArrayList<String> commands =
            new ArrayList<String>(Arrays.asList("init", "add",
            "commit", "rm", "log", "global-log",
            "find", "status", "checkout",
            "branch", "rm-branch", "reset", "merge"));

}
