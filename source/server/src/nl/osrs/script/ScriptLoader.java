package nl.osrs.script;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Loads scripts and executes the statements provided in the scripts.
 * @author j.germeraad
 */
public class ScriptLoader {

	public static boolean executeScript(String script, String function, Object... args) {
		try {
			ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
			
			engine.eval(new FileReader(new File("../../scripts/" + script + ".js")));
			
			Invocable invocable = (Invocable) engine;
			
			return (boolean) invocable.invokeFunction(function, args);
		} catch (NoSuchMethodException | FileNotFoundException | ScriptException e) {
			System.err.println(e.getMessage());
		}
		return false;
	}
	
}
