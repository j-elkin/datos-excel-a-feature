#language: es

Característica: Cargar datos de un archivo excel a un archivo feature
  Como programador
   Quiero refactorizar la carga de datos de excel a un feature
   Para reducir la deuda tecnica reportada por Sonar

  Esquema del escenario: realizar la carga de datos de excel hacia el feature
    Dado realiza el ingreso del cliente
      | <clientType> | <idType> | <idNumber> | <nombre> | <apellido> |

  @1_CedulaCiudadania
    Ejemplos:
      | clientType | idType | idNumber | nombre | apellido |
      ##@externaldata@./src/test/resources/Datadriven/Clientes.xlsx@Hoja1@2
	|	J	|	3	|	5678	|	Raul	|	perez	|


  @2_CedulaCiudadania
    Ejemplos:
      | clientType | idType | idNumber | nombre | apellido |
      ##@externaldata@./src/test/resources/Datadriven/Clientes.xlsx@Hoja1@2
	|	J	|	3	|	5678	|	Raul	|	perez	|


  @3_CedulaCiudadania
    Ejemplos:
      | clientType | idType | idNumber | nombre | apellido |
      ##@externaldata@./src/test/resources/Datadriven/Clientes.xlsx@Hoja1@3
	|	A	|	2	|	10842	|	Andres Esteban	|	Yepez Camargo	|


    
