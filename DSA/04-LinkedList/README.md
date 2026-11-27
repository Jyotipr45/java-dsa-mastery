# Linked List

**Roadmap:** Stage 3: Linear structures in the [learning roadmap](../../ROADMAP.md).

## What is it?

A linked list stores a sequence of values in separate objects called nodes. Each node holds one value and a reference to the next node. The list itself only needs to remember the first node, called the head. The last node's `next` reference is `null`, which marks the end.

```
head -> [7 | •] -> [3 | •] -> [9 | •] -> null
```

Unlike an [array](../01-Array/README.md), the nodes do not sit next to each other in memory. They can be anywhere, and the `next` references are what hold them together in order.

## Why does it exist?

An array keeps its elements in one block, so inserting or removing in the middle means shifting every later element. A linked list removes that cost. To insert a value after a node you already hold, you create one node and change two references. To remove the node after it, you change one reference. Nothing else moves, however long the list is.

Linked lists also grow one node at a time. There is no capacity to run out of and no large copy when the list gets bigger.

The idea of objects that point to other objects matters well beyond lists. Trees, graphs, and the buckets inside some hash tables are all built the same way, so getting comfortable with `next` references here is the first step towards them.

## How does it work?

In Java a node is a small class:

```java
static class Node {
    int value;
    Node next;
}
```

Everything else follows from the fact that you can only move forward through `next`.

**Traversal.** Start at the head and follow `next` until you reach `null`. This is the only way to reach a node, so reading position `i` means walking past `i` nodes first. There is no address calculation like `nums[i]`, which is why access by position is O(n).

**Insert at the head.** Create the node, point its `next` at the current head, and make it the new head. This is O(1).

**Insert after a node you hold.** Point the new node's `next` at the node that currently follows, then point the earlier node at the new one. The order matters: if you change the earlier node first, you lose the only reference to the rest of the list.

**Remove the node after a node you hold.** Set `previous.next = previous.next.next`. The removed node is no longer reachable from the head, and the garbage collector can reclaim it. This is O(1).

**Insert or remove at the tail.** If you also keep a `tail` reference, appending is O(1). Removing the tail of a singly linked list is still O(n), because you need the node before the tail and the only way to find it is to walk from the head.

Notice that every change needs the node *before* the position you are changing. That is the main source of special cases: the head has no node before it. A common fix is a **dummy node** (also called a sentinel): a placeholder node placed before the head. Every real node now has a node before it, so the head is handled by the same code as everything else, and the answer is `dummy.next` at the end. The `remove` method in the example below uses this.

### Singly and doubly linked lists

In a singly linked list each node only knows the next node. In a doubly linked list each node also has a `prev` reference to the node before it. This costs one more reference per node and two more reference updates per change, but it lets you walk backwards and remove a node you hold in O(1) without searching for its predecessor. That is why doubly linked lists are used when nodes must be removed from anywhere quickly, for example in an LRU cache that pairs a `HashMap` with a doubly linked list.

Most interview problems use a singly linked list with a node class like `ListNode`, and give you only the head.

### `java.util.LinkedList`

Java's `LinkedList` is a doubly linked list that keeps references to both the first and last node. It implements both `List` and `Deque`, so adding and removing at either end (`addFirst`, `addLast`, `removeFirst`, `removeLast`) is O(1). `get(i)`, `set(i, x)`, `add(i, x)` and `remove(i)` are O(n), because they walk from whichever end is closer to `i`. To insert or remove at a position in O(1), use a `ListIterator` that is already there, as the example shows.

## Visual explanation

![Two panels. The top panel inserts 5 after the node holding 7: first the new node's next is pointed at 3, then 7's next is pointed at the new node, replacing the old link from 7 to 3. The bottom panel removes 9 from 5, 3, 9, 8 by pointing 3's next directly at 8, so 9 is no longer reachable](../../assets/concepts/linked-list-relinking.webp)

In the top panel, 5 is inserted after 7. Step 1 connects the new node to 3 while 7 still points at 3. Step 2 then points 7 at the new node, and the old link from 7 to 3 (dashed) is gone. Doing step 2 first would leave nothing pointing at 3, and the rest of the list would be lost.

In the bottom panel, 9 is removed by pointing 3 straight at 8. The node holding 9 still points at 8, but nothing points at 9 any more, so it is no longer part of the list.

Compare this with the [Array guide's diagram](../01-Array/README.md#visual-explanation), where inserting near the front shifts every later element. Here no value moves at all. The O(n) part of a linked list operation is finding the node, not changing the references.

## Operations and complexity

`n` is the number of nodes. "You hold the node" means you already have a reference to it, for example because you are in the middle of a traversal.

| Operation | Singly linked | Doubly linked (`java.util.LinkedList`) | Notes |
|---|---|---|---|
| Read or write position `i` | O(n) | O(n) | Must walk from the head (or, for `LinkedList`, the closer end) |
| Search for a value | O(n) | O(n) | Sorting does not help: binary search needs O(1) access by position |
| Insert or remove at the head | O(1) | O(1) | |
| Insert at the tail | O(1) with a tail reference | O(1) | Without a tail reference, O(n) to find the end |
| Remove the tail | O(n) | O(1) | A singly linked list must find the node before the tail |
| Insert after a node you hold | O(1) | O(1) | |
| Remove a node you hold | O(n), or O(1) if you hold the node before it | O(1) | Doubly linked nodes know their predecessor |
| Size | O(n), or O(1) if you keep a counter | O(1) | `LinkedList` keeps a counter |

Each operation above uses O(1) extra space. The list itself uses O(n) space, but more per element than an array: every value is wrapped in a node object with an object header and one or two references. A `LinkedList<Integer>` also boxes every number into an `Integer`.

## When should I use it?

Use a linked list when the problem is about rearranging links rather than reading by position. Typical cases:

- The input is a linked list. Most linked list problems give you a head node and ask you to reverse, merge, split, reorder or remove nodes, usually in O(1) extra space.
- You insert and remove often in the middle of a sequence, and you reach those positions during a traversal anyway.
- You need to remove an element from the middle in O(1) when you already have a reference to it. A doubly linked list paired with a `HashMap` from key to node is the classic design for an LRU cache.
- You need to splice whole sequences together. Joining two linked lists, or moving a run of nodes, is a few reference changes instead of a copy.

Clues in linked list problems often point to a technique rather than to the structure itself. Finding the middle node, detecting a cycle or finding the `k`-th node from the end suggest two references moving at different speeds or with a fixed gap. A problem that changes the head suggests a dummy node.

## When should I avoid it?

Avoid a linked list when:

- You need access by position. `list.get(i)` in a loop over `i` turns an O(n) scan into O(n²). Use an array or `ArrayList`.
- You only add and remove at the ends. `ArrayDeque` does that in O(1) too and is usually faster, because its elements sit in one array that the CPU cache handles well, while linked nodes are scattered in memory.
- Memory matters. Every node carries an object header and its references, so a linked list uses several times more memory per element than an `int[]`.
- You need to search sorted data. Binary search needs O(1) access by position, which a linked list does not have.

In everyday Java code, `ArrayList` and `ArrayDeque` are the right choice far more often than `LinkedList`. The linked list matters most as an idea: as the input to interview problems, and as the building block of trees and graphs.

## Java implementation

This example builds a small singly linked list by hand, shows insertion, access and removal (with a dummy node), and then the same operations on `java.util.LinkedList`.

```java
import java.util.*;

public class LinkedListBasics {

    // One node: a value and a reference to the next node (null at the end)
    static class Node {
        int value;
        Node next;

        Node(int value) {
            this.value = value;
        }
    }

    // Walk from the head, following next until it is null: O(n)
    static String toText(Node head) {
        StringBuilder sb = new StringBuilder();
        for (Node current = head; current != null; current = current.next) {
            sb.append(current.value).append(" -> ");
        }
        return sb.append("null").toString();
    }

    // Insert at the front: O(1). The new node becomes the head.
    static Node addFirst(Node head, int value) {
        Node node = new Node(value);
        node.next = head;
        return node;
    }

    // Insert after a node we already hold: O(1)
    static void insertAfter(Node previous, int value) {
        Node node = new Node(value);
        node.next = previous.next; // link the new node to the rest first
        previous.next = node;      // then point the previous node at it
    }

    // Remove the first node holding value. Finding it is O(n), unlinking is O(1).
    // The dummy node sits before the head, so removing the head needs no special case.
    static Node remove(Node head, int value) {
        Node dummy = new Node(0);
        dummy.next = head;
        Node previous = dummy;
        while (previous.next != null && previous.next.value != value) {
            previous = previous.next;
        }
        if (previous.next != null) {
            previous.next = previous.next.next; // skip over the removed node
        }
        return dummy.next;
    }

    // Reading position i means walking past i nodes: O(n)
    static int get(Node head, int index) {
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.value;
    }

    public static void main(String[] args) {
        // Build 7 -> 3 -> 9 by adding at the front, last value first
        Node head = null;
        head = addFirst(head, 9);
        head = addFirst(head, 3);
        head = addFirst(head, 7);
        System.out.println("built = " + toText(head));

        // Keep a tail reference to append in O(1)
        Node tail = head.next.next;
        insertAfter(tail, 8);
        tail = tail.next;
        System.out.println("append 8 = " + toText(head) + ", tail = " + tail.value);

        insertAfter(head, 5);
        System.out.println("insert 5 after 7 = " + toText(head));
        System.out.println("get(3) = " + get(head, 3));

        head = remove(head, 9);
        System.out.println("remove 9 = " + toText(head));
        head = remove(head, 7);
        System.out.println("remove head 7 = " + toText(head));
        head = remove(head, 42);
        System.out.println("remove 42 (absent) = " + toText(head));

        // java.util.LinkedList is doubly linked and keeps head and tail
        LinkedList<Integer> list = new LinkedList<>(Arrays.asList(3, 9));
        list.addFirst(7);
        list.addLast(8);
        System.out.println("LinkedList = " + list + ", first = " + list.getFirst() + ", last = " + list.getLast());

        // A ListIterator inserts at its current position in O(1)
        ListIterator<Integer> it = list.listIterator();
        it.next();
        it.add(5);
        System.out.println("after iterator add = " + list);

        // remove(int) removes by position, remove(Object) removes by value
        list.remove(1);
        System.out.println("remove(1) = " + list);
        list.remove(Integer.valueOf(9));
        System.out.println("remove(Integer.valueOf(9)) = " + list);
    }
}
```

Output:

```
built = 7 -> 3 -> 9 -> null
append 8 = 7 -> 3 -> 9 -> 8 -> null, tail = 8
insert 5 after 7 = 7 -> 5 -> 3 -> 9 -> 8 -> null
get(3) = 9
remove 9 = 7 -> 5 -> 3 -> 8 -> null
remove head 7 = 5 -> 3 -> 8 -> null
remove 42 (absent) = 5 -> 3 -> 8 -> null
LinkedList = [7, 3, 9, 8], first = 7, last = 8
after iterator add = [7, 5, 3, 9, 8]
remove(1) = [7, 3, 9, 8]
remove(Integer.valueOf(9)) = [7, 3, 8]
```

`get(3)` walks past three nodes to reach 9. Removing 7, the head, goes through the same code as removing 9, because the dummy node gives the head a node before it. In the last two lines, `list.remove(1)` removes the element at position 1, which is 5, while `list.remove(Integer.valueOf(9))` removes the value 9.

On Java 21 and later, `LinkedList` is also a sequenced collection, so `reversed()` gives a reversed view of it. See [Java 21](../../JavaVersions/Java-21.md).

## Common mistakes

- **Following `null`.** `current.next.value` throws `NullPointerException` when `current` is the last node. Check `current != null` or `current.next != null`, whichever your loop actually needs, before following a reference.
- **Losing the rest of the list.** When inserting, set `node.next = previous.next` before `previous.next = node`. When reversing, save `next` in a variable before changing `current.next`.
- **Moving the head instead of a copy.** Traversing with `head = head.next` loses the head. Use a separate `current` variable.
- **Forgetting the head case.** Removing or inserting at the front needs a different update, unless you use a dummy node. Remember to return `dummy.next`, not `dummy`.
- **Forgetting to update `tail` or `size`.** If you keep extra references or counters, every operation that changes the ends or the length must update them.
- **Comparing nodes with `equals` or values with `==`.** Use `==` to ask whether two references are the same node, which is what cycle and intersection problems need. Compare boxed `Integer` values with `equals`, because `==` on two `Integer` objects only works reliably for small values.
- **Calling `get(i)` in a loop on a `LinkedList`.** Each call walks the list again, so the loop is O(n²). Use a for-each loop or an iterator.
- **Mixing up `remove(int)` and `remove(Object)`.** On a `List<Integer>`, `list.remove(1)` removes position 1. Use `list.remove(Integer.valueOf(1))` to remove the value 1.

## Related patterns

The full guides are listed in the [patterns index](../../Patterns/README.md).

- **Fast and Slow Pointers** for finding the middle node, detecting a cycle and finding where a cycle starts.
- [Two Pointers](../../Patterns/TwoPointers.md) for two references a fixed distance apart, such as finding the `k`-th node from the end in one pass, and for merging two sorted lists.
- [Hashing](../../Patterns/Hashing.md) for remembering nodes already visited, and for pairing a map with a doubly linked list to reach any node in O(1).

## Practice problems

### Easy

None yet.

### Medium

None yet.

### Hard

None yet.
