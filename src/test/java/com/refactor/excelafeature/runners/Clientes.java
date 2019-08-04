package com.refactor.excelafeature.runners;

import com.refactor.excelafeature.util.excelfeature.BeforeSuite;
import com.refactor.excelafeature.util.excelfeature.DataToFeatureV2;
import com.refactor.excelafeature.util.excelfeature.RunnerPersonalizado;
import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(RunnerPersonalizado.class)
@CucumberOptions(
		features="src/test/resources/features/clientes.feature",
		glue="co.com.bancolombia.sucursales.stepdefinitions",
		snippets=SnippetType.CAMELCASE)

public class Clientes {

	private Clientes(){
		throw new IllegalStateException("Utility class");
	}

	@BeforeSuite
	public static void test() throws InvalidFormatException, IOException {
		DataToFeatureV2.overrideFeatureFiles("./src/test/resources/features/clientes.feature");
	}
}
