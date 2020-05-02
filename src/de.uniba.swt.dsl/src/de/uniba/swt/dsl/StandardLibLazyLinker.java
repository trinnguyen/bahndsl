/*
 * This file is part of the BahnDSL project, a domain-specific language
 * for configuring and modelling model railways
 *
 * BahnDSL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BahnDSL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BahnDSL.  If not, see <https://www.gnu.org/licenses/>.
 *
 * The following people contributed to the conception and realization of the
 * present BahnDSL (in alphabetic order by surname):
 *
 * - Tri Nguyen <https://github.com/trinnguyen>
 */

package de.uniba.swt.dsl;

import de.uniba.swt.dsl.generator.StandardLibHelper;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.diagnostics.IDiagnosticConsumer;
import org.eclipse.xtext.linking.lazy.LazyLinker;

public class StandardLibLazyLinker extends LazyLinker {
	
    @Override
    protected void beforeModelLinked(EObject model, IDiagnosticConsumer diagnosticsConsumer) {
        super.beforeModelLinked(model, diagnosticsConsumer);
        var set = model.eResource().getResourceSet();
        if (set.getResources().size() < 2 && !model.eResource().getURI().toString().endsWith(StandardLibHelper.FILE_NAME)) {
        	StandardLibHelper.loadStandardLibResource(set);
        }
    }
}
