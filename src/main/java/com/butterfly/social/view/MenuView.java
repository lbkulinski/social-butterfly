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
     * The Twitter log in menu item of this menu view.
     */
    private final MenuItem twitterLogInMenuItem;

    /**
     * The Twitter profile menu item of this menu view.
     */
    private final MenuItem twitterProfileMenuItem;

    /**
     * The Instagram log in menu item of this menu view.
     */
    private final MenuItem instagramLogInMenuItem;

    /**
     * The Instagram profile menu item of this menu view.
     */
    private final MenuItem instagramProfileMenuItem;

    /**
     * The tab radio menu item of this menu view.
     */
    private final RadioMenuItem tabRadioMenuItem;

    /**
     * The split radio menu item of this menu view.
     */
    private final RadioMenuItem splitRadioMenuItem;

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
     * The menu bar of this menu view.
     */
    private final MenuBar menuBar;

    /**
     * Constructs a newly allocated {@code MenuView} object.
     */
    private MenuView() {
        String logInText = "Log In";
        String profileText = "View Profile";
        String tabText = "Tab";
        String splitText = "Split";
        String redditText = "Reddit";
        String twitterText = "Twitter";
        String instagramText = "Instagram";
        String viewText = "View";

        this.redditLogInMenuItem = new MenuItem(logInText);

        this.redditProfileMenuItem = new MenuItem(profileText);

        this.twitterLogInMenuItem = new MenuItem(logInText);

        this.twitterProfileMenuItem = new MenuItem(profileText);

        this.instagramLogInMenuItem = new MenuItem(logInText);

        this.instagramProfileMenuItem = new MenuItem(profileText);

        this.tabRadioMenuItem = new RadioMenuItem(tabText);

        this.splitRadioMenuItem = new RadioMenuItem(splitText);

        this.redditMenu = new Menu(redditText, null, this.redditLogInMenuItem, this.redditProfileMenuItem);

        this.twitterMenu = new Menu(twitterText, null, this.twitterLogInMenuItem, this.twitterProfileMenuItem);

        this.instagramMenu = new Menu(instagramText, null, this.instagramLogInMenuItem, this.instagramProfileMenuItem);

        this.viewMenu = new Menu(viewText, null, this.tabRadioMenuItem, this.splitRadioMenuItem);

        this.menuBar = new MenuBar(this.redditMenu, this.twitterMenu, this.instagramMenu, this.viewMenu);
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
     * Returns the menu bar of this menu view.
     *
     * @return the menu bar of this menu view
     */
    public MenuBar getMenuBar() {
        return this.menuBar;
    } //getMenuBar

    /**
     * Returns the view menu of this menu view.
     *
     * @return the view menu of this menu view
     */
    public Menu getViewMenu() {
        return this.viewMenu;
    } //getViewMenu

    /**
     * Creates, and returns, a {@code MenuView} object.
     *
     * @return a {@code MenuView} object
     */
    public static MenuView createMenuView() {
        ToggleGroup toggleGroup;
        MenuView menuView;

        toggleGroup = new ToggleGroup();

        menuView = new MenuView();

        menuView.tabRadioMenuItem.setToggleGroup(toggleGroup);

        menuView.splitRadioMenuItem.setToggleGroup(toggleGroup);

        return menuView;
    } //createMenuView
}