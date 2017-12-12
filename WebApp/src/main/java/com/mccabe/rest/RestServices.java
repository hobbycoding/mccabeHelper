package com.mccabe.rest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/mccabe")
public class RestServices {
    private static DBService dbService = new DBService();
    @POST
    @Path("/process")
    public String process(String json) {
        return dbService.doProcess(json);
    }
}
