package ast;

import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;

public class AstGraphviz
{
	// writer
	private PrintWriter fileWriter;
	
	// singleton
	private static AstGraphviz instance = null;

	private AstGraphviz() {}

	public static AstGraphviz getInstance()
	{
		if (instance == null)
		{
			instance = new AstGraphviz();
			
			// initialize writer
			try
			{
				String dirname="./output/";
				File dir = new File(dirname);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				String filename="AST_IN_GRAPHVIZ_DOT_FORMAT.txt";
				instance.fileWriter = new PrintWriter(dirname+filename);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			// graph header
			instance.fileWriter.print("digraph\n");
			instance.fileWriter.print("{\n");
			instance.fileWriter.print("graph [ordering = \"out\"]\n");
		}
		return instance;
	}

	// log node
	public void logNode(int nodeSerialNumber,String nodeName)
	{
		fileWriter.format(
			"v%d [label = \"%s\"];\n",
			nodeSerialNumber,
			nodeName);
	}

	// log edge
	public void logEdge(
		int fatherNodeSerialNumber,
		int sonNodeSerialNumber)
	{
		fileWriter.format(
			"v%d -> v%d;\n",
			fatherNodeSerialNumber,
			sonNodeSerialNumber);
	}
	
	// finalize
	public void finalizeFile()
	{
		fileWriter.print("}\n");
		fileWriter.close();
	}
}
