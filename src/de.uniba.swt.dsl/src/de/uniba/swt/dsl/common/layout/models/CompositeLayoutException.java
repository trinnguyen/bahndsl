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

package de.uniba.swt.dsl.common.layout.models;

import java.util.List;
import java.util.stream.Collectors;

public class CompositeLayoutException extends Exception {
    private List<LayoutException> exceptions;

    public CompositeLayoutException(String message) {
        this(List.of(new LayoutException(message)));
    }

    public CompositeLayoutException(List<LayoutException> exceptions) {
        super(exceptions.stream().map(Throwable::getMessage).collect(Collectors.joining("\n")), exceptions.get(0));
        this.exceptions = exceptions;
    }

    public List<LayoutException> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<LayoutException> exceptions) {
        this.exceptions = exceptions;
    }
}
