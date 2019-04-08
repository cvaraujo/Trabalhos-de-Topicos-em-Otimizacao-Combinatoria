package problems.qbf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Arrays;

public class Tripla {
	
	private ArrayList <Integer> variaveis;
	private ArrayList <Integer> estaNaSolucao;
	public Integer contador;	

	public Tripla (int u, int n) {
		variaveis = new ArrayList<Integer>();
		estaNaSolucao = new ArrayList<Integer>();
		Integer g = g(u,n);
		variaveis.add(u);
		variaveis.add(g);
		variaveis.add(h(u,g,n));
		contador = 0;
	}
	
	/**
	 * *funcao linear congruente l, tipicamente usada para a geracao de numeros pseudo-aleatorios
	 */
	public Integer l(Integer u, Integer pi1, Integer pi2, Integer n) {
		return (((pi1 * u + pi2) % n));
	}
	
	public Integer g(Integer u, Integer n) {
		Integer g = l(u, 131, 1031, n);
		if (g == u)
			return (1 + (g%n));
		return g;
	}
	
	public Integer h(Integer u, Integer g, Integer n) {
		Integer h = l(u, 193, 1093, n);
		
		if (h != u && h != g)
			return h;
		else if ((1+(h%n)) != u && (1+(h%n)) != g)
			return (1+(h%n));
				
		return (1+((h+1)%n));
	}

	
	public ArrayList<Integer> getVariaveis() {
		return variaveis;
	}

	public void setVariaveis(ArrayList<Integer> variaveis) {
		this.variaveis = variaveis;
	}
	
	public boolean estaNaTupla (int x) {
		for (int v : variaveis)
			if (v == x) return true;
		return false;
	}
	
	
	public void adicionarNaSolucao (int x) {
		estaNaSolucao.add(x);
		contador++;
	}
	
	public void removerDaSolucao (int x) {
		estaNaSolucao.remove (new Integer(x));
		contador--;
	}
	
	
	public boolean estaSaturada () {
		if (contador == 2) return true;
		return false;
		
	}
	
}
