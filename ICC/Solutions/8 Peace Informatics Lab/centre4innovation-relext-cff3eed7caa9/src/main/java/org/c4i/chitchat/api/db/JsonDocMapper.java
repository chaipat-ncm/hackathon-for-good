package org.c4i.chitchat.api.db;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.text.StringEscapeUtils;
import org.c4i.chitchat.api.model.JsonDoc;
import org.c4i.util.StringUtil;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.joda.time.DateTime;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;

/**
 * Turn database results into {@link JsonDoc} values.
 * @author Arvid Halma
 */
public class JsonDocMapper implements RowMapper<JsonDoc> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<LinkedHashMap<String, Object>> TYPE_REF = new TypeReference<LinkedHashMap<String, Object>>() {};

    public static LinkedHashMap<String, Object> readJson(String json){
        if(json != null) {
            try {
                json = StringEscapeUtils.unescapeJava(StringUtil.unquote(json));
                return MAPPER.readValue(json, TYPE_REF);
            } catch (IOException ignored) {
            }
        }
        return null;
    }

    public static String writeJson(LinkedHashMap<String, Object> obj){
        if(obj != null) {
            try {
                return MAPPER.writeValueAsString(obj);
            } catch (IOException ignored) {
            }
        }
        return null;
    }

    @Override
    public JsonDoc map(ResultSet rs, StatementContext ctx) throws SQLException {
        final String json = rs.getString("body");
        LinkedHashMap<String, Object> body = readJson(json);
        return new JsonDoc()
                .setId(rs.getString("id"))
                .setName(rs.getString("name"))
                .setType(rs.getString("type"))
                .setCreated(new DateTime(rs.getTimestamp("created")))
                .setUpdated(new DateTime(rs.getTimestamp("updated")))
                .setBody(body);
    }
}

