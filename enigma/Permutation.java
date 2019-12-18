package enigma;



import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Jwalin Joshi
 *
 */
class Permutation {

    /** array of cycles. **/
    private String[] _cycles;


    /** returns array of cycles. **/
    public String[] getcyc() {
        return _cycles;
    }


    /**
     * Set this Permutation to that specified by CYCLES, a string in the
     * form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     * is interpreted as a permutation in cycle notation.  Characters in the
     * alphabet that are not included in any cycle map to themselves.
     * Whitespace is ignored.
     */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;

        if (cycles.equals("")) {
            _cycles = new String[0];
        } else {

            cycles = cycles.trim();
            cycles = cycles.replace(")", " ");
            cycles = cycles.replace("(", " ");
            cycles = cycles.trim();
            _cycles = cycles.split(" ");
        }

    }

    /**
     * Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     * c0c1...cm.
     */
    void addCycle(String cycle) {

        cycle = cycle.replace("(", "");
        cycle = cycle.replace(")", "");

        if (cycle.trim().equals("")) {
            return;
        } else if (_cycles.length == 0 || _cycles[0].equals("")) {
            _cycles = new String[] {cycle};
        } else {
            String[] newcyc = new String[_cycles.length + 1];
            for (int i = 0; i < _cycles.length; i++) {
                newcyc[i] = _cycles[i];

            }
            newcyc[_cycles.length] = cycle;
            _cycles = newcyc;

        }

    }

    /**
     * Return the value of P modulo the size of this permutation.
     */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /**
     * Returns the size of the alphabet I permute.
     */
    int size() {
        return _alphabet.getalph().size();
    }

    /**
     * Return the result of applying this permutation to P modulo the
     * alphabet size.
     */
    int permute(int p) {
        int modded = wrap(p);
        return _alphabet.toInt(permute(_alphabet.toChar(modded)));
    }

    /**
     * Return the result of applying the inverse of this permutation
     * to  C modulo the alphabet size.
     */
    int invert(int c) {
        int modded = wrap(c);
        char charac = _alphabet.toChar(modded);
        return _alphabet.toInt((char) invert(charac));
    }

    /**
     * Return the result of applying this permutation to the index of P
     * in ALPHABET, and converting the result to a character of ALPHABET.
     * jwalins note: why does it say alphabet so much?
     */
    char permute(char p) {

        if (_cycles == null) {
            return p;
        }


        for (String cycle : _cycles) {
            if (cycle.indexOf(p) != -1) {
                if (cycle.indexOf(p) == cycle.length() - 1) {

                    return cycle.charAt(0);

                } else {
                    int nextind = cycle.indexOf(p) + 1;
                    char nex = cycle.charAt(nextind);
                    return nex;
                }


            }


        }
        return p;

    }

    /** Return the result of applying the inverse of this permutation to C. */


    char invert(char c) {

        if (_cycles == null) {
            return c;
        }

        for (String cycle : _cycles) {
            if (cycle.indexOf(c) != -1) {
                if (cycle.indexOf(c) == 0) {

                    return cycle.charAt(cycle.length() - 1);

                } else {
                    return cycle.charAt(cycle.indexOf(c) - 1);
                }


            }


        }
        return c;

    }


    /** Return the alphabet used to initialize this Permutation. */

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (Object x : _alphabet.getalph()) {
            if (permute((char) x) == ((char) x)) {
                return false;
            }

        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;


}
