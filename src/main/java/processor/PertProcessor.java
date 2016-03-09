package processor;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;
import java.util.List;

public class PertProcessor {

	public static List<String> types = new ArrayList<String>();

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
		location = 1;
	}

	private static CtClass perturbator = null;
	private static int location = 1;

	///Modifier for each methods
	private static ModifierKind [] modifiersPrivate = {ModifierKind.PRIVATE, ModifierKind.STATIC};
	private static ModifierKind [] modifiersPublic = {ModifierKind.PUBLIC, ModifierKind.STATIC};

	private PertProcessor() {
		
	}
	
	public static CtInvocation createStaticCall(Factory factory, String methodName, CtExpression...arguments) {
		if (perturbator == null)
			createPertubation(factory);

		CtTypeReference<?> classReference = factory.Type().createReference(perturbator);
		CtExecutableReference execRef = factory.Core().createExecutableReference();
		execRef.setDeclaringType(classReference);
		execRef.setSimpleName(methodName);
		execRef.setStatic(true);

		CtTypeAccess typeAccess = factory.Core().createTypeAccess();
		typeAccess.setType(classReference);
		typeAccess.setAccessedType(classReference);

		//Add the location to arguments.
		CtExpression[] args = new CtExpression[arguments.length + 1];
		CtLiteral<Integer> litCounter = factory.Code().createLiteral(location);
		args[0] = litCounter;
		for (int i = 0; i < arguments.length; i++)
			args[i + 1] = arguments[i];
		location++;

		perturbator.getField("number").replace(factory.Code().createCtField("number", factory.Type().INTEGER_PRIMITIVE, location + "", modifiersPublic));

		return factory.Code().createInvocation(typeAccess, execRef, args);
	}

	private static CtIf createIf(Factory factory) {
		CtIf ifFirstTime = factory.Core().createIf();
		CtExpression<Boolean> cond = factory.Code().createCodeSnippetExpression("firstTime && (l.contains(-1) || l.contains(location))");
		ifFirstTime.setCondition(cond);
		return ifFirstTime;
	}

	private static void createPertubation(Factory factory) {

		perturbator = factory.Class().create(factory.Package().create(factory.Package().getRootPackage(),"perturbation"), "Perturbator");
		perturbator.addModifier(ModifierKind.PUBLIC);

		//last statement in case we do not pertub
		CtStatement lastStmt = factory.Code().createCodeSnippetStatement("return value");

		//Boolean field first time
		perturbator.addField(factory.Code().createCtField("firstTime", factory.Type().BOOLEAN_PRIMITIVE, "true", modifiersPrivate));
		perturbator.addField(factory.Code().createCtField("oneTime", factory.Type().BOOLEAN_PRIMITIVE, "false", modifiersPrivate));

		//location of pertubation
		final CtTypeReference<List<Integer>> refListInt = factory.Code().createCtTypeReference(List.class);
		refListInt.addActualTypeArgument(factory.Type().INTEGER);
		perturbator.addField(factory.Code().createCtField("l", refListInt, "new java.util.ArrayList<Integer>()", modifiersPublic));
//		perturbator.addField(factory.Code().createCtField("l", factory.Type().INTEGER_PRIMITIVE, "2", modifiersPrivate));

		//number of pertubation
		perturbator.addField(factory.Code().createCtField("number", factory.Type().INTEGER_PRIMITIVE, "0", modifiersPublic));

		//location parameter
		CtParameter<Integer> location = factory.Core().createParameter();
		location.setSimpleName("location");
		location.setType(factory.Type().INTEGER_PRIMITIVE);

		// Pertubation Int
		CtMethod pInteger = factory.Core().createMethod();
		pInteger.setSimpleName("pint");
		pInteger.setType(factory.Type().INTEGER_PRIMITIVE);
		pInteger.addModifier(ModifierKind.PUBLIC);
		pInteger.addModifier(ModifierKind.STATIC);

		pInteger.addParameter(location);

		CtParameter<Integer> value = factory.Core().createParameter();
		value.setSimpleName("value");
		value.setType(factory.Type().INTEGER_PRIMITIVE);
		pInteger.addParameter(value);

		CtIf ifFirstTime = createIf(factory);
		CtCodeSnippetStatement statementMethod = factory.Code()
				.createCodeSnippetStatement("firstTime = !oneTime ; return (new java.util.Random()).nextInt()");
		ifFirstTime.setThenStatement(factory.Code().createCtBlock(statementMethod));
		CtBlock body = factory.Code().createCtBlock(ifFirstTime);
		body.addStatement(lastStmt);
		pInteger.setBody(body);

		perturbator.addMethod(pInteger);
		
		//Pertubation bool
		CtMethod  pBoolean = factory.Core().createMethod();
		pBoolean.setSimpleName("pboolean");
		pBoolean.setType(factory.Type().BOOLEAN_PRIMITIVE);
		pBoolean.addModifier(ModifierKind.PUBLIC);
		pBoolean.addModifier(ModifierKind.STATIC);

		pBoolean.addParameter(location);

		CtParameter<Boolean> valueBool = factory.Core().createParameter();
		valueBool.setSimpleName("value");
		valueBool.setType(factory.Type().BOOLEAN_PRIMITIVE);
		pBoolean.addParameter(valueBool);

		ifFirstTime = createIf(factory);
		statementMethod = factory.Code()
				.createCodeSnippetStatement("firstTime = !oneTime ; return !value");
		ifFirstTime.setThenStatement(factory.Code().createCtBlock(statementMethod));

		body = factory.Code().createCtBlock(ifFirstTime);
		body.addStatement(lastStmt);
		pBoolean.setBody(body);
		
		perturbator.addMethod(pBoolean);
		
		//Pertubation double
		CtMethod  pDouble = factory.Core().createMethod();
		pDouble.setSimpleName("pdouble");
		pDouble.setType(factory.Type().DOUBLE_PRIMITIVE);
		pDouble.addModifier(ModifierKind.PUBLIC);
		pDouble.addModifier(ModifierKind.STATIC);

		pDouble.addParameter(location);

		CtParameter<Double> valueDouble = factory.Core().createParameter();
		valueDouble.setSimpleName("value");
		valueDouble.setType(factory.Type().DOUBLE_PRIMITIVE);
		pDouble.addParameter(valueDouble );

		ifFirstTime = createIf(factory);
		statementMethod = factory.Code()
				.createCodeSnippetStatement("firstTime = !oneTime ; return (new java.util.Random()).nextDouble()");

		ifFirstTime.setThenStatement(factory.Code().createCtBlock(statementMethod));
		body = factory.Code().createCtBlock(ifFirstTime);
		body.addStatement(lastStmt);
		pDouble.setBody(body);

		perturbator.addMethod(pDouble);

		//pertubation Char
		CtMethod pChar = factory.Core().createMethod();
		pChar.setSimpleName("pchar");
		pChar.setType(factory.Type().CHARACTER_PRIMITIVE);
		pChar.addModifier(ModifierKind.STATIC);
		pChar.addModifier(ModifierKind.PUBLIC);

		pChar.addParameter(location);

		CtParameter<Character> valueChar = factory.Core().createParameter();
		valueChar.setSimpleName("value");
		valueChar.setType(factory.Type().CHARACTER_PRIMITIVE);
		pChar.addParameter(valueChar);

		ifFirstTime = createIf(factory);
		statementMethod = factory.Code()
				.createCodeSnippetStatement("firstTime = !oneTime ; return (char)(new java.util.Random()).nextInt()");

		ifFirstTime.setThenStatement(factory.Code().createCtBlock(statementMethod));
		body = factory.Code().createCtBlock(ifFirstTime);
		body.addStatement(lastStmt);
		pChar.setBody(body);

		perturbator.addMethod(pChar);


//		pertubation Byte
		CtMethod pByte = factory.Core().createMethod();
		pByte.setSimpleName("pbyte");
		pByte.setType(factory.Type().BYTE_PRIMITIVE);
		pByte.addModifier(ModifierKind.STATIC);
		pByte.addModifier(ModifierKind.PUBLIC);

		pByte.addParameter(location);

		CtParameter<Byte> valueByte = factory.Core().createParameter();
		valueByte.setSimpleName("value");
		valueByte.setType(factory.Type().BYTE_PRIMITIVE);
		pByte.addParameter(valueByte);

		ifFirstTime = createIf(factory);
		statementMethod = factory.Code()
				.createCodeSnippetStatement("firstTime = !oneTime ; return (byte)(new java.util.Random()).nextInt()");

		ifFirstTime.setThenStatement(factory.Code().createCtBlock(statementMethod));
		body = factory.Code().createCtBlock(ifFirstTime);
		body.addStatement(lastStmt);
		pByte.setBody(body);

		perturbator.addMethod(pByte);

		//pertubation long
		CtMethod pLong = factory.Core().createMethod();
		pLong.setSimpleName("plong");
		pLong.setType(factory.Type().LONG_PRIMITIVE);
		pLong.addModifier(ModifierKind.STATIC);
		pLong.addModifier(ModifierKind.PUBLIC);

		pLong.addParameter(location);

		CtParameter<Long> valueLong = factory.Core().createParameter();
		valueLong.setSimpleName("value");
		valueLong.setType(factory.Type().LONG_PRIMITIVE);
		pLong.addParameter(valueLong);

		ifFirstTime = createIf(factory);
		statementMethod = factory.Code()
				.createCodeSnippetStatement("firstTime = !oneTime ; return (new java.util.Random()).nextLong()");

		ifFirstTime.setThenStatement(factory.Code().createCtBlock(statementMethod));
		body = factory.Code().createCtBlock(ifFirstTime);
		body.addStatement(lastStmt);
		pLong.setBody(body);

		perturbator.addMethod(pLong);

		//pertubation float
		CtMethod pFloat = factory.Core().createMethod();
		pFloat.setSimpleName("pfloat");
		pFloat.setType(factory.Type().FLOAT_PRIMITIVE);
		pFloat.addModifier(ModifierKind.STATIC);
		pFloat.addModifier(ModifierKind.PUBLIC);

		pFloat.addParameter(location);

		CtParameter<Float> valueFloat = factory.Core().createParameter();
		valueFloat.setSimpleName("value");
		valueFloat.setType(factory.Type().FLOAT_PRIMITIVE);
		pFloat.addParameter(valueFloat);

		ifFirstTime = createIf(factory);
		statementMethod = factory.Code()
				.createCodeSnippetStatement("firstTime = !oneTime ; return (new java.util.Random()).nextFloat()");

		ifFirstTime.setThenStatement(factory.Code().createCtBlock(statementMethod));
		body = factory.Code().createCtBlock(ifFirstTime);
		body.addStatement(lastStmt);
		pFloat.setBody(body);

		perturbator.addMethod(pFloat);

		//pertubation short
		CtMethod pShort = factory.Core().createMethod();
		pShort.setSimpleName("pshort");
		pShort.setType(factory.Type().SHORT_PRIMITIVE);
		pShort.addModifier(ModifierKind.STATIC);
		pShort.addModifier(ModifierKind.PUBLIC);

		pShort.addParameter(location);

		CtParameter<Short> valueShort = factory.Core().createParameter();
		valueShort.setSimpleName("value");
		valueShort.setType(factory.Type().SHORT_PRIMITIVE);
		pShort.addParameter(valueShort);

		ifFirstTime = createIf(factory);
		statementMethod = factory.Code()
				.createCodeSnippetStatement("firstTime = !oneTime ; return (short)(new java.util.Random()).nextInt()");

		ifFirstTime.setThenStatement(factory.Code().createCtBlock(statementMethod));
		body = factory.Code().createCtBlock(ifFirstTime);
		body.addStatement(lastStmt);
		pShort.setBody(body);

		perturbator.addMethod(pShort);

	}

}
