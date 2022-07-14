# excel-a-feature

## Pre-requisitos 📋
- JDK v11 (variables de entorno configuradas)
- IntelliJ IDEA o el IDE de preferencia configurado con Cucumber Plugin for Java
- Gradle v7.4.2 o superior (variables de entorno configuradas)

## Instalación 🔧
- Para clonar este repositorio localmente, se debe ejecutar el siguiente comando:
  ```git clone https://github.com/j-elkin/datos-excel-a-feature.git```
- Importar el proyecto desde el IDE como un proyecto Gradle existente
- Configurar Project SDk 11 para el proyecto importado
- Configurar la codificación a UTF-8 al proyecto una vez sea importado

## Compilar El Proyecto y Generar Wrapper 🔨
- Para compilar el proyecto se debe ejecutar el comando:
  ```gradle clean build -x test```
- (Opcional) Para generar los archivos wrapper del proyecto se debe ejecutar el comando:
  ```gradle wrapper --gradle-distribution-url https://services.gradle.org/distributions/gradle-7.1-bin.zip```
- Si actualizas la url de la distribución de gradle-wrapper, se recomienda ejecutar luego las pruebas en la terminal local con el comando:
  ```gradlew clean test aggregate -i```