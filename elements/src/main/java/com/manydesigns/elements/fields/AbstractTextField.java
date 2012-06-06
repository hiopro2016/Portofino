/*
 * Copyright (C) 2005-2012 ManyDesigns srl.  All rights reserved.
 * http://www.manydesigns.com/
 *
 * Unless you have purchased a commercial license agreement from ManyDesigns srl,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.manydesigns.elements.fields;

import com.manydesigns.elements.Mode;
import com.manydesigns.elements.annotations.FieldSize;
import com.manydesigns.elements.annotations.MaxLength;
import com.manydesigns.elements.reflection.PropertyAccessor;
import com.manydesigns.elements.xml.XhtmlBuffer;
import org.apache.commons.lang.StringUtils;

/*
* @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
* @author Angelo Lupo          - angelo.lupo@manydesigns.com
* @author Giampiero Granatella - giampiero.granatella@manydesigns.com
* @author Alessio Stalla       - alessio.stalla@manydesigns.com
*/
public abstract class AbstractTextField extends AbstractField {
    public static final String copyright =
            "Copyright (c) 2005-2012, ManyDesigns srl";

    protected String stringValue;
    protected boolean autoCapitalize = false;
    protected boolean replaceBadUnicodeCharacters = true;
    protected Integer maxLength = null;

    protected Integer size;

    //**************************************************************************
    // Costruttori
    //**************************************************************************
    public AbstractTextField(PropertyAccessor accessor, Mode mode) {
        this(accessor, mode, null);
    }

    public AbstractTextField(PropertyAccessor accessor, Mode mode, String prefix) {
        super(accessor, mode, prefix);
        if (accessor.isAnnotationPresent(MaxLength.class)) {
            maxLength = accessor.getAnnotation(MaxLength.class).value();
        }
        if (accessor.isAnnotationPresent(FieldSize.class)) {
            size = accessor.getAnnotation(FieldSize.class).value();
        }
    }

    //**************************************************************************
    // Implementazione di Element
    //**************************************************************************
    public boolean validate() {
        if (mode.isView(insertable, updatable) || (mode.isBulk() && !bulkChecked)) {
            return true;
        }

        boolean result = true;
        if (required && StringUtils.isBlank(stringValue)) {
            errors.add(getText("elements.error.field.required"));
            result = false;
        }
        if (maxLength != null && StringUtils.length(stringValue) > maxLength) {
            errors.add(getText("elements.error.field.length.exceeded", maxLength));
            result = false;
        }
        return result;
    }

    //**************************************************************************
    // Field implementation
    //**************************************************************************
    public void valueToXhtml(XhtmlBuffer xb) {
        if (mode.isView(insertable, updatable)) {
            valueToXhtmlView(xb);
        } else if (mode.isEdit()) {
            valueToXhtmlEdit(xb);
        } else if (mode.isPreview()) {
            valueToXhtmlPreview(xb);
        } else if (mode.isHidden()) {
            xb.writeInputHidden(id, inputName, stringValue);
        } else {
            throw new IllegalStateException("Unknown mode: " + mode);
        }
    }

    protected void valueToXhtmlEdit(XhtmlBuffer xb) {
        xb.writeInputText(id, inputName, stringValue,
                fieldCssClass, size, maxLength);
        if(mode.isBulk()) {
            xb.writeJavaScript(
                    "$(function() { " +
                        "configureBulkEditTextField('" + id + "', '" + bulkCheckboxName + "'); " +
                    "});");
        }
    }

    protected void valueToXhtmlPreview(XhtmlBuffer xb) {
        valueToXhtmlView(xb);
        xb.writeInputHidden(inputName, stringValue);
    }

    protected void valueToXhtmlView(XhtmlBuffer xb) {
        xb.openElement("div");
        xb.addAttribute("class", fieldCssClass);
        xb.addAttribute("id", id);
        if (href == null) {
            xb.write(stringValue);
        } else {
            xb.writeAnchor(href, stringValue, null, title);
        }
        xb.closeElement("div");
    }

    //**************************************************************************
    // Getters/setters
    //**************************************************************************
    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public boolean isAutoCapitalize() {
        return autoCapitalize;
    }

    public void setAutoCapitalize(boolean autoCapitalize) {
        this.autoCapitalize = autoCapitalize;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public boolean isReplaceBadUnicodeCharacters() {
        return replaceBadUnicodeCharacters;
    }

    public void setReplaceBadUnicodeCharacters(boolean replaceBadUnicodeCharacters) {
        this.replaceBadUnicodeCharacters = replaceBadUnicodeCharacters;
    }
}
