package com.mccabe.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/mccabe")
public class RestServices {
    private static DBService dbService = new DBService();
    @POST
    @Path("/{param}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String getMsg(@PathParam("param") String msg) {
        return dbService.doProcess(msg);
    }
}
