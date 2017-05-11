///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package UI;
//
//import javafx.beans.property.BooleanProperty;
//import javafx.beans.property.SimpleBooleanProperty;
//import javafx.beans.property.SimpleStringProperty;
//import javafx.beans.property.StringProperty;
//
//public class Item {
//        private final StringProperty name = new SimpleStringProperty();
//        private final BooleanProperty on = new SimpleBooleanProperty();
//
//        public Item(String name, boolean on) {
//            setName(name);
//            setOn(on);
//        }
//
//        public final StringProperty nameProperty() {
//            return this.name;
//        }
//
//        public final String getName() {
//            return this.nameProperty().get();
//        }
//
//        public final void setName(final String name) {
//            this.nameProperty().set(name);
//        }
//
//        public final BooleanProperty onProperty() {
//            return this.on;
//        }
//
//        public final boolean isOn() {
//            return this.onProperty().get();
//        }
//
//        public final void setOn(final boolean on) {
//            this.onProperty().set(on);
//        }
//
//        @Override
//        public String toString() {
//            return getName();
//        }
//
//    }