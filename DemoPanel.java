import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Random;
import java.awt.Dimension;

import javax.swing.JPanel;
public class DemoPanel extends JPanel 
{
    // screen settings
    final int maxCol=20;
    final int maxRow=20;
    final int nodeSize=70;
    final int screenWidth=nodeSize*maxCol;
    final int screenHeight=nodeSize*maxRow;
    
    //Node
    Node[][] node = new Node[maxCol][maxRow];
    Node startNode, goalNode, currentNode;
    ArrayList<Node> openList=new ArrayList<>();
    ArrayList<Node> checkedList=new ArrayList<>();

    //others
    boolean goalReached=false;
    int step=0;


    public DemoPanel()
    {
        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setBackground(Color.black);
        this.setLayout(new GridLayout(maxRow,maxCol));
        this.addKeyListener(new KeyHandler(this));
        this.setFocusable(true);

        //place nodes
        int col=0;
        int row=0;

        while(col<maxCol && row<maxRow)
        {
            node[col][row]=new Node(col,row);
            this.add(node[col][row]);
            col++;
            if(col == maxCol)
            {
                col=0;
                row++;
            }
        }
        // set start and goal node
       Random rand = new Random();
        setStartNode(rand.nextInt(maxCol), rand.nextInt(maxRow));
        setGoalNode(rand.nextInt(maxCol), rand.nextInt(maxRow));

        // place solid nodes
        int numberOfSolidNodes = 150; // Adjust as needed
        for (int i = 0; i < numberOfSolidNodes; i++) {
             col = rand.nextInt(maxCol);
             row = rand.nextInt(maxRow);
            if (node[col][row] != startNode && node[col][row] != goalNode) {
                setSolidNode(col, row);
            }
        }


        //set cost
        setCostOnNodes();
    }
    private void setStartNode(int col, int row)
    {
        node[col][row].setAsStart();
        startNode=node[col][row];
        currentNode=startNode;
    }
    private void setGoalNode(int col, int row)
    {
        node[col][row].setAsGoal();
        goalNode=node[col][row];
    }
    private void setSolidNode(int col, int row)
    {
        node[col][row].setAsSolid();
    }
    private void setCostOnNodes()
    {
        int col=0,row=0;
        while (col<maxCol && row<maxRow) 
        {
            getCost(node[col][row]);
            col++;
            if(col == maxCol)
            {
                col=0;
                row++;
            }
        }

    } 
    private void getCost(Node node)
    {
        //get G-cost (the distance from the start node)
       int xDistance=Math.abs(node.col-startNode.col);
       int yDistance=Math.abs(node.row-startNode.row);
       node.gCost= xDistance+yDistance;

        //get H-cost (the distance from the goal node)
        xDistance=Math.abs(node.col-goalNode.col);
        yDistance=Math.abs(node.row-goalNode.row);
        node.hCost= xDistance+yDistance;

        //get F-cost(the total cost)
        node.fCost =node.gCost+node.hCost;

        //display the cost on node
        if(node!=startNode && node!=goalNode)
        {
            node.setText("<html>F:"+node.fCost+"<br>G:"+node.gCost+"<html>");
        }
    }
    public void search()
    {
        if(goalReached==false)
        {
            int col=currentNode.col;
            int row=currentNode.row;

            currentNode.setAsChecked();
            checkedList.add(currentNode);
            openList.remove(currentNode);

            //open the up node
            if(row-1>=0)
            openNode(node[col][row-1]);

            //open the left node
            if(col-1>=0)
            openNode(node[col-1][row]);
            
            //open the downn node
            if(row+1<maxRow)
            openNode(node[col][row+1]);

            //open the left node
            if(col+1<maxCol)
            openNode(node[col+1][row]);

            //find the best node 
            int bestNodeIndex=0;
            int bestNodeCost=999;

            for(int i=0;i<openList.size();i++)
            {
                //check if this node's F-cost is better
                if(openList.get(i).fCost<bestNodeCost)
                {
                    bestNodeIndex=i;
                    bestNodeCost=openList.get(i).fCost;
                }
                //if F-cost is equal,check the G-cost
                else if(openList.get(i).fCost==bestNodeCost)
                {
                    if(openList.get(i).gCost<openList.get(bestNodeIndex).gCost)
                    {
                        bestNodeIndex=i;
                    }
                }
            }
            //After the loop,we get the best node which is our next step
            currentNode=openList.get(bestNodeIndex);
            if(currentNode==goalNode)
            goalReached=true;
        }
    }
    public void autoSearch()
    {
        while(goalReached==false  && step<300)
        {
            int col=currentNode.col;
            int row=currentNode.row;

            currentNode.setAsChecked();
            checkedList.add(currentNode);
            openList.remove(currentNode);

            //open the up node
            if(row-1>=0)
            openNode(node[col][row-1]);

            //open the left node
            if(col-1>=0)
            openNode(node[col-1][row]);
            
            //open the downn node
            if(row+1<maxRow)
            openNode(node[col][row+1]);

            //open the left node
            if(col+1<maxCol)
            openNode(node[col+1][row]);

            //find the best node 
            int bestNodeIndex=0;
            int bestNodeCost=999;

            for(int i=0;i<openList.size();i++)
            {
                //check if this node's F-cost is better
                if(openList.get(i).fCost<bestNodeCost)
                {
                    bestNodeIndex=i;
                    bestNodeCost=openList.get(i).fCost;
                }
                //if F-cost is equal,check the G-cost
                else if(openList.get(i).fCost==bestNodeCost)
                {
                    if(openList.get(i).gCost<openList.get(bestNodeIndex).gCost)
                    {
                        bestNodeIndex=i;
                    }
                }
            }
            //After the loop,we get the best node which is our next step
            currentNode=openList.get(bestNodeIndex);
            if(currentNode==goalNode)
            {
                goalReached=true;
                trackThePath();
            }
        }
        step++;
    }
    private void openNode(Node node)
    {
        if(node.open==false && node.checked==false && node.solid==false)
        {
            //if the node is not opened yet, add it to the open list
            node.setAsOpen();
            node.parent=currentNode;
            openList.add(node);
        }
    } 
    private void trackThePath()
    {
        //Backtrack and draw the path
        Node current=goalNode;
        while(current!=startNode)
        {
            current=current.parent;
            if(current!=startNode)
            {
                current.setAsPath();
            }
        }
    }
}
