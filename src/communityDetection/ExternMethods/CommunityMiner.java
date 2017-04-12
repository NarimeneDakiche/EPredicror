/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communityDetection.ExternMethods;

/**
 *
 * @author HADJER
 */

import graphclasses1.Community;
import java.util.LinkedList;
import org.graphstream.graph.Graph;

/**
 *
 * @author HADJER
 */
public abstract class CommunityMiner{
	
	
	public abstract LinkedList<Graph> findCommunities(String filePath);
	
	public abstract String getName();
	public abstract String getShortName();
	public String toString(){
		return getName();
	}
}