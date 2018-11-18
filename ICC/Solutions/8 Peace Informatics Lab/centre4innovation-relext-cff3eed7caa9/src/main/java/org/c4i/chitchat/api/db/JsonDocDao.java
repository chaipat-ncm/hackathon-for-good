package org.c4i.chitchat.api.db;

import org.c4i.chitchat.api.model.JsonDoc;
import org.c4i.chitchat.api.model.TextDoc;
import org.jdbi.v3.sqlobject.SqlObject;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.joda.time.DateTime;

import java.util.List;

/**
 * CRUD for JsonDoc
 * @author Arvid Halma
 * @version 13-4-18
 */
public interface JsonDocDao extends SqlObject {


    default void upsert(JsonDoc jsonDoc){
        upsert(
                jsonDoc.getId(),
                jsonDoc.getName(),
                jsonDoc.getType(),
                jsonDoc.getCreated(),
                jsonDoc.getUpdated(),
                JsonDocMapper.writeJson(jsonDoc.getBody())
        );
    }

    @SqlUpdate(// language=SQL
            "INSERT INTO jsondoc (id, name, type, created, updated, body) " +
                    "VALUES (CASE WHEN :id IS NULL THEN CAST(uuid_generate_v4() AS VARCHAR) ELSE :id END, :name, :type, " +
                    "CASE WHEN CAST(:created AS TIMESTAMPTZ) IS NULL THEN now() ELSE CAST(:created AS TIMESTAMPTZ) END, " +
                    "CASE WHEN CAST(:created AS TIMESTAMPTZ) IS NULL THEN now() ELSE CAST(:created AS TIMESTAMPTZ) END, " +
                    "to_jsonb(:body)) " +
                    "ON CONFLICT (id)\n" +
                    "DO UPDATE SET (name, type, created, updated, body) = (:name, :type, CAST(:created AS TIMESTAMPTZ), now(), to_jsonb(:body))\n" +
                    "WHERE jsondoc.id = :id;")
    void upsert(@Bind("id") String id, @Bind("name") String name, @Bind("type") String type, @Bind("created") DateTime created, @Bind("updated") DateTime updated, @Bind("body") String body);

    @SqlQuery(// language=SQL
            "SELECT * FROM jsondoc WHERE id = :id;" )
    JsonDoc getById(@Bind("id") String id);

    @SqlQuery(// language=SQL
            "SELECT DISTINCT name FROM jsondoc WHERE type = :type ORDER BY name ASC;" )
    List<String> getAllNames(@Bind("type") String type);

    @SqlQuery(// language=SQL
            "SELECT * FROM jsondoc WHERE name = :name ORDER BY updated DESC;" )
    List<String> getAllVersions(@Bind("name") String name);

    @SqlQuery(// language=SQL
            "SELECT t1.id, t1.name, t1.type, t1.created, t2.maxupdated AS updated, t1.body\n" +
                    "FROM jsondoc t1 \n" +
                    "INNER JOIN\n" +
                    "(\n" +
                    "  SELECT max(updated) maxupdated, name\n" +
                    "  FROM jsondoc\n" +
                    "  GROUP BY name\n" +
                    ") t2\n" +
                    "  ON t1.name = t2.name\n" +
                    "  AND t1.updated = t2.maxupdated\n" +
                    "WHERE t1.name = :name AND type = :type" )
    JsonDoc getLastUpdatedByName(@Bind("type") String type, @Bind("name") String name);

    @SqlQuery(// language=SQL
            "SELECT t1.id, t1.name, t1.type, t1.created, t2.maxupdated AS updated, t1.body\n" +
                    "FROM jsondoc t1 \n" +
                    "INNER JOIN\n" +
                    "(\n" +
                    "  SELECT max(updated) maxupdated, name\n" +
                    "  FROM jsondoc\n" +
                    "  GROUP BY name\n" +
                    ") t2\n" +
                    "  ON t1.name = t2.name\n" +
                    "  AND t1.updated = t2.maxupdated\n " +
                    " WHERE type = :type" )
    List<JsonDoc> getAllLastUpdated(@Bind("type") String type);

    @SqlQuery(// language=SQL
            "SELECT t1.id, t1.name, t1.type, t1.created, t2.maxupdated AS updated, t1.body\n" +
                    "FROM jsondoc t1 \n" +
                    "INNER JOIN\n" +
                    "(\n" +
                    "  SELECT max(updated) maxupdated, name\n" +
                    "  FROM jsondoc\n" +
                    "  GROUP BY name\n" +
                    ") t2\n" +
                    "  ON t1.name = t2.name\n" +
                    "  AND t1.updated = t2.maxupdated" )
    List<JsonDoc> getAllLastUpdated();

    @SqlQuery(// language=SQL
            "SELECT t1.id, t1.name, t1.type, t1.created, t2.maxupdated AS updated, null AS body\n" +
                    "FROM jsondoc t1 \n" +
                    "INNER JOIN\n" +
                    "(\n" +
                    "  SELECT max(updated) maxupdated, name\n" +
                    "  FROM jsondoc\n" +
                    "  GROUP BY name\n" +
                    ") t2\n" +
                    "  ON t1.name = t2.name\n" +
                    "  AND t1.updated = t2.maxupdated" )
    List<JsonDoc> getAllMetaLastUpdated();

    @SqlQuery(// language=SQL
            "SELECT t1.id, t1.name, t1.type, t1.created, t2.maxupdated AS updated, null AS body\n" +
                    "FROM jsondoc t1 \n" +
                    "INNER JOIN\n" +
                    "(\n" +
                    "  SELECT max(updated) maxupdated, name\n" +
                    "  FROM jsondoc\n" +
                    "  GROUP BY name\n" +
                    ") t2\n" +
                    "  ON t1.name = t2.name\n" +
                    "  AND t1.updated = t2.maxupdated\n" +
                    " WHERE t1.type = :type" )
    List<JsonDoc> getAllMetaLastUpdated(@Bind("type") String type);

    @SqlQuery(// language=SQL
            "SELECT * FROM jsondoc WHERE name = :name AND type = :type ORDER BY updated DESC;" )
    List<JsonDoc> getAllMetas(@Bind("type") String type, @Bind("name") String name);

    @SqlUpdate(// language=SQL
            "DELETE FROM jsondoc;")
    void deleteAlls();

    @SqlUpdate(// language=SQL
            "DELETE FROM jsondoc WHERE type = :type AND name = :name;")
    void deleteByName(@Bind("type") String type, @Bind("name") String name);

    @SqlUpdate(// language=SQL
            "UPDATE jsondoc SET id = :newid WHERE id = :orgid;")
    void updateId(@Bind("orgid") String orgid, @Bind("newid") String newid);


}
