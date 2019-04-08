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
	
	private Integer[] countElem;
	private Double[] intensity;
	
	private ArrayList<Solution<Integer>> poolEliteSols;
	
	public EliteSolutions(Integer r, Integer n) {
		// initialize best and worst costs
		bestCost = 0.0;
		worstCost = Double.POSITIVE_INFINITY;
		
		// initialize count to zero
		countElem = new Integer[n];	
		
		// initialize intensity to zero
		intensity = new Double[n];
		
		// initialize the pool of elite solutions with r empty solutions
		poolEliteSols = new ArrayList<Solution<Integer>>();
		for (int i = 0; i < r; i++)
		{
			poolEliteSols.add(new Solution<Integer>());
		}
		
	}
	

}
