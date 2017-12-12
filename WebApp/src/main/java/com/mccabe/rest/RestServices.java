package com.mccabe.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/mccabe")
public class RestServices {
    private static DBService dbService = new DBService();
    @POST
    @Path("/process")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String process(String json) {
        return dbService.doProcess(json);
    }

    @POST
    @Path("/test")
    @Consumes(MediaType.TEXT_PLAIN)
    public String test(String json) {
        return dbService.doProcess(json);
    }
}
