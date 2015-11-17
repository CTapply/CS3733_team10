package node;

import static org.junit.Assert.*;

import org.junit.Test;

public class AbsNodeTest {

	@Test
	public void testAbsNode() {
		AbsNode node1 = new AbsNode(5, 6, true);
		assertEquals(5, node1.getX());
		assertEquals(6, node1.getY());
		assertEquals(true, node1.getIsWalkable());
		
		//Add a case for when it will fail
	}

	@Test
	public void testGetX() {
		
		AbsNode node1 = new AbsNode(4, 5, true);
		assertEquals(4, node1.getX());
	}

	@Test
	public void testGetY() {
		AbsNode node1 = new AbsNode(4, 5, true);
		assertEquals(5, node1.getY());
	}

	@Test
	public void testGetIsWalkable() {
		AbsNode node1 = new AbsNode(4, 5, true);
		assertEquals(true, node1.getIsWalkable());
	}

	@Test
	public void testSetGetEdges() {
		AbsNode aNode = new AbsNode(4, 5, true);
		AbsNode bNode = new AbsNode(6, 7, true);
		Edge edge1 = new Edge(aNode, bNode, getDistance(aNode, bNode));
		aNode.setEdges(edge1);
		
		assertEquals(edge1, aNode.getEdges().pop());
	}

	@Test
	public void testSetGetParent() {
		AbsNode node19 = new AbsNode(1, 2, true);
		AbsNode node18 = new AbsNode(3, 4, true);
		node19.setParent(node18);
		assertEquals(node18, node19.getParent());
	}


	@Test
	public void testSetGetCost() {
		AbsNode node20 = new AbsNode(1, 2, true);
		node20.setCost(20);
		double testCost = node20.getCost();
		
		assertEquals(20, testCost, 1);
		
	}
	
	public int getDistance(AbsNode n1, AbsNode n2){
    	return (int) Math.sqrt((Math.pow(((int)n1.getX() - (int)n2.getX()), 2)) + (Math.pow(((int)n1.getY() - (int)n2.getY()), 2)));
    }

}