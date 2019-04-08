package solutions;

import java.util.ArrayList;
import java.util.Arrays;

import problems.Evaluator;

/*
 * Pool of elite solutions
 * armazena as melhores solucoes a cada rodada
 */
public class EliteSolutions {
	
	private double bestCost;
	private double worstCost;
	private Integer r, sumCounts;
	private Integer bestIdx, worstIdx;
	
	private Integer[] countElem;
	private Double[] intensity; // computed as the frequency of each element from 0 to 1
	
	private ArrayList<Solution<Integer>> poolEliteSols;
	
	public EliteSolutions(Integer r, Integer n) {
		// initialize best and worst costs
		bestCost = 0.0;
		bestIdx = -1;
		worstCost = 0.0;
		worstIdx = -1;
		sumCounts = 0;
		this.r = r;
		
		// initialize count to zero
		countElem = new Integer[n];	
		
		// initialize intensity to zero
		intensity = new Double[n];
		
		// initialize the pool of elite solutions
		poolEliteSols = new ArrayList<Solution<Integer>>();		
	}
	
	public void setWorstIdx() {
		Double wCost = poolEliteSols.get(0).cost;
		Integer idx = 0;
		
		for (int i = 1; i < poolEliteSols.size(); i ++) {
			if (poolEliteSols.get(i).cost > wCost)
				idx = i;
		}
		
		worstIdx = idx;
	}
	
	public void insertSolution(Solution<Integer> sol) {
		// insert solution: update best and worst costs; 
		// update intensity of variables and countage
		
		for (Integer e : sol) {
			countElem[e]++;
			sumCounts++;
		}
		
		for (int i = 0; i < intensity.length; i++)
			intensity[i] = (double) (countElem[i] / sumCounts);
		
		if (poolEliteSols.size() == r) {
			// remove worst and update idxs
			poolEliteSols.add(worstIdx, sol);
			if (sol.cost <= bestCost)
				bestIdx = worstIdx; 
			setWorstIdx();
		} else {
			poolEliteSols.add(sol);
			if (sol.cost <= bestCost)
				bestIdx = poolEliteSols.size();
		}
	}
	
	public Boolean isDifferent(Solution<Integer> sol) {
		// compare to the others solution in the pool
		// IDEA: correlation entre a intensidade dos elementos da soluçao eleite e a intensidade da solucao corrente??
		
		return true;
	}
	
	public void tryInsertSolution(Solution<Integer> sol) {
		if (sol.cost <= bestCost) {
			insertSolution(sol);
		} else if (sol.cost < worstCost && isDifferent(sol)) {
			insertSolution(sol);
		}
	}
	
	
	

}
