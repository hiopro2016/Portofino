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

package com.manydesigns.portofino.model.site;

import com.manydesigns.elements.annotations.Required;
import com.manydesigns.portofino.model.Model;
import com.manydesigns.portofino.model.ModelObject;
import com.manydesigns.portofino.xml.Identifier;
import javax.xml.bind.annotation.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/*
* @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
* @author Angelo Lupo          - angelo.lupo@manydesigns.com
* @author Giampiero Granatella - giampiero.granatella@manydesigns.com
*/
@XmlAccessorType(value = XmlAccessType.NONE)
public abstract class  SiteNode implements ModelObject {
    public static final String copyright =
            "Copyright (c) 2005-2010, ManyDesigns srl";


    //**************************************************************************
    // Fields
    //**************************************************************************

    protected final SiteNode parent;
    protected final ArrayList<SiteNode> childNodes;

    protected Permissions permissions;
    protected String id;
    protected String actualId;
    protected String actualUrl;
    protected String title;
    protected String description;
    protected String url;


    //**************************************************************************
    // Constructors
    //**************************************************************************

    public SiteNode(SiteNode parent) {
        this.childNodes = new ArrayList<SiteNode>();
        this.parent= parent;
    }

    public SiteNode( SiteNode parent, String id, String title, String description) {
        this(parent);
        this.id = id;
        this.title = title;
        this.description = description;
    }


    //**************************************************************************
    // ModelObject implementation
    //**************************************************************************

    public void reset() {
        for (SiteNode childNode : childNodes) {
            childNode.reset();
        }
    }

    public void init(Model model) {
        if (parent!=null){
            actualId = parent.actualId;
            actualId = (actualId.endsWith("/")?actualId:actualId+"/");
            actualId = actualId+id;
        }else {
            actualId = id;
        }

        for (SiteNode childNode : childNodes) {
            childNode.init(model);
        }

        if (url==null){
            actualUrl = MessageFormat.format(getUrlFormat(), actualId);
        } else {
            actualUrl = url;
        }
    }

    protected abstract String getUrlFormat();

    public String getQualifiedName() {
        return null;
    }

    //**************************************************************************
    // Getters/Setters
    //**************************************************************************


    @Identifier
    @XmlAttribute(required = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute(required = true)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @XmlAttribute(required = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement()
    public Permissions getPermissions() {
        return permissions;
    }

    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
    }

    @XmlElementWrapper(name="childNodes")
    @XmlElements({
          @XmlElement(name="documentNode",type=DocumentNode.class),
          @XmlElement(name="folderNode",type=FolderNode.class),
          @XmlElement(name="customNode",type=CustomNode.class),
          @XmlElement(name="customFolderNode",type=CustomFolderNode.class),
          @XmlElement(name="useCaseNode",type=UseCaseNode.class),
          @XmlElement(name="portletNode",type=PortletNode.class)
    })
    public List<SiteNode> getChildNodes() {
        return childNodes;
    }

    public SiteNode getParent() {
        return parent;
    }

    public String getActualUrl() {
        return actualUrl;
    }

    public String getActualId() {
        return actualId;
    }

    public void setActualId(String actualId) {
        this.actualId = actualId;
    }

    @XmlAttribute()
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isAllowed(List<String> groups) {
        boolean parentAllowed= true;
        if (parent != null){
            parentAllowed= parent.isAllowed(groups);
        }
        if (!parentAllowed) {
            return false;
        }

        boolean result = true;
        if (permissions != null) {
            result = permissions.isAllowed(groups);
        }
        return result;
    }
}
