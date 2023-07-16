package it.polito.tdp.gosales;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.gosales.model.ArcoEsteso;
import it.polito.tdp.gosales.model.Model;
import it.polito.tdp.gosales.model.Products;
import it.polito.tdp.gosales.model.Retailers;
import it.polito.tdp.gosales.model.StatsConnessa;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnAnalizzaComponente;

    @FXML
    private Button btnCreaGrafo;

    @FXML
    private Button btnSimula;

    @FXML
    private ComboBox<Integer> cmbAnno;

    @FXML
    private ComboBox<String> cmbNazione;

    @FXML
    private ComboBox<Products> cmbProdotto;

    @FXML
    private ComboBox<Retailers> cmbRivenditore;

    @FXML
    private TextArea txtArchi;

    @FXML
    private TextField txtN;

    @FXML
    private TextField txtNProdotti;

    @FXML
    private TextField txtQ;

    @FXML
    private TextArea txtResult;

    @FXML
    private TextArea txtVertici;

    @FXML
    void doAnalizzaComponente(ActionEvent event) {
    	//prendo il retailer da analizzare dalla combobox
    	Retailers r=this.cmbRivenditore.getValue();
    	Integer anno=this.cmbAnno.getValue();
    	if(r==null) {
    		this.txtResult.appendText("Selezionare un rivenditore");
    		return;
    	}
    	if(anno==null) {
    		this.txtResult.appendText("Selezionare un anno");
    		return;
    	}
    	
    	StatsConnessa result=this.model.analizzaCOmponente(r); //ovviamente è una sola componente connessa
    	this.txtResult.appendText("La componente connessa di "+ r+ " ha dimensione"+ result.getRetailers().size()+"\n");
    	this.txtResult.appendText("Il peso totale della componente connessa è "+result.getPeso()+"\n");

    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
		
 		//bisogna controllare l'input di testo, ricorda che la lettura scatena un'eccezione 		
 		//prendo la nazione e l anno dalle combobox e controllo
 		
 		String nazione=this.cmbNazione.getValue(); //gli passo una stringa, non ho bisogno di passargli un oggetto
 		if(nazione==null) {
 			this.txtResult.appendText("e' necessario selezionare una nazione");
 			return;
 		}
 		Integer anno=this.cmbAnno.getValue();
 		if(anno==null) {
 			this.txtResult.appendText("e' necessario selezionare un anno");
 			return;
 		}
 		int nminimo=0;
 		try {
 			nminimo=Integer.parseInt(this.txtNProdotti.getText());
 			
 		}catch(NumberFormatException e) {
 			this.txtResult.appendText("Bisogna inserire un numero intero non negativo");
 			return;
 		}
 		if(nminimo<0) {
 			this.txtResult.appendText("Bisogna inserire un numero non negativo");
 		}
 		
 		//se siamo qui possiamo creare il grafo
 		this.model.creaGrafo( nazione, anno, nminimo);
 		
 		this.txtResult.appendText("Grafo creato con: ");
 		this.txtResult.appendText("#Vertici: "+this.model.nVertici()+"\n");
 		this.txtResult.appendText("#Archi: "+this.model.nArchi()+"\n");
 		
 		//popolare la combobox del rivenditore
 		this.cmbRivenditore.getItems().clear();           //pulisco dalla combobox dagli elementi che aveva in un eventuale grafo precendente
 		List<Retailers> vertici=this.model.getVertici();  //il getVertici() che mi restituiva la lista serviva infatti per popolare la seconda combobox 
 		this.cmbRivenditore.getItems().addAll(vertici);   //usa sempre questo algoritmo 
 		
 		//ora disabilito le componenti
 		this.cmbRivenditore.setDisable(false);           // se i bottono sono settati a false allora sono distabilitati, viceversa col true
 		this.btnAnalizzaComponente.setDisable(false);
 		this.cmbProdotto.setDisable(true);
 		this.btnSimula.setDisable(true);
 		
 		
 		//stampare a schermo i vertici del grafo
 		this.txtVertici.clear();
 		for(Retailers r: vertici) {
 			this.txtVertici.appendText(r+"\n");
 		}
 		
 		//stampo a schermo gli archi
 		List<ArcoEsteso> archi=this.model.getArchi();
 		Collections.sort(archi);
 		
 		//il testo mi dice che vuole stampare a schermo la coppia di vertici e il peso del corrispondente arco
 		for(ArcoEsteso a : archi) {
 			this.txtArchi.appendText(a.getPeso()+": "+a.getR1()+" <--> "+ a.getR2()+"\n");
 		}

    }

    @FXML
    void doSimulazione(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert btnAnalizzaComponente != null : "fx:id=\"btnAnalizzaComponente\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnSimula != null : "fx:id=\"btnSimula\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbAnno != null : "fx:id=\"cmbAnno\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbNazione != null : "fx:id=\"cmbNazione\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbProdotto != null : "fx:id=\"cmbProdotto\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbRivenditore != null : "fx:id=\"cmbRivenditore\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtArchi != null : "fx:id=\"txtArchi\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtN != null : "fx:id=\"txtN\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtNProdotti != null : "fx:id=\"txtNProdotti\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtQ != null : "fx:id=\"txtQ\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtVertici != null : "fx:id=\"txtVertici\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    	//popolo la tendine degli anni
    	this.cmbAnno.getItems().add(2015);
     	this.cmbAnno.getItems().add(2016);
     	this.cmbAnno.getItems().add(2017);
     	this.cmbAnno.getItems().add(2018);
     	
     	//popolo la tendina delle nazioni
     	List<String> nazioni=this.model.getAllCountries();
     	Collections.sort(nazioni); //le posso ordinare perche sono stringhe e le stringhe sanno gia come ordinarsi alfabeticamente
     		this.cmbNazione.getItems().addAll(nazioni);
     		
     
     		
     	
    }

}
