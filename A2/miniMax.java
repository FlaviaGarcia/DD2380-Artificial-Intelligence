import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;
import java.lang.Math;


public class miniMax {
    static final int BOARD_SIZE = 8;
    static final boolean STANDARD_ORDER = false;
    static final boolean REVERSE_ORDER = true;

    static final Hashtable<String, Double> sortingCacheMax = new Hashtable<>();
    static final Hashtable<String, Double> sortingCacheMin = new Hashtable<>();


    static MoveVal alphabeta(State parent, int depth, double alpha, double beta, int player) {
        Vector<GameState> nextStates = new Vector<>();
        parent.state.findPossibleMoves(nextStates);
        MoveVal bestMoveVal;

        if (depth == 0 || nextStates.isEmpty())
            return new MoveVal(-1, evaluation_function(parent.state));

        if (player == Player.MY_PLAYER) { // Here we maximize for us
            Vector<State> children = sortMoves(nextStates, STANDARD_ORDER); // Sorting children by values in the cache
            bestMoveVal = new MoveVal(-1, Double.NEGATIVE_INFINITY);
            State bestMove = null; // State correspondent to the best move
            for (State child : children) {
                MoveVal temp = alphabeta(child, depth - 1, alpha, beta, Player.OPPONENT);
                if (temp.val > bestMoveVal.val) {
                    bestMoveVal = new MoveVal(child.index, temp.val);
                    bestMove = child;
                }
                alpha = Math.max(alpha, bestMoveVal.val);
                if (alpha >= beta)
                    break;
            }
            sortingCacheMax.put(bestMove.state.toMessage(), bestMoveVal.val); // Saving the value of best move in the cache
        } else { // Here we minimize for opponent
            Vector<State> children = sortMoves(nextStates, REVERSE_ORDER); // Sort in reverse order since we want to minimize
            bestMoveVal = new MoveVal(-1, Double.POSITIVE_INFINITY);
            State resState = null;
            for (State child : children) {
                MoveVal temp = alphabeta(child, depth - 1, alpha, beta, Player.MY_PLAYER);
                if (temp.val < bestMoveVal.val) {
                    bestMoveVal = new MoveVal(child.index, temp.val);
                    resState = child;
                }
                beta = Math.min(beta, bestMoveVal.val);
                if (alpha >= beta)
                    break;
            }
            sortingCacheMin.put(resState.state.toMessage(), bestMoveVal.val);
        }
        return bestMoveVal;
    }

    /**
     * Take the vector of children GameStates, returns a vector of State objects
     * @param nextStates
     * @param reverse
     * @return
     */
    static Vector<State> sortMoves(Vector<GameState> nextStates, boolean reverse) {
        Vector<State> toSort = toStateVector(nextStates); // GameState vector to State vector
        for (State state : toSort) {
            Double eval;
            if (reverse) {
                eval = sortingCacheMin.get(state.state.toMessage());
                if (eval == null)
                    eval = Double.POSITIVE_INFINITY;
            }else {
                eval = sortingCacheMax.get(state.state.toMessage());
                if (eval == null)
                    eval = Double.NEGATIVE_INFINITY;
            }
            state.evaluationValue = eval;
        }
        if (reverse)
            Collections.sort(toSort, Collections.reverseOrder()); // Sorting by State.evaluationValue
        else Collections.sort(toSort);
        return toSort;
    }

    /**
     * Transform the given GameState vector in a vector of State objects, that keep information about
     * the indexes and evaluation values
     * @param gameStates
     * @return
     */
    static Vector<State> toStateVector(Vector<GameState> gameStates) {
        Vector<State> states = new Vector<>();
        for (int index = 0; index < gameStates.size(); index++) {
            State state = new State(gameStates.get(index), index);
            states.add(state);
        }
        return states;
    }


    public static double evaluation_function(GameState state) {
        if (state.isRedWin() && Player.MY_PLAYER == Constants.CELL_RED)
            return 1;
        if (state.isRedWin() && Player.OPPONENT == Constants.CELL_RED)
            return -1;
        if (state.isWhiteWin() && Player.MY_PLAYER == Constants.CELL_WHITE)
            return 1;
        if (state.isWhiteWin() && Player.OPPONENT == Constants.CELL_WHITE)
            return -1;
        int myMen = countMenFor(state, Player.MY_PLAYER);
        int myKings = countKingFor(state, Player.MY_PLAYER);

        int opponentMen = countMenFor(state, Player.OPPONENT);
        int opponentKings = countKingFor(state, Player.OPPONENT);

        double total = (myMen - opponentMen)+ (myKings - opponentKings);
        total = total/(myMen+myKings+opponentMen+opponentKings);

        return total;
    }

    static int countMenFor(GameState state, int player) {
        int sum = 0;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                int cellValue = state.get(row, col);
                if (equal(cellValue, player) && !isKing(cellValue)) {
                    sum++;
                }
            }
        }
        return sum;
    }

    static int countKingFor(GameState state, int player) {
        int sum = 0;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                int cellValue = state.get(row, col);
                if (equal(cellValue, player) && isKing(cellValue)) {
                    sum++;
                }
            }
        }
        return sum;
    }

    /**
     * Returns the number of safe men for the player.
     * A men is safe if it is close to a lateral border.
     *
     * @param state
     * @param player
     * @return
     */
    static int countSafeFor(GameState state, int player) {
        int sum = 0;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                int cellValue = state.get(row, col);
                if (equal(cellValue, player) && onSide(row, col)) {
                    sum++;
                }
            }
        }
        return sum;
    }

    static boolean onSide(int row, int col) {
        return (col == 0 || col == 7 || row == 0 || row == 7);
    }

    static int countAdvancementFor(GameState state, int player) {
        boolean reverse = player == Constants.CELL_WHITE;
        int sum = 0;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                int cellValue = state.get(row, col);
                if (equal(cellValue, player)) {
                    if (reverse)
                        sum += BOARD_SIZE - row - 1;
                    else
                        sum += row;
                }
            }
        }
        return sum;
    }

    static int countEndangeredFor(GameState state, int player, int movingPlayer) {
        int sum = 0;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                int cellValue = state.get(row, col);
                if (equal(cellValue, player) && isEndangered(state, row, col, movingPlayer)) {
                    sum++;
                }
            }
        }
        return sum;
    }

    /**
     * @param state
     * @param row
     * @param col
     * @param movingPlayer the player that made the move that resulted in this state
     * @return
     */
    static boolean isEndangered(GameState state, int row, int col, int movingPlayer) {
        if (onSide(row, col))
            return false;
        int upLeftVal = state.get(row - 1, col - 1);
        int upRightVal = state.get(row - 1, col + 1);
        int downleftVal = state.get(row + 1, col - 1);
        int downRightVal = state.get(row + 1, col + 1);
        // A white can be eaten by a non-king red in pos (row-1, col-1) or (row-1, col+1)
        if (equal(movingPlayer, Constants.CELL_WHITE)) {
            if (equal(upLeftVal, Constants.CELL_RED) && equal(downRightVal, Constants.CELL_EMPTY))
                return true;
            if (equal(upRightVal, Constants.CELL_RED) && equal(downleftVal, Constants.CELL_EMPTY))
                return true;
            if (equal(downleftVal, Constants.CELL_RED) && equal(downleftVal, Constants.CELL_KING) && equal(upRightVal, Constants.CELL_EMPTY))
                return true;
            if (equal(downRightVal, Constants.CELL_RED) && equal(downRightVal, Constants.CELL_KING) && equal(upLeftVal, Constants.CELL_EMPTY))
                return true;
        } else if (equal(movingPlayer, Constants.CELL_RED)) {
            if (equal(downleftVal, Constants.CELL_WHITE) && equal(upRightVal, Constants.CELL_EMPTY))
                return true;
            if (equal(downRightVal, Constants.CELL_WHITE) && equal(upLeftVal, Constants.CELL_EMPTY))
                return true;
            if (equal(upLeftVal, Constants.CELL_WHITE) && equal(upLeftVal, Constants.CELL_KING) && equal(downRightVal, Constants.CELL_EMPTY))
                return true;
            if (equal(upRightVal, Constants.CELL_WHITE) && equal(upRightVal, Constants.CELL_KING) && equal(downleftVal, Constants.CELL_EMPTY))
                return true;
        }
        return false;
    }

    /**
     * Returns true if given value contains the given color.
     * Example use:
     * equal(value, Constants.CELL_WHITE)
     * equal(value, Constants.CELL_RED)
     * equal(value, Constants.CELL_KING)
     * equal(value, Constants.CELL_INVALID)
     * equal(value, Constants.CELL_EMPTY)
     *
     * @param value
     * @param color
     * @return
     */
    static boolean equal(int value, int color) {
        return (value & color) > 0;
    }

    static boolean isKing(int value) {
        return equal(value, Constants.CELL_KING);
    }

    static class MoveVal {
        int index;
        double val;

        public MoveVal(int index, double val) {
            this.index = index;
            this.val = val;
        }

    }

}
