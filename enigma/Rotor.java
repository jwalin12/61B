package enigma;


import java.util.ArrayList;


/** Superclass that represents a rotor in the enigma machine.
 *  @author Jwalin Joshi
 */
class Rotor {




    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        _posn = 0;
        setRingsetting((char) alphabet().getalph().get(0));
        _relf = false;


    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {

        return _movable;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {

        return _relf;
    }

    /** Return my current setting. */
    int setting() {

        return _posn;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        _posn = permutation().wrap(posn + getRingsetting());
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        _posn = permutation().wrap(alphabet().toInt(cposn) + getRingsetting());
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        return permutation().wrap(permutation().permute(p + _posn) - _posn);
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        return  permutation().wrap(permutation().invert(e + _posn) - _posn);
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        if (_toleft == null || !_toleft._movable) {
            return false;
        }
        return (this._notches != null && this._notches.contains(this._posn));

    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {

    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;

    /** can it move. **/
    private boolean _movable = false;

    /** returns movable. **/

    public boolean getmoveable() {
        return _movable;
    }


    /** changes movable. the PARAMETER is the NEXT moveable. **/
    public void setmovable(boolean next) {
        _movable = next;
    }
    /** what its setting/position is. **/

    private int _posn = 0;

    /** returns setting. **/
    public int getposn() {
        return _posn;
    }

    /** changes position. the NEXT position is the PARAMETER. **/

    public void setposn(int next) {
        _posn = next;
    }

    /** rotor to the left. **/
    private Rotor _toleft = null;


    /** changes reflection. the NEXT boolean is the PARAMETER. **/
    public void setrelf(boolean next) {
        _relf = next;
    }


    /** returns rotor to the left. **/
    public Rotor getleft() {
        return _toleft;
    }

    /** changes LEFT. the Left Rotor is the PARAMTER. **/
    public void setLeft(Rotor left) {
        this._toleft = left;
    }

    /** does it reflect.**/
    private boolean _relf;


    /** the notches it has. **/
    private ArrayList<Integer> _notches = new ArrayList<>();

    /** adds to notches. NEXT is the PARAMETER.**/
    public void addnotch(int next) {
        _notches.add(next);
    }

    /** returns notches. **/
    public ArrayList getnotches() {
        return _notches;
    }

    /**returns ringsetting.**/
    public int getRingsetting() {
        return ringsetting;
    }

    /**sets ringsetting to RINGSETTING1.**/
    public void setRingsetting(char ringsetting1) {
        this.ringsetting = _permutation.wrap(alphabet().toInt(ringsetting1));
    }

    /** ring setting. **/
    private int ringsetting;




}
