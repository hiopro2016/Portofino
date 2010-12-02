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

package com.manydesigns.elements.forms;

import com.manydesigns.elements.fields.helpers.FieldsManager;
import com.manydesigns.elements.options.SelectionProvider;
import com.manydesigns.elements.reflection.ClassAccessor;
import com.manydesigns.elements.reflection.PropertyAccessor;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.*;

/*
* @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
* @author Angelo Lupo          - angelo.lupo@manydesigns.com
* @author Giampiero Granatella - giampiero.granatella@manydesigns.com
*/
public class AbstractFormBuilder {
    public static final String copyright =
            "Copyright (c) 2005-2010, ManyDesigns srl";

    //**************************************************************************
    // Constants
    //**************************************************************************

    public final static String[] PROPERTY_NAME_BLACKLIST = {"class"};

    //**************************************************************************
    // Fields
    //**************************************************************************

    protected final FieldsManager manager;
    protected final ClassAccessor classAccessor;
    protected final Map<String[], SelectionProvider> selectionProviders;

    protected String prefix;

    //**************************************************************************
    // Logging
    //**************************************************************************

    public static final Logger logger =
            LoggerFactory.getLogger(AbstractFormBuilder.class);
    
    //**************************************************************************
    // Fields
    //**************************************************************************

    public AbstractFormBuilder(ClassAccessor classAccessor) {
        logger.debug("Entering AbstractBuilder constructor");

        manager = FieldsManager.getManager();
        this.classAccessor = classAccessor;
        selectionProviders = new HashMap<String[], SelectionProvider>();

        logger.debug("Exiting AbstractBuilder constructor");
    }

    //**************************************************************************
    // Utility methods
    //**************************************************************************

    protected boolean skippableProperty(PropertyAccessor propertyAccessor) {
        // static field?
        if (Modifier.isStatic(propertyAccessor.getModifiers())) {
            return true;
        }
        // blacklisted?
        if (ArrayUtils.contains(PROPERTY_NAME_BLACKLIST,
                propertyAccessor.getName())) {
            return true;
        }
        return false;
    }


    protected void removeUnusedSelectionProviders(
            Collection<PropertyAccessor> propertyAccessors) {
        List<String> propertyNames = new ArrayList<String>();
        for (PropertyAccessor propertyAccessor : propertyAccessors) {
            propertyNames.add(propertyAccessor.getName());
        }
        List<String[]> removeList = new ArrayList<String[]>();
        for (String[] current : selectionProviders.keySet()) {
            List<String> currentNames = Arrays.asList(current);
            if (!propertyNames.containsAll(currentNames)) {
                removeList.add(current);
            }
        }
        for (String[] current : removeList) {
            selectionProviders.remove(current);
        }
    }
}