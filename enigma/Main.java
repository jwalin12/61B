package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Jwalin Joshi
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.out.println("error!!");
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.out.println("about to exit!");
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine M = readConfig();
        String nextline = _input.nextLine();
        if (nextline.trim().charAt(0) == '*') {
            setUp(M, nextline);
        } else {
            throw new EnigmaException("First line must be a setup!");
        }
        while (_input.hasNextLine()) {
            String next = _input.nextLine();
            String nextnoblank = next.replace(" ", "");

            char [] nextarr = nextnoblank.toCharArray();




            if (!next.trim().equals("")) {
                if (next.trim().charAt(0) == ('*')) {
                    setUp(M, next);

                } else {
                    for (int m = 0; m < nextarr.length; m++) {
                        if (!_alphabet.getalph().contains(nextarr[m])
                                && (nextarr[m] != '*' || nextarr[m] != ' ')) {
                            throw new
                                    EnigmaException("Char not in alphabet");
                        }
                    }
                    String converted = M.convert(next);
                    printMessageLine(converted);
                }


            } else {
                _output.println();

            }
        }

    }



    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            int numrotors = 0;
            int numpawls = 0;
            Collection<Rotor> allrotors = new ArrayList<Rotor>();
            _alphabet = new Alphabet(_config.next());
            numrotors = _config.nextInt();
            numpawls = _config.nextInt();
            while (_config.hasNext()) {
                Rotor nextrotor = readRotor();
                if (allrotors.contains(nextrotor)) {
                    throw new EnigmaException("Rotor is repeated");
                }
                allrotors.add(nextrotor);
            }

            return new Machine(_alphabet, numrotors, numpawls, allrotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {

            String name = _config.next();

            String type = _config.next();

            Permutation perm = new Permutation(_config.nextLine(), _alphabet);
            while (_config.hasNext("\\(([^)]+)\\)")) {

                perm.addCycle(_config.next());
            }
            if (type.contains("M")) {

                nummoviing++;
                return new MovingRotor(name, perm, type);

            } else if (type.equals("N")) {
                return new FixedRotor(name, perm);

            } else {
                return new Reflector(name, perm);
            }


        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String [] setting = settings.split(" ");
        String[] rotors = new String[M.numRotors()];

        if (setting.length < M.numRotors() + 2) {
            throw new EnigmaException("Wrong input lenght");
        }
        String initial = setting[M.numRotors() + 1];
        String ringstal = null;
        int adder = 2;
        if (setting.length > M.numRotors() + 2
                &&
                setting[M.numRotors() + 2].charAt(0) != '(') {
            ringstal = setting[M.numRotors() + 2];
            adder++;
        }



        for (int i = 1; i <  M.numRotors() + 1; i++) {
            rotors[i - 1] = setting[i];
        }
        Permutation plug = new Permutation("", _alphabet);

        for (int j = M.numRotors() + adder; j < setting.length; j++) {

            if (setting[j].trim().length()
                    != 4) {
                throw new
                        EnigmaException(" Plugboard must have cycl of len 2");
            }
            plug.addCycle(setting[j]);
        }
        M.insertRotors(rotors);

        if (M.numRotors() != rotors.length
                || M.numRotors()
                != M.getMyrotors().size()) {
            throw new EnigmaException("Not enough rotors!");
        }
        if (ringstal != null) {
            M.setRingsetting(ringstal);
        }
        M.setRotors(initial);
        M.setPlugboard(plug);
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters).
     *
     *  */
    private void printMessageLine(String msg) {
        char[] arr = msg.toCharArray();

        while (msg.length() > 5) {
            _output.print(msg.substring(0, 5) + " ");
            msg = msg.substring(5);
        }

        _output.println(msg);

    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** Returns NUMMOVIING.**/
    public int getNummoviing() {
        return nummoviing;
    }

    /** NUMMOVIING1 is the new nummoviing. **/
    public void setNummoviing(int nummoviing1) {
        this.nummoviing = nummoviing1;
    }

    /** number of moving rotors. **/
    private int nummoviing = 0;
}
