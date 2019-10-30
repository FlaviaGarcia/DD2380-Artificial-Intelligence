import java.util.*;

public class Player {
    /**
     * Performs a move
     *
     * @param gameState
     *            the current state of the board
     * @param deadline
     *            time before which we must have returned
     * @return the next state the board is in after our move
     */
    public GameState play(final GameState gameState, final Deadline deadline) {
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);

        if (nextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(gameState, new Move());
        }

        int depth = 5;  // modify this value to see the impact in the output
        
        int idxNextBestMove = getIdxNextBestMoveX(nextStates, depth);
       
        return nextStates.elementAt(idxNextBestMove);
    }


    public int getIdxNextBestMoveX(Vector<GameState> nextStates, int depth){
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;
        double bestValue_us = Double.NEGATIVE_INFINITY;
        int idxNextBestMove = -1;

        for (int idxMove = 0; idxMove < nextStates.size(); idxMove++){
            double bestValue_opponent = miniMax.alphaBeta(nextStates.get(idxMove), depth, alpha, beta, Constants.CELL_O);
            System.err.println(beta);
            System.err.println(alpha);
            if (bestValue_opponent > bestValue_us) {
                bestValue_us = bestValue_opponent;
                idxNextBestMove = idxMove;
            }
            // update alpha in case we can prune
            alpha = Math.max(alpha, bestValue_opponent);
            
        }
        return idxNextBestMove;
    }

}
