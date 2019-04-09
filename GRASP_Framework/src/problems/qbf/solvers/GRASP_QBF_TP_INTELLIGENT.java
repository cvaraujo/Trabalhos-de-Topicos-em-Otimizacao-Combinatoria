package problems.qbf.solvers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import metaheuristics.grasp.AbstractGRASP;
import problems.qbf.QBF_Inverse;
import problems.qbf.Tripla;
import solutions.EliteSolutions;
import solutions.Solution;



/**
 * Metaheuristic GRASP (Greedy Randomized Adaptive Search Procedure) for
 * obtaining an optimal solution to a QBF (Quadractive Binary Function --
 * {@link #QuadracticBinaryFunction}). Since by default this GRASP considers
 * minimization problems, an inverse QBF function is adopted.
 * 
 * @author ccavellucci, fusberti
 */
public class GRASP_QBF_TP_INTELLIGENT extends AbstractGRASP<Integer> {

	/**
	 * Constructor for the GRASP_QBF_TP class. An inverse QBF objective function is
	 * passed as argument for the superclass constructor.
	 * 
	 * @param alpha
	 *            The GRASP greediness-randomness parameter (within the range
	 *            [0,1])
	 * @param iterations
	 *            The number of iterations which the GRASP will be executed.
	 * @param filename
	 *            Name of the file for which the objective function parameters
	 *            should be read.
	 * @throws IOException
	 *             necessary for I/O operations.
	 */
	public boolean first_improving;
	public GRASP_QBF_TP_INTELLIGENT(Double alpha, Integer iterations, String filename, boolean first_improving, Integer r) throws IOException {
		super(new QBF_Inverse(filename), alpha, iterations);
		inicializaHashMap();
		this.first_improving = first_improving;
		eliteSolutionsPool = new EliteSolutions(r,  ObjFunction.getDomainSize());
		K = new ArrayList<Double>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grasp.abstracts.AbstractGRASP#makeCL()
	 */
	
	public EliteSolutions eliteSolutionsPool;
	public ArrayList <Tripla> triplas;
	public HashMap <Integer, ArrayList<Tripla> > mapaTriplas;
	
	/**
	 * the K(e) = F(c(e),I(e))
	 */
	protected ArrayList<Double> K;
	
	
	/**
	 * The GRASP constructive heuristic, which is responsible for building a
	 * feasible solution by selecting in a greedy-random fashion, candidate
	 * elements to enter the solution.
	 * 
	 * @return A feasible solution to the problem being minimized.
	 */
	public Solution<Integer> constructiveHeuristic() {
		CL = makeCL();
		RCL = makeRCL();
		incumbentSol = createEmptySol();
		incumbentCost = Double.POSITIVE_INFINITY;
		ZeraSolucao();
		double lambda = 20.0, sumK = 0;
		int p = CL.size()/10;
		int iteracoes = 1;
		Double best_p, p_k;
		/* Main loop, which repeats until the stopping criteria is reached. */
		while (!constructiveStopCriteria()) {
			sumK = 0;
			double maxCost = Double.NEGATIVE_INFINITY, minCost = Double.POSITIVE_INFINITY;
			incumbentCost = ObjFunction.evaluate(incumbentSol);
			updateCL();
//			if (iteracoes <= p)
//				alpha = 0.85;
//			else {
				alpha = 0.15;
//			}
			iteracoes++;
			/*
			 * Explore all candidate elements to enter the solution, saving the
			 * highest anp_idxd lowest cost variation achieved by the candidates.
			 */
			for (Integer c : CL) {
				Double deltaCost = ObjFunction.evaluateInsertionCost(c, incumbentSol);
				if (deltaCost < minCost)
					minCost = deltaCost;
				if (deltaCost > maxCost)
					maxCost = deltaCost;
			}

			/*
			 * Among all candidates, insert into the RCL those with the highest
			 * performance using parameter alpha as threshold.
			 */
			for (Integer c : CL) {
				Double deltaCost = ObjFunction.evaluateInsertionCost(c, incumbentSol);
				if (deltaCost <= minCost + alpha * (maxCost - minCost)) {
					RCL.add(c);
					Integer I = eliteSolutionsPool.getCount(c);
					K.add(lambda*deltaCost*-1 + I);
					sumK += K.get(K.size()-1);
				}
			}
			
			
			/* Choose a candidate randomly from the RCL */
			//System.out.println("Tam = " + RCL.size());
			//System.out.println("xxxx");
			if (RCL.size() == 0)
				break;
			
			// Intelligent search
			best_p = (Double) K.get(0)/sumK;
			int p_idx = 0;
			for (int i = 1; i < K.size(); i++) {
				p_k = (Double) K.get(i)/sumK;
				if (p_k > best_p)
				{
					best_p = p_k;
					p_idx = i;
				}
			}
			
//			int rndIndex = rng.nextInt(RCL.size());
//			Integer inCand = RCL.get(rndIndex);
			Integer inCand = RCL.get(p_idx);			
			
			CL.remove(inCand);
			incumbentSol.add(inCand);
			ObjFunction.evaluate(incumbentSol);
			adicionarValorNaSolucao(inCand);
			RCL.clear();
			K.clear();
		}
		
		eliteSolutionsPool.tryInsertSolution(incumbentSol);

		return incumbentSol;
	}
	
	public ArrayList<Tripla> createProhibitedTriples(Integer n)
	{
		ArrayList<Tripla> prohibitedTriples = new ArrayList<Tripla>();
		Tripla triple;
		
		for (int u = 0; u < n; u++)
		{
			triple = new Tripla(u, n);
			//System.out.println(triple.getVariaveis().toString());
			prohibitedTriples.add(triple);			
		}
				
		return prohibitedTriples;
	}
	
	@Override
	public void ZeraSolucao() {
		for (Tripla t : triplas) {
			t.contador = 0;
		}
		
	}

	
	public void inicializaHashMap () {
		mapaTriplas = new HashMap<Integer, ArrayList<Tripla>>();
		triplas = createProhibitedTriples(ObjFunction.getDomainSize());
		
		for (int i = 0; i < ObjFunction.getDomainSize(); i++) {
			mapaTriplas.put(i, new ArrayList<Tripla>());
			for (Tripla t : triplas) {
				if (t.estaNaTupla(i)) {
					mapaTriplas.get(i).add(t);
				}
					
			}
		}
	}
	
	
	@Override
	public void adicionarValorNaSolucao(Integer x) {
		for (Tripla t : mapaTriplas.get(x)) {
			t.adicionarNaSolucao(x);
		}
		
	}
	
	@Override
	public void removerValorDaSolucao(Integer x) {
		for (Tripla t : mapaTriplas.get(x)) {
			t.removerDaSolucao(x);
		}
	}
	
	
	@Override
	public ArrayList<Integer> makeCL() {
		
		ArrayList<Integer> _CL = new ArrayList<Integer>();
		for (int i = 0; i < ObjFunction.getDomainSize(); i++) {
			Integer cand = new Integer(i);
			_CL.add(cand);
		}

		return _CL;

	}	

	/*
	 * (non-Javadoc)
	 * 
	 * @see grasp.abstracts.AbstractGRASP#makeRCL()
	 */
	@Override
	public ArrayList<Integer> makeRCL() {
		
		ArrayList<Integer> _RCL = new ArrayList<Integer>();

		return _RCL;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grasp.abstracts.AbstractGRASP#updateCL()
	 */
	@Override
	public void updateCL() {
		ArrayList <Integer> out = new ArrayList<Integer>();
		for (Integer e : CL) {
			for (Tripla t : mapaTriplas.get(e)) {
				if (t.estaSaturada()) {
					out.add(e);
					break;
				}
			}
		}
		for (Integer e : out) {
			CL.remove(e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * This createEmptySol instantiates an empty solution and it attributes a
	 * zero cost, since it is known that a QBF solution with all variables set
	 * to zero has also zero cost.
	 */
	@Override
	public Solution<Integer> createEmptySol() {
		Solution<Integer> sol = new Solution<Integer>();
		sol.cost = 0.0;
		return sol;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * The local search operator developed for the QBF objective function is
	 * composed by the neighborhood moves Insertion, Removal and 2-Exchange.
	 */
	@Override
	public Solution<Integer> localSearch() {

		Double minDeltaCost;
		Integer bestCandIn = null, bestCandOut = null;

		do {
			minDeltaCost = Double.POSITIVE_INFINITY;
			updateCL();
			
			// Evaluate insertions
			for (Integer candIn : CL) {
				double deltaCost = ObjFunction.evaluateInsertionCost(candIn, incumbentSol);
				if (deltaCost < minDeltaCost) {
					minDeltaCost = deltaCost;
					bestCandIn = candIn;
					bestCandOut = null;
				}
			}
			// Evaluate removals
			for (Integer candOut : incumbentSol) {
				double deltaCost = ObjFunction.evaluateRemovalCost(candOut, incumbentSol);
				if (deltaCost < minDeltaCost) {
					minDeltaCost = deltaCost;
					bestCandIn = null;
					bestCandOut = candOut;
				}
			}
			// Evaluate exchanges
			for (Integer candIn : CL) {
				for (Integer candOut : incumbentSol) {
					double deltaCost = ObjFunction.evaluateExchangeCost(candIn, candOut, incumbentSol);
					if (deltaCost < minDeltaCost) {
						minDeltaCost = deltaCost;
						bestCandIn = candIn;
						bestCandOut = candOut;
					}
				}
			}
			// Implement the best move, if it reduces the solution cost.
			if (minDeltaCost+0.1 < -Double.MIN_VALUE) {
				if (bestCandOut != null) {
					incumbentSol.remove(bestCandOut);
					removerValorDaSolucao(bestCandOut);
					CL.add(bestCandOut);
				}
				if (bestCandIn != null) {
					incumbentSol.add(bestCandIn);
					adicionarValorNaSolucao(bestCandIn);
					CL.remove(bestCandIn);
				}
				ObjFunction.evaluate(incumbentSol);
			}
		} while (minDeltaCost < -Double.MIN_VALUE);

		return null;
	}

	/**
	 * A main method used for testing the GRASP metaheuristic.
	 * 
	 */
	public static void main(String[] args) throws IOException {
		
		
		
		
		String instancia = args[0];
		double alpha = Double.parseDouble(args[1]);
		int x = Integer.parseInt(args[2]);
		int r = 20;
		boolean first = false;
		if (x == 1)
			first = true;
		long startTime = System.currentTimeMillis();
		GRASP_QBF_TP_INTELLIGENT grasp = new GRASP_QBF_TP_INTELLIGENT(alpha, 1000, "instances/"+instancia, first, r);
		Solution<Integer> bestSol = grasp.solve();
		System.out.println("maxVal = " + bestSol);
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Time = "+(double)totalTime/(double)1000+" seg");

	}


	







}
