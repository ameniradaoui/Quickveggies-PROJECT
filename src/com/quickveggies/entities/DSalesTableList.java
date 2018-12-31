package com.quickveggies.entities;

import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.TransformationList;

/**
 * TransformationList implementation. This TransformationList just has one extra
 line at the end, displaying the totalAmounted. We use a subclass of LineItem for that
 line:
 *
 * @author serg.merlin
 */
public class DSalesTableList extends TransformationList<DSalesTableLine, DSalesTableLine> {

    private final TotalLine totalLine;

    public DSalesTableList(ObservableList<? extends DSalesTableLine> source) {
//        super(source);
        super(FXCollections.observableArrayList());
        totalLine = new TotalLine(source);
    }

    @Override
    protected void sourceChanged(Change<? extends DSalesTableLine> c) {
        // no need to modify change:
        // indexes generated by the source list will match indexes in this
        // list
        fireChange(c);
    }

    // if index is in range for source list, just return that index
    // otherwise return -1, indicating index is not represented in source
    @Override
    public int getSourceIndex(int index) {
        if (index < getSource().size()) {
            return index;
        }
        return -1;
    }

    // if index is in range for source list, return corresponding
    // item from source list.
    // if index is one after the last element in the source list,
    // return totalAmounted line.
    @Override
    public DSalesTableLine get(int index) {
        if (index < getSource().size()) {
            return getSource().get(index);
        }
        else if (index == getSource().size()) {
            return totalLine;
        }
        else {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    // size of transformation list is one bigger than size of source list:
    @Override
    public int size() {
        return getSource().size() + 1;
    }

    // Special subclass to represent the totalAmounted of all the line items.
    // Just sets quantity and unit price to null.
    // Overrides totalProperty() to return our own property, that is bound to
    // the data list.
    public static class TotalLine extends DSalesTableLine {

        private final ReadOnlyObjectWrapper<Integer> totalAmounted = new ReadOnlyObjectWrapper<>();
        private final ReadOnlyObjectWrapper<Integer> totalCases = new ReadOnlyObjectWrapper<>();

        public TotalLine(ObservableList<? extends DSalesTableLine> items) {
            super("", "", "", "", "", "0", "", "", "", "", "", "", "", "", "", "", "", "");
            // Bind totalAmounted to the sum of the totals of all the other line items:
            totalAmounted.bind(Bindings.createObjectBinding(() -> items.stream().collect(
                    Collectors.summingInt(DSalesTableLine::getTotalQuantityInt)), items));
            totalCases.bind(Bindings.createObjectBinding(() -> items.stream().collect(
                    Collectors.summingInt(DSalesTableLine::getNetInt)), items));
        }
        
        @Override
        public String getTotalQuantity() {
            return String.valueOf(totalAmounted.getValue());
        }

        @Override
        public Integer getTotalQuantityInt() {
            return totalAmounted.getValue();
        }
        
        public String getNet() {
            return String.valueOf(totalCases.getValue());
        }

        public Integer getNetInt() {
            return totalCases.getValue();
        }
        
        @Override
        public boolean isTotalLine() {
            return true;
        }
    }
}