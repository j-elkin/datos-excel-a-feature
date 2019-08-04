package com.refactor.excelafeature.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.*;

/**
 * Clase principal para el manejo de la bitácora o log del aplicativo.<br>
 * Ejemplo adaptado de <a href=
 * 'https://medium.com/el-acordeon-del-programador/logs-en-java-con-java-util-logging-d344ae2ba7bc'>Logs
 * en Java</a>
 */
public class LoggerApp {
	/**
	 * Preparamos el log para cada paquete del proyecto, esto con el fin de capturar
	 * cada log que se genere e irlo pasando al nivel superior hasta que encuentren
	 * un handler que los maneje.
	 */
	private static final Logger LOG = Logger.getLogger("co.com.bancolombia.sucursales");

	/**
	 * El log para ésta clase en particular.
	 */
	private static final Logger LOGGER = Logger.getLogger(LoggerApp.class.getName());

	/**
	 * Con el manejador de archivo, indicamos el archivo a donde se mandaran los logs.<br><br>
	 * El segundo argumento controla si se sobre-escribe el archivo o se agregan los logs al final.<br>
	 * Para sobre-escribir pase un <b>false</b>, para agregar al final pase un <b>true</b>.
	 */
	private Handler fileHandler = null;
	
	/**
	 * Constructor de la clase. Inicia el Log raíz del paquete padre para capturar con el handler los log 
	 * que se generen en las clases de los paquetes hijos.
	 */
	public LoggerApp() {

		try {
			fileHandler = new FileHandler("./automationApp.log", true);

			// El formateador indica como presentar los datos, en este caso usaremos el formato sencillo
			// el cuál es más fácil de leer, si no úsamos esto el log estará en formato xml por defecto.
			SimpleFormatter simpleFormatter = new SimpleFormatter();
			// Se especifica qué formateador usará el manejador (handler) de archivo
			fileHandler.setFormatter(simpleFormatter);

			// Asignamos los handles previamente declarados al log *raíz* esto es muy importante ya que
			// permitirá que los logs de todas y cada una de las clases del programa que esten en ese paquete
			// o sus subpaquetes se almacenen en el archivo y aparezcan en consola.
			LOG.addHandler(fileHandler);
			// Indicamos a partir de que nivel deseamos mostrar los logs, podemos especificar un nivel en especifico
			// para ignorar información que no necesitemos
			fileHandler.setLevel(Level.ALL);

		} catch (SecurityException e) {
			LOGGER.log(Level.SEVERE, "Error de Seguridad: "+e);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error de IO: "+e);
		}
	}
	/**
	 * Dar un mensaje inicial luego de instanciar el logger raíz.
	 * Por defecto, incluye dos saltos de línea antes del mensaje.
	 * @param strMsg Mensaje inicial a registrar en el log.
	 */
	public void mensajeInicial(String strMsg) {
		LOGGER.log(Level.INFO, "\n\n							►►	 {0}	◄◄", strMsg);
	}
	
	/**
    * Esta función nos permite convertir el stackTrace de una excepción en un String, 
    * necesario para poder imprimirlo y almacenarlo en el log para un análisis posterior.
    * @param e Excepcion de la que queremos el StackTrace.
    * @return StackTrace de la excepción en forma de String.
    */
    public static String getStackTrace(Exception e) {
        StringWriter sWriter = new StringWriter();
        PrintWriter pWriter = new PrintWriter(sWriter);
        e.printStackTrace(pWriter);
        return sWriter.toString();
    }
}
