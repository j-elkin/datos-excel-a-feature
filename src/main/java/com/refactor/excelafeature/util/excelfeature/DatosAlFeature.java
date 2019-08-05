package com.refactor.excelafeature.util.excelfeature;

import com.refactor.excelafeature.util.LoggerApp;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

/**
 * Ingresa los datos obtenidos del archivo de Excel al archivo feature del cual se está llamando
 * 
 * @since 27/11/2017
 * @author bgaona
 * <br>
 * <b>Refactorizado por:</b> jerendon (04/08/2019)
 *
 */
public class DatosAlFeature {

	private static final Logger LOGGER = Logger.getLogger(DatosAlFeature.class.getName());

	private List<Map<String, String>> datosDeExcel;
	private List<String> datosDelFeature;
	private String filaDelFeature;
	private boolean omitirFilaDelFeature;
	private String[] dataVectorRango;
	private int filaSeleccionada;
	private int pos;
	private boolean etiquetaEncontrada;


	public DatosAlFeature(){
		datosDelFeature = new ArrayList<String>();
		etiquetaEncontrada = false;
	}

	public static DatosAlFeature extraerDatosDeExcel(){
		return new DatosAlFeature();
	}


	public void sobreEscribirElArchivoFeature(String rutaDelArchivoFeature)  {
		File archivoFeature = new File(rutaDelArchivoFeature);
		List<String> featureConDatosDeExcel = obtenerFeatureConDatosDeExcel(archivoFeature);
		try (BufferedWriter escribirEnElFeature = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(archivoFeature), "UTF-8"));) {
			for (String linea : featureConDatosDeExcel) {
				escribirEnElFeature.write(linea);
				escribirEnElFeature.write("\n");
			}
		} catch (IOException e) {
			LOGGER.severe(LoggerApp.getStackTrace(e));
		}
	}


	private List<String> obtenerFeatureConDatosDeExcel(File archivoFeature) {

		try (BufferedReader lectorFeature = new BufferedReader(
				new InputStreamReader(new BufferedInputStream(new FileInputStream(archivoFeature)), "UTF-8"))) {

			while ((filaDelFeature = lectorFeature.readLine()) != null) {

				agregarDatosDelExcelAlFeature();

				if (etiquetaEncontrada) {
					etiquetaEncontrada = false;
					omitirFilaDelFeature = true;//Se omite la siguiente fila

				} else if ( filaDelFeature.trim().startsWith("|") && filaDelFeature.trim().endsWith("|") && !omitirFilaDelFeature) {
					datosDelFeature.add(filaDelFeature);

				} else if( !filaDelFeature.trim().startsWith("|") && !filaDelFeature.trim().endsWith("|") ) {
					datosDelFeature.add(filaDelFeature);
					omitirFilaDelFeature = false;
				}
			}
		} catch (IOException e) {
			LOGGER.severe(LoggerApp.getStackTrace(e));
		}
		return datosDelFeature;
	}


	private void agregarDatosDelExcelAlFeature() {
		filaSeleccionada = 0;
		pos = 0;

		if (filaDelFeature.trim().contains("##@externaldata")) {
			String[] dataVector = filaDelFeature.trim().split("@");
			String rutaArchivoExcel = dataVector[2];
			String nombreHojaExcel = dataVector[3];
			datosDeExcel = new LectorExcel().getData(rutaArchivoExcel, nombreHojaExcel);

			if( datosDeExcel == null){
				LOGGER.severe("\n*** No fue posible obtener los datos del archivo Excel. " +
						"Por favor revise si la ruta indicada es correcta o sino tiene el archivo abierto.***\n");

			} else if (dataVector.length == 4 ) {
				datosDelFeature.add(filaDelFeature);
				agregarTodasLasFilasDelExcelAlFeature();
				etiquetaEncontrada = true;

			} else if (dataVector.length == 5) {
				datosDelFeature.add(filaDelFeature);
				if (dataVector[4].contains("-")) {
					dataVectorRango = dataVector[4].trim().split("-");
					filaSeleccionada = Integer.parseInt(dataVectorRango[0]) - 1;
					agregarRangoDeFilasDelExcelAlFeature();

				} else if (dataVector[4].contains(",")) {
					dataVectorRango = dataVector[4].trim().split(",");
					filaSeleccionada = Integer.parseInt(dataVectorRango[0]) - 1;
					agregarFilasEspecificasDelExcelAlFeature();

				} else {
					filaSeleccionada = Integer.parseInt(dataVector[4]) - 1;
					agregarUnaFilaDelExcelAlFeature();
				}
				etiquetaEncontrada = true;
			}

		}

	}

	private void agregarTodasLasFilasDelExcelAlFeature() {
		for (int numeroFila = 0; numeroFila < datosDeExcel.size() - 1; numeroFila++) {
			StringBuilder valoresDeLaFila = new StringBuilder();
			for (Entry<String, String> valorCelda : datosDeExcel.get(numeroFila).entrySet()) {
				valoresDeLaFila.append("	|	" + valorCelda.getValue());
			}
			datosDelFeature.add(valoresDeLaFila.toString() + "	|");

		}
	}

	private void agregarRangoDeFilasDelExcelAlFeature(){
		for (int numeroFila = filaSeleccionada; numeroFila < datosDeExcel.size() - 1; numeroFila++) {
			StringBuilder valoresDeLaFila = new StringBuilder();
			for (Entry<String, String> valorCelda : datosDeExcel.get(numeroFila).entrySet()) {
				if (numeroFila < Integer.parseInt(dataVectorRango[1])) {
					valoresDeLaFila.append("	|	" + valorCelda.getValue());
				}
			}
			datosDelFeature.add(valoresDeLaFila.toString() + "	|");

			if (numeroFila + 1 == Integer.parseInt(dataVectorRango[1])) {
				break;
			}
		}
	}

	private void agregarFilasEspecificasDelExcelAlFeature(){
		for (int numeroFila = filaSeleccionada; numeroFila < datosDeExcel.size() - 1; numeroFila++) {
			StringBuilder valoresDeLaFila = new StringBuilder();
			for (Entry<String, String> valorCelda : datosDeExcel.get(numeroFila).entrySet()) {
				if (numeroFila + 1 == Integer.parseInt(dataVectorRango[pos]) ) {
					valoresDeLaFila.append("	|	" + valorCelda.getValue());
				}
			}
			datosDelFeature.add(valoresDeLaFila.toString() + "	|");

			if (pos + 1 < dataVectorRango.length) {
				filaSeleccionada = Integer.parseInt(dataVectorRango[pos + 1]) - 1;
				numeroFila = filaSeleccionada - 1;
				pos++;
			} else {
				break;
			}
		}
	}

	private void agregarUnaFilaDelExcelAlFeature(){
		StringBuilder valoresDeLaFila = new StringBuilder();
		for (Entry<String, String> valorCelda : datosDeExcel.get(filaSeleccionada).entrySet()) {
			valoresDeLaFila.append("	|	" + valorCelda.getValue());
		}
		datosDelFeature.add(valoresDeLaFila.toString() + "	|");
	}

}
