package perturbator;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;
import java.util.List;

public class UtilPerturbation {

	public static List<String> types = new ArrayList<String>();

	private static int location = 0;

	private static ModifierKind [] modifiersPublic = {ModifierKind.PUBLIC, ModifierKind.STATIC};

	private static CtClass perturbator = null;

	private UtilPerturbation() {

	}

	static  {
		types.add("char");
		types.add("Character");

		types.add("byte");
		types.add("Byte");
		types.add("short");
		types.add("Short");
		types.add("int");
		types.add("Integer");
		types.add("long");
		types.add("Long");

		types.add("boolean");
		types.add("Boolean");

		types.add("float");
		types.add("Float");
		types.add("double");
		types.add("Double");
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

	public static void reset() {
		perturbator = null;
		location = 0;
	}

	public static boolean checkClass(CtElement candidate) {
		CtElement parent = candidate;
		while(! ((parent = parent.getParent()) instanceof CtClass)) ;
		return ((CtClass) parent).getQualifiedName().equals("perturbator.Perturbator");
	}
	
	public static CtInvocation createStaticCall(Factory factory, String methodName, CtExpression...arguments) {

		if (perturbator == null)
			perturbator = (CtClass) factory.Class().get("perturbator.Perturbator");

		CtTypeReference<?> classReference = factory.Type().createReference(perturbator);
		CtExecutableReference execRef = factory.Core().createExecutableReference();
		execRef.setDeclaringType(classReference);
		execRef.setSimpleName(methodName);
		execRef.setStatic(true);

		CtTypeAccess typeAccess = factory.Core().createTypeAccess();
		typeAccess.setType(classReference);
		typeAccess.setAccessedType(classReference);

		String position = arguments[0].getPosition().getFile().getName()+":"+arguments[0].getPosition().getLine();



		//Add the location to arguments.
		CtExpression[] args = new CtExpression[arguments.length + 1];
		CtLiteral<Integer> litCounter = factory.Code().createLiteral(location);
//		args[0] = litCounter;
		args[0] = factory.Code().createConstructorCall(factory.Type().createReference(Location.class),
				new CtExpression[] {factory.Code().createLiteral(location), factory.Code().createLiteral(position)});
		for (int i = 0; i < arguments.length; i++)
			args[i + 1] = arguments[i];
		location++;

		perturbator.getField("number").replace(factory.Code().createCtField("number", factory.Type().INTEGER_PRIMITIVE, location + "", modifiersPublic));

		return factory.Code().createInvocation(typeAccess, execRef, args);
	}

}
