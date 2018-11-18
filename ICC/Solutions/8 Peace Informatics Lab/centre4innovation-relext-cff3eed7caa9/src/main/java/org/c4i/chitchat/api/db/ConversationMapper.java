package org.c4i.chitchat.api.db;

import org.c4i.chitchat.api.model.JsonDoc;
import org.c4i.nlp.chat.Conversation;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Turn database results into {@link Conversation} values.
 * @author Arvid Halma
 */
public class ConversationMapper implements RowMapper<Conversation> {

    @Override
    public Conversation map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new Conversation()
                .setId(rs.getString("id"))
                .setChannel(rs.getString("channel"))
                .setBotId(rs.getString("botId"))
                .setUserId(rs.getString("userId"))
                .setLang(rs.getString("lang"));
    }
}
