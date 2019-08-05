package com.refactor.excelafeature.util.excelfeature;

import com.refactor.excelafeature.util.LoggerApp;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class LectorExcel {

	private static final Logger LOGGER = Logger.getLogger(LectorExcel.class.getName());

	/**
	 * Obtiene los datos de un archivo de excel, teniendo en cuenta el nombre de la
	 * hoja
	 * 
	 * @param excelFilePath
	 *            Ruta del libro de excel
	 * @param sheetName
	 *            Nombre de la hoja que contiene los datos
	 * @return
	 * @throws InvalidFormatException
	 *             Manejo de error por formato inválido
	 * @throws IOException
	 *             Manejo de error para el proceso de entrada y salida de datos
	 */
	public List<Map<String, String>> getData(String excelFilePath, String sheetName)  {
		List<Map<String, String>> datosExcel = null;
		try {
			Sheet sheet = getSheetByName(excelFilePath, sheetName);
			datosExcel = readSheet(sheet);
		} catch (IOException e) {
			LOGGER.severe(LoggerApp.getStackTrace(e));
		} catch (InvalidFormatException e) {
			LOGGER.severe(LoggerApp.getStackTrace(e));
		}
		return datosExcel;
	}

	/**
	 * Obtiene los datos de un archivo de excel, teniendo en cuenta el numero de la
	 * hoja
	 * 
	 * @param excelFilePath
	 *            Ruta del libro de excel
	 * @param sheetNumber
	 *            Nombre de la hoja que contiene los datos
	 * @return
	 * @throws InvalidFormatException
	 *             Manejo de error por formato inválido
	 * @throws IOException
	 *             Manejo de error para el proceso de entrada y salida de datos
	 */
	public List<Map<String, String>> getData(String excelFilePath, int sheetNumber)
			throws InvalidFormatException, IOException {
		Sheet sheet = getSheetByIndex(excelFilePath, sheetNumber);
		return readSheet(sheet);
	}

	/**
	 * Obtiene la hoja de trabajo donde se encuentran los datos de acuerdo a la ruta
	 * del archivo
	 *
	 * @param excelFilePath
	 *            Ruta del libro de excel
	 * @param sheetName
	 *            Nombre de la hoja que contiene los datos
	 * @return
	 * @throws IOException
	 * @throws InvalidFormatException
	 */
	private Sheet getSheetByName(String excelFilePath, String sheetName) throws IOException, InvalidFormatException {
		return getWorkBook(excelFilePath).getSheet(sheetName);

	}

	/**
	 * Obtiene los hoja de trabajo donde se encuentran los datos de acuerdo al index
	 * de la hoja
	 * 
	 * @since 27/11/2017
	 * @author bgaona
	 * @param excelFilePath
	 *            Ruta del libro de excel
	 * @param sheetNumber
	 *            Indice de tipo entero de la hoja en el libro de excel
	 * @return
	 * @throws IOException
	 * @throws InvalidFormatException
	 */
	private Sheet getSheetByIndex(String excelFilePath, int sheetNumber) throws IOException, InvalidFormatException {
		return getWorkBook(excelFilePath).getSheetAt(sheetNumber);

	}

	/**
	 * Devuelve el libro correspondiente a la hoja determinada con antelación
	 * 
	 * @since 27/11/2017
	 * @author bgaona
	 * @param excelFilePath
	 *            Ruta del archivo de excel
	 * @return
	 * @throws IOException
	 * @throws InvalidFormatException
	 */
	private Workbook getWorkBook(String excelFilePath) throws IOException, InvalidFormatException {
		return WorkbookFactory.create(new File(excelFilePath));
	}

	/**
	 * Retorna la lista en forma de Map de todas las filas que contiene la hoja de
	 * excel, teniendo en cuenta la primera fila como los nombres de la columna
	 * 
	 * @since 27/11/2017
	 * @author bgaona
	 * @param sheet
	 *            Hoja de excel
	 * @return
	 */
	private List<Map<String, String>> readSheet(Sheet sheet) {
		Row row;
		int totalRow = sheet.getPhysicalNumberOfRows(); // -------------> se deja la última fila Se quita una de las
														// filas que la librería considera que tiene datos debido a que
														// deja una en blanco
		List<Map<String, String>> excelRows = new ArrayList<Map<String, String>>();
		int headerRowNumber = getHeaderRowNumber(sheet);
		if (headerRowNumber != -1) {
			int totalColumn = sheet.getRow(headerRowNumber).getLastCellNum();
			int setCurrentRow = 1;
			for (int currentRow = setCurrentRow; currentRow <= totalRow; currentRow++) {
				row = getRow(sheet, sheet.getFirstRowNum() + currentRow);
				LinkedHashMap<String, String> columnMapdata = new LinkedHashMap<String, String>();
				for (int currentColumn = 0; currentColumn < totalColumn; currentColumn++) {
					columnMapdata.putAll(getCellValue(sheet, row, currentColumn));
				}
				excelRows.add(columnMapdata);
			}
		}
		return excelRows;
	}

	/**
	 * Obtiene el número de filas conceniernte a encabezado de la hoja
	 * 
	 * @since 27/11/2017
	 * @author bgaona
	 * @param sheet
	 * @return
	 */
	private int getHeaderRowNumber(Sheet sheet) {
		Row row;
		int totalRow = sheet.getLastRowNum();
		for (int currentRow = 0; currentRow <= totalRow + 1; currentRow++) {
			row = getRow(sheet, currentRow);
			if (row != null) {
				int totalColumn = row.getLastCellNum();
				for (int currentColumn = 0; currentColumn < totalColumn; currentColumn++) {
					Cell cell;
					cell = row.getCell(currentColumn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					if (cell.getCellTypeEnum() == CellType.STRING) {
						return row.getRowNum();
					} else if (cell.getCellTypeEnum() == CellType.NUMERIC) {
						return row.getRowNum();

					} else if (cell.getCellTypeEnum() == CellType.BOOLEAN) {
						return row.getRowNum();
					} else if (cell.getCellTypeEnum() == CellType.ERROR) {
						return row.getRowNum();
					}
				}
			}
		}
		return (-1);
	}

	/**
	 * Obtiene la fila de acuerdo a la hoja y el número de ésta
	 * 
	 * @since 27/11/2017
	 * @author bgaona
	 * @param sheet
	 * @param rowNumber
	 * @return
	 */
	private Row getRow(Sheet sheet, int rowNumber) {
		return sheet.getRow(rowNumber);
	}

	/**
	 * Obtiene el valor de cada una de las celdas -------> reevaluar y dejar como
	 * texto todos los valores
	 * 
	 * @since 27/11/2017
	 * @author bgaona
	 * @param sheet
	 * @param row
	 * @param currentColumn
	 * @return
	 */
	private LinkedHashMap<String, String> getCellValue(Sheet sheet, Row row, int currentColumn) {
		LinkedHashMap<String, String> columnMapdata = new LinkedHashMap<String, String>();
		Cell cell;
		if (row == null) {
			if (sheet.getRow(sheet.getFirstRowNum()).getCell(currentColumn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
					.getCellTypeEnum() != CellType.BLANK) {
				String columnHeaderName = sheet.getRow(sheet.getFirstRowNum()).getCell(currentColumn)
						.getStringCellValue();
				columnMapdata.put(columnHeaderName, "");
			}
		} else {
			cell = row.getCell(currentColumn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			if (cell.getCellTypeEnum() == CellType.STRING) {
				if (sheet.getRow(sheet.getFirstRowNum())
						.getCell(cell.getColumnIndex(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
						.getCellTypeEnum() != CellType.BLANK) {
					String columnHeaderName = sheet.getRow(sheet.getFirstRowNum()).getCell(cell.getColumnIndex())
							.getStringCellValue();
					columnMapdata.put(columnHeaderName, cell.getStringCellValue());
				}
			} else if (cell.getCellTypeEnum() == CellType.NUMERIC) {
				if (sheet.getRow(sheet.getFirstRowNum())
						.getCell(cell.getColumnIndex(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
						.getCellTypeEnum() != CellType.BLANK) {
					String columnHeaderName = sheet.getRow(sheet.getFirstRowNum()).getCell(cell.getColumnIndex())
							.getStringCellValue();
					columnMapdata.put(columnHeaderName, NumberToTextConverter.toText(cell.getNumericCellValue()));
				}
			} else if (cell.getCellTypeEnum() == CellType.BLANK) {
				if (sheet.getRow(sheet.getFirstRowNum())
						.getCell(cell.getColumnIndex(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
						.getCellTypeEnum() != CellType.BLANK) {
					String columnHeaderName = sheet.getRow(sheet.getFirstRowNum()).getCell(cell.getColumnIndex())
							.getStringCellValue();
					columnMapdata.put(columnHeaderName, "");
				}
			} else if (cell.getCellTypeEnum() == CellType.BOOLEAN) {
				if (sheet.getRow(sheet.getFirstRowNum())
						.getCell(cell.getColumnIndex(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
						.getCellTypeEnum() != CellType.BLANK) {
					String columnHeaderName = sheet.getRow(sheet.getFirstRowNum()).getCell(cell.getColumnIndex())
							.getStringCellValue();
					columnMapdata.put(columnHeaderName, Boolean.toString(cell.getBooleanCellValue()));
				}
			} else if (cell.getCellTypeEnum() == CellType.ERROR && sheet.getRow(sheet.getFirstRowNum())
					.getCell(cell.getColumnIndex(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
					.getCellTypeEnum() != CellType.BLANK) {

				String columnHeaderName = sheet.getRow(sheet.getFirstRowNum()).getCell(cell.getColumnIndex())
						.getStringCellValue();
				columnMapdata.put(columnHeaderName, Byte.toString(cell.getErrorCellValue()));

			}
		}
		return columnMapdata;
	}
}
