package processor;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UtilPerturbation {

	public static List<String> perturbableTypes = new ArrayList<String>();

	private static int currentLocation = 0;

	private static ModifierKind [] modifiersPublic = {ModifierKind.PUBLIC, ModifierKind.STATIC};

	private static CtClass perturbator = null;

	private static CtClass location = null;

	private UtilPerturbation() {

	}

	static  {
		perturbableTypes.add("char");
		perturbableTypes.add("Character");

		perturbableTypes.add("byte");
		perturbableTypes.add("Byte");
		perturbableTypes.add("short");
		perturbableTypes.add("Short");
		perturbableTypes.add("int");
		perturbableTypes.add("Integer");
		perturbableTypes.add("long");
		perturbableTypes.add("Long");

		perturbableTypes.add("boolean");
		perturbableTypes.add("Boolean");

		perturbableTypes.add("float");
		perturbableTypes.add("Float");
		perturbableTypes.add("double");
		perturbableTypes.add("Double");
	}

	public static String function(CtExpression e) {
		String type = e.getType().getSimpleName().toLowerCase();
		if (e.getTypeCasts().size() > 0)
			type = ((CtTypeReference)e.getTypeCasts().get(0)).getSimpleName().toLowerCase();
		switch (type) {
			case "integer":
				return "int";
			case "character":
				return "char";
			default:
				return type;
		}
	}

	public static boolean checkIsNotInPerturbatorPackage(CtElement candidate) {
		CtElement parent = candidate;
		while(! ((parent = parent.getParent()) instanceof CtPackage)) ;
		return ((CtPackage) parent).getQualifiedName().equals("perturbator");
	}

	private static String getTypeParametersAsString(CtMethod method) {
		String listOfParameters = "";
		List<CtParameter<?>> parameters = method.getParameters();
		for (CtParameter<?> parameter : parameters)
			listOfParameters += parameter.getType()+">";
		return listOfParameters.length()>0?listOfParameters.substring(0, listOfParameters.length()-1):listOfParameters;
	}


	//@TODO replace try/catch
	private static String getMethodName(CtExpression arg) {
		try {
			CtMethod<?>[] methods = ((Set<CtMethod<?>>) arg.getPosition().getCompilationUnit().getDeclaredTypes().get(0).getMethods()).toArray(new CtMethod<?>[]{});
			SourcePosition argPosition = arg.getPosition();
			for (int i = 0; i < methods.length; i++) {
				if (methods[i].getPosition().getSourceStart() <= argPosition.getSourceStart() &&
						methods[i].getPosition().getSourceEnd() >= argPosition.getSourceEnd())
					return ":" + methods[i].getSimpleName() + ":" + getTypeParametersAsString(methods[i]);
			}
		} catch (java.lang.UnsupportedOperationException e) {
				return "";
			}
		return "";
	}
	
	public static CtInvocation createStaticCallOfPerturbationFunction(Factory factory, String methodName, CtExpression argument) {

		if (perturbator == null) {
			perturbator = (CtClass) factory.Class().get("perturbator.Perturbator");
			location = (CtClass) factory.Class().get("perturbator.Location");
		}

		CtTypeReference<?> classReference = factory.Type().createReference(perturbator);
		CtExecutableReference execRef = factory.Core().createExecutableReference();
		execRef.setDeclaringType(classReference);
		execRef.setSimpleName(methodName);
		execRef.setStatic(true);

		CtTypeAccess typeAccess = factory.Core().createTypeAccess();
		typeAccess.setType(classReference);
		typeAccess.setAccessedType(classReference);

		//ToReview
//		CtLiteral stringPosition = factory.Code().createLiteral(argument.getPosition().getCompilationUnit().getFile().getName()
//				+ getMethodName(argument) + ":" + argument.getPosition().getLine());
//		((CtNewArray) location.getField("locations").getDefaultExpression()).addElement(stringPosition);

		//Add the currentLocation to arguments.
		CtExpression[] args = new CtExpression[2];
		args[0] = factory.Code().createLiteral(currentLocation);
		args[1] = argument;

		currentLocation++;

		return factory.Code().createInvocation(typeAccess, execRef, args);
	}

}
