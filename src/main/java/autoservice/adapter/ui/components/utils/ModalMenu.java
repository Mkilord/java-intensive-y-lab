package autoservice.adapter.ui.components.utils;

import autoservice.adapter.ui.components.menu.Action;
import autoservice.adapter.ui.components.menu.Menu;
import autoservice.adapter.ui.components.menu.SelectAction;

import java.util.Scanner;

public class ModalMenu {

    public static void getYesOrNoDialog(Scanner in, String info, Action yes, Action no) {
        Menu.create("Yes", "No").withHeader(info).print();
        SelectAction.create(yes, no).withErrorMsg("Incorrect input! Select Yes or No.").read(in);
    }

    public static void getSaveOrNoDialog(Scanner in, String info, Action save, Action no) {
        Menu.create("Save", "No").withHeader(info).print();
        SelectAction.create(save, no).withErrorMsg("Incorrect input! Select Save or No.").read(in);
    }

}
