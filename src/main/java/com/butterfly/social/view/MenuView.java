package com.butterfly.social.view;

import javafx.scene.control.*;

/**
 * A view for the menu of the Social Butterfly application.
 *
 * @author Logan Kulinski, lbk@purdue.edu
 * @version March 13, 2021
 */
public final class MenuView {
    /**
     * The Reddit log in menu item of this menu view.
     */
    private final MenuItem redditLogInMenuItem;

    /**
     * The Reddit log out menu item of this menu view
     */
    private final MenuItem redditLogOutMenuItem;

    /**
     * The Reddit profile menu item of this menu view.
     */
    private final MenuItem redditProfileMenuItem;

    /**
     * The Reddit follow menu item of this menu view.
     */
    private final MenuItem redditFollowUserMenuItem;

    /**
     * The Reddit saved posts menu item of this menu view.
     */
    private final MenuItem redditSavedPostsMenuItem;

    /**
     * The Reddit messages menu item of this menu view.
     */
    private final MenuItem redditMessagesMenuItem;

    /**
     * The Reddit send direct messages menu item of this menu view.
     */
    private final MenuItem redditSendMessageMenuItem;

    /**
     * The Twitter send direct messages menu item of this menu view.
     */
    private final MenuItem twitterSendMessagesmenuItem;

    /**
     * The Twitter log in menu item of this menu view.
     */
    private final MenuItem twitterLogInMenuItem;

    /**
     * The Twitter log out menu item of this menu view.
     */
    private final MenuItem twitterLogOutMenuItem;

    /**
     * The Twitter profile menu item of this menu view.
     */
    private final MenuItem twitterProfileMenuItem;

    /**
     * The Twitter follow menu item of this menu view.
     */
    private final MenuItem twitterFollowUserMenuItem;
  
    /**
     * The Twitter messages menu item of this menu view.
     */
    private final MenuItem twitterMessagesMenuItem;

    /**
     * The Twitter saved posts menu item of this menu view.
     */
    private final MenuItem twitterSavedPostsMenuItem;

    /**
     * The Instagram log in menu item of this menu view.
     */
    private final MenuItem instagramLogInMenuItem;

    /**
     * The Instagram log out menu item of this menu view.
     */
    private final MenuItem instagramLogOutMenuItem;

    /**
     * The Instagram profile menu item of this menu view.
     */
    private final MenuItem instagramProfileMenuItem;

    /**
     * The Instagram bio menu item of this menu view.
     */
    private final MenuItem instagramBioMenuItem;

    /**
     * The Instagram search menu item of this menu view.
     */
    private final MenuItem instagramSearchMenuItem;

    /**
     * The Instagram profile picture menu item of this menu view.
     */
    private final MenuItem instagramProfilePictureItem;

    /**
     * The Instagram saved posts menu item of this menu view.
     */
    private final MenuItem instagramSavedPostsMenuItem;

    /**
     * The Instagram story menu item of this menu view.
     */
    private final MenuItem instagramStoryMenuItem;

    /**
     * The Instagram messages menu item of this menu view.
     */
    private final MenuItem instagramMessagesMenuItem;

    /**
     * The Instagram follow menu item of this menu view.
     */
    private final MenuItem instagramFollowUserMenuItem;

    /**
     * The light radio menu item of this menu view.
     */
    private final RadioMenuItem lightRadioMenuItem;

    /**
     * The dark radio menu item of this menu view.
     */
    private final RadioMenuItem darkRadioMenuItem;

    /**
     * The tab radio menu item of this menu view.
     */
    private final RadioMenuItem tabRadioMenuItem;

    /**
     * The split radio menu item of this menu view.
     */
    private final RadioMenuItem splitRadioMenuItem;

    /**
     * The font size spinner of this menu view.
     */
    private final Spinner<Integer> fontSizeSpinner;

    /**
     * The all saved posts radio menu item of this menu view.
     */
    private final MenuItem allSavedPostsRadioMenuItem;

    /**
     * The multipost menu item of this menu view.
     */
    private final MenuItem multiPostMenuItem;

    /**
     * The time sort radio menu item of this menu view.
     */
    private final RadioMenuItem timeSortRadioMenuItem;

    /**
     * The popularity sort radio menu item of this menu view.
     */
    private final RadioMenuItem popularitySortRadioMenuItem;

    /**
     * The Reddit menu of this menu view.
     */
    private final Menu redditMenu;

    /**
     * The Twitter menu of this menu view.
     */
    private final Menu twitterMenu;

    /**
     * The Instagram menu of this menu view.
     */
    private final Menu instagramMenu;

    /**
     * The all menu of this menu view.
     */
    private final Menu allMenu;

    /**
     * The menu bar of this menu view.
     */
    private final MenuBar menuBar;

    /**
     * Constructs a newly allocated {@code MenuView} object.
     */
    private MenuView() {
        String logInText = "Log In";
        String logoutText = "Log Out";
        String profileText = "View Profile";
        String savedPostsText = "Saved Posts";
        String editBioText = "Edit Bio";
        String searchUsersText = "Search For Users";
        String followUserText = "Follow user";
        String profilePictureText = "Set Profile Picture";
        String storyText = "Make Story Post";
        String messagesText = "Direct Messages";
        String lightText = "Light";
        String darkText = "Dark";
        String tabText = "Tab";
        String splitText = "Split";
        int min = 1;
        int max = 100;
        int initialValue = 12;
        int stepCount = 1;
        String timeText = "Time";
        String popularityText = "Popularity";
        String redditText = "Reddit";
        String twitterText = "Twitter";
        String instagramText = "Instagram";
        String allText = "All";
        String multiPostText = "Multi Post";
        String twitterDirectMessageText = "Send Direct Message";
        String redditDirectMessageText = "Send Direct Message";

        this.multiPostMenuItem = new MenuItem(multiPostText);

        this.redditLogInMenuItem = new MenuItem(logInText);

        this.redditLogOutMenuItem = new MenuItem(logoutText);

        this.redditProfileMenuItem = new MenuItem(profileText);

        this.redditFollowUserMenuItem = new MenuItem(followUserText);

        this.redditSavedPostsMenuItem = new MenuItem(savedPostsText);
      
        this.redditMessagesMenuItem = new MenuItem(messagesText);

        this.redditSendMessageMenuItem = new MenuItem(redditDirectMessageText);

        this.twitterSendMessagesmenuItem = new MenuItem(twitterDirectMessageText);

        this.twitterLogInMenuItem = new MenuItem(logInText);

        this.twitterLogOutMenuItem = new MenuItem(logoutText);

        this.twitterProfileMenuItem = new MenuItem(profileText);

        this.twitterFollowUserMenuItem = new MenuItem(followUserText);
      
        this.twitterMessagesMenuItem = new MenuItem(messagesText);

        this.twitterSavedPostsMenuItem = new MenuItem(savedPostsText);

        this.instagramLogInMenuItem = new MenuItem(logInText);

        this.instagramLogOutMenuItem = new MenuItem(logoutText);

        this.instagramProfileMenuItem = new MenuItem(profileText);

        this.instagramSavedPostsMenuItem = new MenuItem(savedPostsText);

        this.instagramBioMenuItem = new MenuItem(editBioText);

        this.instagramSearchMenuItem = new MenuItem(searchUsersText);

        this.instagramProfilePictureItem = new MenuItem(profilePictureText);

        this.instagramStoryMenuItem = new MenuItem(storyText);

        this.instagramMessagesMenuItem = new MenuItem(messagesText);

        this.timeSortRadioMenuItem = new RadioMenuItem(timeText);

        this.popularitySortRadioMenuItem = new RadioMenuItem(popularityText);

        this.instagramFollowUserMenuItem = new MenuItem(followUserText);

        this.lightRadioMenuItem = new RadioMenuItem(lightText);

        this.darkRadioMenuItem = new RadioMenuItem(darkText);

        this.tabRadioMenuItem = new RadioMenuItem(tabText);

        this.splitRadioMenuItem = new RadioMenuItem(splitText);

        this.fontSizeSpinner = new Spinner<>(min, max, initialValue, stepCount);
        
        this.allSavedPostsRadioMenuItem = new MenuItem(savedPostsText);

        this.redditMenu = new Menu(redditText, null, this.redditLogInMenuItem);

        this.twitterMenu = new Menu(twitterText, null, this.twitterLogInMenuItem);

        this.instagramMenu = new Menu(instagramText, null, this.instagramLogInMenuItem);

        this.allMenu = new Menu(allText, null);

        this.menuBar = new MenuBar(this.redditMenu, this.twitterMenu, this.instagramMenu, this.allMenu);
    } //MenuView

    /**
     * Returns the reddit direct message menu item of this menu view.
     *
     * @return the reddit direct message menu item of this menu view
     */
    public MenuItem getRedditSendMessageMenuItem() {
        return redditSendMessageMenuItem;
    }

    /**
     * Returns the twitter direct message menu item of this menu view.
     *
     * @return the twitter direct message menu item of this menu view
     */
    public MenuItem getTwitterSendMessagesmenuItem() {
        return twitterSendMessagesmenuItem;
    }

    /**
     * Returns the Multi post menu item of this menu view.
     *
     * @return the Menu post menu item of this menu view
     */
    public MenuItem getMultiPostMenuItem() {
        return multiPostMenuItem;
    }

    /**
     * Returns the Reddit log in menu item of this menu view.
     *
     * @return the Reddit log in menu item of this menu view
     */
    public MenuItem getRedditLogInMenuItem() {
        return this.redditLogInMenuItem;
    } //getRedditLogInMenuItem

    /**
     * Returns the Reddit log out menu item of this menu view
     *
     * @return the Reddit log out menu item of this menu view
     */
    public MenuItem getRedditLogOutMenuItem() {
        return this.redditLogOutMenuItem;
    } //getRedditLogoutMenuItem

    /**
     * Returns the Reddit profile menu item of this menu view.
     *
     * @return the Reddit profile menu item of this menu view
     */
    public MenuItem getRedditProfileMenuItem() {
        return this.redditProfileMenuItem;
    } //getRedditProfileMenuItem

    /**
     * Returns the Reddit follow menu item of this menu view.
     *
     * @return the Reddit follow menu item of this menu view
     */
    public MenuItem getRedditFollowUserMenuItem() {
        return this.redditFollowUserMenuItem;
    } //getRedditFollowUserMenuItem

    /**
     * Returns the Reddit saved posts menu item of this menu view.
     *
     * @return the Reddit saved posts menu item of this menu view
     */
    public MenuItem getRedditSavedPostsMenuItem() {
        return this.redditSavedPostsMenuItem;
    } //getRedditSavedPostsMenuItem
    
    /**
     * Returns the Reddit messages menu item of this menu view.
     *
     * @return the Reddit messages menu item of this menu view
     */
    public MenuItem getRedditMessagesMenuItem() {
        return this.redditMessagesMenuItem;
    } //getRedditMessagesMenuItem

    /**
     * Returns the Twitter log in menu item of this menu view.
     *
     * @return the Twitter log in menu item of this menu view
     */
    public MenuItem getTwitterLogInMenuItem() {
        return this.twitterLogInMenuItem;
    } //getTwitterLogInMenuItem

    /**
     * Returns the Twitter log out menu item of this menu view.
     *
     * @return the Twitter log out menu item of this menu view
     */
    public MenuItem getTwitterLogOutMenuItem() {
        return this.twitterLogOutMenuItem;
    } //getTwitterLogOutMenuItem

    /**
     * Returns the Twitter profile menu item of this menu view.
     *
     * @return the Twitter profile menu item of this menu view
     */
    public MenuItem getTwitterProfileMenuItem() {
        return this.twitterProfileMenuItem;
    } //getTwitterProfileMenuItem

    /**
     * Returns the Twitter follow user menu item of this menu view.
     *
     * @return the Twitter follow user menu item of this menu view
     */
    public MenuItem getTwitterFollowUserMenuItem() {
        return this.twitterFollowUserMenuItem;
    } //getTwitterFollowUserMenuItem
  
    /**
     * Returns the Twitter messages menu item of this menu view.
     *
     * @return the Twitter messages menu item of this menu view
     */
    public MenuItem getTwitterMessagesMenuItem() {
        return this.twitterMessagesMenuItem;
    } //getTwitterMessagesMenuItem

    /**
     * Returns the Twitter saved posts menu item of this menu view.
     *
     * @return the Twitter saved posts menu item of this menu view
     */
    public MenuItem getTwitterSavedPostsMenuItem() {
        return this.twitterSavedPostsMenuItem;
    }

    /**
     * Returns the Instagram log in menu item of this menu view.
     *
     * @return the Instagram log in menu item of this menu view
     */
    public MenuItem getInstagramLogInMenuItem() {
        return this.instagramLogInMenuItem;
    } //getInstagramLogInMenuItem

    /**
     * Returns the Instagram log out menu item of this menu view.
     *
     * @return the Instagram log out menu item of this menu view
     */
    public MenuItem getInstagramLogOutMenuItem() {
        return this.instagramLogOutMenuItem;
    } //getInstagramLogOutMenuItem

    /**
     * Returns the Instagram profile menu item of this menu view.
     *
     * @return the Instagram profile menu item of this menu view
     */
    public MenuItem getInstagramProfileMenuItem() {
        return this.instagramProfileMenuItem;
    } //getInstagramProfileMenuItem

    /**
     * Returns the Instagram bio menu item of this menu view.
     *
     * @return the Instagram bio menu item of this menu view
     */
    public MenuItem getInstagramBioMenuItem() {
        return this.instagramBioMenuItem;
    } //getInstagramBioMenuItem

    /**
     * Returns the Instagram search menu item of this menu view.
     *
     * @return the Instagram search menu item of this menu view
     */
    public MenuItem getInstagramSearchMenuItem() {
        return this.instagramSearchMenuItem;
    } //getInstagramBioMenuItem

    /**
     * Returns the Instagram profile picture menu item of this menu view.
     *
     * @return the Instagram profile picture menu item of this menu view
     */
    public MenuItem getInstagramProfilePictureMenuItem() {
        return this.instagramProfilePictureItem;
    } //getInstagramProfilePictureMenuItem

    /**
     * Returns the Instagram story menu item of this menu view.
     *
     * @return the Instagram story menu item of this menu view
     */
    public MenuItem getInstagramStoryMenuItem() {
        return this.instagramStoryMenuItem;
    } //getInstagramStoryMenuItem

    /**
     * Returns the Instagram follow user menu item of this menu view.
     *
     * @return the Instagram follow user menu item of this menu view
     */
    public MenuItem getInstagramFollowUserMenuItem() {
        return this.instagramFollowUserMenuItem;
    } //getInstagramFollowUserMenuItem
  
    /**
     * Returns the Instagram saved posts menu item of this menu view.
     *
     * @return the Instagram saved posts menu item of this menu view
     */
    public MenuItem getInstagramSavedPostsMenuItem() {
        return this.instagramSavedPostsMenuItem;
    } //getInstagramSavedPostsMenuItem

    /**
     * Returns the Instagram messages menu item of this menu view.
     *
     * @return the Instagram messages menu item of this menu view
     */
    public MenuItem getInstagramMessagesMenuItem() {
        return this.instagramMessagesMenuItem;
    } //getInstagramMessagesMenuItem

    /**
     * Returns the time sort radio menu item of this menu view.
     *
     * @return the time sort radio menu item of this menu view
     */
    public RadioMenuItem getTimeSortRadioMenuItem() {
        return this.timeSortRadioMenuItem;
    } //getTimeSortRadioMenuItem

    /**
     * Returns the popularity sort radio menu item of this menu view.
     *
     * @return the popularity sort radio menu item of this menu view
     */
    public RadioMenuItem getPopularitySortRadioMenuItem() {
        return this.popularitySortRadioMenuItem;
    } //getPopularitySortRadioMenuItem

    /**
     * Returns the light radio menu item of this menu view.
     *
     * @return the light radio menu item of this menu view
     */
    public RadioMenuItem getLightRadioMenuItem() {
        return this.lightRadioMenuItem;
    } //getLightRadioMenuItem

    /**
     * Returns the dark radio menu item of this menu view.
     *
     * @return the dark radio menu item of this menu view
     */
    public RadioMenuItem getDarkRadioMenuItem() {
        return this.darkRadioMenuItem;
    } //getDarkRadioMenuItem

    /**
     * Returns the tab radio menu item of this menu view.
     *
     * @return the tab radio menu item of this menu view
     */
    public RadioMenuItem getTabRadioMenuItem() {
        return this.tabRadioMenuItem;
    } //getTabRadioMenuItem

    /**
     * Returns the split radio menu item of this menu view.
     *
     * @return the split radio menu item of this menu view
     */
    public RadioMenuItem getSplitRadioMenuItem() {
        return this.splitRadioMenuItem;
    } //getSplitRadioMenuItem

    /**
     * Returns the font size spinner of this menu view.
     *
     * @return the font size spinner of this menu view
     */
    public Spinner<Integer> getFontSizeSpinner() {
        return this.fontSizeSpinner;
    } //getFontSizeSpinner

    /**
     * Returns the all saved posts radio menu item of this menu view.
     *
     * @return the all saved posts radio menu item of this menu view
     */
    public MenuItem getAllSavedPostsRadioMenuItem() {
        return this.allSavedPostsRadioMenuItem;
    }

    /**
     * Returns the Reddit menu of this menu view.
     *
     * @return the Reddit menu of this menu view
     */
    public Menu getRedditMenu() {
        return this.redditMenu;
    } //getRedditMenu

    /**
     * Returns the Twitter menu of this menu view.
     *
     * @return the Twitter menu of this menu view
     */
    public Menu getTwitterMenu() {
        return this.twitterMenu;
    } //getTwitterMenu

    /**
     * Returns the Instagram menu of this menu view.
     *
     * @return the Instagram menu of this menu view
     */
    public Menu getInstagramMenu() {
        return this.instagramMenu;
    } //getInstagramMenu

    /**
     * Returns the all menu of this menu view.
     *
     * @return the all menu of this menu view
     */
    public Menu getAllMenu() {
        return this.allMenu;
    } //getAllMenu

    /**
     * Returns the menu bar of this menu view.
     *
     * @return the menu bar of this menu view
     */
    public MenuBar getMenuBar() {
        return this.menuBar;
    } //getMenuBar

    /**
     * Creates, and returns, a {@code MenuView} object.
     *
     * @return a {@code MenuView} object
     */
    public static MenuView createMenuView() {
        MenuView menuView;
        CustomMenuItem customMenuItem;
        Menu fontSizeMenu;
        String fontSizeText = "Font Size";
        SeparatorMenuItem separator;
        Menu themeMenu;
        String themeText = "Theme";
        Menu layoutMenu;
        String layoutText = "Layout";
        Menu sortMenu;
        String sortText = "Sort";
        Menu viewMenu;
        String viewText = "View";
        ToggleGroup toggleGroup0;
        ToggleGroup toggleGroup1;
        ToggleGroup toggleGroup2;

        menuView = new MenuView();

        customMenuItem = new CustomMenuItem(menuView.fontSizeSpinner);

        fontSizeMenu = new Menu(fontSizeText, null, customMenuItem);

        separator = new SeparatorMenuItem();

        themeMenu = new Menu(themeText, null, menuView.lightRadioMenuItem, separator, menuView.darkRadioMenuItem);

        separator = new SeparatorMenuItem();

        layoutMenu = new Menu(layoutText, null, menuView.tabRadioMenuItem, separator, menuView.splitRadioMenuItem);

        separator = new SeparatorMenuItem();

        sortMenu = new Menu(sortText, null, menuView.timeSortRadioMenuItem, separator,
                            menuView.popularitySortRadioMenuItem);

        viewMenu = new Menu(viewText);

        separator = new SeparatorMenuItem();

        viewMenu.getItems()
                .addAll(fontSizeMenu, separator);

        separator = new SeparatorMenuItem();

        viewMenu.getItems()
                .addAll(themeMenu, separator);

        separator = new SeparatorMenuItem();

        viewMenu.getItems()
                .addAll(layoutMenu, separator);

        viewMenu.getItems()
                .addAll(sortMenu);

        menuView.menuBar.getMenus()
                        .add(viewMenu);

        toggleGroup0 = new ToggleGroup();

        toggleGroup1 = new ToggleGroup();

        toggleGroup2 = new ToggleGroup();

        menuView.lightRadioMenuItem.setToggleGroup(toggleGroup0);

        menuView.darkRadioMenuItem.setToggleGroup(toggleGroup0);

        menuView.tabRadioMenuItem.setToggleGroup(toggleGroup1);

        menuView.splitRadioMenuItem.setToggleGroup(toggleGroup1);

        menuView.timeSortRadioMenuItem.setToggleGroup(toggleGroup2);

        menuView.popularitySortRadioMenuItem.setToggleGroup(toggleGroup2);

        return menuView;
    } //createMenuView
}