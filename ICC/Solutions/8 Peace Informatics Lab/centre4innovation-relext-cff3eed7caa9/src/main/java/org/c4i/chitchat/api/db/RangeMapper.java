package org.c4i.chitchat.api.db;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.text.StringEscapeUtils;
import org.c4i.nlp.match.Range;
import org.c4i.util.StringUtil;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;


/**
 * Turn database results into {@link Range} values.
 */
public class RangeMapper implements RowMapper<Range> {
    public static final ObjectMapper PROPS_MAPPER = new ObjectMapper();
    private static final TypeReference<HashMap<String, String>> MAP_TYPE_REFERENCE
            = new TypeReference<HashMap<String, String>>() {};

    @Override
    public Range map(ResultSet rs, StatementContext ctx) throws SQLException {

        final Range range = new Range()
                .setConversationId(rs.getString("conversationId"))
                .setLabel(rs.getString("label"))
                .setValue(rs.getString("value"))
                .setTokenStart(rs.getInt("tokenStart"))
                .setTokenEnd(rs.getInt("tokenEnd"))
                .setCharStart(rs.getInt("charStart"))
                .setCharEnd(rs.getInt("charEnd"))
                .setCharEnd(rs.getInt("charEnd"))
                .setSection(rs.getInt("section"));
        try {
            String props = rs.getString("props");
            if(props != null) {
                range.setProps(PROPS_MAPPER.readValue(StringEscapeUtils.unescapeJava(StringUtil.unquote(props)), MAP_TYPE_REFERENCE));
            }
        } catch (IOException ignored) {
        }
        return range;
    }
}
