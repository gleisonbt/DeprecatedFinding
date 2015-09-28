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
		

		FieldDeclarationVisitor visitorField = new FieldDeclarationVisitor();
		TypeDeclarationVisitor visitorType = new TypeDeclarationVisitor();
		MethodDeclarationVisitor visitorMethod = new MethodDeclarationVisitor();
		compilationUnit.accept(visitorField);
		compilationUnit.accept(visitorType);
		compilationUnit.accept(visitorMethod);
		
		return source.getAbsolutePath() + "," +  // endereco da classe analisada
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
	
	public static void parseFilesInDir(File file, PrintWriter writeCSVFieldProject) throws IOException {
		
		if (file.isFile()) {
			if (file.getName().endsWith(".java")) {
				
				String line = parse(readFileToString(file.getAbsolutePath()), file);
				if (line != null) {
					writeCSVFieldProject.println(line);
					System.out.println(line);
				}
			}
		} else {
			for (File f : file.listFiles()) {
				parseFilesInDir(f, writeCSVFieldProject);
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		//receive the projects list (args[0]) 
		
		FileReader fileProjects = new FileReader(args[0]);
		BufferedReader readFile = new BufferedReader(fileProjects);
		
		String project = readFile.readLine();
		while (project != null) {
			
			FileWriter csvFieldProject = new FileWriter(project + File.separatorChar + project + ".csv");
			PrintWriter writeCSVFieldProject = new PrintWriter(csvFieldProject);
			
			parseFilesInDir(new File(project), writeCSVFieldProject);
			
			csvFieldProject.close();
			
			project = readFile.readLine();
		}

	}

}
