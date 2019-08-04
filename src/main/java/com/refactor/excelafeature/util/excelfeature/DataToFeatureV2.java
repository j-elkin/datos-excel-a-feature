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

	private static String datos;
	private static List<Map<String, String>> datosExcel = null;
	private static boolean foundHashTag = false;
	private static boolean featureData = false;
	private static boolean esUnRango = false;
	private static boolean esMultiple = false;
	private static boolean esRangoDefinido = false;

	private static String[] dataVector;
	private static String[] dataVectorRango;
	private static String sheetName;
	private static String excelFilePath;
	private static int filaSeleccionada;
	private static int pos = 0;


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

	private static List<String> setExcelDataToFeature2(File featureFile) {
		List<String> fileData = new ArrayList<String>();
		try (BufferedReader buffReader = new BufferedReader(
				new InputStreamReader(new BufferedInputStream(new FileInputStream(featureFile)), "UTF-8"))) {

			while ((datos = buffReader.readLine()) != null) {
				dataVector = null;
				dataVectorRango = null;
				sheetName = null;
				excelFilePath = null;
				filaSeleccionada = 0;
				pos = 0;

				if (datos.trim().contains("##@externaldata")) {
					dataVector = datos.trim().split("@");
					excelFilePath = dataVector[2];
					sheetName = dataVector[3];
					if (dataVector.length == 4) {
						esUnRango = true;
					}
					if (dataVector.length == 5) {
						if (dataVector[4].toString().contains("-")) {
							dataVectorRango = dataVector[4].trim().split("-");
							esRangoDefinido = true;
							filaSeleccionada = Integer.parseInt(dataVectorRango[pos]) - 1;
						} else if (dataVector[4].toString().contains(",")) {
								dataVectorRango = dataVector[4].trim().split(",");
								esUnRango = true;
								esMultiple = true;
								filaSeleccionada = Integer.parseInt(dataVectorRango[pos]) - 1;
						} else {
								filaSeleccionada = Integer.parseInt(dataVector[4]) - 1;
						}
					}
					foundHashTag = true;
					fileData.add(datos);
				}
				if (foundHashTag) {
					datosExcel = new LectorExcel().getData(excelFilePath, sheetName);
					System.out.println(datosExcel.toString());
					for (int rowNumber = filaSeleccionada; rowNumber < datosExcel.size() - 1; rowNumber++) {
						StringBuilder allCellData = new StringBuilder();
						for (Entry<String, String> mapData : datosExcel.get(rowNumber).entrySet()) {
							if (dataVectorRango == null) {
								allCellData.append("   |" + mapData.getValue());
							} else {
								if (esRangoDefinido) {
									if (rowNumber < Integer.parseInt(dataVectorRango[1])) {
										allCellData.append("   |" + mapData.getValue());
									}
								} else {
									if (rowNumber + 1 == Integer.parseInt(dataVectorRango[pos]) && esUnRango) {
										allCellData.append("   |" + mapData.getValue());
									}
								}
							}
						}
						fileData.add(allCellData.toString() + "|");
						if (!esUnRango && !esRangoDefinido) {
								rowNumber = datosExcel.size();
						}
						if (esMultiple) {
							if (pos + 1 < dataVectorRango.length) {
								filaSeleccionada = Integer.parseInt(dataVectorRango[pos + 1]) - 1;
								rowNumber = filaSeleccionada - 1;
								pos++;
							} else {
								rowNumber = datosExcel.size() - 1;
							}
						}
						if (esRangoDefinido) {
							if (rowNumber + 1 == Integer.parseInt(dataVectorRango[1])) {
								rowNumber = datosExcel.size() - 1;
								pos++;
							} else {
								pos++;
							}
						}
					}
					foundHashTag = false;
					featureData = true;
					continue;
				}
				if (datos.startsWith("|") || datos.endsWith("|")) {
					if (featureData) {
						continue;
					} else {
						fileData.add(datos);
						continue;
					}
				} else {
					featureData = false;
				}
				fileData.add(datos);
			}
		} catch (UnsupportedEncodingException e) {
			LOGGER.severe(LoggerApp.getStackTrace(e));
		} catch (FileNotFoundException e) {
			LOGGER.severe(LoggerApp.getStackTrace(e));
		} catch (IOException e) {
			LOGGER.severe(LoggerApp.getStackTrace(e));
		} catch (InvalidFormatException e) {
			LOGGER.severe(LoggerApp.getStackTrace(e));
		}
		return fileData;
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
			List<String> featureWithExcelData = setExcelDataToFeature2(featureFile);
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
