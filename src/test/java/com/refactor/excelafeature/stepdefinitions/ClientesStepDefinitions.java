package com.refactor.excelafeature.stepdefinitions;

import cucumber.api.java.es.Dado;

import java.util.List;
import java.util.logging.Logger;

public class ClientesStepDefinitions {

	private static final Logger LOGGER = Logger.getLogger(ClientesStepDefinitions.class.getName());

	@Dado("realiza el ingreso del cliente")
	public void ingresarCliente(List<String> datos){
		LOGGER.info("Cliente ingresado! ["+datos.get(3)+"]");
	}
}
