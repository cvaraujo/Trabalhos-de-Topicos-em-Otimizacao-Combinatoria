package solutions;

import java.util.ArrayList;
import java.util.Arrays;

import problems.Evaluator;

/*
 * Pool of elite solutions
 * armazena as melhores solucoes a cada rodada
 */
public class EliteSolutions {
	
	private double bestCost, worstCost, d;
	private Integer r, n, sumCounts;
	private Integer bestIdx, worstIdx;
	
	public Integer[] countElem;
	private Double[] intensity; // computed as the frequency of each element from 0 to 1
	
	private ArrayList<Solution<Integer>> poolEliteSols;
	
	public EliteSolutions(Integer r, Integer n) {
		// initialize best and worst costs
		bestCost = Double.POSITIVE_INFINITY;
		bestIdx = -1;
		worstCost = Double.POSITIVE_INFINITY;
		worstIdx = -1;
		sumCounts = 0;
		this.r = r;
		this.n = n;
		this.d = 0.5;
		
		// initialize count to zero
		countElem = new Integer[n];
		Arrays.fill(countElem, 0);
		
		// initialize intensity to zero
		intensity = new Double[n];
		Arrays.fill(intensity, 0.0);
		
		// initialize the pool of elite solutions
		poolEliteSols = new ArrayList<Solution<Integer>>();		
	}
	
	public Integer getCount(Integer idx) {
		return countElem[idx];
	}
	
	public void setWorstIdx() {
		Double wCost = poolEliteSols.get(0).cost;
		Integer idx = 0;
		
		for (int i = 1; i < poolEliteSols.size(); i ++) {
			if (poolEliteSols.get(i).cost > wCost)
				idx = i;
		}
		
		worstIdx = idx;
		worstCost = poolEliteSols.get(idx).cost;
	}
	
	
	
	public void insertSolution(Solution<Integer> sol) {
		// insert solution: update best and worst costs; 
		// update intensity of variables and countage
		
		if (poolEliteSols.size() == r) {
			// remove worst and update idxs
			removeCounts(worstIdx);
			poolEliteSols.add(worstIdx, sol);
			addCounts(worstIdx);
			if (sol.cost <= bestCost)
			{
				bestIdx = worstIdx; 
				bestCost = sol.cost;
			}
				
			setWorstIdx();
		} else {
			poolEliteSols.add(sol);
			addCounts(poolEliteSols.size()-1);
			if (sol.cost <= bestCost)
			{
				bestIdx = poolEliteSols.size()-1;
				bestCost = sol.cost;
			}
			
			if (poolEliteSols.size() == r)
				setWorstIdx();
		}
		computeI();
	}
	
	public void removeCounts(Integer idx) {
		if (idx < 0)
			return;
		for (Integer e : poolEliteSols.get(idx)) {
			countElem[e]--;
			sumCounts--;			
		}		
	}
	
	public void addCounts(Integer idx) {
		for (Integer e : poolEliteSols.get(idx)) {
			countElem[e]++;
			sumCounts++;			
		}
	}
	
	public void computeI() {
		for (int i = 0; i < intensity.length; i++)
			intensity[i] = (double) (countElem[i] / sumCounts);
	}
	
	public Boolean isDifferent(Solution<Integer> sol) {
		// compare to the others solution in the pool
		// if any element from sol occurs in more than 1/2 of the elite sol, discart it
		// or if for any s in pool if sol[i] == s[i] for more than d*n vars discart it, d = 0.5 
//		for (Integer e : sol) {
//			if (intensity[e] > 0.5)
//				return false;
//		}
		Integer common;
		// if sol have more than d*n elements equals to an elite sol it is not different enought
		for (Solution<Integer> s : poolEliteSols) {
			common = 0;
			for (Integer e : s)
				if (sol.contains(e))
					common++;
			if (common >= d*n)
				return false;							
		}		
		
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
