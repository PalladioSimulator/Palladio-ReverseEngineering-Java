/*******************************************************************************
 * Copyright (c) 2006-2014
 * Software Technology Group, Dresden University of Technology
 * DevBoost GmbH, Berlin, Amtsgericht Charlottenburg, HRB 140026
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Software Technology Group - TU Dresden, Germany;
 *   DevBoost GmbH - Berlin, Germany
 *      - initial API and implementation
 *   Martin Armbruster
 *      - Extension of getStatements()
 ******************************************************************************/
package org.emftext.language.java.extensions.members;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.classifiers.Interface;
import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.expressions.LambdaExpression;
import org.emftext.language.java.members.Member;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.modifiers.Default;
import org.emftext.language.java.parameters.Parameter;
import org.emftext.language.java.parameters.ReceiverParameter;
import org.emftext.language.java.parameters.VariableLengthParameter;
import org.emftext.language.java.references.MethodCall;
import org.emftext.language.java.statements.Block;
import org.emftext.language.java.statements.Return;
import org.emftext.language.java.statements.Statement;
import org.emftext.language.java.types.InferableType;
import org.emftext.language.java.types.Type;
import org.emftext.language.java.types.TypeReference;
import org.emftext.language.java.types.TypesFactory;

public class MethodExtension {
	
	/**
	 * Returns <code>true</code> if the given method matches the given call.
	 * 
	 * @param methodCall
	 * @return
	 */
	public static boolean isSomeMethodForCall(Method me, MethodCall methodCall) {
		return me.isMethodForCall(methodCall, false);
	}

	/**
	 * Returns <code>true</code> if the given {@link Method} <code>me</code> is
	 * a better match for the given method call than {@link Method}
	 * <code>otherMethod</code>.
	 * 
	 * @param otherMethod
	 * @param methodCall
	 * @return
	 */
	public static boolean isBetterMethodForCall(Method me, Method otherMethod,
			MethodCall methodCall) {
		
		if (!me.isMethodForCall(methodCall, false)) {
			return false;
		}
		
		if (otherMethod.isMethodForCall(methodCall, true)) {
			if (me.isMethodForCall(methodCall, true)) {
				// We both match perfectly; lets compare our return types
				Type target = me.getTypeReference().getTarget();
				if (target instanceof ConcreteClassifier) {
					if (((ConcreteClassifier) target).getAllSuperClassifiers().contains(
							otherMethod.getTypeReference().getTarget())) {
						// I am the more concrete type
						return true;
					}
				}
			}
			
			//the other already matches perfectly; I am not better
			return false;
		}
		
		if (!otherMethod.isMethodForCall(methodCall, false)) {
			//I match, but the other does not
			return true;
		}
		//we both match, I am only better if I match perfectly <- 
		//TODO #763: this is not enough: we need to check for "nearest subtype" here
		return me.isMethodForCall(methodCall, true);
	}

	public static boolean isMethodForCall(Method me, MethodCall methodCall,
			boolean needsPerfectMatch) {
		
		EList<Type> argumentTypeList = methodCall.getArgumentTypes();
		EList<Parameter> parameterList = new BasicEList<Parameter>(me.getParameters());
		
		if (parameterList.size() > 0 && parameterList.get(0) instanceof ReceiverParameter) {
			parameterList.remove(0);
		}
		
		EList<Type> parameterTypeList = new BasicEList<Type>();
		for (Parameter parameter : parameterList)  {
			// Determine types before messing with the parameters
			TypeReference typeReference = parameter.getTypeReference();
			Type boundTarget = typeReference.getBoundTarget(methodCall);
			parameterTypeList.add(boundTarget);
		}

		if (!parameterList.isEmpty()) {
			Parameter lastParameter = parameterList.get(parameterList.size() - 1);
			Type lastParameterType  = parameterTypeList.get(parameterTypeList.size() - 1);
			if (lastParameter instanceof VariableLengthParameter) {
				// In case of variable length add/remove some parameters
				while (parameterList.size() < argumentTypeList.size()) {
					if (needsPerfectMatch) {
						return false;
					}
					parameterList.add(lastParameter);
					parameterTypeList.add(lastParameterType);
				}
				
				if (parameterList.size() > argumentTypeList.size()) {
					if (needsPerfectMatch) {
						return false;
					}
					parameterList.remove(lastParameter);
					parameterTypeList.remove(parameterTypeList.size() - 1);
				}
			}
		}
		
		if (parameterList.size() == argumentTypeList.size()) { 
			boolean parametersMatch = true;
			for (int i = 0; i < argumentTypeList.size() && parametersMatch; i++) {
				Parameter parameter = parameterList.get(i);
				Expression argument = methodCall.getArguments().get(i);

				Type parameterType = parameterTypeList.get(i);
				Type argumentType = argumentTypeList.get(i);
				
				if (argumentType == null || parameterType == null) {
					return false;
				}
				
				if (!parameterType.eIsProxy() || !argumentType.eIsProxy()) {
					if (argument instanceof LambdaExpression) {
						if (!(parameterType instanceof Interface)) {
							return false;
						}
						parametersMatch = parametersMatch
								&& doesLambdaMatchFunctionalInterface((Interface) parameterType,
										(LambdaExpression) argument);
						continue;
					}
					long argumentArrayDimension = argument.getArrayDimension();
					if (needsPerfectMatch) {
						long parameterArrayDimension = parameter.getArrayDimension();
						parametersMatch = parametersMatch
							&& argumentType.equalsType(argumentArrayDimension,
									parameterType, parameterArrayDimension);
					} else {
						parametersMatch = parametersMatch 
							&& argumentType.isSuperType(argumentArrayDimension,
									parameterType, parameter);
					}
				} else {
					return false;
				}
			}
			return parametersMatch;
		}
		
		return false;		
	}
	
	private static boolean doesLambdaMatchFunctionalInterface(Interface functionalInterface, LambdaExpression expr) {
		Method m = findFunctionalMethod(functionalInterface);
		if (m.getParameters().size() == expr.getParameters().getParameters().size()) {
			for (int index = 0; index < m.getParameters().size(); index++) {
				Parameter lambdaParam = expr.getParameters().getParameters().get(index);
				if (!(lambdaParam.getTypeReference() instanceof InferableType)) {
					Parameter methodParameter = m.getParameters().get(index);
					if (!lambdaParam.getTypeReference().getTarget()
							.isSuperType(lambdaParam.getArrayDimension(),
								methodParameter.getTypeReference().getTarget(), methodParameter)) {
						return false;
					}
				}
			}
			Type methReturn = m.getTypeReference().getTarget();
			Type lambdaReturn = getReturnType(expr, methReturn);
			return lambdaReturn.isSuperType(expr.getArrayDimension(), methReturn, m);
		}
		return false;
	}
	
	private static Type getReturnType(LambdaExpression me, Type potentialReturnType) {
		if (me.getBody() instanceof LambdaExpression) {
			if (!(potentialReturnType instanceof Interface)) {
				return null;
			}
			if (doesLambdaMatchFunctionalInterface((Interface) potentialReturnType,
					(LambdaExpression) me.getBody())) {
				return potentialReturnType;
			}
		} else if (me.getBody() instanceof Expression) {
				return ((Expression) me.getBody()).getType();
		} else {
			Block b = (Block) me.getBody();
			EList<Return> list = b.getChildrenByType(Return.class);
			if (list.isEmpty() || list.get(0).getReturnValue() != null) {
				return TypesFactory.eINSTANCE.createVoid();
			}
			return list.get(0).getReturnValue().getType();
		}
		return null;
	}
	
	/**
	 * Checks if the signature, i. e., the parameter types and return type, of two methods match,
	 * independently of the method and parameter names. It is possible for one method to have specialized parameter
	 * and return types compared to the other method.
	 * 
	 * @param one the first method.
	 * @param two the second method which can have specialized types.
	 * @return true if the signatures match. false otherwise.
	 */
	public static boolean isSignatureMatching(Method one, Method two) {
		EList<Parameter> parameterListOne = new BasicEList<>(one.getParameters());
		EList<Parameter> parameterListTwo = new BasicEList<>(two.getParameters());
		
		if (parameterListOne.size() > 0 && parameterListOne.get(0) instanceof ReceiverParameter) {
			parameterListOne.remove(0);
		}
		if (parameterListTwo.size() > 0 && parameterListTwo.get(0) instanceof ReceiverParameter) {
			parameterListTwo.remove(0);
		}

		if (parameterListOne.size() == parameterListTwo.size()) { 
			boolean parametersMatch = true;
			for (int i = 0; i < parameterListOne.size() && parametersMatch; i++) {
				Parameter paramOne = parameterListOne.get(i);
				Parameter paramTwo = parameterListTwo.get(i);
				
				Type parameterType = paramOne.getTypeReference().getTarget();
				Type argumentType = paramTwo.getTypeReference().getTarget();
				
				if (argumentType == null || parameterType == null) {
					return false;
				}
				
				if (!parameterType.eIsProxy() || !argumentType.eIsProxy()) {
					parametersMatch = parametersMatch && argumentType.isSuperType(
						paramTwo.getArrayDimension(), parameterType, paramOne);
				} else {
					return false;
				}
			}
			
			Type target = two.getTypeReference().getTarget();
			parametersMatch = parametersMatch && target.isSuperType(two.getArrayDimension(),
				one.getTypeReference().getTarget(), one);
			
			return parametersMatch;
		}
		return false;
	}
	
	/**
	 * Returns a list of all statements within the block of a method.
	 * This is a legacy method to provide a stable and backwards-compatible API.
	 * 
	 * @param me the method for which the statements are obtained.
	 * @return the list of all statements.
	 * @deprecated Use getBlock().getStatements().
	 */
	@Deprecated
	public static EList<Statement> getStatements(Method me) {
		Block b = getBlock(me);
		return b != null ? b.getStatements() : new BasicEList<>();
	}
	
	/**
	 * Returns a block representing the body of a method.
	 * 
	 * @param me the method for which the body is returned.
	 * @return the block or null if the method has no implementation.
	 */
	public static Block getBlock(Method me) {
		if (me.getStatement() instanceof Block) {
			return (Block) me.getStatement();
		}
		return null;
	}
	
	/**
	 * Finds the method of a functional interface.
	 * 
	 * @param classifier the functional interface.
	 * @return the method.
	 */
	public static Method findFunctionalMethod(ConcreteClassifier classifier) {
		ConcreteClassifier objectClass = classifier.getObjectClass();
		for (Member mem : classifier.getAllMembers(classifier)) {
			if (mem instanceof Method && !((Method) mem).isStatic() && !((Method) mem).hasModifier(Default.class)) {
				if (objectClass.getMembersByName(mem.getName()).isEmpty()) {
					return (Method) mem;
				}
			}
		}
		return null;
	}
}
