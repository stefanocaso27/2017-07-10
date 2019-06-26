package it.polito.tdp.artsmia.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import com.sun.org.apache.xerces.internal.impl.dtd.models.DFAContentModel;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {
	
	private List<ArtObject> listaObjects;
	private Map<Integer, ArtObject> idMap;
	
	private ArtsmiaDAO dao;
	
	private SimpleWeightedGraph<ArtObject, DefaultWeightedEdge> grafo;
	
	public Model() {
		this.listaObjects = new LinkedList<>();
		this.dao = new ArtsmiaDAO();
		this.idMap = new HashMap<>();
	}
	
	public void creaGrafo() {
		this.listaObjects = dao.listObjects(idMap);
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		Graphs.addAllVertices(grafo, listaObjects);
		
		dao.pesoVertici(grafo, idMap);
		
		System.out.println("# vertici: " + grafo.vertexSet().size());
		System.out.println("# archi: " + grafo.edgeSet().size());	
	}
	
	public int calcolaComponente(SimpleWeightedGraph<ArtObject, DefaultWeightedEdge> grafo, int id) {
		
		BreadthFirstIterator<ArtObject, DefaultWeightedEdge> bfi = new BreadthFirstIterator<>(grafo, idMap.get(id));
		List<ArtObject> result = new ArrayList<>();
		
		while(bfi.hasNext()) {
			result.add(bfi.next());
		}
		
		int num = result.size();
		
		return num;
	}

	public SimpleWeightedGraph<ArtObject, DefaultWeightedEdge> getGrafo() {
		return grafo;
	}

	public void setGrafo(SimpleWeightedGraph<ArtObject, DefaultWeightedEdge> grafo) {
		this.grafo = grafo;
	}

}
