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
 * Ingresa los datos obtenidos del archivo de Excel al archivo feature del cual
 * se está llamando
 * 
 * @since 27/11/2017
 * @author bgaona
 *
 */
public class DataToFeatureV2 {

	private static final Logger LOGGER = Logger.getLogger(DataToFeatureV2.class.getName());

	private static String filaDelFeature;
	private static List<Map<String, String>> datosDeExcel;
	private static boolean omitirFilaDelFeature;
	private static String[] dataVectorRango;
	private static int filaSeleccionada;
	private static int pos;
	private static boolean etiquetaEncontrada = false;

	private static List<String> datosDelFeature = new ArrayList<String>();


	private DataToFeatureV2(){
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Ingresa los datos obtenidos de un excel al archivo .feature del cual se está
	 * llamando, hace que se genere la tabla en el escenario Outline como Data Table
	 * 
	 * @since 27/11/2017
	 * @author bgaona
	 * @param featureFile
	 *            Nombre del archivo .feature el cual se modificará, debe tener la
	 *            ruta del archivo y la hoja ser usada
	 * @return
	 * @throws InvalidFormatException
	 * @throws IOException
	 */

	private static List<String> sobreEscribirArchivoFeature(File featureFile) {

		try (BufferedReader buffReader = new BufferedReader(
				new InputStreamReader(new BufferedInputStream(new FileInputStream(featureFile)), "UTF-8"))) {

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


	private static void agregarDatosDelExcelAlFeature() {
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
						agregarUnRangoDeFilasDelExcelAlFeature();

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

	private static void agregarUnaFilaDelExcelAlFeature(){
		StringBuilder allCellData = new StringBuilder();
		for (Entry<String, String> mapData : datosDeExcel.get(filaSeleccionada).entrySet()) {
			allCellData.append("	|	" + mapData.getValue());
		}
		datosDelFeature.add(allCellData.toString() + "	|");
	}

	private static void agregarUnRangoDeFilasDelExcelAlFeature(){
		for (int rowNumber = filaSeleccionada; rowNumber < datosDeExcel.size() - 1; rowNumber++) {
			StringBuilder allCellData = new StringBuilder();
			for (Entry<String, String> mapData : datosDeExcel.get(rowNumber).entrySet()) {
				if (rowNumber < Integer.parseInt(dataVectorRango[1])) {
					allCellData.append("	|	" + mapData.getValue());
				}
			}
			datosDelFeature.add(allCellData.toString() + "	|");

			if (rowNumber + 1 == Integer.parseInt(dataVectorRango[1])) {
				rowNumber = datosDeExcel.size() - 1;
			}
		}
	}

	private static void agregarFilasEspecificasDelExcelAlFeature(){
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
				rowNumber = datosDeExcel.size() - 1;
			}
		}
	}

	private static void agregarTodasLasFilasDelExcelAlFeature() {
		for (int rowNumber = 0; rowNumber < datosDeExcel.size() - 1; rowNumber++) {
			StringBuilder allCellData = new StringBuilder();
			for (Entry<String, String> mapData : datosDeExcel.get(rowNumber).entrySet()) {
				allCellData.append("	|	" + mapData.getValue());
			}
			datosDelFeature.add(allCellData.toString() + "	|");

		}
	}

	/**
	 * Lista de todos los features con sus respectivos archivo de excel que se
	 * usarán en la prueba
	 * 
	 * @since 27/11/2017
	 * @author bgaona
	 * @param folder
	 *            Carpeta donde estarán los archivo .feature
	 * @return
	 */
	private static List<File> listOfFeatureFiles(File folder) {
		List<File> featureFiles = new ArrayList<File>();
		if (folder.getName().endsWith(".feature")) {
			featureFiles.add(folder);
		} else {
			for (File fileEntry : folder.listFiles()) {
				if (fileEntry.isDirectory()) {
					featureFiles.addAll(listOfFeatureFiles(fileEntry));
				} else {
					if (fileEntry.isFile() && fileEntry.getName().endsWith(".feature")) {
						featureFiles.add(fileEntry);
					}
				}
			}
		}
		return featureFiles;
	}

	/**
	 * Hace una lista con todos los features dependiendo de la ruta asignada
	 * 
	 * @since 27/11/2017
	 * @author bgaona
	 * @param featuresDirectoryPath
	 *            Ruta donde se encuentran los features que tendrán las tablas
	 * @throws IOException
	 * @throws InvalidFormatException
	 */
	public static void overrideFeatureFiles(String featuresDirectoryPath) throws IOException, InvalidFormatException {
		List<File> listOfFeatureFiles = listOfFeatureFiles(new File(featuresDirectoryPath));
		for (File featureFile : listOfFeatureFiles) {
			List<String> featureWithExcelData = sobreEscribirArchivoFeature(featureFile);
			try (BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(featureFile), "UTF-8"));) {
				for (String string : featureWithExcelData) {
					writer.write(string);
					writer.write("\n");
				}
			}
		}
	}
}
