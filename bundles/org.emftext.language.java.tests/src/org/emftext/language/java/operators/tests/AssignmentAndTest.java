/**
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
 *      - Extension for Java 7-13
 *  
 */
package org.emftext.language.java.operators.tests;

import junit.textui.TestRunner;

import org.emftext.language.java.operators.AssignmentAnd;
import org.emftext.language.java.operators.OperatorsFactory;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Assignment And</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class AssignmentAndTest extends AssignmentOperatorTest {

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(AssignmentAndTest.class);
	}

	/**
	 * Constructs a new Assignment And test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AssignmentAndTest(String name) {
		super(name);
	}

	/**
	 * Returns the fixture for this Assignment And test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected AssignmentAnd getFixture() {
		return (AssignmentAnd)fixture;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#setUp()
	 * @generated
	 */
	@Override
	protected void setUp() throws Exception {
		setFixture(OperatorsFactory.eINSTANCE.createAssignmentAnd());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#tearDown()
	 * @generated
	 */
	@Override
	protected void tearDown() throws Exception {
		setFixture(null);
	}

} //AssignmentAndTest