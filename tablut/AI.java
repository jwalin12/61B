package tablut;

import java.util.HashMap;
import java.util.List;

import static java.lang.Math.*;

import static tablut.Square.sq;
import static tablut.Piece.*;

/** A Player that automatically generates moves.
 *  @author Jwalin Joshi
 */
class AI extends Player {

    /** A position-score magnitude indicating a win (for white if positive,
     *  black if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /** A position-score magnitude indicating a forced win in a subsequent
     *  move.  This differs from WINNING_VALUE to avoid putting off wins. */
    private static final int WILL_WIN_VALUE = Integer.MAX_VALUE - 40;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI with no piece or controller (intended to produce
     *  a template). */
    AI() {
        this(null, null);
    }

    /** A new AI playing PIECE under control of CONTROLLER. */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        Move move = findMove();
        _controller.reportMove(move);
        return move.toString();
    }

    @Override
    boolean isManual() {
        return false;
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {

        Board b = new Board(board());
        _lastFoundMove = null;
        findMove(b, 2, true,  0, -INFTY, INFTY);
        return _lastFoundMove;
    }

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;

    /** checks the move length. MOVES is the moves BOARD is the board.
     *
     */
    private void checkmoves(List<Move> moves, Board board) {
        if (moves.size() == 0) {
            board.setwinner(myPiece().opponent());
        }
    }


    /** records move. BOARD is board, DEPTH is depth, A is a, B is b*
     * @param board
     * @param depth
     * @param a
     * @param b
     */
    private void recordmove(Board board, int depth, int a, int b) {
        HashMap<Move, Integer> moveval =  new HashMap<Move, Integer>();
        List<Move> moves = board.legalMoves(board.turn());
        for (Move move:moves) {
            Board next = board.deepcopy();
            next.makeMove(move);
            int nextval = findMove(next, depth - 1, false, 0, a, b);
            moveval.put(move, nextval);
        }
        if (this.myPiece().equals(BLACK)) {
            int max = -INFTY;
            Move maxmove = null;
            for (Move movemade: moveval.keySet()) {
                if (moveval.get(movemade) > max) {
                    maxmove = movemade;
                    max = moveval.get(movemade);
                }
            }
            _lastFoundMove = maxmove;
        } else {
            int min = INFTY;
            Move minmove = null;
            for (Move movemade: moveval.keySet()) {
                if (moveval.get(movemade) < min) {
                    minmove = movemade;
                    min = moveval.get(movemade);
                }
            }
            _lastFoundMove = minmove;

        }

    }

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > B if SENSE==1,
     *  and minimal value or value < A if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. */
    private int findMove(Board board, int depth, boolean saveMove,
                         int sense, int a, int b) {
        if (depth == 0) {
            return staticScore(board);
        }
        List<Move> moves = board.legalMoves(board.turn());
        checkmoves(moves, board);

        if (saveMove) {
            recordmove(board, depth, a, b);
        } else if (board.turn().equals(WHITE)) {
            int min = INFTY;
            for (Move move: moves) {
                Board next = board.deepcopy();
                next.makeMove(move);
                int nextval = findMove(next, depth - 1, false, sense, a, b);
                if (nextval < min) {
                    min = nextval;
                    b = Math.min(b, nextval);
                    if (b <= a) {
                        break;
                    }
                }
                min = min(min, findMove(board, depth - 1, false, sense, a, b));
                return min;
            }
        } else {
            int max = -INFTY;
            for (Move move: moves) {
                Board next = board.deepcopy();
                next.makeMove(move);
                int nextval = findMove(next, depth - 1, false, sense, a, b);
                if (nextval > max) {
                    max = nextval;
                    a = Math.max(a, nextval);
                    if (a >= b) {
                        break;
                    }
                }
                return max;
            }
        }
        return 0;
    }
    /** Return a heuristically determined maximum search depth
     *  based on characteristics of BOARD. */
    private static int maxDepth(Board board) {
        return 4;
    }

    /** Return a heuristic value for BOARD. */
    private int staticScore(Board board) {
        int score = 0;
        if (board.winner() != null && board.winner().equals(BLACK)) {
            return INFTY;
        }
        if (board.winner() != null && board.winner().equals(WHITE)) {
            return -INFTY;
        }
        for (Square sq: board.getPiecetosquare().keySet()) {
            if (board.getPiecetosquare().get(sq).equals(WHITE)) {
                score -= 1;
                if (!sq.adjacent(board.kingPosition())) {
                    score -= 2;
                }
            }
            if (board.getPiecetosquare().get(sq).equals(BLACK)) {
                score += 3;
                if (sq.adjacent(board.kingPosition())) {
                    score += 8;
                }
            }
            if (board.kingPosition().equals(sq("e5"))) {
                if (board.getPiecetosquare().get(sq("f6")).equals(BLACK)) {
                    score += 5;
                }
                if (board.getPiecetosquare().get(sq("f4")).equals(BLACK)) {
                    score += 5;
                }
                if (board.getPiecetosquare().get(sq("d4")).equals(BLACK)) {
                    score += 5;
                }
                if (board.getPiecetosquare().get(sq("d6")).equals(BLACK)) {
                    score += 5;
                }
            }
        }
        return score;
    }



}
