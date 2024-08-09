package autoservice.adapter.ui.components.menu;

import autoservice.adapter.ui.components.utils.Viewer;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    public static String GO_BACK_VIEW = "-Back";
    public static final Action GO_BACK_ACTION = () -> SelectAction.EXIT;
    private final List<String> elements;

    private String header = "Select menu point!";

    private Menu(String... elements) {
        this.elements = new ArrayList<>(List.of(elements));
    }

    public static Menu create(String... elements) {
        return new Menu(elements);
    }

    public Menu withElement(String element) {
        elements.add(element);
        return this;
    }

    public Menu withHeader(String header) {
        this.header = header;
        return this;
    }

    public void print() {
        if (!header.isEmpty()) {
            System.out.println(header);
        }
        Viewer.viewAllStringsWithNumber(elements, "Menu points not found!");
    }
}
