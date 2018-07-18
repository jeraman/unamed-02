package frontend.core;

/************************************************
 ** Class used to interpret expressions developed by Sofian
 ************************************************/


 import javax.script.*;

import frontend.Main;

import java.io.Serializable;
import java.util.Iterator;

import processing.core.PApplet;

/// Expression class which allows to compute javascript-style expressions with variables from the blackboard.
public class Expression implements Serializable {

  // Static components.
  //static transient ScriptEngineManager manager;
  static transient ScriptEngine engine;

  String expression;

  public Expression(String expression) {
    this.expression = expression;
    PApplet p = Main.instance();
    this.build(p);
  }

  void build(PApplet p) {

    if (this.engine == null) {
      ScriptEngineManager manager = new ScriptEngineManager();
      this.engine = manager.getEngineByName("js");

      try {
        java.util.Scanner s = new java.util.Scanner(new java.net.URL("file://" + p.dataPath("math.js")).openStream()).useDelimiter("\\A");
        engine.eval(s.hasNext() ? s.next() : "");
      }
      catch (Exception e) {
        System.out.println(e);
      }
    }
  }
  
  public static void addToEngine(String name, Object value) {
	  if (engine != null)
		  engine.put(name, value);
  }
  
  public static void replaceInEngine(String name, Object value) {
	  if (engine != null) {
//		  Bindings b = engine.getBindings(ScriptContext.ENGINE_SCOPE);
//		  b.replace(name, value);
		  engine.put(name, value);
	  }
  }
  
  public static void removeFromEngine(String name) {
	  if (engine != null) {
//		  Bindings b = engine.getBindings(ScriptContext.ENGINE_SCOPE);
//		  b.remove(name);
			try {
				engine.eval("delete " + name);
			} catch (ScriptException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  }
  }

	/// Computes expression using blackboard and returns result.
	public Object eval(Blackboard agent) throws ScriptException {
//		Bindings b = engine.getBindings(ScriptContext.ENGINE_SCOPE);
//		System.out.println(b.size());
//		System.out.println(engine.getBindings(ScriptContext.ENGINE_SCOPE));
//		System.out.println(b.values());
//		Iterator i2 = b.keySet().iterator();
//		for (Iterator iterator = b.values().iterator(); iterator.hasNext();) {
//			  System.out.println(i2.next()+ " " + iterator.next()); 
//			}
//		System.out.println(b.keySet());
		
		return engine.eval(agent.processExpression(expression));
	}

  public String toString() {
    return expression;
  }

}
