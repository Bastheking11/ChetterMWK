import controller.APIController;

import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

@Stateless
@Path("/paths")
@Produces(MediaType.TEXT_HTML)
public class PathController {
    @GET
    public Response showAll(@Context Application application) {
        try {
            List<Endpoint> endpoints = findRESTEndpoints("controller");
            endpoints.sort(Comparator.<Endpoint, String>comparing(endpoint -> endpoint.uri).thenComparing(o -> o.method));
            StringBuilder html = new StringBuilder();

            html.append("<html>");
            html.append("<head>");
            createHeader(html, endpoints);
            html.append("</head>");
            html.append("<body>");
            createBody(html, endpoints);
            html.append("</body>");
            html.append("</html>");

            return Response.ok(html.toString()).build();

        } catch (IOException | ClassNotFoundException e) {
            return APIController.forbidden();
        }
    }

    private void createHeader(StringBuilder html, List<Endpoint> endpoints) {
        html.append("<title> Paths </title>");
        html.append("<style> body { color: #333; font-size: 14px; padding-left: 20px; padding-right: 20px; } h3 { margin-bottom:-5px; } * { font-family: verdana; }</style>");
    }

    private void createBody(StringBuilder html, List<Endpoint> endpoints) {
        html.append("<h1> REST paths </h1>");
        String last = "";
        for (Endpoint e : endpoints) {
            if (!Objects.equals(e.uri, last)) {
                last = e.uri;
                html.append("<h3>").append(e.uri).append("</h3>");
            }

            html.append("<br> [ <strong>").append(e.method.name()).append("</strong> ] ").append("<i>").append(e.javaClass).append("::").append(e.javaMethodName).append("</i><br>");

            if (!(e.queryParameters.isEmpty() && e.pathParameters.isEmpty() && e.payloadParameters.isEmpty())) {
                html.append("<span style=\"background-color:#f7f7f9;display:block;padding:9px 14px;border:1px solid #e1e1e8;border-radius:4px;\">");

                if (!e.queryParameters.isEmpty()) {
                    html.append("<strong>Query Params</strong> <br>");
                    for (EndpointParameter param : e.queryParameters) {
                        html.append("<i>").append(param.javaType).append("</i> ").append(param.name);
                        if (param.defaultValue != null)
                            html.append(" [def=").append(param.defaultValue).append("]");
                        html.append("<br>");
                    }
                }
                if (!e.pathParameters.isEmpty()) {
                    html.append("<strong>Path Params</strong> <br>");
                    for (EndpointParameter param : e.pathParameters) {
                        html.append("<i>").append(param.javaType).append("</i> ").append(param.name);
                        if (param.defaultValue != null)
                            html.append(" [def=").append(param.defaultValue).append("]");
                        html.append("<br>");
                    }
                }
                if (!e.payloadParameters.isEmpty()) {
                    html.append("<strong>Payload Params</strong> <br>");
                    for (EndpointParameter param : e.payloadParameters) {
                        html.append("<i>").append(param.javaType).append("</i> ").append(param.name);
                        if (param.defaultValue != null)
                            html.append(" [def=").append(param.defaultValue).append("]");
                        html.append("<br>");
                    }
                }

                html.append("</span>");
            }
        }
    }

    static final String NEWLINE = System.getProperty("line.separator");

    enum MethodEnum {PUT, POST, GET, PATCH, DELETE}

    enum ParameterType {QUERY, PATH, PAYLOAD}

    public class Endpoint {
        String uri;
        MethodEnum method;

        String javaClass;
        String javaMethodName;

        List<EndpointParameter> queryParameters = new ArrayList<EndpointParameter>();
        List<EndpointParameter> pathParameters = new ArrayList<EndpointParameter>();
        List<EndpointParameter> payloadParameters = new ArrayList<EndpointParameter>();
    }

    public class EndpointParameter {
        ParameterType parameterType = ParameterType.PAYLOAD;
        String javaType;
        String defaultValue;
        String name;
    }

    private List<Class> getClasses(String pkg) throws IOException, ClassNotFoundException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        // turn package into the folder equivalent
        String path = pkg.replace('.', '/');
        Enumeration<URL> resources = classloader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(getClasses(directory, pkg));
        }
        return classes;
    }

    private List<Class> getClasses(File dir, String pkg) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!dir.exists()) {
            return classes;
        }
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(getClasses(file, pkg + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(pkg + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    private String getRESTEndpointPath(Class<?> clazz) {
        String path = "";
        Annotation annotation = clazz.getAnnotation(Path.class);
        if (annotation != null) {
            path = ((Path) annotation).value() + path;
        }

        if (path.endsWith("/") == false) {
            path = path + "/";
        }
        return path;
    }

    private void discoverParameters(Method method, Endpoint endpoint) {

        Annotation[][] annotations = method.getParameterAnnotations();
        Class[] parameterTypes = method.getParameterTypes();

        for (int i = 0; i < parameterTypes.length; i++) {
            Class parameter = parameterTypes[i];

            // ignore parameters used to access context
            if ((parameter == Request.class) ||
                    (parameter == javax.servlet.http.HttpServletResponse.class) ||
                    (parameter == javax.servlet.http.HttpServletRequest.class)) {
                continue;
            }

            EndpointParameter nextParameter = new EndpointParameter();
            nextParameter.javaType = parameter.getName();

            Annotation[] parameterAnnotations = annotations[i];
            for (Annotation annotation : parameterAnnotations) {
                if (annotation instanceof PathParam) {
                    nextParameter.parameterType = ParameterType.PATH;
                    PathParam pathparam = (PathParam) annotation;
                    nextParameter.name = pathparam.value();
                } else if (annotation instanceof QueryParam) {
                    nextParameter.parameterType = ParameterType.QUERY;
                    QueryParam queryparam = (QueryParam) annotation;
                    nextParameter.name = queryparam.value();
                } else if (annotation instanceof DefaultValue) {
                    DefaultValue defaultvalue = (DefaultValue) annotation;
                    nextParameter.defaultValue = defaultvalue.value();
                }
            }

            switch (nextParameter.parameterType) {
                case PATH:
                    endpoint.pathParameters.add(nextParameter);
                    break;
                case QUERY:
                    endpoint.queryParameters.add(nextParameter);
                    break;
                case PAYLOAD:
                    endpoint.payloadParameters.add(nextParameter);
                    break;
            }
        }
    }

    private Endpoint createEndpoint(Method javaMethod, MethodEnum restMethod, Class<?> clazz, String classUri) {
        Endpoint newEndpoint = new Endpoint();
        newEndpoint.method = restMethod;
        newEndpoint.javaMethodName = javaMethod.getName();
        newEndpoint.javaClass = clazz.getName();

        Path path = javaMethod.getAnnotation(Path.class);
        if (path != null) {
            newEndpoint.uri = classUri + path.value();
        } else {
            newEndpoint.uri = classUri;
        }
        discoverParameters(javaMethod, newEndpoint);
        return newEndpoint;
    }

    public List<Endpoint> findRESTEndpoints(String basepackage) throws IOException, ClassNotFoundException {
        List<Endpoint> endpoints = new ArrayList<Endpoint>();

        List<Class> classes = getClasses(basepackage);

        for (Class<?> clazz : classes) {
            Annotation annotation = clazz.getAnnotation(Path.class);
            if (annotation != null) {

                String basePath = getRESTEndpointPath(clazz);
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    Method om = method;
                    while (true) {
                        try {
                            Method f = method.getDeclaringClass().getSuperclass().getMethod(method.getName(), method.getParameterTypes());
                            if (f != null)
                                method = f;
                            else break;

                        } catch (Exception e) {
                            break;
                        }
                    }

                    if (method.isAnnotationPresent(GET.class)) {
                        endpoints.add(createEndpoint(om, MethodEnum.GET, clazz, basePath));
                    } else if (method.isAnnotationPresent(PUT.class)) {
                        endpoints.add(createEndpoint(om, MethodEnum.PUT, clazz, basePath));
                    } else if (method.isAnnotationPresent(POST.class)) {
                        endpoints.add(createEndpoint(om, MethodEnum.POST, clazz, basePath));
                    } else if (method.isAnnotationPresent(DELETE.class)) {
                        endpoints.add(createEndpoint(om, MethodEnum.DELETE, clazz, basePath));
                    } else if (method.isAnnotationPresent(PATCH.class)) {
                        endpoints.add(createEndpoint(om, MethodEnum.PATCH, clazz, basePath));
                    }
                }
            }
        }

        return endpoints;
    }
}
