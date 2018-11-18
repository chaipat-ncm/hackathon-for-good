package org.c4i.chitchat.api.db;

import org.c4i.chitchat.api.model.JsonDoc;
import org.c4i.nlp.chat.Conversation;
import org.c4i.nlp.chat.Message;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Turn database results into {@link Message} values.
 * @author Arvid Halma
 */
public class MessageMapper implements RowMapper<Message> {
    @Override
    public Message map(ResultSet rs, StatementContext ctx) throws SQLException {
        String pgTime = rs.getString("timestamp").replaceFirst(" ", "T") + ":00";
        return new Message()
                .setId(rs.getString("id"))
                .setText(rs.getString("text"))
                .setConversationId(rs.getString("conversationId"))
                .setRecipientId(rs.getString("recipientId"))
                .setSenderId(rs.getString("senderId"))
                .setIncoming(rs.getBoolean("incoming"))
                .setTimestamp(ISODateTimeFormat.dateTimeParser().parseDateTime(pgTime));
//                .setTimestamp(new DateTime(rs.getString("timestamp")));
    }
}
