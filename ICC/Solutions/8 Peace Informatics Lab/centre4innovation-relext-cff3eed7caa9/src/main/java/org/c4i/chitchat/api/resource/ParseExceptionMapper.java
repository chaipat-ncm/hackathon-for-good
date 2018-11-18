package org.c4i.chitchat.api.resource;

import com.google.common.collect.ImmutableMap;
import org.c4i.nlp.match.Compiler;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Turn {@link Compiler.ParseError} into {@link javax.ws.rs.WebApplicationException}
 * @author Arvid Halma
 * @version 8-8-2017 - 21:08
 */
public class ParseExceptionMapper  implements ExceptionMapper<Compiler.ParseError> {
    @Override
    public Response toResponse(Compiler.ParseError e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(ImmutableMap.of(
                        "line", e.getLine(),
                        "message", e.getMessage()
                )).build();
    }
}
