package it.polito.tdp.gosales.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.gosales.model.Arco;
import it.polito.tdp.gosales.model.DailySale;
import it.polito.tdp.gosales.model.Products;
import it.polito.tdp.gosales.model.Retailers;

public class GOsalesDAO {
	
	
	/**
	 * Metodo per leggere la lista di tutti i rivenditori dal database
	 * @return
	 */

	public List<Retailers> getAllRetailers(){
		String query = "SELECT * from go_retailers";
		List<Retailers> result = new ArrayList<Retailers>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Retailers(rs.getInt("Retailer_code"), 
						rs.getString("Retailer_name"),
						rs.getString("Type"), 
						rs.getString("Country")));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}
	
	
	/**
	 * Metodo per leggere la lista di tutti i prodotti dal database
	 * @return
	 */
	public List<Products> getAllProducts(){
		String query = "SELECT * from go_products";
		List<Products> result = new ArrayList<Products>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Products(rs.getInt("Product_number"), 
						rs.getString("Product_line"), 
						rs.getString("Product_type"), 
						rs.getString("Product"), 
						rs.getString("Product_brand"), 
						rs.getString("Product_color"),
						rs.getDouble("Unit_cost"), 
						rs.getDouble("Unit_price")));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}

	
	/**
	 * Metodo per leggere la lista di tutte le vendite nel database
	 * @return
	 */
	public List<DailySale> getAllSales(){
		String query = "SELECT * from go_daily_sales";
		List<DailySale> result = new ArrayList<DailySale>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new DailySale(rs.getInt("retailer_code"),
				rs.getInt("product_number"),
				rs.getInt("order_method_code"),
				rs.getTimestamp("date").toLocalDateTime().toLocalDate(),
				rs.getInt("quantity"),
				rs.getDouble("unit_price"),
				rs.getDouble("unit_sale_price")  ));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	///query per popolare la tendina con tutte le nazioni dei rivenditori
	public List <String> getAllCountries(){
		String sql="SELECT DISTINCT Country "
				+ "FROM go_retailers "
				+ "ORDER BY Country";
		List<String> result=new ArrayList<String> ();
		try {
			
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {
				result.add(rs.getString("Country"));
			}

			conn.close();
			return result;
			
		}catch(SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	//query per i vertici
	public List<Retailers> getVertici(String nazione){
		String sql="SELECT * "                                //seleziono tutto perche voglio una lista di retailers quindi poi usero quel costruttore
				+ "FROM go_retailers "
				+ "WHERE Country=?";
		
		List<Retailers> result=new ArrayList<Retailers> ();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, nazione);
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {
				result.add(new Retailers(rs.getInt("Retailer_code"), 
						rs.getString("Retailer_name"),
						rs.getString("Type"), 
						rs.getString("Country")));
			}
			
			conn.clearWarnings();
			return result;
			
		}catch(SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	//query per gli archi
	//la query mi restituisce i codici della coppia di retailers ed il numero di prodotti in comune
	public List<Arco> getArchi( String nazione, int anno, int nmin){
		String sql="SELECT r1.Retailer_code AS rCode1, r2.Retailer_code AS rCode2, COUNT(DISTINCT s1.Product_number) AS N " //perchè cosi avro in ouput entrambi i codici ed il numero di prodotti che vendono in comune
				+ "FROM go_retailers r1,  go_retailers r2, go_daily_sales s1, go_daily_sales s2 "
				+ "WHERE r1.Country=? AND r1.Country=r2.Country  "
				+ "AND r1.Retailer_code=s1.Retailer_code AND r2.Retailer_code=s2.Retailer_code "                           //è una doppia join tra due copie di due tabelle
				+ "AND s1.Product_number=s2.Product_number  "                                                              //condizione percui vendono lo stesso prodotto
				+ "AND r1.Retailer_code<r2.Retailer_code "                                                                 //condizione per cui nella coppia di rivenditori r1 è divero da r2
				+ "AND YEAR(s1.Date)=? "                                                                                   //filtro sull'anno, c'era anche nella query per gli archi
				+ "AND YEAR(s1.Date)=YEAR(s2.Date) "                                            
				+ "GROUP BY r1.Retailer_code, r2.Retailer_code "
				+ "having N> ?";                                                                                           //la clausola HAVING sta sempre per ultima, non se c'è l'ORDER BY, in quel caso si metter per penultima
	//NOTA BENE: se la query per gli archi dovesse avere tre filtri e/o richiedere la join di tre tabelle prova prova ad usare questo schema	
		
		
		List<Arco> result=new ArrayList<Arco>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
		
	
			st.setString(1, nazione);
			st.setInt(2, anno);
			st.setInt(3, nmin);
			
			ResultSet rs=st.executeQuery();
			
			while(rs.next()) {
				result.add(new Arco(rs.getInt("rCode1"), rs.getInt("rCode2"), rs.getInt("N")));
				//NOTA BENE: quando in casi come questo, l'output che mi restituisce gli archi è formato da una un format diverso,
				//allora è conveniente creare un'altra classe dove ogni oggetto ha gli attributi(se è possibile) che mi servonosss
			}
			
			conn.close();
			return result;
		}catch(SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
				
	}
	
	
}
