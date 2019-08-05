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
      | clientType | idType | idNumber | nombre         | apellido        |
    ##@externaldata@./src/test/resources/Datadriven/Clientes.xlsx@Hoja1@
	|	N	|	1	|	1234	|	Juan	|	Soto	|
	|	J	|	3	|	5678	|	Raul	|	perez	|
	|	A	|	2	|	10842	|	Andres Esteban	|	Yepez Camargo	|
	|	P	|	40	|	31941	|	Camilo Andres	|	Arango Suarez	|
	|	E	|	12	|	322112	|	Felipe	|	Jaramillo Lopez	|


  @2_CedulaCiudadania
    Ejemplos:
      | clientType | idType | idNumber | nombre         | apellido        |
    ##@externaldata@./src/test/resources/Datadriven/Clientes.xlsx@Hoja1@1
	|	N	|	1	|	1234	|	Juan	|	Soto	|


  @3_CedulaCiudadania
    Ejemplos:
      | clientType | idType | idNumber | nombre         | apellido        |
	##@externaldata@./src/test/resources/Datadriven/Clientes.xlsx@Hoja1@2,3,5
	|	J	|	3	|	5678	|	Raul	|	perez	|
	|	A	|	2	|	10842	|	Andres Esteban	|	Yepez Camargo	|
	|	E	|	12	|	322112	|	Felipe	|	Jaramillo Lopez	|

  @3_CedulaCiudadania
    Ejemplos:
      | clientType | idType | idNumber | nombre         | apellido        |
	##@externaldata@./src/test/resources/Datadriven/Clientes.xlsx@Hoja1@2-4



