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
	
	public static boolean executeScript(String scriptCall, Object... args) {
		String[] split = scriptCall.split("@");
		
		if (split.length != 2) {
			System.err.println("Invalid script call format: " + scriptCall);
			return false;
		}
		
		return executeScript(split[0].replace(".", "/"), split[1], args);
	}

	public static boolean executeScript(String script, String function, Object... args) {
		try {
			ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
			
			engine.eval(new FileReader(new File("../../scripts/" + script + ".js")));
			
			Invocable invocable = (Invocable) engine;
			
			Object result = invocable.invokeFunction(function, args);

			if (result == null)
				return true;
			
			return (boolean) result;
		} catch (NoSuchMethodException | FileNotFoundException | ScriptException e) {
			System.err.println(e.getMessage());
		}
		return false;
	}
	
}
