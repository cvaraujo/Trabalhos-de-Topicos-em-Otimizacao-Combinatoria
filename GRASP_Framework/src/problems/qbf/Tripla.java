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
	public Tripla (Integer a, Integer b, Integer c) {
		variaveis = new ArrayList<Integer>();
		estaNaSolucao = new ArrayList<Integer> ();
		variaveis.add(a);
		variaveis.add(b);
		variaveis.add(c);
		contador = 0;
		
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
