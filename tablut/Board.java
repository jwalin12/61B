package tablut;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static tablut.Piece.*;
import static tablut.Square.*;
import static tablut.Move.mv;


/** The state of a Tablut Game.
 *  @author Jwalin Joshi
 */
class Board {




    /** The number of squares on a side of the board. */
    static final int SIZE = 9;

    /** The throne (or castle) square and its four surrounding squares.. */
    static final Square THRONE = sq(4, 4),
        NTHRONE = sq(4, 5),
        STHRONE = sq(4, 3),
        WTHRONE = sq(3, 4),
        ETHRONE = sq(5, 4);

    /** Initial positions of attackers. */
    static final Square[] INITIAL_ATTACKERS = {
        sq(0, 3), sq(0, 4), sq(0, 5), sq(1, 4),
        sq(8, 3), sq(8, 4), sq(8, 5), sq(7, 4),
        sq(3, 0), sq(4, 0), sq(5, 0), sq(4, 1),
        sq(3, 8), sq(4, 8), sq(5, 8), sq(4, 7)
    };

    /** Initial positions of defenders of the king. */
    static final Square[] INITIAL_DEFENDERS = {
        NTHRONE, ETHRONE, STHRONE, WTHRONE,
        sq(4, 6), sq(4, 2), sq(2, 4), sq(6, 4)
    };




    /** Initializes a game board with SIZE squares on a side in the
     *  initial position. */
    Board() {
        init();
    }

    /** Initializes a copy of MODEL. */
    Board(Board model) {
        copy(model);
    }

    /**
     *
     * @return
     */
    public Piece getturn() {
        return _turn;
    }
    /** TURN1. **/
    public void setturn(Piece turn1) {
        this._turn = turn1;
    }

    /**
     *
     * @return
     */
    public Piece getwinner() {
        return _winner;
    }

    /**
     *
     * WINNER1.
     */
    public void setwinner(Piece winner1) {
        this._winner = winner1;
    }

    /**
     *
     * @return
     */
    public int getmoveCount() {
        return _moveCount;
    }

    /**
     *
     * MOVECOUNT1.
     */
    public void setmoveCount(int moveCount1) {
        this._moveCount = moveCount1;
    }

    /**
     *
     * @return
     */

    public int getMovelim() {
        return movelim;
    }

    /**
     *
     * @return
     */
    public HashMap<Square, Piece> getPiecetosquare() {
        return piecetosquare;
    }


    /**
     *
     * TURN1, WINNER1, MOVECOUNT1, MOVELIM1, PIECETOSQUARE1.
     *
     *
     */
    Board(Piece turn1, Piece winner1,
           int moveCount1, int movelim1,
           HashMap<Square, Piece> piecetosquare1) {
        this._turn = turn1;
        this._winner = winner1;
        this._moveCount = moveCount1;
        this.piecetosquare = deepcopy(piecetosquare1);



    }
    /***
     * @return
     */
    Board deepcopy() {
        return new Board(this.turn(), this.winner(),
                this.getmoveCount(),
                this.getMovelim(),
                deepcopy(this.getPiecetosquare()));

    }




    /** Copies MODEL into me. */
    void copy(Board model) {
        if (model == this) {
            return;
        } else {
            this.piecetosquare = model.piecetosquare;

            if (model._turn != null) {
                this._turn = model._turn;
            }

            if (model._winner != null) {
                this._winner = model._winner;
            }
            this._moveCount = model.moveCount();
            this.movelim = model.movelim;

        }

    }

    /** Clears the board to the initial position. */
    void init() {
        _turn = BLACK;
        _winner = null;
        piecetosquare.clear();
        piecetosquare.put(sq(4, 4), KING);

        for (Square attacker: INITIAL_ATTACKERS) {
            piecetosquare.put(attacker, BLACK);
        }
        for (Square defender: INITIAL_DEFENDERS) {
            piecetosquare.put(defender, WHITE);
        }
        for (Square key: SQUARE_LIST) {
            if (!piecetosquare.containsKey(key)) {
                piecetosquare.put(key, EMPTY);
            }
        }

    }

    /** Set the move limit to LIM.  It is an error if 2*LIM <= moveCount().
     * N. */
    void setMoveLimit(int n) {


        if (2 * n <= moveCount()) {
            throw new Error();
        }
        movelim = n;
    }

    /** Return a Piece representing whose move it is (WHITE or BLACK). */
    Piece turn() {
        return _turn;
    }

    /** Return the winner in the current position, or null if there is no winner
     *  yet. */
    Piece winner() {
        return _winner;
    }

    /** Returns true iff this is a win due to a repeated position. */
    boolean repeatedPosition() {
        for (Board board: positionstack) {
            if (this.piecetosquare.equals(board.piecetosquare)) {
                if (this.turn() == board.turn()) {
                    return true;
                }
            }
        }
        return _repeated;
    }

    /** Record current position and set winner() next mover if the current
     *  position is a repeat. */
    private void checkRepeated() {
        if (repeatedPosition()) {
            _winner = _turn;
        }
    }

    /** Return the number of moves since the initial position that have not been
     *  undone. */
    int moveCount() {
        return _moveCount;
    }

    /** Return location of the king. */
    Square kingPosition() {

        Square result = null;
        for (Square sq: piecetosquare.keySet()) {
            if (piecetosquare.get(sq).equals(KING)) {
                result = sq;
            }
        }
        return result;
    }

    /** Return the contents the square at S. */
    final Piece get(Square s) {
        return get(s.col(), s.row());
    }

    /** Return the contents of the square at (COL, ROW), where
     *  0 <= COL, ROW <= 9. */
    final Piece get(int col, int row) {
        Piece result = null;

        for (Square sq: piecetosquare.keySet()) {
            if (sq.col() == col && sq.row() == row) {
                result = piecetosquare.get(sq);
            }
        }
        return result;
    }

    /** Return the contents of the square at COL ROW. */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /** Set square S to P. */
    final void put(Piece p, Square s) {
        p.setPosition(s);
    }







    /** Set square S to P and record for undoing. */
    final void revPut(Piece p, Square s) {
        piecetosquare.put(s, p);

    }

    /** Set square COL ROW to P. */
    final void put(Piece p, char col, char row) {
        put(p, sq(col - 'a', row - '1'));
    }

    /** Return true iff FROM - TO is an unblocked rook move on the current
     *  board.  For this to be true, FROM-TO must be a rook move and the
     *  squares along it, other than FROM, must be empty. */
    boolean isUnblockedMove(Square from, Square to) {

        if (!piecetosquare.get(to).equals(EMPTY)) {
            return false;
        }

        if (from == null || to == null) {
            return false;
        }


        boolean rookmove = from.isRookMove(to);
        boolean unblocked = true;
        if (rookmove) {
            if (piecetosquare.get(from) != KING) {
                unblocked = !(to.equals(sq("e5")));
            }
            if (from.row() == to.row()) {

                if (from.col() < to.col()) {
                    for (int col = from.col() + 1; col < to.col(); col++) {

                        if (piecetosquare.get(sq(col, from.row())) != EMPTY) {
                            unblocked = false;
                        }
                    }
                } else {
                    for (int col = to.col() + 1; col < from.col(); col++) {
                        if (piecetosquare.get(sq(col, from.row())) != EMPTY) {
                            unblocked = false;
                        }
                    }
                }
            } else {
                if (from.row() < to.row()) {
                    for (int row = from.row() + 1; row < to.row(); row++) {
                        if (piecetosquare.get(sq(from.col(), row)) != EMPTY) {
                            unblocked = false;
                        }
                    }

                } else {
                    for (int row = to.row() + 1; row < from.row(); row++) {
                        if (piecetosquare.get(sq(from.col(), row)) != EMPTY) {
                            unblocked = false;
                        }

                    }
                }
            }
        }
        return rookmove && unblocked;
    }





    /** Return true iff FROM is a valid starting square for a move. */
    boolean isLegal(Square from) {
        return piecetosquare.get(from) != EMPTY
                && !isHostile(piecetosquare.get(from), from);
    }

    /** Return true iff FROM-TO is a valid move. */
    boolean isLegal(Square from, Square to) {

        if (piecetosquare.get(from).equals(KING)) {
            if (!_turn.equals(WHITE)) {
                return false;
            }
            if (to.equals(sq("e5"))) {
                return true;
            } else {
                return isLegal(from)
                        && getPiecetosquare().get(to).equals(EMPTY);
            }
        } else {
            return  piecetosquare.get(from).equals(_turn)
                    && isLegal(from) && isUnblockedMove(from, to);
        }

    }


    /** Return true iff MOVE is a legal move in the current
     *  position. */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to());
    }

    /**
     *
     * P, SQ2.
     * @return
     */
    boolean isHostile(Piece p, Square sq2) {
        if (piecetosquare.get(sq2).equals(p.opponent())
                || p.equals(BLACK) && piecetosquare.get(sq2).equals(KING)
                || kingPosition() != sq("e5") && sq2.equals(sq("e5"))) {
            return true;
        }

        if (kingPartiallySurrounded() && p.equals(WHITE)
                && sq2.equals(sq("e5"))) {
            return true;
        }
        return false;

    }

    /**
     *
     * @return
     */

    public boolean kingPartiallySurrounded() {


        int surroundcount = 0;

        if (kingPosition() == null) {

            return false;
        }
        if (kingPosition().adjacent(sq("e5"))
                || kingPosition().equals(sq("e5"))) {
            if (piecetosquare.get(sq(kingPosition().col() + 1,
                    kingPosition().row())).equals(BLACK)) {
                surroundcount += 1;
            }
            if (piecetosquare.get(sq(kingPosition().col() - 1,
                    kingPosition().row())).equals(BLACK)) {
                surroundcount += 1;
            }
            if (piecetosquare.get(sq(kingPosition().col(),
                    kingPosition().row() + 1)).equals(BLACK)) {
                surroundcount += 1;
            }
            if (piecetosquare.get(sq(kingPosition().col(),
                    kingPosition().row() - 1)).equals(BLACK)) {
                surroundcount += 1;
            }


        }

        return surroundcount == 3;

    }

    /** Move FROM-TO, assuming this is a legal move. */
    void makeMove(Square from, Square to) {
        if (winner() != null) {
            return;
        }
        boolean islegal = isLegal(from, to);
        assert islegal;
        Board old = new Board(this._turn,
                this._winner, this._moveCount,
                this.movelim, this.piecetosquare);
        positionstack.add(old);
        Piece origpiece = piecetosquare.get(from);
        piecetosquare.put(from, EMPTY);
        piecetosquare.put(to, origpiece);
        for (Square sqr: piecetosquare.keySet()) {
            if (to.adjacent(sqr)
                    &&
                    piecetosquare.get(to).opponent()
                            .equals(piecetosquare.get(sqr))) {
                if (!sqr.isEdge()) {
                    if (piecetosquare.get(sqr)
                            .equals(piecetosquare.get(to).opponent())) {
                        int direc = to.direction(sqr);
                        Square oneover = null;
                        if (direc == 0) {
                            oneover = sq(sqr.col(), sqr.row() + 1);
                        }
                        if (direc == 1) {
                            oneover =  sq(sqr.col() + 1, sqr.row());
                        }
                        if (direc == 2) {
                            oneover = sq(sqr.col(), sqr.row() - 1);
                        }
                        if (direc == 3) {
                            oneover = sq(sqr.col() - 1, sqr.row());
                        }
                        if (oneover != null) {
                            if (isHostile(piecetosquare.get(sqr), oneover)
                                    && !piecetosquare.get(sqr).equals(KING)) {
                                capture(to, oneover);
                            }

                        }

                    }

                }


            }


        }
        _turn = _turn.opponent();
        checkKing();
        checkRepeated();
        _moveCount++;
    }

    /**
     * checks the king.
     */

    private void checkKing() {


        if (kingPosition() == null) {
            _winner = BLACK;
            return;
        }


        if (kingPosition().isEdge()) {
            _winner = WHITE;
            return;
        }
        if (!kingPosition().equals(sq("e5"))
                && !kingPosition().adjacent(sq("e5"))) {
            if (isHostile(KING, sq(kingPosition().col() + 1,
                    kingPosition().row()))
                    && isHostile(KING, sq(kingPosition().col() - 1,
                    kingPosition().row()))) {
                piecetosquare.put(kingPosition(), EMPTY);
                _winner = BLACK;
                return;
            }
            if (isHostile(KING, sq(kingPosition().col(),
                    kingPosition().row() + 1))
                    && isHostile(KING,
                    sq(kingPosition().col(),
                            kingPosition().row() - 1))) {
                piecetosquare.put(kingPosition(), EMPTY);
                _winner = BLACK;
                return;
            }
        }



        if (kingSurrounded()) {
            piecetosquare.put(kingPosition(), EMPTY);
            _winner = BLACK;
            return;
        }
        if (kingPartiallySurrounded() && !kingPosition().equals(sq("e5"))) {
            piecetosquare.put(kingPosition(), EMPTY);
            _winner = BLACK;
            return;

        }

    }
    /***
     * @return
     */

    public boolean kingSurrounded() {
        if (kingPosition().equals(sq("e5"))
                || kingPosition().adjacent(sq("e5"))) {
            if (piecetosquare.get(sq(kingPosition().col() + 1,
                    kingPosition().row())).equals(BLACK)) {
                if (piecetosquare.get(sq(kingPosition().col() - 1,
                        kingPosition().row())).equals(BLACK)) {
                    if (piecetosquare.get(sq(kingPosition().col(),
                            kingPosition().row() + 1)).equals(BLACK)) {
                        if (piecetosquare.get(sq(kingPosition().col(),
                                kingPosition().row() - 1)).equals(BLACK)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;

    }

    /** Move according to MOVE, assuming it is a legal move. */
    void makeMove(Move move) {
        makeMove(move.from(), move.to());
    }

    /** Capture the piece between SQ0 and SQ2, assuming a piece just moved to
     *  SQ0 and the necessary conditions are satisfied. */
    private void capture(Square sq0, Square sq2) {
        piecetosquare.put(sq0.between(sq2),
                EMPTY);
    }

    /** Undo one move.  Has no effect on the initial board. */
    void undo() {
        if (_moveCount > 0) {
            _moveCount--;
            this.copy(positionstack.pop());
        }
        _turn = _turn.opponent();
    }



    /** Remove record of current position in the set of positions encountered,
     *  unless it is a repeated position or we are at the first move. */
    private void undoPosition() {

        if (_repeated || (positionstack.contains(this) && moveCount() != 0)) {
            _winner = _turn.opponent();
        } else {
            if (moveCount() != 0) {
                this.copy(positionstack.peek());
                positionstack.pop();
            }
        }
        _repeated = false;
    }

    /** Clear the undo stack and board-position counts. Does not modify the
     *  current position or win status. */
    void clearUndo() {
        positionstack.clear();
    }

    /** Return a new mutable list of all legal moves on the current board for
     *  SIDE (ignoring whose turn it is at the moment). */
    List<Move> legalMoves(Piece side) {

        ArrayList<Move> allmoves = new ArrayList<Move>();

        for (Square piece: pieceLocations(side)) {
            for (Square sq : SQUARE_LIST) {

                if (isUnblockedMove(piece, sq)) {
                    if (!allmoves.contains(mv(piece, sq))) {
                        allmoves.add(mv(piece, sq));
                    }
                }
            }

        }
        return allmoves;

    }

    /** Return true iff SIDE has a legal move. */
    boolean hasMove(Piece side) {
        return _turn == side;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    /** Return a text representation of this Board.  If COORDINATES, then row
     *  and column designations are included along the left and bottom sides.
     */
    String toString(boolean coordinates) {
        Formatter out = new Formatter();
        for (int r = SIZE - 1; r >= 0; r -= 1) {
            if (coordinates) {
                out.format("%2d", r + 1);
            } else {
                out.format("  ");
            }
            for (int c = 0; c < SIZE; c += 1) {
                out.format(" %s", get(c, r));
            }
            out.format("%n");
        }
        if (coordinates) {
            out.format("  ");
            for (char c = 'a'; c <= 'i'; c += 1) {
                out.format(" %c", c);
            }
            out.format("%n");
        }
        return out.toString();
    }

    /** Return the locations of all pieces on SIDE. */
    private HashSet<Square> pieceLocations(Piece side) {

        HashSet<Square> result = new HashSet<Square>();
        assert side != EMPTY;
        for (Square sq: piecetosquare.keySet()) {
            if (piecetosquare.get(sq).equals(side)) {
                result.add(sq);
            }
        }
        if (side.equals(WHITE)) {
            result.add(kingPosition());
        }

        return result;
    }

    /** Return the contents of _board in the order of SQUARE_LIST as a sequence
     *  of characters: the toString values of the current turn and Pieces. */
    String encodedBoard() {
        char[] result = new char[Square.SQUARE_LIST.size() + 1];
        result[0] = turn().toString().charAt(0);
        for (Square sq : SQUARE_LIST) {
            result[sq.index() + 1] = get(sq).toString().charAt(0);
        }
        return new String(result);
    }

    /** Piece whose turn it is (WHITE or BLACK). */
    private Piece _turn;
    /** Cached value of winner on this board, or EMPTY if it has not been
     *  computed. */
    private Piece _winner;
    /** Number of (still undone) moves since initial position. */
    private int _moveCount;
    /** True when current board is a repeated position (ending the game). */
    private boolean _repeated;
    /**
     * move limit.
     */
    private int movelim;
    /**
     * piecetosquare.
     */
    private HashMap<Square, Piece> piecetosquare = new HashMap<Square, Piece>();
    /**
     * positionstack.
     */
    private Stack<Board> positionstack = new Stack<>();


    /**
     * ORIGINAL.
     * @return
     */
    public static HashMap<Square, Piece> deepcopy(
            HashMap<Square, Piece> original) {
        HashMap<Square, Piece> copy = new HashMap<Square, Piece>();
        for (Map.Entry<Square, Piece> entry : original.entrySet()) {
            copy.put(entry.getKey(),
                    entry.getValue());
        }
        return copy;
    }

}
