package it.polito.tdp.artsmia.model;

import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {

	private List<ArtObject> artObjects;
	private Graph<ArtObject, DefaultWeightedEdge> graph;

	/**
	 * Popola la lista artObjects (leggendo dal DB) e crea il grafo.
	 */
	public void creaGrafo() {

		// Leggi lista degli oggetti dal DB
		ArtsmiaDAO dao = new ArtsmiaDAO();
		this.artObjects = dao.listObjects();

		// Crea il grafo
		this.graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

		// Aggiungi i vertici
		// for(ArtObject ao: this.artObjects) {
		// this.graph.addVertex(ao) ;
		// }

		Graphs.addAllVertices(this.graph, this.artObjects);

		// Aggiungi gli archi (con il loro peso)

		/*
		 * VERSIONE 3 - la più efficiente di tutte ** esegue una query unica (complessa,
		 * ma una sola) con la quale ottiene in un sol colpo tutti gli archi del grafo
		 */
		// TODO: basata sulla query:
//		SELECT eo1.object_id, count(eo2.exhibition_id), eo2.object_id
//		FROM exhibition_objects eo1, exhibition_objects eo2
//		WHERE eo1.exhibition_id=eo2.exhibition_id
//		AND eo2.object_id>eo1.object_id
//		GROUP BY eo1.object_id, eo2.object_id
		
		/*
		 * VERSIONE 2 - più efficiente ** Utilizza il metodo listArtObjectAndCount per
		 * fare una sola query per vertice, ottenendo in un solo colpo tutti gli archi
		 * adiacenti a tale vertice (ed il relativo peso)
		 */
		for (ArtObject ao : this.artObjects) {
			List<ArtObjectAndCount> connessi = dao.listArtObjectAndCount(ao);

			for (ArtObjectAndCount c : connessi) {
				ArtObject dest = new ArtObject(c.getArtObjectId(), null, null, null, 0, null, null, null, null, null, 0,
						null, null, null, null, null); // l'oggetto "dest" è un ArtObject inizializzato in modo
														// <i>lazy</i>, cioè solo con i campi utili per il calcolo di
														// hashCode/equals. In questo modo può "impersonare" un vertice
														// del grafo.
				// System.out.format("(%d, %d) peso %d\n", ao.getId(), dest.getId(),
				// c.getCount()) ;
				Graphs.addEdge(this.graph, ao, dest, c.getCount());
			}
		}
		System.out.format("Grafo creato: %d vertici, %d archi\n", graph.vertexSet().size(), graph.edgeSet().size());
		/* FINE VERSIONE 2 */

		/* VERSIONE 1 - per nulla efficiente ** esegue una query per ogni coppia di vertici */
		for (ArtObject aop : this.artObjects) {
			for (ArtObject aoa : this.artObjects) {
				if (!aop.equals(aoa) && aop.getId() < aoa.getId()) { // escludo coppie (ao, ao) per escludere i loop
					int peso = exhibitionComuni(aop, aoa);

					if (peso != 0) {
						// DefaultWeightedEdge e = this.graph.addEdge(aop, aoa) ;
						// graph.setEdgeWeight(e, peso);
						System.out.format("(%d, %d) peso %d\n", aop.getId(), aoa.getId(), peso);

						Graphs.addEdge(this.graph, aop, aoa, peso);
					}
				}
			}
		}
		/* FINE VERSIONE 1 */
	}

	private int exhibitionComuni(ArtObject aop, ArtObject aoa) {
		ArtsmiaDAO dao = new ArtsmiaDAO();

		int comuni = dao.contaExhibitionComuni(aop, aoa);

		return comuni;
	}

}
