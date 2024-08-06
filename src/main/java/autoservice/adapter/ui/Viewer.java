package autoservice.adapter.ui;

import autoservice.core.model.View;

import java.util.List;

/**
 * Utility class for viewing objects and strings.
 */
public class Viewer {
    /**
     * Displays the view of each object in the provided list.
     * If the list is empty, prints the provided error message.
     *
     * @param list the list of objects to view
     * @param errorMsg the error message to print if the list is empty
     * @param <T> the type of objects that extend {@link View}
     */
    public static <T extends View> void viewAll(List<T> list, String errorMsg) {
        if (list.isEmpty()) {
            System.out.println(errorMsg);
            return;
        }
        list.forEach(o -> System.out.println(o.getView()));
    }
    /**
     * Displays each string in the provided list.
     * If the list is empty, prints the provided error message.
     *
     * @param list the list of strings to view
     * @param errorMsg the error message to print if the list is empty
     */
    public static void viewAllStrings(List<String> list, String errorMsg) {
        if (list.isEmpty()) {
            System.out.println(errorMsg);
            return;
        }
        list.forEach(System.out::println);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private Viewer() {
    }
    /**
     * Displays the view of the provided object.
     *
     * @param o the object to view
     * @param <T> the type of object that extends {@link View}
     */
    public static <T extends View> void viewOf(T o) {
        System.out.println(o.getView());
    }
}
