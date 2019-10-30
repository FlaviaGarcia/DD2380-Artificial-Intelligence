import java.util.Vector;
import java.lang.Math;


public class miniMax {

    final static int INVALID_LINE = -1;

    public static double alphaBeta(GameState state, int depth, double alpha, double beta, int player){
        double node_value;
        Vector<GameState> nextStates = new Vector<GameState>();
        state.findPossibleMoves(nextStates);

        if (depth == 0 || nextStates.size() == 0){ // terminal state
            return evaluation_function(state);
        }else{
            if (player == Constants.CELL_X){ //us
                node_value = Double.NEGATIVE_INFINITY;
                for (int child_idx = 0; child_idx < nextStates.size(); child_idx++){
                    GameState child = nextStates.get(child_idx);
                    node_value = Math.max(node_value, alphaBeta(child, depth-1, alpha, beta, Constants.CELL_O));
                    alpha = Math.max(alpha, node_value);
                    if(beta<=alpha){
                        break; // prune
                    }
                }
            }else{ //the other player
                node_value = Double.POSITIVE_INFINITY;
                for (int child_idx = 0; child_idx < nextStates.size(); child_idx++){
                    GameState child = nextStates.get(child_idx);
                    node_value = Math.min(node_value, alphaBeta(child, depth -1, alpha, beta, Constants.CELL_X));
                    beta = Math.min(beta, node_value);
                    if(beta<=alpha){
                        break; // prune
                    }
                }
            }
            return node_value;
        }
    }


    public static int evaluation_function(GameState state){
        
        int[] rows = new int[]{0, 0, 0, 0};
        int[] columns = new int[]{0, 0, 0, 0};
        int[] diagonals = new int[]{0, 0};
        int total = 0;

        for (int row = 0; row < GameState.BOARD_SIZE; row++) {
            for (int col = 0; col < GameState.BOARD_SIZE; col++) {
                if (state.at(row, col) == Constants.CELL_X) { 
                    if (rows[row] != INVALID_LINE)
                        rows[row]++;
                    if (columns[col] != INVALID_LINE)
                        columns[col]++;
                    if (row == col && diagonals[0] != INVALID_LINE)
                        diagonals[0]++;
                    if (row + col == GameState.BOARD_SIZE -1 && diagonals[1] != INVALID_LINE)
                        diagonals[1]++;
                } else if (state.at(row, col) == Constants.CELL_O) {
                    rows[row] = INVALID_LINE;
                    columns[col] = INVALID_LINE;
                    if (row == col)
                        diagonals[0] = INVALID_LINE;
                    if (row + col == GameState.BOARD_SIZE - 1)
                        diagonals[1] = INVALID_LINE;
                }
            }
        }

        for (int i=0; i < rows.length; i++) {
            total += points(rows[i]) + points(columns[i]);
            if (i==0 || i==1){
                total += points(diagonals[i]);
            }
        }
        return total;
    }

    static int points(int lineLength) {
        int[] points = new int[] {0, 1, 2, 10, 99999};
        if (lineLength == INVALID_LINE)
            return INVALID_LINE;
        return points[lineLength];
    }

}
