package lesson3;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// Attention: comparable supported but comparator is not
@SuppressWarnings("WeakerAccess")
public class BinaryTree<T extends Comparable<T>> extends AbstractSet<T> implements SortedSet<T> {

    private static class Node<T> {
        final T value;

        Node<T> left = null;

        Node<T> right = null;

        Node<T> parent = null;

        Node(T value) {
            this.value = value;
        }
        Node(Node<T> parent, T value) {
            this.parent = parent;
            this.value = value;
        }
    }

    private Node<T> root = null;

    private int size = 0;

    @Override
    public boolean add(T t) {
        Node<T> closest = find(t);
        int comparison = closest == null ? -1 : t.compareTo(closest.value);
        if (comparison == 0) {
            return false;
        }
        Node<T> newNode = new Node<>(t);
        if (closest == null) {
            root = newNode;
        }
        else if (comparison < 0) {
            assert closest.left == null;
            closest.left = newNode;
            newNode.parent = closest;
        }
        else {
            assert closest.right == null;
            closest.right = newNode;
            newNode.parent = closest;
        }
        size++;
        return true;
    }

    boolean checkInvariant() {
        return root == null || checkInvariant(root);
    }

    private boolean checkInvariant(Node<T> node) {
        Node<T> left = node.left;
        if (left != null && (left.value.compareTo(node.value) >= 0 || !checkInvariant(left))) return false;
        Node<T> right = node.right;
        return right == null || right.value.compareTo(node.value) > 0 && checkInvariant(right);
    }

    @Override
    public boolean remove(Object o) {
        //Метод contains нет смысла использовать, т.к. при резульатате true придется еще раз искать тот же элемент
        T t = (T) o;
        Node<T> closest = find(t); //Находим элемент
        //Проверяем, нужный ли это элемент или только близкий к нему
        if (closest != null && t.compareTo(closest.value) == 0) {
            if (closest.left == null && closest.right == null) {
                if (closest.parent == null)
                    root = null;
                else {
                    if (closest.parent.left == closest) closest.parent.left = null;
                    else closest.parent.right = null;
                }
            } else if (closest.left == null || closest.right == null) {
                Node<T> onlyChild = (closest.left == null) ? closest.right : closest.left;
                if (closest.parent == null) {
                    root = onlyChild;
                    onlyChild.parent = null;
                }
                else {
                    if (closest.parent.left == closest) closest.parent.left = onlyChild;
                    else closest.parent.right = onlyChild;
                    onlyChild.parent = closest.parent;
                }
            } else {
                //Самое маленькое значение из тех, что больше заданного
                //Берем его чтобы сохранить структуру дерева
                Node<T> minSubNode = min(closest.right);
                //Перезаписываем ссылку у родителя удаляемой ноды
                if (closest.parent.left == closest) closest.parent.left = minSubNode;
                else closest.parent.right = minSubNode;
                //Убираем ссылки у прошлого родителя новой ноды
                if (minSubNode.parent.left == minSubNode) minSubNode.parent.left = minSubNode.right;
                else minSubNode.parent.right = minSubNode.right;
                //Переписываем ссылки на дочерние ноды
                minSubNode.right = closest.right;
                if (closest.right != null) closest.right.parent = minSubNode;
                minSubNode.left = closest.left;
                if (closest.left != null) closest.left.parent = minSubNode;
                //Заменяем родителя
                minSubNode.parent = closest.parent;
            }
            return true;
        } else
            return false;
    }

    private Node<T> min(Node<T> start) {
        if (start.left == null)
            return start;
        return min(start.left);
    }

    @Override
    public boolean contains(Object o) {
        @SuppressWarnings("unchecked")
        T t = (T) o;
        Node<T> closest = find(t);
        return closest != null && t.compareTo(closest.value) == 0;
    }

    private Node<T> find(T value) {
        if (root == null) return null;
        return find(root, value);
    }

    private Node<T> find(Node<T> start, T value) {
        int comparison = value.compareTo(start.value);
        if (comparison == 0) {
            return start;
        }
        else if (comparison < 0) {
            if (start.left == null) return start;
            return find(start.left, value);
        }
        else {
            if (start.right == null) return start;
            return find(start.right, value);
        }
    }

    public class BinaryTreeIterator implements Iterator<T> {

        private Node<T> current = null;

        private BinaryTreeIterator() {}

        private Node<T> findNext() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasNext() {
            return findNext() != null;
        }

        @Override
        public T next() {
            current = findNext();
            if (current == null) throw new NoSuchElementException();
            return current.value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new BinaryTreeIterator();
    }

    @Override
    public int size() {
        return size;
    }


    @Nullable
    @Override
    public Comparator<? super T> comparator() {
        return null;
    }

    @NotNull
    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public SortedSet<T> headSet(T toElement) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public SortedSet<T> tailSet(T fromElement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T first() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.value;
    }

    @Override
    public T last() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.value;
    }
}
