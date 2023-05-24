package it.polito.tdp.itunes.model;

import java.util.*;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.itunes.db.ItunesDAO;

public class Model {
	private List<Album> allAlbum;
	private SimpleDirectedWeightedGraph<Album, DefaultWeightedEdge> graph;
	private ItunesDAO dao;
	private List<Album> bestPath;
	private int bestScore;
	
	public Model() {
		this.allAlbum= new ArrayList<>();
		this.graph=new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		this.dao= new ItunesDAO();
		
	}
	
	public List<Album> getPath(Album source, Album target, int threshold){
		List<Album> parziale=new ArrayList<>();
		this.bestPath=new ArrayList<>();
		this.bestScore=0;
		parziale.add(source);
		
		ricorsione(parziale, target, threshold);
		System.out.println("MIGLIOR soluzione ha : "+parziale.size());
		System.out.println(parziale);
		return this.bestPath;
	}
	
	
	
	
	private void ricorsione(List<Album> parziale, Album target, int threshold) {
		Album current=parziale.get(parziale.size()-1);
		
		//condizione di uscita
		if(current.equals(target)) {
			//controllo se qst soluzione è migliore del best
			if(getScore(parziale)>this.bestScore) {
				this.bestScore=getScore(parziale);
				this.bestPath=new ArrayList<>(parziale);
			}
			return;
		}
		//continuo ad aggiungere elemnti in parziale
		List<Album> successori=Graphs.successorListOf(this.graph, current);
		
		for(Album a: successori) {
			if(this.graph.getEdgeWeight(this.graph.getEdge(current, a))>=threshold) {
				parziale.add(a);
				ricorsione(parziale, target, threshold);
				parziale.remove(a); //backtracking
			}
		}
		
	}

	private int getScore(List<Album> parziale) { //conta archi 
		int score=0;
		Album source= parziale.get(0);
		for(Album a: parziale.subList(1, parziale.size()-1)) {
			if(getBilancio(a)>getBilancio(source))
				score+=1;
		}
		return score;
	}

	public List<bilancioAlbum> getAdiacenti(Album a ) {
		List<Album> successori=Graphs.successorListOf(this.graph, a); //gli archi uscenti dal vertici
		List<bilancioAlbum> bilancioSuccessori=new ArrayList<>();
		
		for(Album al: successori) {
			//calcola il bilancio di tutti i 
			bilancioSuccessori.add(new bilancioAlbum(al, getBilancio(al)));
		}
		
		Collections.sort(bilancioSuccessori);
		return bilancioSuccessori;
	}
	
	public void creaGrafo(int n) {
		loadNodes(n);
		
		Graphs.addAllVertices(this.graph, this.allAlbum);
		
		System.out.println("vertice size: "+this.graph.vertexSet().size());
		
		for(Album a1: this.allAlbum) {
			for(Album a2: this.allAlbum) {
				int peso=a1.getNumSongs()-a2.getNumSongs();
				
				if(peso>0) { //esclude che a1 e a2 sono diversi e che a1 abbia + canzoni di a2
					Graphs.addEdgeWithVertices(this.graph, a2, a1, peso);
				}
			}
		}

		System.out.println("arco size: "+this.graph.edgeSet().size());
		
	}
	
	
	
	private int getBilancio(Album a) {
		int bilancio=0;
		List<DefaultWeightedEdge> edgesIN= new ArrayList<>(this.graph.incomingEdgesOf(a));
		List<DefaultWeightedEdge> edgesOUT= new ArrayList<>(this.graph.outgoingEdgesOf(a));
		
		for(DefaultWeightedEdge edge: edgesIN) {
			bilancio+=this.graph.getEdgeWeight(edge);
		}
		for(DefaultWeightedEdge edge: edgesOUT) {
			bilancio-=this.graph.getEdgeWeight(edge);
		}
		return bilancio;
		
	}
	
	public List<Album> getVertices(){
		List<Album> allVertices=new ArrayList<>(this.graph.vertexSet());
		Collections.sort(allVertices);
		return allVertices;
	}
	
	
	private void loadNodes(int n) {
		if(this.allAlbum.isEmpty()) {
			this.allAlbum=dao.getFilteredAlbum(n);
		}
	}

	public int getNumVertices() {
		return this.graph.vertexSet().size();
	}

	public int getNumEdges() {
		return this.graph.edgeSet().size();
	}

	public void clearGraph() {
		this.graph=new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
	}
	
	
	
}

