/*
 * Copyright (C) 2005-2011 ManyDesigns srl.  All rights reserved.
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

package com.manydesigns.elements.fields;

import com.manydesigns.elements.ElementsProperties;
import com.manydesigns.elements.Mode;
import com.manydesigns.elements.annotations.DateFormat;
import com.manydesigns.elements.reflection.PropertyAccessor;
import com.manydesigns.elements.xml.XhtmlBuffer;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringEscapeUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.Date;

/*
* @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
* @author Angelo Lupo          - angelo.lupo@manydesigns.com
* @author Giampiero Granatella - giampiero.granatella@manydesigns.com
* @author Alessio Stalla       - alessio.stalla@manydesigns.com
*/
public class DateField extends AbstractTextField {
    public static final String copyright =
            "Copyright (c) 2005-2011, ManyDesigns srl";

    //**************************************************************************
    // Fields
    //**************************************************************************

    protected final String datePattern;
    protected final DateTimeFormatter dateTimeFormatter;
    protected final boolean containsTime;
    protected final String jsDatePattern;

    protected Date dateValue;
    protected boolean dateFormatError;


    //**************************************************************************
    // Constructors
    //**************************************************************************

    public DateField(PropertyAccessor accessor, Mode mode) {
        this(accessor, mode, null);
    }

    public DateField(PropertyAccessor accessor, Mode mode, String prefix) {
        super(accessor, mode, prefix);

        DateFormat dateFormatAnnotation =
                accessor.getAnnotation(DateFormat.class);
        if (dateFormatAnnotation != null) {
            datePattern = dateFormatAnnotation.value();
        } else {
            Configuration elementsConfiguration =
                    ElementsProperties.getConfiguration();
            datePattern = elementsConfiguration.getString(
                    ElementsProperties.FIELDS_DATE_FORMAT);
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(datePattern);
        setMaxLength(dateTimeFormatter.getParser().estimateParsedLength());

        containsTime = datePattern.contains("HH")
                || datePattern.contains("mm")
                || datePattern.contains("ss");

        if(!containsTime) {
            dateTimeFormatter = dateTimeFormatter.withZone(DateTimeZone.UTC);
        }
        this.dateTimeFormatter = dateTimeFormatter;

        String tmpPattern = datePattern;
        if (tmpPattern.contains("yyyy")) {
            tmpPattern = tmpPattern.replaceAll("yyyy", "yy");
        }
        if (tmpPattern.contains("MM")) {
            tmpPattern = tmpPattern.replaceAll("MM", "mm");
        }
        if (tmpPattern.contains("dd")) {
            tmpPattern = tmpPattern.replaceAll("dd", "dd");
        }
        jsDatePattern = tmpPattern;
    }


    //**************************************************************************
    // Element implementation
    //**************************************************************************

    public void readFromRequest(HttpServletRequest req) {
        super.readFromRequest(req);

        if (mode.isView(immutable, autogenerated)) {
            return;
        }

        String reqValue = req.getParameter(inputName);
        if (reqValue == null) {
            return;
        }

        stringValue = reqValue.trim();
        dateFormatError = false;
        dateValue = null;

        if (stringValue.length() == 0) {
            return;
        }

        try {
            if(containsTime) {
                DateTime dateTime = dateTimeFormatter.parseDateTime(stringValue);
                dateValue = new Date(dateTime.getMillis());
            } else {
                long millis = dateTimeFormatter.parseMillis(stringValue);
                LocalDate localDate = new LocalDate(millis);
                dateValue = localDate.toDateTimeAtStartOfDay().toDate();
            }
        } catch (Throwable e) {
            dateFormatError = true;
            logger.debug("Cannot parse date: {}", stringValue);
        }
    }

    @Override
    public boolean validate() {
        if (mode.isView(immutable, autogenerated)
                || (mode.isBulk() && !bulkChecked)) {
            return true;
        }

        if (!super.validate()) {
            return false;
        }

        if (dateFormatError) {
            errors.add(getText("elements.error.field.date.format"));
            return false;
        }

        return true;
    }

    public void readFromObject(Object obj) {
        super.readFromObject(obj);
        if (obj == null) {
            dateValue = null;
        } else {
            Object value = accessor.get(obj);
            if (value == null) {
                dateValue = null;
            } else {
                dateValue = (Date)value;
            }
        }
        if (dateValue == null) {
            stringValue = null;
        } else {
            DateTime dateTime = new DateTime(dateValue);
            stringValue = dateTimeFormatter.print(dateTime);
        }
    }

    public void writeToObject(Object obj) {
        writeToObject(obj, dateValue);
    }

    //**************************************************************************
    // AbstractTextField overrides
    //**************************************************************************

    @Override
    public void valueToXhtmlEdit(XhtmlBuffer xb) {
        xb.openElement("input");
        xb.addAttribute("type", "text");
        xb.addAttribute("class", "text");
        xb.addAttribute("id", id);
        xb.addAttribute("name", inputName);
        if (stringValue != null) {
            xb.addAttribute("value", stringValue);
        }
        if (maxLength != null) {
            xb.addAttribute("maxlength", Integer.toString(maxLength));
            xb.addAttribute("size", Integer.toString(maxLength));
        }

        xb.closeElement("input");
        xb.write(" (");
        xb.write(datePattern);
        xb.write(") ");

        if (!containsTime) {
            String js = MessageFormat.format(
                    "setupDatePicker(''#{0}'', ''{1}'');",
                    StringEscapeUtils.escapeJavaScript(id),
                    StringEscapeUtils.escapeJavaScript(jsDatePattern));
            xb.writeJavaScript(js);
        }
    }

    //**************************************************************************
    // Getters/getters
    //**************************************************************************

    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    public String getDatePattern() {
        return datePattern;
    }

}
