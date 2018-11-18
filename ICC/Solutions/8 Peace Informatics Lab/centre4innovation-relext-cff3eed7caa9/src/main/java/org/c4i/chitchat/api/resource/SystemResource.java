package org.c4i.chitchat.api.resource;


import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.c4i.chitchat.api.Config;
import org.c4i.util.Hash;
import org.c4i.util.Tail;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Show system specific info, such as JRE params, memory usage.
 * @author Arvid Halma
 * @version 23-11-2015
 */

@Path("/system")
@Api("/system")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class SystemResource {

    private Config config;
    private final Logger logger = LoggerFactory.getLogger(SystemResource.class);
    private static final DateTime SYSTEM_START = DateTime.now();

    public SystemResource(Config configuration) {
        this.config = configuration;
    }

    @GET
    @Path("/stats")
    @ApiOperation(
            value = "Get DB and system resource stats. ",
            response = Map.class)
    public Map<String, Object> stats(){
        Map<String, Object> stats = new LinkedHashMap<>();

        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        stats.put("rt.processors.count", Runtime.getRuntime().availableProcessors());
        stats.put("rt.memory.max.MB", MB(Runtime.getRuntime().maxMemory()));
        stats.put("rt.memory.total.MB", MB(totalMemory));
        stats.put("rt.memory.free.MB", MB(freeMemory));
        stats.put("app.memory.used.MB", MB(usedMemory));
        stats.put("uptime.start", SYSTEM_START.toDateTimeISO().toString());
        stats.put("uptime.duration", periodString(SYSTEM_START));

        return stats;
    }

    private static String periodString(DateTime otherdate) {

        Period period;
        if (otherdate.isBefore(new DateTime())) {
            period = new Period(otherdate, new DateTime());
        } else {
            period = new Period(new DateTime(), otherdate);
        }

        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendYears().appendSuffix(" year ")
                .appendMonths().appendSuffix(" months ")
                .appendDays().appendSuffix(" days ")
                .appendHours().appendSuffix(" hours ")
                .appendMinutes().appendSuffix(" minutes ")
                .printZeroNever().toFormatter();

        String s = formatter.print(period).trim();
        return s.isEmpty() ? "less than 1 minute" : s;
    }

    @GET
    @Path("/nlp/info")
    @ApiOperation(
            value = "Get NLP model info. ",
            response = Map.class)
    public Map<String, Map<String, Object>> nlpInfo(){
        return config.getNlp().info();
    }

    @GET
    @Path("/log")
    @ApiOperation(
            value = "Show latest log lines. ",
            response = String.class)
    @Produces(MediaType.TEXT_PLAIN)
    public String log(@QueryParam("n") @DefaultValue("800") int n) {
        try {
            return Tail.tailFileText(new File("log/chitchat.log").toPath(), n);
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @GET
    @Path("/log/file")
    @ApiOperation(
            value = "Download log file. ",
            response = String.class)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response logFile() throws FileNotFoundException {
        InputStream is = new FileInputStream("log/chitchat.log");
        return Response.ok(is)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"chitchat.log\"")
                .build();
    }

    @GET
    @Path("/properties")
    @ApiOperation(
            value = "Get java system properties. ",
            response = Properties.class)
    public Properties properties(){
        return System.getProperties();
    }


    private static long MB(long bytes){
        return bytes / (1024L*1024L);
    }

    /**
     * Retrieve file count and total size in bytes
     * @param path a directory
     * @return {count, sizeInBytes}
     */
    public static long[] folderStats(File path) {
        long size = 0;
        long n = 0;
        if (path.isDirectory()) {
            for (File file : path.listFiles()) {
                if (file.isFile()) {
                    n++;
                    size += file.length();
                } else {
                    long[] subStats = folderStats(file);
                    n += subStats[0];
                    size += subStats[1];
                }
            }
        } else if (path.isFile()) {
            n++;
            size += path.length();
        }
        return new long[]{n, size};
    }

    @GET
    @Path("/logout")
    @ApiOperation(
            value = "Return 401 (Unauthorized) to force a browser to logout",
            response = Response.class)
    public Response logout(){
        return Response.status(401).type("text/plain")
                .entity("logout").build();
    }

    @GET
    @Path("/createlogin")
    @RolesAllowed("ADMIN")
    @ApiOperation(
            value = "Generate basic authentication entry for basic authentication (settings.yml)",
            response = Map.class)
    public Map credentials(@QueryParam("username") @NotEmpty String username, @QueryParam("password") @NotEmpty String password){
        String salt = Hash.randomString256bit();
        return ImmutableMap.of(
                "username", username.trim(),
                "salt", salt,
                "hashedPassword", Hash.sha256Hex(salt + password)
        );
    }

    @GET
    @Path("/jar/version")
    @ApiOperation(
            value = "Retrieve the version pattern from the jar currently running",
            response = String.class)
    @Produces(MediaType.TEXT_PLAIN)
    public String versionJar() {
        try {
            String name = new File(Config.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getPath())
                    .getName();
            Matcher matcher = Pattern.compile("\\d+\\.\\d+\\.\\d+").matcher(name);
            if(matcher.find()){
                return matcher.group(0);
            }
        } catch (Exception ignored) {}
        return "?";
    }


}
