import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Hashtable;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class Main {

	public static String parse(String str, File source) throws IOException {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(str.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		String[] classpath = java.lang.System.getProperty("java.class.path")
				.split(";");
		String[] sources = { source.getParentFile().getAbsolutePath() };

		Hashtable<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM,
				JavaCore.VERSION_1_8);
		 options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
		parser.setUnitName(source.getAbsolutePath());
		
		parser.setCompilerOptions(options);
		parser.setEnvironment(classpath, sources, new String[] { "UTF-8" },
				true);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);

		final CompilationUnit compilationUnit = (CompilationUnit) parser
				.createAST(null);
		

		MethodVisitor visitor = new MethodVisitor();
		compilationUnit.accept(visitor);

		
		
		if (!visitor.getMethodsDeprecated().isEmpty()) {
			return source.getAbsolutePath() + "," +
				visitor.getMethods().size() + "," +
				visitor.getMethodsDeprecated().size() + "," +
				visitor.getMethodsDeprecatedJavaDoc().size() + "," +
				(visitor.getMethodsDeprecated().size() - visitor.getMethodsDeprecatedJavaDoc().size());
		}
		
		return null;

	}

	public static String readFileToString(String filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));

		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}

		reader.close();

		return fileData.toString();
	}

	public static void parseFilesInDir(File file, CSVWriter writer) throws IOException {
		
		if (file.isFile()) {
			String csvLineWords = parse(readFileToString(file.getAbsolutePath()), file);
			if (file.getName().endsWith(".java") && csvLineWords != null) {
				writer.writeNext(csvLineWords.split(","));
			}

		} else {
			for (File f : file.listFiles()) {
				parseFilesInDir(f, writer);
			}
		}
	}
	
	
	
	public static void recordCVSLine(PrintWriter printWriter, String type, int quantTotalMethodDeprecated, int quantDeprecatedAnnotation, int quantDeprecatedJavaDoc) {
		printWriter.printf(type + "," + quantTotalMethodDeprecated + "," + quantDeprecatedAnnotation + "," + quantDeprecatedJavaDoc);
	}
		

	public static void main(String[] args) throws IOException {
		FileWriter report = new FileWriter(args[0].replaceAll(File.separator, "") + ".csv");
		CSVWriter writeReport = new CSVWriter(report);
		
		for (File file : new File(args[0]).listFiles()) {
			if (file.isDirectory()) {
				FileWriter fileWriter = new FileWriter(file.getAbsolutePath() + ".csv");
				CSVWriter writer = new CSVWriter(fileWriter);
				parseFilesInDir(new File(file.getAbsolutePath()), writer);
				writer.close();
				
				
				int sumMethods = 0;
				int sumDeprecatedMethods = 0;
				int sumDeprecatedJavaDoc = 0;
				int sumDeprecatedWithoutJavaDoc = 0;
				
				CSVReader reader = new CSVReader(new FileReader(file.getAbsolutePath() + ".csv"));
				String[] row = null;
				while ((row = reader.readNext()) != null) {
					sumMethods += Integer.parseInt(row[1]);
					sumDeprecatedMethods += Integer.parseInt(row[2]);
					sumDeprecatedJavaDoc += Integer.parseInt(row[3]);
				}
				
				sumDeprecatedWithoutJavaDoc = sumDeprecatedMethods - sumDeprecatedJavaDoc;
				
				double percentDeprecatedMethods = ((float)sumDeprecatedMethods/sumMethods)*100; 
				double percentDeprecatedJavaDoc = ((float)sumDeprecatedJavaDoc/sumDeprecatedMethods)*100;
				double percentDeprecatedWithoutJavaDoc = percentDeprecatedMethods = percentDeprecatedJavaDoc;
				
				writeReport.writeNext((file.getName() + "," + sumMethods + "," + sumDeprecatedMethods + "," + sumDeprecatedJavaDoc + "," + 
						sumDeprecatedWithoutJavaDoc + "," + percentDeprecatedMethods +
						"," + percentDeprecatedJavaDoc + "," + percentDeprecatedWithoutJavaDoc).split(","));

			}
			
		}
		writeReport.close();

	}

}

