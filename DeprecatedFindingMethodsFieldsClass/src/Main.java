import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

//import com.opencsv.CSVWriter;


public class Main {

	public static int[] valuesTotal = {0,0,0,0,0,0}; 
	
	public static int[] parse(String str, File source) throws IOException {
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
		

		FieldDeclarationVisitor visitorField = new FieldDeclarationVisitor();
		TypeDeclarationVisitor visitorType = new TypeDeclarationVisitor();
		MethodDeclarationVisitor visitorMethod = new MethodDeclarationVisitor();
		compilationUnit.accept(visitorField);
		compilationUnit.accept(visitorType);
		compilationUnit.accept(visitorMethod);
		
		int[] retorno = {visitorField.getNumberDeprecatedWithRelevantMessages() , 
				visitorType.getNumberDeprecatedWithRelevantMessages() , 
				visitorMethod.getNumberDeprecatedWithRelevantMessages(), visitorField.getNumberDeprecateds() , 
							visitorType.getNumberDeprecateds() , 
							visitorMethod.getNumberDeprecateds()};
		return retorno;
		
		//return source.getAbsolutePath() + "," +  // endereco da classe analisada

        	/*	
			visitorField.getNumberFields() + "," + // numero de fields
			visitorField.getNumberDeprecateds() + "," + // numero de fields depreciados
			visitorField.getNumberDeprecatedsWithAnnotation() + "," + //numero de fields depreciados sem mensagem
			visitorField.getNumberDeprecatedsWithMessage() + "," + //numero de fields depreciados com mensagem
			visitorField.getNumberDeprecatedWithRelevantMessages() + "," + //numero de fields depreciados com mensagem relevante
			visitorField.getNumberUse() + "," + // number use
			visitorField.getNumberReplace() + "," + // number replace
			visitorField.getNumberRefer() + "," + // number refer
			visitorField.getNumberEquivalent() + "," + // number equivalent
			visitorField.getNumberLink() + "," + // number link
			visitorField.getNumberSee() + "," + // number see
			visitorField.getNumberCode()+ "," + // number code
			
			visitorType.getNumberTypes() + "," + // numero de classes
			visitorType.getNumberDeprecateds() + "," + // numero de classes depreciados
			visitorType.getNumberDeprecatedsWithAnnotation() + "," + //numero de classes depreciados sem mensagem
			visitorType.getNumberDeprecatedsWithMessage() + "," + //numero de classes depreciados com mensagem
			visitorType.getNumberDeprecatedWithRelevantMessages() + "," + //numero de classes depreciados com mensagem relevante
			visitorType.getNumberUse() + "," + // number use
			visitorType.getNumberReplace() + "," + // number replace
			visitorType.getNumberRefer() + "," + // number refer
			visitorType.getNumberEquivalent() + "," + // number equivalent
			visitorType.getNumberLink() + "," + // number link
			visitorType.getNumberSee() + "," + // number see
			visitorType.getNumberCode() + "," + // number code
			
			visitorMethod.getNumberMethods() + "," + // numero de metodos
			visitorMethod.getNumberDeprecateds() + "," + // numero de metodos depreciados
			visitorMethod.getNumberDeprecatedsWithAnnotation() + "," + //numero de metodos depreciados sem mensagem
			visitorMethod.getNumberDeprecatedsWithMessage() + "," + //numero de metodos depreciados com mensagem
			visitorMethod.getNumberDeprecatedWithRelevantMessages() + "," + //numero de metodos	depreciados com mensagem relevante
			visitorMethod.getNumberUse() + "," + // number use
			visitorMethod.getNumberReplace() + "," + // number replace
			visitorMethod.getNumberRefer() + "," + // number refer
			visitorMethod.getNumberEquivalent() + "," + // number equivalent
			visitorMethod.getNumberLink() + "," + // number link
			visitorMethod.getNumberSee() + "," + // number see
			visitorMethod.getNumberCode(); // number code
		*/
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
	
	public static void parseFilesInDir(File file, int[] values) throws IOException {
		if (file.isFile()) {
			if (file.getName().endsWith(".java")) {
				
				int[] line = parse(readFileToString(file.getAbsolutePath()), file);
				//System.out.println(file.getAbsolutePath());
				//System.out.println(line[0] + ", " + line[1] + ", " + line[2] + ", " + line[3] + " , " + line[4] + ", " + line[5]);
				values[0] += line[0];
				values[1] += line[1];
				values[2] += line[2];
				values[3] += line[3];
				values[4] += line[4];
				values[5] += line[5];
				
				//System.out.println(values[0] + ", " + values[1] + ", " + values[2] + ", " + values[3] + " , " + values[4] + ", " + values[5]);
				//if (line != null) {
					//writeCSVFieldProject.println(line);
					//System.out.println(line);
				//}
			}
		} else {
			for (File f : file.listFiles()) {
				parseFilesInDir(f, valuesTotal);
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		
		
		parseFilesInDir(new File(args[0]), valuesTotal);
		float totalReplacementMessages = valuesTotal[0] + valuesTotal[1] + valuesTotal[2];
		float totalDeprecated = valuesTotal[3] + valuesTotal[4] + valuesTotal[5];
		if (totalDeprecated == 0) {
			System.out.println(totalReplacementMessages + "," + totalDeprecated + "," + 0);
		} else {
			System.out.println(totalReplacementMessages + "," + totalDeprecated + "," + (totalReplacementMessages * 100/totalDeprecated));
		}
		
		

	}

}
