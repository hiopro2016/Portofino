/*
 * Copyright (C) 2005-2014 ManyDesigns srl.  All rights reserved.
 * http://www.manydesigns.com/
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.manydesigns.portofino.reflection;

import com.manydesigns.elements.annotations.*;
import com.manydesigns.elements.annotations.impl.*;
import com.manydesigns.elements.reflection.PropertyAccessor;
import com.manydesigns.portofino.model.database.Column;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.Map;

/*
* @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
* @author Angelo Lupo          - angelo.lupo@manydesigns.com
* @author Giampiero Granatella - giampiero.granatella@manydesigns.com
* @author Alessio Stalla       - alessio.stalla@manydesigns.com
*/
public class ColumnAccessor
        extends AbstractAnnotatedAccessor 
        implements PropertyAccessor {
    public static final String copyright =
            "Copyright (c) 2005-2014, ManyDesigns srl";

    //**************************************************************************
    // Fields
    //**************************************************************************

    protected final Column column;
    protected final PropertyAccessor nestedPropertyAccessor;

    public static final Logger logger =
            LoggerFactory.getLogger(ColumnAccessor.class);


    //**************************************************************************
    // Constructors
    //**************************************************************************

    public ColumnAccessor(Column column, boolean inPk, boolean autoGenerated,
                          PropertyAccessor nestedPropertyAccessor) {
        super(column.getAnnotations());
        this.column = column;
        this.nestedPropertyAccessor = nestedPropertyAccessor;

        annotations.put(Required.class, new RequiredImpl(!column.isNullable()));
        if(String.class.equals(column.getActualJavaType())) {
            annotations.put(MaxLength.class, new MaxLengthImpl(column.getLength()));
        }
        if(column.getLength() > 0 && column.getScale() >= 0) {
            annotations.put(PrecisionScale.class,
                    new PrecisionScaleImpl(column.getLength(), column.getScale()));
        }
        annotations.put(Enabled.class, new EnabledImpl(true));
        annotations.put(Updatable.class, new UpdatableImpl(!inPk));
        annotations.put(Insertable.class,
                new InsertableImpl(!column.isAutoincrement() && !autoGenerated));
        annotations.put(Searchable.class,
                new SearchableImpl(column.isSearchable()));
    }


    //**************************************************************************
    // PropertyAccessor implementation
    //**************************************************************************

    public String getName() {
        return column.getActualPropertyName();
    }

    public Class getType() {
        return column.getActualJavaType();
    }

    public int getModifiers() {
        return Modifier.PUBLIC;
    }

    public Object get(Object obj) {
        if (nestedPropertyAccessor == null) {
            return ((Map)obj).get(column.getActualPropertyName());
        } else {
            return nestedPropertyAccessor.get(obj);
        }
    }

    public void set(Object obj, Object value) {
        if (nestedPropertyAccessor == null) {
            //noinspection unchecked
            ((Map)obj).put(column.getActualPropertyName(), value);
        } else {
            nestedPropertyAccessor.set(obj, value);
        }
    }

    //**************************************************************************
    // Getters/setters
    //**************************************************************************

    public Column getColumn() {
        return column;
    }

    //**************************************************************************
    // toString()
    //**************************************************************************

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("column", column.getQualifiedName()).toString();
    }
}
