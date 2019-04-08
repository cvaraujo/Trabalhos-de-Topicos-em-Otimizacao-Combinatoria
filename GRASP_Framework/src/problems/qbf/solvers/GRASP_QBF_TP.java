package problems.qbf.solvers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import metaheuristics.grasp.AbstractGRASP;
import problems.qbf.QBF_Inverse;
import problems.qbf.Tripla;
import solutions.Solution;



/**
 * Metaheuristic GRASP (Greedy Randomized Adaptive Search Procedure) for
 * obtaining an optimal solution to a QBF (Quadractive Binary Function --
 * {@link #QuadracticBinaryFunction}). Since by default this GRASP considers
 * minimization problems, an inverse QBF function is adopted.
 * 
 * @author ccavellucci, fusberti
 */
public class GRASP_QBF_TP extends AbstractGRASP<Integer> {

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
	public GRASP_QBF_TP(Double alpha, Integer iterations, String filename) throws IOException {
		super(new QBF_Inverse(filename), alpha, iterations);
		inicializaHashMap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grasp.abstracts.AbstractGRASP#makeCL()
	 */
	
	public ArrayList <Tripla> triplas;
	public HashMap <Integer, ArrayList<Tripla> > mapaTriplas;
	
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

		// do nothing since all elements off the solution are viable candidates.
		//ArrayList<Integer> _CL = new ArrayList<Integer>();
		/*CL.clear();
		for (int i = 0; i < ObjFunction.getDomainSize(); i++) {
			if (incumbentSol.contains(i))
				continue;
			boolean podeEntrar = true;
			for (Tripla t : mapaTriplas.get(i)) {
				if (t.estaSaturada()) {
					podeEntrar = false;
					break;
				}
			}
			if (podeEntrar)
				CL.add(i);
		}*/
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

		long startTime = System.currentTimeMillis();
		//Tripla t = new Tripla (30,50);
		//ArrayList<Tripla> triplas = new ArrayList<Tripla>();
		//triplas.add(t);
		
		GRASP_QBF_TP grasp = new GRASP_QBF_TP(0.95, 1000, "instances/qbf040");

		Solution<Integer> bestSol = grasp.solve();
		System.out.println("maxVal = " + bestSol);
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Time = "+(double)totalTime/(double)1000+" seg");

	}



	







}
