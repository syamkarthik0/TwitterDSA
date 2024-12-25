package com.auth.graph;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A thread-safe implementation of a directed graph using adjacency lists.
 */
public class Graph {
    private final Map<Long, Set<Long>> adjacencyList;

    public Graph() {
        // Using ConcurrentHashMap for thread safety
        this.adjacencyList = new ConcurrentHashMap<>();
    }

    /**
     * Adds a node to the graph.
     */
    public synchronized void addNode(Long nodeId) {
        adjacencyList.putIfAbsent(nodeId, ConcurrentHashMap.newKeySet());
    }

    /**
     * Adds a directed edge from source to target.
     */
    public synchronized void addEdge(Long fromNode, Long toNode) {
        addNode(fromNode);
        addNode(toNode);
        adjacencyList.get(fromNode).add(toNode);
    }

    /**
     * Removes a directed edge from source to target.
     */
    public synchronized void removeEdge(Long fromNode, Long toNode) {
        if (adjacencyList.containsKey(fromNode)) {
            adjacencyList.get(fromNode).remove(toNode);
        }
    }

    /**
     * Gets all nodes that this node has edges to.
     */
    public Set<Long> getNeighbors(Long nodeId) {
        return new HashSet<>(adjacencyList.getOrDefault(nodeId, Collections.emptySet()));
    }

    /**
     * Checks if there is a directed edge from source to target.
     */
    public boolean hasEdge(Long fromNode, Long toNode) {
        return adjacencyList.containsKey(fromNode) && 
               adjacencyList.get(fromNode).contains(toNode);
    }

    /**
     * Gets all nodes that are two hops away from the source node.
     * Useful for friend-of-friend recommendations.
     */
    public Set<Long> getTwoHopNodes(Long sourceId) {
        Set<Long> result = new HashSet<>();
        Set<Long> firstHop = getNeighbors(sourceId);
        
        for (Long friendId : firstHop) {
            result.addAll(getNeighbors(friendId));
        }
        
        // Remove the original node and its direct connections
        result.remove(sourceId);
        result.removeAll(firstHop);
        return result;
    }

    /**
     * Gets mutual connections between two nodes.
     */
    public Set<Long> getMutualConnections(Long node1Id, Long node2Id) {
        Set<Long> connections1 = getNeighbors(node1Id);
        Set<Long> connections2 = getNeighbors(node2Id);
        
        Set<Long> mutual = new HashSet<>(connections1);
        mutual.retainAll(connections2);
        return mutual;
    }
}
