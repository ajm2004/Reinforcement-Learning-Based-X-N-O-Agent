package ticTacToe;

/**
 * YEAR - 3 
 * Ajay Menon
 * H00418802
 * 
 */

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
/**
 * A policy iteration agent. You should implement the following methods:
 * (1) {@link PolicyIterationAgent#evaluatePolicy}: this is the policy evaluation step from your lectures
 * (2) {@link PolicyIterationAgent#improvePolicy}: this is the policy improvement step from your lectures
 * (3) {@link PolicyIterationAgent#train}: this is a method that should runs/alternate (1) and (2) until convergence. 
 * 
 * NOTE: there are two types of convergence involved in Policy Iteration: Convergence of the Values of the current policy, 
 * and Convergence of the current policy to the optimal policy.
 * The former happens when the values of the current policy no longer improve by much (i.e. the maximum improvement is less than 
 * some small delta). The latter happens when the policy improvement step no longer updates the policy, i.e. the current policy 
 * is already optimal. The algorithm should stop when this happens.
 * 
 * @author ae187
 *
 */
public class PolicyIterationAgent extends Agent {

	/**
	 * This map is used to store the values of states according to the current policy (policy evaluation). 
	 */
	HashMap<Game, Double> policyValues=new HashMap<Game, Double>();
	
	/**
	 * This stores the current policy as a map from {@link Game}s to {@link Move}. 
	 */
	HashMap<Game, Move> curPolicy=new HashMap<Game, Move>();
	
	double discount=0.9;
	
	/**
	 * The mdp model used, see {@link TTTMDP}
	 */
	TTTMDP mdp;
	
	/**
	 * loads the policy from file if one exists. Policies should be stored in .pol files directly under the project folder.
	 */
	public PolicyIterationAgent() {
		super();
		this.mdp=new TTTMDP();
		initValues();
		initRandomPolicy();
		train();
		
		
	}
	
	
	/**
	 * Use this constructor to initialise your agent with an existing policy
	 * @param p
	 */
	public PolicyIterationAgent(Policy p) {
		super(p);
		
	}

	/**
	 * Use this constructor to initialise a learning agent with default MDP paramters (rewards, transitions, etc) as specified in 
	 * {@link TTTMDP}
	 * @param discountFactor
	 */
	public PolicyIterationAgent(double discountFactor) {
		
		this.discount=discountFactor;
		this.mdp=new TTTMDP();
		initValues();
		initRandomPolicy();
		train();
	}
	/**
	 * Use this constructor to set the various parameters of the Tic-Tac-Toe MDP
	 * @param discountFactor
	 * @param winningReward
	 * @param losingReward
	 * @param livingReward
	 * @param drawReward
	 */
	public PolicyIterationAgent(double discountFactor, double winningReward, double losingReward, double livingReward, double drawReward)
	{
		this.discount=discountFactor;
		this.mdp=new TTTMDP(winningReward, losingReward, livingReward, drawReward);
		initValues();
		initRandomPolicy();
		train();
	}
	/**
	 * Initialises the {@link #policyValues} map, and sets the initial value of all states to 0 
	 * (V0 under some policy pi ({@link #curPolicy} from the lectures). Uses {@link Game#inverseHash} and {@link Game#generateAllValidGames(char)} to do this. 
	 * 
	 */
	public void initValues()
	{
		List<Game> allGames=Game.generateAllValidGames('X');//all valid games where it is X's turn, or it's terminal.
		for(Game g: allGames)
			this.policyValues.put(g, 0.0);
		
	}
	
	/**
	 *  You should implement this method to initially generate a random policy, i.e. fill the {@link #curPolicy} for every state. Take care that the moves you choose
	 *  for each state ARE VALID. You can use the {@link Game#getPossibleMoves()} method to get a list of valid moves and choose 
	 *  randomly between them. 
	 */
	public void initRandomPolicy()
	{
		// States
		Set<Game> games = policyValues.keySet();
		// Random instance
		Random random = new Random();
		// Random number
		int rNo = 0;
		// Looping through states
		for(Game g: games){
			// If game is not terminal
			if(!g.isTerminal()){
				// Get possible moves
				List<Move> moves = g.getPossibleMoves();
				// Random number within range of possible moves
				rNo = random.nextInt(moves.size());
				// Current policy is set to random move for that state
				curPolicy.put(g, moves.get(rNo));
			}
		}
	}
	
	
	/**
	 * Performs policy evaluation steps until the maximum change in values is less than {@code delta}, in other words
	 * until the values under the currrent policy converge. After running this method, 
	 * the {@link PolicyIterationAgent#policyValues} map should contain the values of each reachable state under the current policy. 
	 * You should use the {@link TTTMDP} {@link PolicyIterationAgent#mdp} provided to do this.
	 *
	 * @param delta
	 */
	protected void evaluatePolicy(double delta)
	{
		// Bool to check if change less than delta
		boolean lD = false;
		// States
		Set<Game> games = curPolicy.keySet();
		// Change > delta
		while(lD == false){
			// Setting max to minimal number 
			double maxChange = -100;
			// Looping through states
			for(Game g: games){
				// If game is not terminal
				if(!g.isTerminal()){
					// List of transition states/outcomes
					List<TransitionProb> ocms = mdp.generateTransitions(g, curPolicy.get(g));
					// Initializing VS to 0
					double vs = 0;
					// Looping through transition states/outcomes
					for(int i = 0; i < ocms.size(); i++){
						// Calculating VS by bellman fords equation (by retrieving necessary values)
						Game vsk = ocms.get(i).outcome.sPrime; 
						vs += ocms.get(i).prob * (ocms.get(i).outcome.localReward + this.discount * policyValues.get(vsk));
					}
					// Checking if change is greater than max, if so set new max
					double chng = (vs - policyValues.get(g));
					if (chng > maxChange){
						maxChange = chng;
					}
					// Adding to hashmap
					this.policyValues.put(g, vs);
				}
				// If game is terminal (val is 0)
				else{
					this.policyValues.put(g, 0.0);
				}	

			}
			// Checking if change is less than delta
			if(maxChange < delta){
				lD = true;
			}
		} 
		
	}
		
	
	
	/**This method should be run AFTER the {@link PolicyIterationAgent#evaluatePolicy} train method to improve the current policy according to 
	 * {@link PolicyIterationAgent#policyValues}. You will need to do a single step of expectimax from each game (state) key in {@link PolicyIterationAgent#curPolicy} 
	 * to look for a move/action that potentially improves the current policy. 
	 * 
	 * @return true if the policy improved. Returns false if there was no improvement, i.e. the policy already returned the optimal actions.
	 */
	protected boolean improvePolicy()
	{
		// States
		Set<Game> games = curPolicy.keySet();
		// Bool to check if policy improved
		boolean imp = false;
		// Looping through states
		for(Game g: games){
			// If game is not terminal
			if(!g.isTerminal()){
				// List of possible moves
				List<Move> moves = g.getPossibleMoves();
				// Setting max to minimal number
				double max = -100;
				// Initializing best move
				Move best = null;
				// Looping through possible moves
				for(Move m: moves){
					// Initializing VS to 0
					double vs = 0;
					// List of transition states/outcomes
					List<TransitionProb> outcomes = mdp.generateTransitions(g, m);
					// Looping through transition states/outcomes
					for(int i = 0; i < outcomes.size(); i++){
						// Calculating VS by bellman fords equation (by retrieving necessary values)
						Game vsp=outcomes.get(i).outcome.sPrime;
						vs += outcomes.get(i).prob * (outcomes.get(i).outcome.localReward + this.discount * policyValues.get(vsp));
					}
					// Checking is VS lower than max, if so set new max
					if (max < vs){
						max = vs;
						// Setting best move
						best = m;
					}
				}
				// Checking if max is greater than current policy value, if so set new policy
				if(max > this.policyValues.get(g)){
					this.curPolicy.put(g, best);
					// Policy improved
					imp = true;
				}
			}
			
		
		}
		// Returning true if policy improved else false
		if (imp){
			return true;
		}else {
			return false;}
		

	}
	
	/**
	 * The (convergence) delta
	 */
	double delta=0.1;
	
	/**
	 * This method should perform policy evaluation and policy improvement steps until convergence (i.e. until the policy
	 * no longer changes), and so uses your 
	 * {@link PolicyIterationAgent#evaluatePolicy} and {@link PolicyIterationAgent#improvePolicy} methods.
	 */
	public void train()
	{
		// Bool to check if policy converged, nconv meaning not converged
		boolean nconv = true;
		// Looping until policy converged
		while(nconv){
			// Evaluating policy
			evaluatePolicy(this.delta);
			// Improving policy
			nconv = improvePolicy();
		}
		// Setting policy to current policy after training
		super.policy = new Policy(curPolicy);
		
		
		
	}
	
	public static void main(String[] args) throws IllegalMoveException
	{
		/**
		 * Test code to run the Policy Iteration Agent agains a Human Agent.
		 */
		PolicyIterationAgent pi=new PolicyIterationAgent();
		
		HumanAgent h=new HumanAgent();
		
		Game g=new Game(pi, h, h);
		
		g.playOut();
		
		
	}
	

}
