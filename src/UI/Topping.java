/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 *
 * @author ado_k
 */
public class Topping {

    private final String name;

    private final BooleanProperty selected = new SimpleBooleanProperty();

    public Topping(String name) {
        this.name = name;
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public final boolean isSelected() {
        return selectedProperty().get();
    }

    public final void setSelected(boolean selected) {
        selectedProperty().set(selected);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
