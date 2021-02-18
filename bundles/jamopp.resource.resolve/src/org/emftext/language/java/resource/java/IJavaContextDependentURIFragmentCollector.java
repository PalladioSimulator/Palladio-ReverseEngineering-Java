package org.emftext.language.java.resource.java;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.emftext.language.java.resource.java.mopp.JavaContextDependentURIFragment;

public interface IJavaContextDependentURIFragmentCollector {
	IJavaContextDependentURIFragmentCollector GLOBAL_INSTANCE = new IJavaContextDependentURIFragmentCollectorImpl();
	
	void setBaseURI(URI uri);
	<ContainerType extends EObject> void registerContextDependentURIFragment(ContainerType container,
			EReference reference, String id, EObject proxyElement, int position);
	Map<String, IJavaContextDependentURIFragment> getContextDependentURIFragmentMap();
	
	class IJavaContextDependentURIFragmentCollectorImpl implements IJavaContextDependentURIFragmentCollector {
		private URI altURI = URI.createURI("empty:/nothing.java");
		private URI baseURI;
		private int proxyCounter = 0;
		private Map<String, IJavaContextDependentURIFragment> internalURIFragmentMap = new LinkedHashMap<>();
		
		@Override
		public void setBaseURI(URI uri) {
			baseURI = uri;
		}
		
		@Override
		public <ContainerType extends EObject> void registerContextDependentURIFragment(ContainerType container,
				EReference reference, String id, EObject proxyElement, int position) {
			InternalEObject proxy = (InternalEObject) proxyElement;
			String internalURIFragment = IJavaContextDependentURIFragment.INTERNAL_URI_FRAGMENT_PREFIX
					+ (proxyCounter++) + "_" + id;
			IJavaContextDependentURIFragment uriFragment = new JavaContextDependentURIFragment<ContainerType>(
					id, container, reference, position, proxy);
			proxy.eSetProxyURI((baseURI == null ? altURI : baseURI).appendFragment(internalURIFragment));
			internalURIFragmentMap.put(internalURIFragment, uriFragment);
		}
		
		@Override
		public Map<String, IJavaContextDependentURIFragment> getContextDependentURIFragmentMap() {
			return internalURIFragmentMap;
		}
	}
}
