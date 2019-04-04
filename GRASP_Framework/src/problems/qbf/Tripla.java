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
	
	public Tripla (int a, int b, int c) {
		variaveis.add(a);
		variaveis.add(b);
		variaveis.add(c);
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
	
	
	public boolean estaSaturada (int varSolucao[]) {
		int cont = 0;
		for (int x: varSolucao) {
			if (estaNaTupla(x))
				cont++;
		}
		
		if (cont == 2) return true;
		return false;
		
	}
	
}
