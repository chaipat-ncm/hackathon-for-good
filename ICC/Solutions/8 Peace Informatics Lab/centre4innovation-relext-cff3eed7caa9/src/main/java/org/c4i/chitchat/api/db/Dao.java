package org.c4i.chitchat.api.db;


/**
 * Access to Data Acces Objects.
 * Inception exception thrown!
 * @author Arvid Halma
 * @version 13-4-18
 */
public class Dao {
    public ConversationDao conversationDao;
    public TextDocDao textDocDao;
    public JsonDocDao jsonDocDao;

    public Dao(ConversationDao conversationDao, TextDocDao textDocDao, JsonDocDao jsonDocDao) {
        this.conversationDao = conversationDao;
        this.textDocDao = textDocDao;
        this.jsonDocDao = jsonDocDao;
    }
}
