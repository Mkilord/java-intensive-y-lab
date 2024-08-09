package autoservice.adapter.ui.components.utils;

import autoservice.model.View;

import java.util.List;
import java.util.Optional;

/**
 * Utility class for viewing objects and strings.
 */
public class Viewer {
    /**
     * Private constructor to prevent instantiation.
     */
    private Viewer() {
    }

    /**
     * Displays the view of each object in the provided list.
     * If the list is empty, prints the provided error message.
     *
     * @param list     the list of objects to view
     * @param errorMsg the error message to print if the list is empty
     * @param <T>      the type of objects that extend {@link View}
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
     * @param list     the list of strings to view
     * @param errorMsg the error message to print if the list is empty
     */
    public static void viewAllStrings(List<String> list, String errorMsg) {
        if (list.isEmpty()) {
            System.out.println(errorMsg);
            return;
        }
        list.forEach(System.out::println);
    }

    public static void viewAllStringsWithNumber(List<String> list, String errorMsg) {
        if (list.isEmpty()) {
            System.out.println(errorMsg);
            return;
        }
        int i = 0;
        for (String s : list) {
            System.out.println(i + 1 + " " + s);
            i++;
        }
    }

    /**
     * Prints the view representation of the given {@link Optional} if it contains a value,
     * or prints the specified error message if the {@link Optional} is empty.
     *
     * @param <T>     the type of the {@link Optional} that must extend {@link View}
     * @param viewOpt the {@link Optional} containing a {@link View} or empty
     * @param error   the error message to print if {@code viewOpt} is empty
     */
    public static <T extends Optional<View>> void viewOf(T viewOpt, String error) {
        viewOpt.ifPresentOrElse(view -> System.out.println(viewOpt.get().getView()), () -> System.out.println(error));
    }


    /**
     * Displays the view of the provided object.
     *
     * @param o   the object to view
     * @param <T> the type of object that extends {@link View}
     */
    public static <T extends View> void viewOf(T o) {
        System.out.println(o.getView());
    }

}
