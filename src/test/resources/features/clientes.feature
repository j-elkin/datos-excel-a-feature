#language: es

Característica: Abrir una cuenta de ahorros en plan básico a un cliente nuevo
  Como asesor de una sucursal Bancolombia
   Quiero realizar la apertura de una cuenta de ahorros a un cliente nuevo
   Para que el cliente pueda utilizarla

  Esquema del escenario: vincular un cliente nuevo y aperturar una cuenta de ahorros en plan básico
    Dado realiza el ingreso del cliente
      | <clientType> | <idType> | <idNumber> | <nombre> | <apellido> |


    Ejemplos:
      | clientType | idType | idNumber | nombre         | apellido      |
      ##@externaldata@./src/test/resources/Datadriven/Clientes.xlsx@Hoja1@1-3
   |N   |1   |1234   |Juan   |Soto|
   |J   |3   |5678   |Raul   |perez|
   |A   |2   |10842   |Andres Esteban   |Yepez Camargo|
