package it.polito.tdp.gosales.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.gosales.dao.GOsalesDAO;

public class Model {
	private GOsalesDAO dao;
	private Graph <Retailers, DefaultWeightedEdge> grafo;
	Map<Integer, Retailers> retailersIdMap; //mi rendo conto che serve perchè gli id dei retailer sono dei numeri
	
	public Model() {
		this.dao=new GOsalesDAO();
		//le idmap vanno creato sempre nel costruttore del model
		this.retailersIdMap=new HashMap<Integer, Retailers>();
		
		//quando costruisco le mappe l'algoritmo è questo, tramite una lista prendo tutti gli elementi con un metodo del dao 
		//poi un elemento alla volta popolo la mappa con identificativo e oggetto
		//questo è un altro modo per popolare la mappa 
		//facendolo nel costruttore del mode, ogni voltra che si apre l'applicazione si crea un nuovo model e quindi una nuova mappa
		
		
		//COPIA PEZZO SE TI SERVE LA MAPPA DEI RETAILERS
		
		List<Retailers> retailers=this.dao.getAllRetailers();
		for(Retailers r: retailers) {
			this.retailersIdMap.put(r.getCode(), r); 
		}
		

	}

	//mi serve un metodo che dal dao si prende le nazioni per popolare la prima combobox (primo parametro del grafo)
	public List<String> getAllCountries(){
		List<String> countries=this.dao.getAllCountries();
		
		return countries;
	}
	
	public void creaGrafo( String nazione, Integer anno, Integer nminimo) {
		//creiamo un nuovo grafo
		//conviene crearlo qui il grafo perchè poi ogni volta che viene richiamato dal controller ne viene grato una nuovo 
		//e non ho sovrapposizione di vertici e archi con quello precedente
		this.grafo=new SimpleWeightedGraph<Retailers, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		//Aggiungiamo i vertici
		
		List<Retailers> vertici=this.dao.getVertici(nazione);
		
		for(Retailers r: vertici) {
			Graphs.addAllVertices(this.grafo, vertici);
		}
		
		//aggiungiamo gli archi
		//la query degli archi ci restituisce i codici dei retailers e il numero di prodotti che vendono in comune in quell anno
		//ricaviamo quindi i rispettivi oggetti dalla mappa che abbiamo creato
		List<Arco> archi=this.dao.getArchi(  nazione, anno, nminimo);
		for(Arco a: archi){
			Retailers r1=this.retailersIdMap.get(a.getRcode1());
			Retailers r2=this.retailersIdMap.get(a.getRcode2()); //con questa sintassi prendo l'oggetto arco e dico alla mappa di prendermi gli oggetti corrispondenti ai codici di ogni oggetto
			
			
			int peso=a.getNcomune();
			
			Graphs.addEdge(this.grafo, r1, r2, peso); //sono passata dalla mappa perchè per poter assegnare un arco devo avere gli oggetti rivenditori
			//ma essendo che la query mi dava come risultato i codici sono passata dalla mappa
		}
	}
	
	
	//metodo che mi serve per stampare e asschermo la lista dei rivenditori ordinati alfabeticamente in senso crescente
	public List <Retailers> getVertici(){
		return new ArrayList<Retailers>(this.grafo.vertexSet());
	}
	
	//metodo che mi serve per stampare a schermo gli archi 
	//lo faccio perche la query mi da i codici
	public List<ArcoEsteso> getArchi(){
		List<ArcoEsteso> result=new ArrayList<ArcoEsteso>();
		
		//per ogni arco che gia ho del grafo corrente aggiungo al risultato i due vertici e il peso corrispondente
		//ArcoEsteso è una classe che mi serviva per facilitare la stampa a schermo degli altri nel format che ciedeva il testo
		for(DefaultWeightedEdge e: this.grafo.edgeSet()) {
			result.add(new ArcoEsteso(this.grafo.getEdgeSource(e), this.grafo.getEdgeTarget(e), (int)(this.grafo.getEdgeWeight(e))));
		}
		Collections.sort(result);
		
		return result;
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	
	public StatsConnessa analizzaCOmponente(Retailers r) {
		//a partire dal vertice selezionato dall'utente troviamo la componente connessa col COnnecitivityInspector
		ConnectivityInspector<Retailers,DefaultWeightedEdge > inspector=new ConnectivityInspector<Retailers, DefaultWeightedEdge> (this.grafo);
		Set<Retailers> connessi=inspector.connectedSetOf(r);
		
		//giacche calcolo anche la somma dei peis degli archi della componente
		int peso=0;
		
		//ALGORITMO PER POPOLARE LA COMPONENTE CONNESSA A CUI APPARTIENE UN VERTICE: 
		//per ogni arco del grafo corrente controllo che un arco specifico faccia parte dell'insieme deii vertici della componente connessa del vertice fatto selezionare dall'utente
		//in caso affermativo allora chiedo al grafo di restituirmi i due vertici di tale grafo e il peso dell'arco
		
		//ATTENZIONE: faccio questo algoritmo perchè cosi posso chiedere direttamente al grafo di restituirmi il peso di ogni arco
		//è piu semplice controllare quali tra tutti gli archi del grafo siano presenti nel set degli archi della componente connessa del vertice scelto
		for(DefaultWeightedEdge e: this.grafo.edgeSet()) {
			if(connessi.contains(this.grafo.getEdgeSource(e)) && connessi.contains(this.grafo.getEdgeTarget(e))){
				peso=peso+(int)this.grafo.getEdgeWeight(e);
			}
		}
		
		StatsConnessa result=new StatsConnessa(connessi, peso);
		
		return result;
	}
	
	
}
