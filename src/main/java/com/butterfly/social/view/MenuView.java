package com.butterfly.social.view;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

/**
 * A view for the menu of the Social Butterfly application.
 *
 * @author Logan Kulinski, lbk@purdue.edu
 * @version March 20, 2021
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
     * The menu bar of this menu view.
     */
    private final MenuBar menuBar;

    /**
     * Constructs a newly allocated {@code MenuView} object.
     */
    private MenuView() {
        String logInText = "Log In";
        String profileText = "View Profile";
        String redditText = "Reddit";
        String twitterText = "Twitter";
        String instagramText = "Instagram";

        this.redditLogInMenuItem = new MenuItem(logInText);

        this.redditProfileMenuItem = new MenuItem(profileText);

        this.twitterLogInMenuItem = new MenuItem(logInText);

        this.twitterProfileMenuItem = new MenuItem(profileText);

        this.instagramLogInMenuItem = new MenuItem(logInText);

        this.instagramProfileMenuItem = new MenuItem(profileText);

        this.redditMenu = new Menu(redditText, null, this.redditLogInMenuItem, this.redditProfileMenuItem);

        this.twitterMenu = new Menu(twitterText, null, this.twitterLogInMenuItem, this.twitterProfileMenuItem);

        this.instagramMenu = new Menu(instagramText, null, this.instagramLogInMenuItem, this.instagramProfileMenuItem);

        this.menuBar = new MenuBar(this.redditMenu, this.twitterMenu, this.instagramMenu);
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
     * Creates, and returns, a {@code MenuView} object.
     *
     * @return a {@code MenuView} object
     */
    public static MenuView createMenuView() {
        return new MenuView();
    } //createMenuView
}