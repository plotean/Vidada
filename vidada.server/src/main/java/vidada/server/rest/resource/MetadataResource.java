package vidada.server.rest.resource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides dynamic Metadata for a REST Resource
 */
public class MetadataResource extends AbstractResource {

    private final Class<?> restResourceType;

    /**
     * Type of the REST Resource for which metadata should be generated.
     * @param restResourceType
     */
    public MetadataResource(Class<?> restResourceType){
        this.restResourceType = restResourceType;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public String getMetadataImpl(){
        return serializeJson(fetchMetadata(restResourceType));
    }

    /**
     * Fetches the metadata for the given REST type
     * @param restType
     * @return
     */
    private List<RestDoc> fetchMetadata(Class<?> restType){
        List<RestDoc> documentation = new ArrayList<>();

        String rootUri = "";

        if(restType.isAnnotationPresent(Path.class)){
            rootUri += restType.getAnnotation(Path.class).value();
        }
        rootUri += "/";

        for(Method m : restType.getMethods()){
            if(
                    m.isAnnotationPresent(GET.class)
                    || m.isAnnotationPresent(POST.class)
                    || m.isAnnotationPresent(PUT.class)
                    || m.isAnnotationPresent(DELETE.class)){
                // Just document this operation
                RestDoc doc = documentMethod(rootUri, m);
                documentation.add(doc);
            }else{
                // If no HTTP method is specified but we still have a path
                // it means that we have to look at the specified sub resource
                if(m.isAnnotationPresent(Path.class)){

                    if(!m.getReturnType().equals(MetadataResource.class)){

                        String subPath = m.getAnnotation(Path.class).value();
                        String subUri = rootUri + subPath;

                        // fetch sub resources...
                        List<RestDoc> dubDocs = fetchMetadata(m.getReturnType());
                        for(RestDoc sub : dubDocs){
                            sub.setUri(subUri + sub.getUri());
                            documentation.add(sub);
                        }

                    }
                }
            }
        }
        return documentation;
    }

    /**
     * Create a documentation for the given REST Method
     * @param rootUri
     * @param m
     * @return
     */
    private RestDoc documentMethod(String rootUri, Method m){
        HttpType type = toHttpType(m);

        RestDoc doc = new RestDoc(type, m.getName(), getResourceURI(rootUri, m));

        Class<?>[] paramTypes = m.getParameterTypes();
        Annotation[][] paramAnnotations = m.getParameterAnnotations();

        for(int i=0; i<paramTypes.length; i++){
            Class<?> paramType = paramTypes[i];
            Annotation[] paramAns = paramAnnotations[i];
            QueryParam queryAno = null;
            for(Annotation annotation : paramAns){
                if(annotation.annotationType().equals(QueryParam.class)){
                    queryAno = (QueryParam)annotation;
                    break;
                }
            }
            if(queryAno != null){
                doc.addParameter(queryAno.value(), paramType.getSimpleName());
            }
        }

        return doc;
    }

    private String getResourceURI(String base, Method m){
        if(m.isAnnotationPresent(Path.class)){
            String subPath = m.getAnnotation(Path.class).value();
            base += subPath;
        }
        return base;
    }

    private HttpType toHttpType(Method m){
        HttpType type;
        if(m.isAnnotationPresent(GET.class)){
            type = HttpType.Get;
        }else
        if(m.isAnnotationPresent(POST.class)){
            type = HttpType.Post;
        }else
        if(m.isAnnotationPresent(PUT.class)){
            type = HttpType.Put;
        }else
        if(m.isAnnotationPresent(DELETE.class)){
            type = HttpType.Delete;
        }else{
            type = HttpType.Get;
        }
        return type;
    }


    /**
     * REST Http type
     */
    private enum HttpType {
        Get,
        Post,
        Put,
        Delete
    }

    /**
     * Represents a REST Method parameter
     */
    private class Parameter {
        private String name;
        private String typeName;

        public Parameter(String name, String typeName){
            this.name = name;
            this.typeName = typeName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }
    }

    /**
     * Represents documentation for a single REST operation
     */
    private class RestDoc {
        private HttpType type;
        private String uri;
        private List<Parameter> parameters = new ArrayList<>();
        private String name;


        public RestDoc(HttpType type, String name, String uri){
            this.type = type;
            this.uri = uri;
            this.name = name;
        }

        public HttpType getType() {
            return type;
        }

        public void setType(HttpType type) {
            this.type = type;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void addParameter(String param, String typeName){
            parameters.add(new Parameter(param, typeName));
        }
    }

}
