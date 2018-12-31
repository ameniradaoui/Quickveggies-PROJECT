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
public class PartyProfileList extends TransformationList<PartyProfile, PartyProfile> {

    private final TotalLine totalLine;

    public PartyProfileList(ObservableList<? extends PartyProfile> source) {
//        super(source);
        super(FXCollections.observableArrayList());
        totalLine = new TotalLine(source);
    }

    @Override
    protected void sourceChanged(Change<? extends PartyProfile> c) {
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
    public PartyProfile get(int index) {
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
    public static class TotalLine extends PartyProfile {

        private final ReadOnlyObjectWrapper<Integer> totalAmountPaid = new ReadOnlyObjectWrapper<>();
        private final ReadOnlyObjectWrapper<Integer> totalAmountRcvd = new ReadOnlyObjectWrapper<>();
        private final ReadOnlyObjectWrapper<Integer> totalCases = new ReadOnlyObjectWrapper<>();

        public TotalLine(ObservableList<? extends PartyProfile> items) {
            super("", "0", "0", "0", "");
            // Bind totalAmounted to the sum of the totals of all the other line items:
            totalAmountPaid.bind(Bindings.createObjectBinding(() -> items.stream().collect(
                    Collectors.summingInt(PartyProfile::getAmountPaidInt)), items));
            totalAmountRcvd.bind(Bindings.createObjectBinding(() -> items.stream().collect(
                    Collectors.summingInt(PartyProfile::getAmountReceivedInt)), items));
            totalCases.bind(Bindings.createObjectBinding(() -> items.stream().collect(
                    Collectors.summingInt(PartyProfile::getCasesInt)), items));
        }
        
        @Override
        public Integer getAmountPaidInt() {
            return totalAmountPaid.getValue();
        }
        
        @Override
        public Integer getAmountReceivedInt() {
            return totalAmountRcvd.getValue();
        }
        
        @Override
        public Integer getCasesInt() {
            return totalCases.getValue();
        }
        
        @Override
        public String getAmountPaid() {
            return String.valueOf(totalAmountPaid.getValue());
        }
        
        @Override
        public String getAmountReceived() {
            return String.valueOf(totalAmountRcvd.getValue());
        }
        
        @Override
        public String getCases() {
            return String.valueOf(totalCases.getValue());
        }
        
        @Override
        public boolean isTotalLine() {
            return true;
        }
    }
}