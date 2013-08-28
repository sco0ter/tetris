package treeview;

import javafx.beans.property.ListProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Christian Schudt
 */
public class TreeViewWithItems<T extends HierarchyData<T>> extends TreeView<T> {

    public TreeViewWithItems(TreeItem<T> root) {
        super(root);
    }

    /**
     * Sets items for the tree.
     *
     * @param list The list.
     */
    public void setItems(ObservableList<? extends T> list) {
        getRoot().getChildren().clear();
        map.clear();

        for (T value : list) {
            getRoot().getChildren().add(addRecursively(value));
        }

        list.addListener(getListChangeListener(getRoot()));
    }


    public Map<TreeItem<T>, ListChangeListener<T>> map = new HashMap<TreeItem<T>, ListChangeListener<T>>();

    /**
     * Gets a {@link javafx.collections.ListChangeListener} for a list of RosterItems. It listens to changes on the underlying list and updates the UI accordingly.
     *
     * @param treeItem The tree item which holds the list.
     * @return The listener.
     */
    private ListChangeListener<T> getListChangeListener(final TreeItem<T> treeItem) {
        return new ListChangeListener<T>() {
                    @Override
                    public void onChanged(final Change<? extends T> change) {
                        while (change.next()) {
                            if (change.wasRemoved()) {
                                for (T removed : change.getRemoved()) {

                                    for (TreeItem<T> item : treeItem.getChildren()) {
                                        if (item.getValue().equals(removed)) {
                                            treeItem.getChildren().remove(item);
                                            removeRecursively(item);
                                            break;
                                        }
                                    }

                                }
                            }
                            // If items have been added
                            if (change.wasAdded()) {
                                ObservableList<? extends T> list = change.getList();

                                // Get the new items
                                for (int i = change.getFrom(); i < change.getTo(); i++) {
                                    treeItem.getChildren().add(i, addRecursively(list.get(i)));
                                }
                            }

                            // If the list was sorted.
                            if (change.wasPermutated()) {
                                // Store the new order.
                                Map<Integer, TreeItem<T>> tempMap = new HashMap<Integer, TreeItem<T>>();

                                for (int i = change.getFrom(); i < change.getTo(); i++) {
                                    int a = change.getPermutation(i);
                                    tempMap.put(a, treeItem.getChildren().get(i));
                                }
                                getSelectionModel().clearSelection();
                                treeItem.getChildren().clear();

                                // Add the items in the new order.
                                for (int i = change.getFrom(); i < change.getTo(); i++) {
                                    treeItem.getChildren().add(tempMap.get(i));
                                }
                            }
                        }
                    }
                };
    }

    /**
     * Removes the listener recursively.
     *
     * @param item The tree item.
     */
    private void removeRecursively(TreeItem<T> item) {
        if (item.getValue() != null && item.getValue().getChildren() != null) {
            item.getValue().getChildren().removeListener(map.remove(item));

            for (TreeItem<T> treeItem : item.getChildren()) {
                removeRecursively(treeItem);
            }
        }
    }


    /**
     * Adds the children to the tree recursively.
     *
     * @param value The initial value.
     * @return The tree item.
     */
    private TreeItem<T> addRecursively(T value) {

        TreeItem<T> treeItem = new TreeItem<T>();
        treeItem.setValue(value);
        treeItem.setExpanded(true);

        if (value != null && value.getChildren() != null) {
            ListChangeListener<T> listChangeListener = getListChangeListener(treeItem);
            value.getChildren().addListener(listChangeListener);

            map.put(treeItem, listChangeListener);
            for (T child : value.getChildren()) {
                treeItem.getChildren().add(addRecursively(child));
            }
        }
        return treeItem;
    }
}