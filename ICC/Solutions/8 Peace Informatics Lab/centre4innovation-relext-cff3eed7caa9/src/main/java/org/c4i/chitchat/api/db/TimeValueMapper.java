package org.c4i.chitchat.api.db;

import org.c4i.util.TimeValue;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.joda.time.DateTime;
//import org.skife.jdbi.v2.StatementContext;
//import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Turn database results into {@link org.c4i.util.TimeValue} values.
 * @author Arvid Halma
 * @version 3-4-2016
 */
public class TimeValueMapper implements RowMapper<TimeValue>
{
    @Override
    public TimeValue map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new TimeValue(new DateTime(rs.getTimestamp(1)), rs.getDouble(2));
    }

}