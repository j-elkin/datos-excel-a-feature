package com.refactor.excelafeature.util.excelfeature;

import com.refactor.excelafeature.util.LoggerApp;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

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


	public void sobreEscribirElArchivoFeature(String rutaDelArchivoFeature) throws IOException, InvalidFormatException {
		File archivoFeature = new File(rutaDelArchivoFeature);
		List<String> featureConDatosDeExcel = obtenerFeatureConDatosDeExcel(archivoFeature);
		try (BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(archivoFeature), "UTF-8"));) {
			for (String string : featureConDatosDeExcel) {
				writer.write(string);
				writer.write("\n");
			}
		}
	}


	private List<String> obtenerFeatureConDatosDeExcel(File archivoFeature) {

		try (BufferedReader buffReader = new BufferedReader(
				new InputStreamReader(new BufferedInputStream(new FileInputStream(archivoFeature)), "UTF-8"))) {

			while ((filaDelFeature = buffReader.readLine()) != null) {

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
		} catch (UnsupportedEncodingException e) {
			LOGGER.severe(LoggerApp.getStackTrace(e));
		} catch (FileNotFoundException e) {
			LOGGER.severe(LoggerApp.getStackTrace(e));
		} catch (IOException e) {
			LOGGER.severe(LoggerApp.getStackTrace(e));
		}
		return datosDelFeature;
	}


	private void agregarDatosDelExcelAlFeature() {
		filaSeleccionada = 0;
		pos = 0;

		try {
			if (filaDelFeature.trim().contains("##@externaldata")) {
				datosDelFeature.add(filaDelFeature);
				String[] dataVector = filaDelFeature.trim().split("@");
				String excelFilePath = dataVector[2];
				String sheetName = dataVector[3];
				datosDeExcel = new LectorExcel().getData(excelFilePath, sheetName);

				if (dataVector.length == 4) {
					agregarTodasLasFilasDelExcelAlFeature();

				} else if (dataVector.length == 5) {
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
				}
				etiquetaEncontrada = true;

			}
		} catch (IOException e) {
			LOGGER.severe(LoggerApp.getStackTrace(e));
		} catch (InvalidFormatException e) {
			LOGGER.severe(LoggerApp.getStackTrace(e));
		}
	}

	private void agregarTodasLasFilasDelExcelAlFeature() {
		for (int rowNumber = 0; rowNumber < datosDeExcel.size() - 1; rowNumber++) {
			StringBuilder allCellData = new StringBuilder();
			for (Entry<String, String> mapData : datosDeExcel.get(rowNumber).entrySet()) {
				allCellData.append("	|	" + mapData.getValue());
			}
			datosDelFeature.add(allCellData.toString() + "	|");

		}
	}

	private void agregarRangoDeFilasDelExcelAlFeature(){
		for (int rowNumber = filaSeleccionada; rowNumber < datosDeExcel.size() - 1; rowNumber++) {
			StringBuilder allCellData = new StringBuilder();
			for (Entry<String, String> mapData : datosDeExcel.get(rowNumber).entrySet()) {
				if (rowNumber < Integer.parseInt(dataVectorRango[1])) {
					allCellData.append("	|	" + mapData.getValue());
				}
			}
			datosDelFeature.add(allCellData.toString() + "	|");

			if (rowNumber + 1 == Integer.parseInt(dataVectorRango[1])) {
				break;
			}
		}
	}

	private void agregarFilasEspecificasDelExcelAlFeature(){
		for (int rowNumber = filaSeleccionada; rowNumber < datosDeExcel.size() - 1; rowNumber++) {
			StringBuilder allCellData = new StringBuilder();
			for (Entry<String, String> mapData : datosDeExcel.get(rowNumber).entrySet()) {
				if (rowNumber + 1 == Integer.parseInt(dataVectorRango[pos]) ) {
					allCellData.append("	|	" + mapData.getValue());
				}
			}
			datosDelFeature.add(allCellData.toString() + "	|");

			if (pos + 1 < dataVectorRango.length) {
				filaSeleccionada = Integer.parseInt(dataVectorRango[pos + 1]) - 1;
				rowNumber = filaSeleccionada - 1;
				pos++;
			} else {
				break;
			}
		}
	}

	private void agregarUnaFilaDelExcelAlFeature(){
		StringBuilder allCellData = new StringBuilder();
		for (Entry<String, String> mapData : datosDeExcel.get(filaSeleccionada).entrySet()) {
			allCellData.append("	|	" + mapData.getValue());
		}
		datosDelFeature.add(allCellData.toString() + "	|");
	}

}
