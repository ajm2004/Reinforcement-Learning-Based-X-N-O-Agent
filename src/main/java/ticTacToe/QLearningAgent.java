package ticTacToe;

/**
 * YEAR - 3 
 * Ajay Menon
 * H00418802
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

/**
 * TEST
 * A Q-Learning agent with a Q-Table, i.e. a table of Q-Values. This table is implemented in the {@link QTable} class.
 * 
 *  The methods to implement are: 
 * (1) {@link QLearningAgent#train}
 * (2) {@link QLearningAgent#extractPolicy}
 * 
 * Your agent acts in a {@link TTTEnvironment} which provides the method {@link TTTEnvironment#executeMove} which returns an {@link Outcome} object, in other words
 * an [s,a,r,s']: source state, action taken, reward received, and the target state after the opponent has played their move. You may want/need to edit
 * {@link TTTEnvironment} - but you probably won't need to. 
 * @author ae187
 */

public class QLearningAgent extends Agent {
	
	/**
	 * The learning rate, between 0 and 1.
	 */
	double alpha=0.1;
	
	/**
	 * The number of episodes to train for
	 */
	int numEpisodes=25000;
	
	/**
	 * The discount factor (gamma)
	 */
	double discount=0.9;
	
	
	/**
	 * The epsilon in the epsilon greedy policy used during training.
	 */
	double epsilon=0.1;
	
	/**
	 * This is the Q-Table. To get an value for an (s,a) pair, i.e. a (game, move) pair.
	 * 
	 */
	
	QTable qTable=new QTable();
	
	
	/**
	 * This is the Reinforcement Learning environment that this agent will interact with when it is training.
	 * By default, the opponent is the random agent which should make your q learning agent learn the same policy 
	 * as your value iteration and policy iteration agents.
	 */
	TTTEnvironment env=new TTTEnvironment();
	
	
	/**
	 * Construct a Q-Learning agent that learns from interactions with {@code opponent}.
	 * @param opponent the opponent agent that this Q-Learning agent will interact with to learn.
	 * @param learningRate This is the rate at which the agent learns. Alpha from your lectures.
	 * @param numEpisodes The number of episodes (games) to train for
	 * @throws IllegalMoveException 
	 */
	public QLearningAgent(Agent opponent, double learningRate, int numEpisodes, double discount) throws IllegalMoveException
	{
		env=new TTTEnvironment(opponent);
		this.alpha=learningRate;
		this.numEpisodes=numEpisodes;
		this.discount=discount;
		initQTable();
		train();
	}
	
	/**
	 * Initialises all valid q-values -- Q(g,m) -- to 0.
	 *  
	 */
	
	protected void initQTable()
	{
		List<Game> allGames=Game.generateAllValidGames('X');//all valid games where it is X's turn, or it's terminal.
		for(Game g: allGames)
		{
			List<Move> moves=g.getPossibleMoves();
			for(Move m: moves)
			{
				this.qTable.addQValue(g, m, 0.0);
				//System.out.println("initing q value. Game:"+g);
				//System.out.println("Move:"+m);
			}
			
		}
		
	}
	
	/**
	 * Uses default parameters for the opponent (a RandomAgent) and the learning rate (0.2). Use other constructor to set these manually.
	 * @throws IllegalMoveException 
	 */
	public QLearningAgent() throws IllegalMoveException
	{
		this(new RandomAgent(), 0.1, 25000, 0.9);
		
	}
	
	public HashMap.Entry<Move, Double> maxQ(Game state) {
		if(!state.isTerminal()) 
		{
			HashMap<Move, Double> moveQs = qTable.get(state);
			Double maxQ = -Double.MAX_VALUE;
			HashMap.Entry<Move, Double> maxEntry = null;
		
			for (HashMap.Entry<Move, Double> q : moveQs.entrySet())
			{
				if (q.getValue() > maxQ)
			    	{
						
				 		maxQ = q.getValue();
				 		maxEntry = q;
			    	}
			}
			return maxEntry;
		}
		return null;
		
	}
	/**
	 *  Implement this method. It should play {@code this.numEpisodes} episodes of Tic-Tac-Toe with the TTTEnvironment, updating q-values according 
	 *  to the Q-Learning algorithm as required. The agent should play according to an epsilon-greedy policy where with the probability {@code epsilon} the
	 *  agent explores, and with probability {@code 1-epsilon}, it exploits. 
	 *  
	 *  At the end of this method you should always call the {@code extractPolicy()} method to extract the policy from the learned q-values. This is currently
	 *  done for you on the last line of the method.
	 *  @throws IllegalMoveException
	 */
	
	public void train() throws IllegalMoveException
	{
		// Train the agent here using numEpisodes of the game
		for(int i=0; i<numEpisodes; i++) {	
			// Max Q value of gPrimeQValue 
			Double maxQVGP = 0.0;
			// Array list of Q values of s prime														
			ArrayList<Double> gPQV = new ArrayList<Double>();
			// Sample							
			Double spl = 0.0;
			// Move																
			Move mv = null;
			// Game State																	
			Game game = env.game;
			// Q value 															
			double qV = 0.0;	
			// While game is not terminal																	
			while(!game.isTerminal()) {	
				// Random instance															
				Random random = new Random();
				// Random number
				double rDo = random.nextDouble();
				// List of possible moves																									
				List<Move> moves = game.getPossibleMoves();
				// If random number is less than epsilon explore. 				
				if(rDo < epsilon) {						
					if(moves.size()!=0) {
						Random random2 = new Random();
						int no = random2.nextInt(moves.size());													
						mv = moves.get(no);
						// Generate random action and save the moves.																		
					}
				}
				// Else exploit.
				else {			
					// Array list of Q values of that state and move.																						
					ArrayList<Double> bQVL = new ArrayList<Double>();
					// Highest Q value of that state and move.											
					Double bQV = 0.0;		
					// Looping through possible moves.																	
					for(Move best : moves) {
						// Add Q value of that state and move to array list.
						bQVL.add(qTable.getQValue(game, best));		
						// Set highest Q value to max of array list.								
						bQV = Collections.max(bQVL);													
					}
					// Get the Q values of that state and move.
					HashMap<Move,Double> mp = qTable.get(game);
					// Looping through Q values of that state and move.												
					for(Entry<Move, Double> bestmp : mp.entrySet()) {		
						// If Q value is equal to highest Q value.								
						if(bestmp.getValue().equals(bQV)) {
							// Set move to that move.
							mv = bestmp.getKey();																
						}
					}
					// Clear array list for next state and move.
					bQVL.clear();																				
				}
				// Clear possible moves for next state.
				moves.clear();							
				// Execute move.																	
				Outcome Exp = env.executeMove(mv);			
				// Target state.														
				Game gP = Exp.sPrime;		
				// Source state.										
				Game gme = Exp.s;	
				// List of possible moves of target state.																			
				List<Move> Pr = gP.getPossibleMoves();    
				// Looping through possible moves of target state. 													
				for(Move mo : Pr) {
					// Add Q value of target state and move to array list.
					gPQV.add(qTable.getQValue(gP, mo));
					// Set max Q value to max of array list.										
					maxQVGP = Collections.max(gPQV);											
					}
				// If target state is terminal set max Q value to 0.
					if(gP.isTerminal()) {
					maxQVGP = 0.0;																			
					}
				// Calculate sample.
				spl = Exp.localReward + (discount*maxQVGP);																								
				qV = ( ((1-alpha)*(qTable.getQValue(gme, mv))) + (alpha*(spl)) );		
				// Add Q value to Q table.											
				qTable.addQValue(gme, mv, qV);
				// Clear array list for next state and move.																				
				gPQV.clear();
				}
			// Reset game/episode.
			env.reset();																											
			}
		
		
		//--------------------------------------------------------
		//you shouldn't need to delete the following lines of code.
		this.policy=extractPolicy();
		if (this.policy==null)
		{
			System.out.println("Unimplemented methods! First implement the train() & extractPolicy methods");
			//System.exit(1);
		}
	}
	
	
	/** Implement this method. It should use the q-values in the {@code qTable} to extract a policy and return it.
	 *
	 * @return the policy currently inherent in the QTable
	 */
	public Policy extractPolicy()
	{
		// States
		Set<Game> games = qTable.keySet();
		// Policy map													
		HashMap<Game, Move> map = new HashMap<Game,Move>();	
		// Looping through states.								
		for(Game g : games) {
			// List of possible moves.
			List<Move> moves = g.getPossibleMoves();		
			// Highest Q value of that state and move.
			Double bQ = 0.0;		
			// Array list of Q values of that state and move.														
			ArrayList<Double> bQVL = new ArrayList<Double>();	
			// Looping through possible moves.							
			for(Move move : moves) {		
				// Add Q value of that state and move to array list.														
				bQVL.add(qTable.getQValue(g, move));				
				// Set highest Q value to max of array list.		
				bQ = Collections.max(bQVL);																	
			}
			// Get the Q value and move and in map.
			HashMap<Move,Double> mm = qTable.get(g);
			// Best move.									
			Move best = null;	
			// Looping through MAP.																
			for(Entry<Move, Double> bestm : mm.entrySet()) {
				// If Q value is equal to highest Q value.							
				if(bestm.getValue().equals(bQ)) {
					// Move with highest Q value.
					best = bestm.getKey();												
				}
			}
			// Add to policy map.
		    map.put(g, best);	
					    // Clear array list for next state and move.												
			bQVL.clear();																	
		}
		// Create policy
		Policy policy = new Policy(map);
		// Return policy												
		return policy;	
	}
	
	public static void main(String a[]) throws IllegalMoveException
	{
		//Test method to play your agent against a human agent (yourself).
		QLearningAgent agent=new QLearningAgent();
		
		HumanAgent d=new HumanAgent();
		
		Game g=new Game(agent, d, d);
		g.playOut();
		
		
		

		
		
	}
	
	
	


	
}
