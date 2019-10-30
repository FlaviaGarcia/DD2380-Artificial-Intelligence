import java.util.*;

public class Player {
    static int MY_PLAYER;
    static int OPPONENT;

    /**
     * Performs a move
     *
     * @param pState the current state of the board
     * @param pDue   time before which we must have returned
     * @return the next state the board is in after our move
     */
    public GameState play(final GameState pState, final Deadline pDue) {
        MY_PLAYER = pState.getNextPlayer();
        if (MY_PLAYER == Constants.CELL_WHITE)
            OPPONENT = Constants.CELL_RED;
        else
            OPPONENT = Constants.CELL_WHITE;

        Vector<GameState> lNextStates = new Vector<GameState>();
        pState.findPossibleMoves(lNextStates);


        if (lNextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(pState, new Move());
        }

        /**
         * Here you should write your algorithms to get the best next move, i.e.
         * the best next state. This skeleton returns a random move instead.
         */
        miniMax.MoveVal bestMove = null;
        State parent = new State(pState, 0);
        int maxDepth = 100;  // modify this value to see the impact in the output
        int reachedDepth = 0;
        for (int depth = 1; depth < maxDepth && pDue.timeUntil() > 750e6; depth++) {
                bestMove = miniMax.alphabeta(parent, depth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, MY_PLAYER);
                reachedDepth++;
        }
        System.err.println("CACHE SIZE: "+miniMax.sortingCacheMax.size());
        System.err.println("REACHED DEPTH: "+reachedDepth);
        return lNextStates.elementAt(bestMove.index);
    }

    /**
     * Returns the color of the given cell.
     * The result can be compared with Constants.CELL_RED or Constants.CELL_WHITE
     * Example: getColor(cell) == Constants.CELL_RED
     *
     * @param cell value of a cell in the board
     * @return the value of the correspondent color, invalid, or empty
     */
    public static int getColor(int cell) {
        int mask = 0xFFFFFFFB;
        return cell & mask;
    }

}
