package ticTacToe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

/**
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
		// int episodes = this.numEpisodes;
		// for(int i = 0; i < episodes; i++){
		// 	TTTEnvironment env = new TTTEnvironment();
		// 	while(!env.game.isTerminal()){
		// 		Game game = env.game;
		// 		double rand = Math.random();

		// 		double cavg = 0;
		// 		Outcome outcome = null;
		// 		Game prevS = null;
		// 		double smpl = 0;
		// 		double reward = 0;
		// 		Game sP = null;
		// 		HashMap.Entry<Move, Double> maxQ = null;

		// 		if (rand <= epsilon){
		// 			List<Move> moves = game.getPossibleMoves();
		// 			int MxMoves = moves.size()-1;
		// 			int min = 0;
		// 			int rNO = (int)(Math.random() * ((MxMoves - min) + 1)) + min;
		// 			Move m = moves.get(rNO);
		// 			if(game.isLegal(m)){
		// 				try{
		// 					outcome = env.executeMove(m);
		// 				}
		// 				catch(IllegalMoveException e){
		// 					System.out.println("Illegal move");
		// 				}
		// 				reward = outcome.localReward;
		// 				sP = outcome.sPrime;
		// 				prevS = outcome.s;
		// 				if (!sP.isTerminal()){
		// 					maxQ = maxQ(sP);
		// 					smpl = (reward + discount * (maxQ.getValue()));
		// 					cavg = ((1-this.alpha)*qTable.getQValue(prevS, m)) + (this.alpha * smpl);
		// 				}
		// 				else{
		// 					smpl = reward;
		// 					cavg = ((1-this.alpha)*qTable.getQValue(prevS, m)) + (this.alpha * smpl);
		// 					qTable.get(prevS).replace(m, cavg);
		// 					break;
		// 				}
		// 				qTable.get(prevS).replace(m, cavg);
		// 			}
		// 		}
		// 	}
		// }
//		for (int i=0; i<numEpisodes; i++){
//			ArrayList<Double> gPrimeQ = new ArrayList<Double>();
//			Game g = env.game;
//			Move m = null;
//			double qV = 0.0;
//			double MaxqV = 0.0;
//			double sample = 0.0;
//
//			while(!(g.isTerminal())){
//				List<Move> moves = g.getPossibleMoves();
//				Random rand = new Random();
//				double r = rand.nextDouble();
//
//				if (r < epsilon){
//					if (moves.size() != 0){
//						Random rand2 = new Random();
//						int r2 = rand2.nextInt(moves.size());
//						m = moves.get(r2);
//					}
//
//				}
//				else{
//					ArrayList<Double> BqList = new ArrayList<Double>();
//					Double bQvalue = 0.0;
//					for (Move move: moves){
//						BqList.add(qTable.getQValue(g, move));
//						bQvalue = Collections.max(BqList);
//					}
//					HashMap<Move, Double> BqMap = qTable.get(g);
//
//					for(Entry<Move, Double> entry: BqMap.entrySet()){
//						if (entry.getValue() == bQvalue){
//							m = entry.getKey();
//						}
//					}
//					BqList.clear();
//				}
//				moves.clear();
//				Outcome ocm = env.executeMove(m);
//				Game gm = ocm.s;
//				Game gPr = ocm.sPrime;
//				List<Move> mPr = gPr.getPossibleMoves();
//				for (Move mo : mPr){
//					gPrimeQ.add(qTable.getQValue(gPr, mo));
//					MaxqV = Collections.max(gPrimeQ);
//				}
//				sample = ocm.localReward + (discount * MaxqV);
//
//				Double prevEst = qTable.getQValue(gm, m);
//
//				qV = (((1 - alpha) * prevEst) + (alpha * sample));
//				qTable.addQValue(gm, m, qV);
//				gPrimeQ.clear();
//				
//				
//
//			}
//			env.reset();
//
//		}
		
		
		
//		for(int i=0; i<numEpisodes; i++) {
//			while(!this.env.isTerminal()) {
//				// for a game state g in environment env
//				Game g = this.env.getCurrentGameState();
//				// if g is terminal state, skip it.
//				if(g.isTerminal()){
//					continue;
//				}
//				// pick a move using epsilon greedy policy,
//				// check helper method pickEpsMove(Game g) for implementation
//				Move m = pickEpsMove(g);
//				Outcome outcome=null;
//				try {
//					// outcome after executing a move
//					outcome = this.env.executeMove(m);
//				} catch (IllegalMoveException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				// current q-value
//				double qvalue = this.qTable.getQValue(outcome.s, outcome.move);
//				double newqvalue;
//				// updated Q(g, m) = (1 - alpha) * old Q(g, m) + alpha * (reward + discount * maxQvalue(g'))
//				newqvalue = (1 - this.alpha)*qvalue + this.alpha*(outcome.localReward + this.discount*maxQvalue(outcome.sPrime));
//				this.qTable.addQValue(outcome.s, outcome.move, newqvalue);
//				
//			}
//			// reset after one iteration
//		this.env.reset();
//		}
		
		
for(int i=0; i<numEpisodes; i++) {
			
			Game gameStates = env.game;															
			
			Move movez = null;																	
			double qValue = 0.0;																		
			Double sample = 0.0;																
			ArrayList<Double> gPrimeQValues = new ArrayList<Double>();							
			Double maxQValueGPrime = 0.0;														
			
			
			
			while(!(gameStates.isTerminal())) {																
				List<Move> m = gameStates.getPossibleMoves();												
				
				Random rando = new Random();
				double randDouble = rando.nextDouble();																									
				
				if(randDouble < epsilon) {																	
					if(m.size()!=0) {
						Random rando1 = new Random();
						int num = rando1.nextInt(m.size());													
						movez = m.get(num);																		
					}
					
				}
				else {																									
					ArrayList<Double> bestQValueList = new ArrayList<Double>();											
					Double bestQValue = 0.0;																			
					for(Move bestMove : m) {
						bestQValueList.add(qTable.getQValue(gameStates, bestMove));										
						bestQValue = Collections.max(bestQValueList);													
					}
					
					
				
					HashMap<Move,Double> moveMap = qTable.get(gameStates);												
			
					for(Entry<Move, Double> bestMoveMap : moveMap.entrySet()) {										
						if(bestMoveMap.getValue().equals(bestQValue)) {
							movez = bestMoveMap.getKey();																
							
						}
					}
					
					
					bestQValueList.clear();																				
						
				}
				
				m.clear();																								
				
				
				
					
				
						Outcome bestExperience = env.executeMove(movez);													
						Game g = bestExperience.s;																				
						
						
						
						Game gPrime = bestExperience.sPrime;																
						List<Move> mPrime = gPrime.getPossibleMoves();     													
						
						
						
						
						
								for(Move mo : mPrime) {
									
									
									
										gPrimeQValues.add(qTable.getQValue(gPrime, mo));										
										maxQValueGPrime = Collections.max(gPrimeQValues);											
									
								}
								
								if(gPrime.isTerminal() == true) {
									maxQValueGPrime = 0.0;																			
									
								}
								
						
						sample = bestExperience.localReward + (discount*maxQValueGPrime);											
						
		
						
						Double oldEstimate = qTable.getQValue(g, movez);															
						
						
						
						 qValue = ( ((1-alpha)*oldEstimate) + (alpha*(sample)) );													
						 
						
						 
						 qTable.addQValue(g, movez, qValue);																				
						
					
					gPrimeQValues.clear();
				}
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
		Set<Game> gameSet = qTable.keySet();													
		
		HashMap<Game, Move> policyMap = new HashMap<Game,Move>();									
		
		for(Game gameStates : gameSet) {
			List<Move> m = gameStates.getPossibleMoves();											 
			ArrayList<Double> bestQValueList = new ArrayList<Double>();								
			Double bestQValue = 0.0;																
			//if(m.size()!=0) {
				for(Move movez : m) {																
					bestQValueList.add(qTable.getQValue(gameStates, movez));						
					bestQValue = Collections.max(bestQValueList);																	
				}
			//}	
			Move bestMove = null;																	
			
			HashMap<Move,Double> moveMap = qTable.get(gameStates);									
			
			for(Entry<Move, Double> bestMoveMap : moveMap.entrySet()) {							
				if(bestMoveMap.getValue().equals(bestQValue)) {
					bestMove = bestMoveMap.getKey();												
				}
			}
			policyMap.put(gameStates, bestMove);													
			bestQValueList.clear();																	
			
		}
		
		Policy policy = new Policy(policyMap);												
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
