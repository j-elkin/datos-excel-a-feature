package com.refactor.excelafeature.util.excelfeature;

import com.refactor.excelafeature.util.LoggerApp;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * Personalización del Runner con el cual se puede determinar que busque y
 * modifique los .feature antes de ser ejecutados
 * 
 * @since 27/11/2017
 */
public class RunnerPersonalizado extends Runner {

	private static final Logger LOGGER = Logger.getLogger(RunnerPersonalizado.class.getName());
	/**
	 * private Class<Cucumber> classValue; private Cucumber cucumber;
	 */

	private Class<CucumberWithSerenity> classValue;
	private CucumberWithSerenity cucumberWithSerenity;

	public RunnerPersonalizado(Class<CucumberWithSerenity> classValue) {
		this.classValue = classValue;
		try {
			cucumberWithSerenity = new CucumberWithSerenity(classValue);
		} catch (InitializationError initializationError) {
			LOGGER.severe(LoggerApp.getStackTrace(initializationError));
		} catch (IOException e) {
			LOGGER.severe(LoggerApp.getStackTrace(e));
		}
	}

	@Override
	public Description getDescription() {
		return cucumberWithSerenity.getDescription();
	}

	private void runAnnotatedMethods(Class<?> annotation)  {
		if (!annotation.isAnnotation()) {
			return;
		}
		Method[] methods = this.classValue.getMethods();
		for (Method method : methods) {
			Annotation[] annotations = method.getAnnotations();
			for (Annotation item : annotations) {
				if (item.annotationType().equals(annotation)) {
					try {
						method.invoke(null);
					} catch (IllegalAccessException e) {
						LOGGER.severe(LoggerApp.getStackTrace(e));
					} catch (InvocationTargetException e) {
						LOGGER.severe(LoggerApp.getStackTrace(e));
					}
					break;
				}
			}
		}
	}

	@Override
	public void run(RunNotifier notifier) {
		try {
			runAnnotatedMethods(BeforeSuite.class);
			cucumberWithSerenity = new CucumberWithSerenity(classValue);
		} catch (Exception e) {
			LOGGER.warning(LoggerApp.getStackTrace(e));
		}
		cucumberWithSerenity.run(notifier);
	}
}
