public class State implements Comparable{
    GameState state;
    int index;
    double evaluationValue;

    public State(GameState state, int index) {
        this.state = state;
        this.index = index;
    }

    @Override
    public int compareTo(Object that) {
        double diff = this.evaluationValue - ((State) that).evaluationValue;
        if (diff > 0)
            return -1;
        if (diff < 0)
            return 1;
        return 0;
    }
}
