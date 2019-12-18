package enigma;

import java.util.ArrayList;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Jwalin
 */
class Alphabet {

    /** arraylist that contains alphabet.**/
    private ArrayList<Character> _alph  = new ArrayList<>();

    /** returns arraylist alph. **/
    public ArrayList getalph() {
        return _alph;
    }


    /** A new alphabet containing CHARS.  Character number #k has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {

        for (int i = 0; i < chars.length(); i++) {
            if (!_alph.contains((chars.charAt(i)))) {
                this._alph.add(chars.charAt(i));

            }


        }


    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return  _alph.size();
    }

    /** Returns true if preprocess(CH) is in this alphabet. */
    boolean contains(char ch) {
        return _alph.contains(ch);
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        return (char) _alph.get(index);
    }

    /** Returns the index of character preprocess(CH), which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        return _alph.indexOf(ch);
    }

}
