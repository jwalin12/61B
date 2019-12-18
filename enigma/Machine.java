package enigma;



import java.util.ArrayList;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Jwalin Joshi
 */
class Machine {


    /** number of rotors in engima.**/
    private int _numrotors;
    /** number of pawls in the machine.**/
    private int _pawls;
    /** all possible rotors.**/
    private ArrayList<Rotor> _allRotors;
    /** machines plugboard. **/
    private Permutation _plugboard;

    /** RETURN rotors in the machine. **/
    public ArrayList<Rotor> getMyrotors() {
        return myrotors;
    }

    /** PARAMETER MYROTORS1 is the next rotor. **/
    public void setMyrotors(ArrayList<Rotor> myrotors1) {
        this.myrotors = myrotors1;
    }
    /**rotors fed into the machine.**/
    private ArrayList<Rotor> myrotors = new ArrayList<>();

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        assert (numRotors > 1);
        assert (pawls < numRotors);
        assert (pawls >= 0);

        _alphabet = alpha;
        _numrotors = numRotors;
        _pawls = pawls;
        _allRotors = (ArrayList<Rotor>) allRotors;

    }

    /** Return the number of rotor slots I have. */
    public int numRotors() {
        return _numrotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    public int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        myrotors.clear();


        for (int i = 0; i < rotors.length; i++) {
            boolean in = false;
            for (Rotor rotor: _allRotors) {
                if (rotor.name().equals(rotors[i])) {
                    if (myrotors.contains(rotor)) {
                        throw new EnigmaException("Rotors repeated");
                    } else {
                        in = true;
                        myrotors.add(rotor);
                    }


                }

            }
            if (!in) {
                throw new EnigmaException("Bad rotor Name");
            }

        }
        for (int j = 1; j < myrotors.size(); j++) {
            myrotors.get(j).setLeft(myrotors.get(j - 1));

        }
        if (!myrotors.get(0).reflecting()) {
            throw new EnigmaException("First Rotor must be a reflector");
        }

    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        char [] arr = setting.toCharArray();

        if (setting.length() != myrotors.size() - 1) {
            throw new EnigmaException("Wrong setting size");
        }

        for (int i = 1; i < myrotors.size(); i++) {

            if (!_alphabet.contains(arr[i - 1])) {
                throw new EnigmaException("Setting not in Alphabet");
            }

            myrotors.get(i).set(arr[i - 1]);
        }

    }

    /** parameter SETTING is the ring setting. **/
    void setRingsetting(String setting) {
        char [] arr = setting.toCharArray();
        if (setting.length() != myrotors.size() - 1) {
            throw new EnigmaException("Wrong setting size");
        }

        for (int i = 1; i < myrotors.size(); i++) {

            if (!_alphabet.contains(arr[i - 1])) {
                throw new EnigmaException("Setting not in Alphabet");
            }

            myrotors.get(i).setRingsetting(arr[i - 1]);
        }

    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing

     *  the machine. */
    int convert(int c) {

        int curr = c;
        boolean [] advance = new boolean [myrotors.size()];
        advance[0] = false;
        advance[myrotors.size() - 1] = true;


        for (int i = myrotors.size() - 1; i > 0; i--) {
            if (myrotors.get(i).atNotch()) {
                advance[i] = true;
                advance[i - 1] = true;
            }
        }
        for (int j = myrotors.size() - 1; j > 0; j--) {
            if (advance[j]) {
                myrotors.get(j).advance();
            }
        }

        if (this._plugboard != null) {
            curr = this._plugboard.permute(curr);
        }

        for (int i = myrotors.size() - 1; i >= 0; i--) {
            curr = myrotors.get(i).convertForward(curr);
        }

        for (int i = 1; i < myrotors.size(); i++) {
            curr = myrotors.get(i).convertBackward(curr);
        }

        if (this._plugboard != null) {
            curr = this._plugboard.permute(curr);
        }

        return curr;



    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {

        msg = msg.replaceAll(" ", "");
        String [] msgarr = msg.split("");
        int [] convarr = new int[msgarr.length];
        String result = "";


        for (int i = 0; i < msgarr.length; i++) {
            char charac = msgarr[i].charAt(0);
            int readyconv = _alphabet.toInt(charac);
            int converted = this.convert(readyconv);
            char done = this._alphabet.toChar(converted);
            result += done;

        }
        return result;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

}
