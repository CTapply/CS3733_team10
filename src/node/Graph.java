package node;

import java.util.LinkedList;
import java.util.TreeMap;

public class Graph {
	
	private LinkedList<AbsNode> nodes;
	
	public Graph(){
		
	}
	
	public void addNode(AbsNode nodeToAdd){
		
	}
	
	public void deleteNode(AbsNode nodeToDelete){
		
	}
	
	public void addEdge(Edge edgeToAdd){
		
	}
	
	public LinkedList<AbsNode> findRoute(AbsNode from, AbsNode to){
		if (to.getIsWalkable()){
			return null;
		}
		
		if (from.getIsWalkable()){
			return null;
		}
		
		LinkedList<AbsNode> path = new LinkedList<AbsNode>();
		TreeMap<Double, AbsNode> unknownFrontier = new TreeMap<Double, AbsNode>();
		from.setCost(0);
		unknownFrontier.put((from.getCost() + d(from, to)), from);
		LinkedList<AbsNode> explored = new LinkedList<AbsNode>();
		
		for (AbsNode n : nodes){
			n.setParent(null);
		}
		
		while (unknownFrontier.size() != 0){
			AbsNode current = unknownFrontier.pollFirstEntry().getValue();
			
			if (current == to){
				path = backtrack(current);
				return path;
			}
			
			explored.add(current);
			
			for (Edge neighbor : current.getEdges()){
				AbsNode neighborNode = neighbor.getTo();
				if (neighborNode.getParent() == null){
					neighborNode.setCost(d(current, neighborNode) + current.getCost());
					neighborNode.setParent(current);
					unknownFrontier.put((neighborNode.getCost()+ d(neighborNode, to)), neighborNode);
				}
				else {
					if ((d(current, neighborNode) + current.getCost()) < neighborNode.getCost()){
						neighborNode.setCost(d(current, neighborNode) + current.getCost());
						neighborNode.setParent(current);
					}
				}
			}
		}
			
		return null; // No Path Found
	}
	
	public void drawPath(LinkedList<Node> nodeList, LinkedList<AbsNode> absList){
		
	}
	
	public LinkedList<AbsNode> getNodes(){
		return nodes;
	}
	
	public double d(AbsNode from, AbsNode to){
		return Math.sqrt( (from.getX() - to.getX())^2 + (from.getY()-to.getY())^2);
	}
	
	public LinkedList<AbsNode> backtrack(AbsNode current){
		LinkedList <AbsNode> path = new LinkedList<AbsNode>();
		path.push(current);
		while (current.getParent() != null){
			path.push(current.getParent());
			current = current.getParent();
		}
		return path;
	}
}
		
