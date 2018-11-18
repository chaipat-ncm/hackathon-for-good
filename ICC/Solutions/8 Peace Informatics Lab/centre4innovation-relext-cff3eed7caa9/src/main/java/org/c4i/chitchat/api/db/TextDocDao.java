package org.c4i.chitchat.api.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.c4i.chitchat.api.model.TextDoc;
import org.c4i.nlp.chat.Conversation;
import org.c4i.nlp.chat.Message;
import org.c4i.nlp.match.Range;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.mapper.MapMapper;
import org.jdbi.v3.core.statement.PreparedBatch;
import org.jdbi.v3.sqlobject.SqlObject;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * CRUD for TextDoc
 * @author Arvid Halma
 * @version 13-4-18
 */
public interface TextDocDao extends SqlObject {


    @SqlUpdate(// language=SQL
            "INSERT INTO textdoc (id, name, type, created, updated, body) " +
            "VALUES (CASE WHEN :id IS NULL THEN CAST(uuid_generate_v4() AS VARCHAR) ELSE :id END, :name, :type, " +
                    "CASE WHEN CAST(:created AS TIMESTAMPTZ) IS NULL THEN now() ELSE CAST(:created AS TIMESTAMPTZ) END, " +
                    "CASE WHEN CAST(:created AS TIMESTAMPTZ) IS NULL THEN now() ELSE CAST(:created AS TIMESTAMPTZ) END, " +
                    ":body) " +
            "ON CONFLICT (id)\n" +
            "DO UPDATE SET (name, type, created, updated, body) = (:name, :type, CAST(:created AS TIMESTAMPTZ), now(), :body)\n" +
            "WHERE textdoc.id = :id;")
    void upsert(@BindBean TextDoc textDoc);

    @SqlQuery(// language=SQL
            "SELECT * FROM textdoc WHERE id = :id;" )
    TextDoc getById(@Bind("id") String id);

    @SqlQuery(// language=SQL
            "SELECT DISTINCT name FROM textdoc WHERE type = :type ORDER BY name ASC;" )
    List<String> getAllNames(@Bind("type") String type);

    @SqlQuery(// language=SQL
            "SELECT * FROM textdoc WHERE name = :name ORDER BY updated DESC;" )
    List<String> getAllNamesVersions(@Bind("name") String name);

    @SqlQuery(// language=SQL
            "SELECT t1.id, t1.name, t1.type, t1.created, t2.maxupdated AS updated, t1.body\n" +
                    "FROM textdoc t1 \n" +
                    "INNER JOIN\n" +
                    "(\n" +
                    "  SELECT max(updated) maxupdated, name\n" +
                    "  FROM textdoc\n" +
                    "  GROUP BY name\n" +
                    ") t2\n" +
                    "  ON t1.name = t2.name\n" +
                    "  AND t1.updated = t2.maxupdated\n" +
                    "WHERE t1.name = :name AND type = :type" )
    TextDoc getLastUpdatedByName(@Bind("type") String type, @Bind("name") String name);

    @SqlQuery(// language=SQL
            "SELECT t1.id, t1.name, t1.type, t1.created, t2.maxupdated AS updated, t1.body\n" +
                    "FROM textdoc t1 \n" +
                    "INNER JOIN\n" +
                    "(\n" +
                    "  SELECT max(updated) maxupdated, name\n" +
                    "  FROM textdoc\n" +
                    "  GROUP BY name\n" +
                    ") t2\n" +
                    "  ON t1.name = t2.name\n" +
                    "  AND t1.updated = t2.maxupdated\n " +
                    " WHERE type = :type" )
    List<TextDoc> getAllLastUpdated(@Bind("type") String type);

    @SqlQuery(// language=SQL
            "SELECT t1.id, t1.name, t1.type, t1.created, t2.maxupdated AS updated, t1.body\n" +
                    "FROM textdoc t1 \n" +
                    "INNER JOIN\n" +
                    "(\n" +
                    "  SELECT max(updated) maxupdated, name\n" +
                    "  FROM textdoc\n" +
                    "  GROUP BY name\n" +
                    ") t2\n" +
                    "  ON t1.name = t2.name\n" +
                    "  AND t1.updated = t2.maxupdated" )
    List<TextDoc> getAllLastUpdated();

    @SqlQuery(// language=SQL
            "SELECT t1.id, t1.name, t1.type, t1.created, t2.maxupdated AS updated, null AS body\n" +
                    "FROM textdoc t1 \n" +
                    "INNER JOIN\n" +
                    "(\n" +
                    "  SELECT max(updated) maxupdated, name\n" +
                    "  FROM textdoc\n" +
                    "  GROUP BY name\n" +
                    ") t2\n" +
                    "  ON t1.name = t2.name\n" +
                    "  AND t1.updated = t2.maxupdated" )
    List<TextDoc> getAllMetaLastUpdated();

    @SqlQuery(// language=SQL
            "SELECT t1.id, t1.name, t1.type, t1.created, t2.maxupdated AS updated, null AS body\n" +
                    "FROM textdoc t1 \n" +
                    "INNER JOIN\n" +
                    "(\n" +
                    "  SELECT max(updated) maxupdated, name\n" +
                    "  FROM textdoc\n" +
                    "  GROUP BY name\n" +
                    ") t2\n" +
                    "  ON t1.name = t2.name\n" +
                    "  AND t1.updated = t2.maxupdated\n" +
                    " WHERE t1.type = :type" )
    List<TextDoc> getAllMetaLastUpdated(@Bind("type") String type);

    @SqlQuery(// language=SQL
            "SELECT * FROM textdoc WHERE name = :name AND type = :type ORDER BY updated DESC;" )
    List<TextDoc> getAllMetas(@Bind("type") String type, @Bind("name") String name);

    @SqlUpdate(// language=SQL
            "DELETE FROM textdoc;")
    void deleteAll();

    @SqlUpdate(// language=SQL
            "DELETE FROM textdoc WHERE type = :type AND name = :name;")
    void deleteByName(@Bind("type") String type, @Bind("name") String name);

    @SqlUpdate(// language=SQL
            "UPDATE textdoc SET id = :newid WHERE id = :orgid;")
    void updateId(@Bind("orgid") String orgid, @Bind("newid") String newid);


}
