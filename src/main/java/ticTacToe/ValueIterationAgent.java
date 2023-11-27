package ticTacToe;

/**
 * YEAR - 3 
 * Ajay Menon
 * H00418802
 * 
 */


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A Value Iteration Agent, only very partially implemented. The methods to implement are: 
 * (1) {@link ValueIterationAgent#iterate}
 * (2) {@link ValueIterationAgent#extractPolicy}
 * 
 * You may also want/need to edit {@link ValueIterationAgent#train} - feel free to do this, but you probably won't need to.
 * @author ae187
 *
 */
public class ValueIterationAgent extends Agent {

	/**
	 * This map is used to store the values of states
	 */
	Map<Game, Double> valueFunction=new HashMap<Game, Double>();
	
	/**
	 * the discount factor
	 */
	double discount=0.9;
	
	/**
	 * the MDP model
	 */
	TTTMDP mdp=new TTTMDP();
	
	/**
	 * the number of iterations to perform - feel free to change this/try out different numbers of iterations
	 */
	int k=50;
	
	
	/**
	 * This constructor trains the agent offline first and sets its policy
	 */
	public ValueIterationAgent()
	{
		super();
		mdp=new TTTMDP();
		this.discount=0.9;
		initValues();
		train();
	}
	
	
	/**
	 * Use this constructor to initialise your agent with an existing policy
	 * @param p
	 */
	public ValueIterationAgent(Policy p) {
		super(p);
		
	}

	public ValueIterationAgent(double discountFactor) {
		
		this.discount=discountFactor;
		mdp=new TTTMDP();
		initValues();
		train();
	}
	
	/**
	 * Initialises the {@link ValueIterationAgent#valueFunction} map, and sets the initial value of all states to 0 
	 * (V0 from the lectures). Uses {@link Game#inverseHash} and {@link Game#generateAllValidGames(char)} to do this. 
	 * 
	 */
	public void initValues()
	{
		
		List<Game> allGames=Game.generateAllValidGames('X');//all valid games where it is X's turn, or it's terminal.
		for(Game g: allGames)
			this.valueFunction.put(g, 0.0);
		
		
		
	}
	
	
	
	public ValueIterationAgent(double discountFactor, double winReward, double loseReward, double livingReward, double drawReward)
	{
		this.discount=discountFactor;
		mdp=new TTTMDP(winReward, loseReward, livingReward, drawReward);
	}
	
	/**
	 
	
	/*
	 * Performs {@link #k} value iteration steps. After running this method, the {@link ValueIterationAgent#valueFunction} map should contain
	 * the (current) values of each reachable state. You should use the {@link TTTMDP} provided to do this.
	 * 
	 *
	 */
	public void iterate()
	{
		// Steps
		int l = k;
		// Looping by steps
		while(l != 0 && l>0){
			// States
			Set<Game> games = valueFunction.keySet();
			// Looping by states
			for(Game g: games){
				// Ensuring not in terminal state
				if(!g.isTerminal()){
					// List of possible moves
					List<Move> moves = g.getPossibleMoves();
					// Setting max to minimal number
					double max = -50;
					// Looping by possible moves
					for(Move m: moves){
						// Initializing VS to 0
						double vs = 0;
						// List of transition states/outcomes
						List<TransitionProb> outcomes = mdp.generateTransitions(g, m);
						// Looping by transition states/outcomes
						for(int i = 0; i < outcomes.size(); i++){
							// Calculating VS by bellman fords equation (by retrieving necessary values)
							Game vsp=outcomes.get(i).outcome.sPrime;
							vs += outcomes.get(i).prob * (outcomes.get(i).outcome.localReward + this.discount * valueFunction.get(vsp));
						}
						// Checking is VS lower than max, if so set new max
						if (max < vs){
							max = vs;
						}
					}	
					// Adding to hashmap
					this.valueFunction.put(g, max);
				}
			}
			// Step - 1
			l--;	
		}
	}
	
	/**This method should be run AFTER the train method to extract a policy according to {@link ValueIterationAgent#valueFunction}
	 * You will need to do a single step of expectimax from each game (state) key in {@link ValueIterationAgent#valueFunction} 
	 * to extract a policy.
	 * 
	 * @return the policy according to {@link ValueIterationAgent#valueFunction}
	 */
	public Policy extractPolicy()
	{
		// Initializing policy
		Policy p = new Policy();
		// States
		Set<Game> games = valueFunction.keySet();
		// Looping by states
		for (Game g: games){
			// List of possible moves
			List<Move> moves = g.getPossibleMoves();
			// Setting max to minimal number
			double max = -50;
			// Initializing best move
			Move best = null;
			// Looping by possible moves
			for(Move m: moves){
				// Initializing VS to 0
				double vs = 0;
				// List of transition states/outcomes
				List<TransitionProb> outcomes = mdp.generateTransitions(g, m);
				// Looping by transition states/outcomes
				for(int i = 0; i < outcomes.size(); i++){
					// Calculating VS by bellman fords equation (by retrieving necessary values)
					Game vsp=outcomes.get(i).outcome.sPrime;
					vs += outcomes.get(i).prob * (outcomes.get(i).outcome.localReward + this.discount * valueFunction.get(vsp));
				}
				// Checking is VS lower than max, if so set new max
				if (max < vs){
					max = vs;
					// Setting best move
					best = m;
				}
				// Adding to hashmap
				p.policy.put(g, best);
			}

		}
		// Returning policy
		return p;
	}
	
	/**
	 * This method solves the mdp using your implementation of {@link ValueIterationAgent#extractPolicy} and
	 * {@link ValueIterationAgent#iterate}. 
	 */
	public void train()
	{
		/**
		 * First run value iteration
		 */
		this.iterate();
		/**
		 * now extract policy from the values in {@link ValueIterationAgent#valueFunction} and set the agent's policy 
		 *  
		 */
		
		super.policy=extractPolicy();
		
		if (this.policy==null)
		{
			System.out.println("Unimplemented methods! First implement the iterate() & extractPolicy() methods");
			//System.exit(1);
		}
		
		
		
	}

	public static void main(String a[]) throws IllegalMoveException
	{
		//Test method to play the agent against a human agent.
		ValueIterationAgent agent=new ValueIterationAgent();
		HumanAgent d=new HumanAgent();
		
		Game g=new Game(agent, d, d);
		g.playOut();
		
		
		

		
		
	}
}
