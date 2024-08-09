package autoservice.adapter.ui.common.viewers;

import autoservice.model.User;

import java.util.List;

public interface PeopleViewer {
    void showUsers(List<User> users);

    void showUsersMenu();

    void showUserSearch(List<User> users);

    void selectUser(List<User> users);

    void showUserOptions(User user);

    void showUserSortMenu(List<User> users);
}
