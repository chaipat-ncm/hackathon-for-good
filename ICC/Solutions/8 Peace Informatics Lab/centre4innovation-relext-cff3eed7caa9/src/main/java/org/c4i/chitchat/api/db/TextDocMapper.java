package org.c4i.chitchat.api.db;

import org.c4i.chitchat.api.model.JsonDoc;
import org.c4i.chitchat.api.model.TextDoc;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.joda.time.DateTime;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Turn database results into {@link TextDoc} values.
 * @author Arvid Halma
 */
public class TextDocMapper implements RowMapper<TextDoc> {
    @Override
    public TextDoc map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new TextDoc()
                .setId(rs.getString("id"))
                .setName(rs.getString("name"))
                .setType(rs.getString("type"))
                .setCreated(new DateTime(rs.getTimestamp("created")))
                .setUpdated(new DateTime(rs.getTimestamp("updated")))
                .setBody(rs.getString("body"));
    }
}
