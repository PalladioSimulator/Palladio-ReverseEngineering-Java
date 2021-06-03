/*******************************************************************************
 * Copyright (c) 2006-2012
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
 ******************************************************************************/
package org.emftext.language.java.resolver;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.imports.Import;
import org.emftext.language.java.resolver.result.IJavaReferenceResolveResult;

public class ClassifierImportClassifierReferenceResolver implements
	IJavaReferenceResolver<Import, ConcreteClassifier> {
	@Override
	public void resolve(String identifier, Import theImport, EReference reference, int position,
			IJavaReferenceResolveResult<ConcreteClassifier> result) {
		ConcreteClassifier importedClassifier = theImport.getImportedClassifier(identifier);
		if (importedClassifier != null) {
			if (importedClassifier.eIsProxy()) {
				importedClassifier = (ConcreteClassifier) EcoreUtil.resolve(importedClassifier, theImport.eResource());
			}
			if (!importedClassifier.eIsProxy()) {
				result.addMapping(identifier, importedClassifier);
			}
		}
	}
}
