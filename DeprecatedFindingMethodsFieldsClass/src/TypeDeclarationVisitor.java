import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TypeDeclaration;


public class TypeDeclarationVisitor extends ASTVisitor {
	int numberTypes = 0;
	int numberDeprecateds = 0;
	int numberDeprecatedsWithAnnotation = 0; 
	int numberDeprecatedsWithMessage = 0;
	int numberDeprecatedWithRelevantMessages = 0;
	
	int numberUse = 0;
	int numberReplace = 0;
	int numberRefer = 0;
	int numberEquivalent = 0;
	int numberLink = 0;
	int numberSee = 0;
	int numberCode = 0;

	@Override
	public boolean visit(TypeDeclaration node) {
		boolean privateModifier = false;
		for (Object modifier : node.modifiers()) {
			if (modifier.getClass().equals(Modifier.class) && ((Modifier)modifier).isPrivate()) {
				privateModifier = true;
				break;
			}
			
		}
		
		if (!privateModifier && node.resolveBinding() != null && node.resolveBinding().isDeprecated()) {
			numberDeprecateds++;
			if (containsAnnotation(node, "Deprecated")) {
				numberDeprecatedsWithAnnotation++;
			}
			if (containsTagJavaDoc(node, "@deprecated")) {
				numberDeprecatedsWithMessage++;
				
					if (node.getJavadoc().toString().toLowerCase().contains("use") ||
							node.getJavadoc().toString().toLowerCase().contains("replace") ||
							node.getJavadoc().toString().toLowerCase().contains("refer") ||
							node.getJavadoc().toString().toLowerCase().contains("equivalent") || 
							node.getJavadoc().toString().toLowerCase().contains("@link") || 
							node.getJavadoc().toString().toLowerCase().contains("@see") ||
							node.getJavadoc().toString().toLowerCase().contains("@code")) {
						numberDeprecatedWithRelevantMessages++;
						if (node.getJavadoc().toString().toLowerCase().contains("use")) {
							numberUse++;
						}
						if (node.getJavadoc().toString().toLowerCase().contains("replace")) {
							numberReplace++;
						}
						if (node.getJavadoc().toString().toLowerCase().contains("refer")) {
							numberRefer++;
						}
						if (node.getJavadoc().toString().toLowerCase().contains("equivalent")) {
							numberEquivalent++;
						}
						if (node.getJavadoc().toString().toLowerCase().contains("@link")) {
							numberLink++;
						}
						if (node.getJavadoc().toString().toLowerCase().contains("@see")) {
							numberSee++;
						}
						if (node.getJavadoc().toString().toLowerCase().contains("@code")) {
							numberCode++;
						}
					}
				}
			
			
		}
		
		numberTypes++;
		return super.visit(node);
	}
	
	private boolean containsAnnotation(TypeDeclaration node, String annotation) {
		for (IAnnotationBinding annotationBinding : node.resolveBinding().getAnnotations()) {
			if (annotationBinding.getName().equals(annotation)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean containsTagJavaDoc(TypeDeclaration node, String tag) {
		//"gleiosn".toLowerCase().contains("Use".toLowerCase());
		if (node.getJavadoc() != null && 
				node.getJavadoc().tags() != null &&
				!node.getJavadoc().tags().isEmpty()) {
			for (Object tagElement : node.getJavadoc().tags()) {
				if (((TagElement) tagElement).getTagName() != null &&((TagElement) tagElement).getTagName().equals(tag)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public int getNumberTypes() {
		return numberTypes;
	}


	public int getNumberDeprecateds() {
		return numberDeprecateds;
	}


	public int getNumberDeprecatedsWithAnnotation() {
		return numberDeprecatedsWithAnnotation;
	}


	public int getNumberDeprecatedsWithMessage() {
		return numberDeprecatedsWithMessage;
	}


	public int getNumberDeprecatedWithRelevantMessages() {
		return numberDeprecatedWithRelevantMessages;
	}


	public int getNumberUse() {
		return numberUse;
	}


	public int getNumberReplace() {
		return numberReplace;
	}


	public int getNumberRefer() {
		return numberRefer;
	}


	public int getNumberEquivalent() {
		return numberEquivalent;
	}


	public int getNumberLink() {
		return numberLink;
	}


	public int getNumberSee() {
		return numberSee;
	}


	public int getNumberCode() {
		return numberCode;
	}


}
