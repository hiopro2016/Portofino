/*
 * Copyright (C) 2005-2010 ManyDesigns srl.  All rights reserved.
 * http://www.manydesigns.com/
 *
 * Unless you have purchased a commercial license agreement from ManyDesigns srl,
 * the following license terms apply:
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * There are special exceptions to the terms and conditions of the GPL
 * as it is applied to this software. View the full text of the
 * exception in file OPEN-SOURCE-LICENSE.txt in the directory of this
 * software distribution.
 *
 * This program is distributed WITHOUT ANY WARRANTY; and without the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see http://www.gnu.org/licenses/gpl.txt
 * or write to:
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307  USA
 *
 */

package com.manydesigns.portofino.base.reflection;

import com.manydesigns.elements.reflection.ClassAccessor;
import com.manydesigns.elements.reflection.PropertyAccessor;
import com.manydesigns.portofino.base.model.Table;
import com.manydesigns.portofino.base.model.Column;

import java.util.List;

/*
* @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
* @author Angelo Lupo          - angelo.lupo@manydesigns.com
* @author Giampiero Granatella - giampiero.granatella@manydesigns.com
*/
public class TableAccessor implements ClassAccessor {
    public static final String copyright =
            "Copyright (c) 2005-2010, ManyDesigns srl";

    private final Table table;

    public TableAccessor(Table table) {
        this.table = table;
    }

    public PropertyAccessor getProperty(String fieldName) throws NoSuchFieldException {
        for (Column current : table.getColumns()) {
            if (current.getColumnName().equals(fieldName)) {
                return new ColumnAccessor(current);
            }
        }

        throw new NoSuchFieldException(fieldName);
    }

    public PropertyAccessor[] getProperties() {
        List<Column> columns = table.getColumns();
        ColumnAccessor[] result =
                new ColumnAccessor[columns.size()];
        int i = 0;
        for (Column current : columns) {
            result[i] = new ColumnAccessor(current);
            i++;
        }
        return result;
    }
}
