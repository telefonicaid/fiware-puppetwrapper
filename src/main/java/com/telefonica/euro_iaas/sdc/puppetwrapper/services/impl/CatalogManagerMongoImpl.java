/**
 * Copyright 2014 Telefonica Investigación y Desarrollo, S.A.U <br>
 * This file is part of FI-WARE project.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License.
 * </p>
 * <p>
 * You may obtain a copy of the License at:<br>
 * <br>
 * http://www.apache.org/licenses/LICENSE-2.0
 * </p>
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * </p>
 * <p>
 * See the License for the specific language governing permissions and limitations under the License.
 * </p>
 * <p>
 * For those usages not covered by the Apache version 2.0 License please contact with opensource@tid.es
 * </p>
 */

package com.telefonica.euro_iaas.sdc.puppetwrapper.services.impl;

import static java.text.MessageFormat.format;

import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.telefonica.euro_iaas.sdc.puppetwrapper.data.Node;
import com.telefonica.euro_iaas.sdc.puppetwrapper.services.CatalogManager;

@Service("catalogManagerMongo")
public class CatalogManagerMongoImpl implements CatalogManager {

    @Resource
    protected MongoTemplate mongoTemplate;

    private String eol = System.getProperty("line.separator");

    public void addNode(Node node) {
        try {
            getNode(node.getId());
            mongoTemplate.remove(node);
            mongoTemplate.insert(node);

        } catch (NoSuchElementException ex) {
            mongoTemplate.insert(node);
        }

    }

    public Node getNode(String nodeName) {
        Query searchNodeQuery = new Query(Criteria.where("id").is(nodeName));
        Node savedNode = mongoTemplate.findOne(searchNodeQuery, Node.class);
        if (savedNode == null) {
            throw new NoSuchElementException(format("The node {0} could not be found", nodeName));
        }
        return savedNode;
    }

    public void removeNode(String nodeName) {
        Query searchNodeQuery = new Query(Criteria.where("id").is(nodeName));
        mongoTemplate.remove(searchNodeQuery, Node.class);

    }

    public int getNodeLength() {
        List<Node> nodes = mongoTemplate.findAll(Node.class);
        return nodes.size();
    }

    public String generateManifestStr(String nodeName) {
        StringBuffer sb = new StringBuffer();

        Node node = getNode(nodeName);
        node.setManifestGenerated(true);
        addNode(node);

        sb.append(node.generateFileStr());
        return sb.toString();
    }

    public String generateSiteStr() {

        List<Node> nodeList = mongoTemplate.findAll(Node.class);

        StringBuffer sb = new StringBuffer();

        for (Node node : nodeList) {
            if (node.isManifestGenerated() && sb.indexOf("import '" + node.getGroupName() + "/*.pp'") == -1) {
                sb.append("import '" + node.getGroupName() + "/*.pp'");
                sb.append(eol);
            }
        }
        return sb.toString();

    }

    public void removeNodesByGroupName(String groupName) {
        Query searchNodeQuery = new Query(Criteria.where("groupName").is(groupName));
        mongoTemplate.remove(searchNodeQuery, Node.class);

    }
    
    public boolean isLastGroupNode(String groupName) {
        Query searchNodeQuery = new Query(Criteria.where("groupName").is(groupName));
        List<Node> nodeList = mongoTemplate.find(searchNodeQuery, Node.class);
        if(nodeList!= null && nodeList.size()==1){
            return true;
        }else{
            return false;
        }
    }

    // public void setMongoTemplate(MongoTemplate mongoTemplate) {
    // this.mongoTemplate = mongoTemplate;
    // }

}
