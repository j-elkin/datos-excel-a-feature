package com.refactor.excelafeature.stepdefinitions;

import cucumber.api.java.es.Dado;

import java.util.List;

public class ClientesStepDefinitions {

	@Dado("realiza el ingreso del cliente")
	public void ingresarCliente(List<String> datos){
		System.out.println("Cliente ingresado! ["+datos.get(3)+"]");
	}
}
