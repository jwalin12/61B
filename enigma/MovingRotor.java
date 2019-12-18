package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Jwalin Joshi
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        setmovable(true);
        char[] arr = notches.toCharArray();
        for (int i = 1; i < arr.length; i++) {
            addnotch((perm.alphabet().toInt(arr[i])));
        }


    }

    @Override
    void advance() {
        this.setposn(this.permutation().wrap(this.getposn() + 1));
    }



}
