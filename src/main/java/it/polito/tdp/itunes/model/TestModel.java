package it.polito.tdp.itunes.model;

public class TestModel {
	
	public static void main(String [] args) {
			Model model=new Model();
			
			model.creaGrafo(18);
			Album a1=model.getVertices().get(4);
			Album a2=model.getVertices().get(10);
			model.getPath(a1, a2, 5);
		
	}

}
