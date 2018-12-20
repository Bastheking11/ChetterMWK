package controller;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Produces(MediaType.APPLICATION_JSON)
public abstract class APIControllerCRUD<T> extends APIController<T> {

    // Calls
    public abstract Response Get();

    public abstract Response Get(long id);

    public abstract Response Create(T entity);

    public abstract Response Update(T entity);

    @DELETE
    public Response Delete(T entity) {
        getService().delete(entity);
        return success();
    }

}
