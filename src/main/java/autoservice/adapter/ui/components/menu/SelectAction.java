package autoservice.adapter.ui.components.menu;

import autoservice.adapter.ui.components.utils.ConsoleUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class SelectAction {
    public final static boolean EXIT = false;
    public final static boolean CONTINUE = true;

    private final List<Action> actions;

    private String errorMsg = "Invalid number of menu.";

    private SelectAction(Action... actions) {
        this.actions = new ArrayList<>(List.of(actions));
    }

    public static SelectAction create(Action... actions) {
        return new SelectAction(actions);
    }

    public SelectAction withAction(Action action) {
        actions.add(action);
        return this;
    }

    public SelectAction withErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }

    public void read(Scanner in) {
        var point = ConsoleUtils.readIntOfRange(in, 1, actions.size(), errorMsg);
        point--;
        actions.get(point).something();
    }

    public void readInCycle(Scanner in, Menu menu) {
        int point;
        do {
            menu.print();
            point = ConsoleUtils.readIntOfRange(in, 1, actions.size(), errorMsg);
            point--;
        } while (actions.get(point).something());
    }
}
