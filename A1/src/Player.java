import java.util.ArrayList;

class Player {
    private final int TIME_FOR_SHOOT = 50;
    private final int ROUND_FOR_SHOOT = 3;
    private final double HIT_PROBABILITY_THRESHOLD = 0.6;
    private final double NOT_BS_THRESHOLD = 0.99999;

    private int time = 0;
    private SpecieModel[] specieModels = new SpecieModel[Constants.COUNT_SPECIES];
    private ArrayList<BirdModel> unknownBirds = new ArrayList<>();
    private double[] priorProbabilities = new double[Constants.COUNT_SPECIES];
    private int totalBirds = 0;
    private int totalShots = 0;
    private int totalHits = 0;
    boolean seenBlackStork = false;
    private boolean shoot = true;

    public Player() {
        for (int specie = 0; specie < Constants.COUNT_SPECIES; specie++)
            specieModels[specie] = new SpecieModel();
    }

    /**
     * Shoot!
     * <p>
     * This is the function where you start your work.
     * <p>
     * You will receive a variable pState, which contains information about all
     * birds, both dead and alive. Each bird contains all past moves.
     * <p>
     * The state also contains the scores for all players and the number of
     * time steps elapsed since the last time this function was called.
     *
     * @param pState the GameState object with observations etc
     * @param pDue   time before which we must have returned
     * @return the prediction of a bird we want to shoot at, or cDontShoot to pass
     */
    public Action shoot(GameState pState, Deadline pDue) {
        Action actionToTake = cDontShoot;

        if (time == 0)
            beginRound(pState);

        // Add observations for each bird
        for (int birdIndex = 0; birdIndex < pState.getNumBirds(); birdIndex++) {
            Bird bird = pState.getBird(birdIndex);
            if (bird.isAlive())
                unknownBirds.get(birdIndex).addObservation(bird.getLastObservation());
            if (time > 2) {
                BirdModel birdModel = unknownBirds.get(birdIndex);
                birdModel.learn();
                birdModel.probabilityOfSpecie = pSpecies(birdModel);
            }
        }

        BirdModel.Move bestMove = new BirdModel.Move(-1, Double.NEGATIVE_INFINITY);
        int birdToShoot = -1;
        for (int birdIndex = 0; birdIndex < pState.getNumBirds() && shoot && time > 2; birdIndex++) {
            Bird bird = pState.getBird(birdIndex);
            BirdModel birdModel = unknownBirds.get(birdIndex);
            BirdModel.Move nextMove = birdModel.mostLikelyNextMove();
            if (bird.isAlive()
                    && nextMove.probability > bestMove.probability
                    && time >= TIME_FOR_SHOOT
                    && pState.getRound() >= ROUND_FOR_SHOOT
                    && nextMove.probability > HIT_PROBABILITY_THRESHOLD
                    && birdModel.mostLikelySpecies() != Constants.SPECIES_BLACK_STORK
                    && birdModel.pNotBS() >= NOT_BS_THRESHOLD) {
                bestMove = nextMove;
                birdToShoot = birdIndex;
            }
        }

        if (birdToShoot != -1) {
            actionToTake = new Action(birdToShoot, bestMove.move);
            totalShots++;
        }

        time++;
        return actionToTake;
    }

    void beginRound(GameState pState) {
        for (int birdIndex = 0; birdIndex < pState.getNumBirds(); birdIndex++)
            unknownBirds.add(new BirdModel());
    }

    /**
     * Guess the species!
     * This function will be called at the end of each round, to give you
     * a chance to identify the species of the birds for extra points.
     * <p>
     * Fill the vector with guesses for the all birds.
     * Use SPECIES_UNKNOWN to avoid guessing.
     *
     * @param pState the GameState object with observations etc
     * @param pDue   time before which we must have returned
     * @return a vector with guesses for all the birds
     */
    public int[] guess(GameState pState, Deadline pDue) {
        int[] lGuess = new int[pState.getNumBirds()];

        if (pState.getRound() == 0) {
            for (int birdIndex = 0; birdIndex < pState.getNumBirds(); ++birdIndex)
                lGuess[birdIndex] = Constants.SPECIES_PIGEON;
            return lGuess;
        }

        for (int birdIndex = 0; birdIndex < pState.getNumBirds(); ++birdIndex)
            lGuess[birdIndex] = mostLikelySpecie(birdIndex);

        System.err.println("Shots: " + totalShots + ", hits: " + totalHits);
        return lGuess;
    }

    double[] pSpecies(BirdModel birdModel) {
        double[] probabilities = new double[Constants.COUNT_SPECIES];
        for (int specie = 0; specie < probabilities.length; specie++)
            probabilities[specie] = logProbabilityOfSpecie(birdModel, specie);
        probabilities = VectorUtils.normalizeLogLikelyhoods(probabilities);
        return probabilities;
    }

    double logProbabilityOfSpecie(BirdModel birdModel, int specie) {
        return specieModels[specie].logLikelyhoodOf(birdModel.getObservations()) + Math.log(priorProbabilities[specie]);
    }

    public int mostLikelySpecie(int birdIndex) {
        return VectorUtils.argmax(unknownBirds.get(birdIndex).probabilityOfSpecie);
    }

    /**
     * If you hit the bird you were trying to shoot, you will be notified
     * through this function.
     *
     * @param pState the GameState object with observations etc
     * @param pBird  the bird you hit
     * @param pDue   time before which we must have returned
     */
    public void hit(GameState pState, int pBird, Deadline pDue) {
        System.err.println("HIT BIRD!!!");
        totalHits++;
    }

    /**
     * If you made any guesses, you will find out the true species of those
     * birds through this function.
     *
     * @param pState   the GameState object with observations etc
     * @param pSpecies the vector with species
     * @param pDue     time before which we must have returned
     */
    public void reveal(GameState pState, int[] pSpecies, Deadline pDue) {
        // Adding observations to the correct specie
        for (int birdIndex = 0; birdIndex < pState.getNumBirds(); birdIndex++) {
            int specie = pSpecies[birdIndex];
            if (specie == Constants.SPECIES_BLACK_STORK)
                seenBlackStork = true;
            if (specie != Constants.SPECIES_UNKNOWN)
                specieModels[specie].addObservations(new ArrayList<>(unknownBirds.get(birdIndex).getObservations()));
        }

        // Training each specie model
        for (SpecieModel specieModel : specieModels) {
            if (specieModel.getNobservations() > 1)
                specieModel.learn();
        }

        endRound(pState, pSpecies);
    }


    void endRound(GameState pState, int[] pSpecies) {
        updateProbabilities(pSpecies);
        unknownBirds.clear();
        time = 0;
    }

    void updateProbabilities(int[] pSpecies) {
        totalBirds += pSpecies.length;

        for (int birdIndex = 0; birdIndex < pSpecies.length; birdIndex++) {
            int specie = pSpecies[birdIndex];
            if (specie == Constants.SPECIES_UNKNOWN)
                totalBirds--;
            else
                specieModels[specie].increaseObservedBirds();
        }

        for (int specie = 0; specie < Constants.COUNT_SPECIES; specie++) {
            priorProbabilities[specie] = (double) specieModels[specie].getNumberObservedBirds() / (double) totalBirds;
        }
    }

    public static final Action cDontShoot = new Action(-1, -1);
}
