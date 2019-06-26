package it.polito.tdp.artsmia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.artsmia.model.ArtObject;

public class ArtsmiaDAO {

	public List<ArtObject> listObjects(Map<Integer, ArtObject> idMap) {
		
		String sql = "SELECT * from objects";
		List<ArtObject> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				ArtObject artObj = new ArtObject(res.getInt("object_id"), res.getString("classification"), res.getString("continent"), 
						res.getString("country"), res.getInt("curator_approved"), res.getString("dated"), res.getString("department"), 
						res.getString("medium"), res.getString("nationality"), res.getString("object_name"), res.getInt("restricted"), 
						res.getString("rights_type"), res.getString("role"), res.getString("room"), res.getString("style"), res.getString("title"));
				
				idMap.put(res.getInt("object_id"), artObj);
				result.add(artObj);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void pesoVertici(SimpleWeightedGraph<ArtObject, DefaultWeightedEdge> grafo, Map<Integer, ArtObject> idMap) {
		
		String sql = "SELECT eo1.object_id AS o1, eo2.object_id AS o2, COUNT(eo1.exhibition_id) AS num " + 
				"FROM exhibition_objects eo1, exhibition_objects eo2 " + 
				"WHERE eo1.object_id < eo2.object_id AND eo2.exhibition_id = eo1.exhibition_id " + 
				"GROUP BY eo1.object_id, eo2.object_id ";
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				ArtObject a1 = idMap.get(res.getInt("o1"));
				ArtObject a2 = idMap.get(res.getInt("o2"));
				double peso = res.getDouble("num");
				
				if(grafo.containsVertex(a1) && grafo.containsVertex(a2)) {
					grafo.addEdge(a1, a2);
					DefaultWeightedEdge e = grafo.getEdge(a1, a2);
					grafo.setEdgeWeight(e, peso);
				}
			}
			
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
}
