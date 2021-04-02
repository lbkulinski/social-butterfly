package com.butterfly.social.view;

import javafx.scene.control.*;

/**
 * A view for the menu of the Social Butterfly application.
 *
 * @author Logan Kulinski, lbk@purdue.edu
 * @version March 22, 2021
 */
public final class MenuView {
    /**
     * The Reddit log in menu item of this menu view.
     */
    private final MenuItem redditLogInMenuItem;

    /**
     * The Reddit profile menu item of this menu view.
     */
    private final MenuItem redditProfileMenuItem;

    /**
     * The Reddit follow menu item of this menu view.
     */
    private final MenuItem redditFollowUserMenuItem;

    /**
     * The Reddit logout menu item of this menu view
     */
    private final MenuItem redditLogoutMenuItem;

     * The Reddit saved posts menu item of this menu view.
     */

    private final MenuItem redditSavedPostsMenuItem;
    /**
     * The Reddit messages menu item of this menu view.
     */
    private final MenuItem redditMessagesMenuItem;

    /**
     * The Twitter log in menu item of this menu view.
     */
    private final MenuItem twitterLogInMenuItem;

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

    private final MenuItem twitterSavedPostsMenuItem;


    /**
     * The Instagram log in menu item of this menu view.
     */
    private final MenuItem instagramLogInMenuItem;

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

    private final MenuItem instagramSavedPostsMenuItem;
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

    private final MenuItem allSavedPostsRadioMenuItem;

    private final MenuItem timeSortMenuItem;

    private final MenuItem popularitySortMenuItem;

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
     * The view menu of this menu view.
     */
    private final Menu viewMenu;

    /**
     * The sort menu of this menu view.
     */
    private final Menu sortMenu;

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
        String messagesText = "Direct Messages";
        String lightText = "Light";
        String darkText = "Dark";
        String tabText = "Tab";
        String splitText = "Split";
        String redditText = "Reddit";
        String twitterText = "Twitter";
        String instagramText = "Instagram";
        String viewText = "View";
        String sortText = "Sort By";
        String timeText = "Time";
        String popularityText = "Popularity";
        SeparatorMenuItem separator;

        this.redditLogInMenuItem = new MenuItem(logInText);

        this.redditProfileMenuItem = new MenuItem(profileText);

        this.redditFollowUserMenuItem = new MenuItem(followUserText);

        this.redditLogoutMenuItem = new MenuItem(logoutText);

        this.redditSavedPostsMenuItem = new MenuItem(savedPostsText);
      
        this.redditMessagesMenuItem = new MenuItem(messagesText);

        this.twitterLogInMenuItem = new MenuItem(logInText);

        this.twitterProfileMenuItem = new MenuItem(profileText);

        this.twitterFollowUserMenuItem = new MenuItem(followUserText);
      
        this.twitterMessagesMenuItem = new MenuItem(messagesText);

        this.twitterSavedPostsMenuItem = new MenuItem(savedPostsText);

        this.instagramLogInMenuItem = new MenuItem(logInText);

        this.instagramProfileMenuItem = new MenuItem(profileText);

        this.instagramSavedPostsMenuItem = new MenuItem(savedPostsText);

        this.instagramBioMenuItem = new MenuItem(editBioText);

        this.instagramSearchMenuItem = new MenuItem(searchUsersText);

        this.instagramProfilePictureItem = new MenuItem(profilePictureText);
        this.instagramMessagesMenuItem = new MenuItem(messagesText);

        this.timeSortMenuItem = new MenuItem(timeText);

        this.popularitySortMenuItem = new MenuItem(popularityText);

        this.instagramFollowUserMenuItem = new MenuItem(followUserText);

        this.lightRadioMenuItem = new RadioMenuItem(lightText);

        this.darkRadioMenuItem = new RadioMenuItem(darkText);

        this.tabRadioMenuItem = new RadioMenuItem(tabText);

        this.splitRadioMenuItem = new RadioMenuItem(splitText);
        
        this.allSavedPostsRadioMenuItem = new MenuItem(savedPostsText);

        this.redditMenu = new Menu(redditText, null, this.redditLogInMenuItem, this.redditProfileMenuItem, this.redditSavedPostsMenuItem, this.redditMessagesMenuItem, this.redditFollowUserMenuItem, redditLogoutMenuItem);

        this.twitterMenu = new Menu(twitterText, null, this.twitterLogInMenuItem, this.twitterProfileMenuItem, this.twitterMessagesMenuItem, this.twitterSavedPostsMenuItem, this.twitterFollowUserMenuItem);

        this.instagramMenu = new Menu(instagramText, null, this.instagramLogInMenuItem, this.instagramProfileMenuItem,
                                    this.instagramBioMenuItem, this.instagramSearchMenuItem, this.instagramProfilePictureItem, this.instagramSavedPostsMenuItem, this.instagramMessagesMenuItem, this.instagramFollowUserMenuItem);

        separator = new SeparatorMenuItem();

        this.viewMenu = new Menu(viewText, null, this.lightRadioMenuItem, this.darkRadioMenuItem, separator,
                                 this.tabRadioMenuItem, this.splitRadioMenuItem, this.allSavedPostsRadioMenuItem);

        this.sortMenu = new Menu(sortText, null, this.timeSortMenuItem, this.popularitySortMenuItem);

        this.menuBar = new MenuBar(this.redditMenu, this.twitterMenu, this.instagramMenu, this.viewMenu, this.sortMenu);
    } //MenuView

    /**
     * Returns the Reddit log in menu item of this menu view.
     *
     * @return the Reddit log in menu item of this menu view
     */
    public MenuItem getRedditLogInMenuItem() {
        return this.redditLogInMenuItem;
    } //getRedditLogInMenuItem

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
     * Returns the Reddit logout menu item of this menu view
     *
     * @return the Reddit logout menu item of this menu view
     */
    public MenuItem getRedditLogoutMenuItem() {
        return this.redditLogoutMenuItem;
    } //getRedditLogoutMenuItem
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
     * @return the Twitter folllow user menu item of this menu view
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
    } //getInstagramBioMenuItem

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
     * Returns the time sort menu item of this menu view.
     *
     * @return the time sort menu item of this menu view
     */
    public MenuItem getTimeSortMenuItem() {
        return this.timeSortMenuItem;
    } //getTimeSortMenuItem

    /**
     * Returns the popularity sort menu item of this menu view.
     *
     * @return the popularity sort menu item of this menu view
     */
    public MenuItem getPopularitySortMenuItem() {
        return this.popularitySortMenuItem;
    } //getPopularitySortMenuItem

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
     * Returns the view menu of this menu view.
     *
     * @return the view menu of this menu view
     */
    public Menu getViewMenu() {
        return this.viewMenu;
    } //getViewMenu

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
        ToggleGroup toggleGroup0;
        ToggleGroup toggleGroup1;
        MenuView menuView;

        toggleGroup0 = new ToggleGroup();

        toggleGroup1 = new ToggleGroup();

        menuView = new MenuView();

        menuView.lightRadioMenuItem.setToggleGroup(toggleGroup0);

        menuView.darkRadioMenuItem.setToggleGroup(toggleGroup0);

        menuView.tabRadioMenuItem.setToggleGroup(toggleGroup1);

        menuView.splitRadioMenuItem.setToggleGroup(toggleGroup1);

        return menuView;
    } //createMenuView

}