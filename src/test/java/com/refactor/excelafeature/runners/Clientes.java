package com.refactor.excelafeature.runners;

import com.refactor.excelafeature.util.excelfeature.BeforeSuite;
import com.refactor.excelafeature.util.excelfeature.DatosAlFeature;
import com.refactor.excelafeature.util.excelfeature.RunnerPersonalizado;
import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import org.junit.runner.RunWith;

@RunWith(RunnerPersonalizado.class)
@CucumberOptions(
		features="src/test/resources/features/clientes.feature",
		glue="com.refactor.excelafeature.stepdefinitions",
		snippets=SnippetType.CAMELCASE)

public class Clientes {

	private Clientes(){
		throw new IllegalStateException("Utility class");
	}

	@BeforeSuite
	public static void test() {
		DatosAlFeature.extraerDatosDeExcel().sobreEscribirElArchivoFeature("./src/test/resources/features/clientes.feature");
	}
}
