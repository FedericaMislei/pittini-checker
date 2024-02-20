package it.quix.pittini.checker.rest.v1;

import it.quix.pittini.checker.job.Check;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

@Slf4j
@Path("")
public class ControlloRest {
    @Inject
    protected Check check;

    @Path("InviaEmailJob/start")
    @GET
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "500",
                    description = "Errore",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))),
            @APIResponse(
                    responseCode = "403",
                    description = "Permission error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))),
            @APIResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "Avvia il Job Invio Email",
            description = "Avvia il Job Invio Email",
            operationId = "startJob")
    public Response startJob() {
        try {
            new Thread(() -> {
                try {
                    check.start();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }catch (Exception e){
            log.error("Avvia il Job Invio Email Error");
            return Response.serverError().build();
        }
        return Response.ok().build();
    }

    @Path("InviaEmailJobVolte/start")
    @GET
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "500",
                    description = "Errore",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))),
            @APIResponse(
                    responseCode = "403",
                    description = "Permission error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))),
            @APIResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))})
    @Operation(summary = "Avvia il Job Invio Email",
            description = "Avvia il Job Invio Email",
            operationId = "startJobVolte")
    public Response startJobVolte() {
        try {
            new Thread(() -> {
                try {
                    check.start1();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }catch (Exception e){
            log.error("Avvia il Job Invio Email Error");
            return Response.serverError().build();
        }
        return Response.ok().build();
    }

}
