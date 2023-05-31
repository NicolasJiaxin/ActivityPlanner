import com.google.maps.routing.v2.*;
import com.google.type.LatLng;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ClientInterceptors;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.NettyChannelBuilder;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class RoutesClient {
    // For more detail on inserting API keys, see:
    // https://cloud.google.com/endpoints/docs/grpc/restricting-api-access-with-api-keys#java
    // For more detail on system parameters (such as FieldMask), see:
    // https://cloud.google.com/apis/docs/system-parameters
    private static final class RoutesInterceptor implements ClientInterceptor {
        private final String apiKey;
        private static final Logger logger = Logger.getLogger(RoutesInterceptor.class.getName());
        private static Metadata.Key API_KEY_HEADER = Metadata.Key.of("x-goog-api-key",
                Metadata.ASCII_STRING_MARSHALLER);
        private static Metadata.Key FIELD_MASK_HEADER = Metadata.Key.of("x-goog-fieldmask",
                Metadata.ASCII_STRING_MARSHALLER);

        public RoutesInterceptor(String apiKey) {
            this.apiKey = apiKey;
        }

        @Override
        public  ClientCall interceptCall(MethodDescriptor method,
                                         CallOptions callOptions, Channel next) {
            logger.info("Intercepted " + method.getFullMethodName());
            ClientCall call = next.newCall(method, callOptions);
            call = new ForwardingClientCall.SimpleForwardingClientCall(call) {
                @Override
                public void start(Listener responseListener, Metadata headers) {
                    headers.put(API_KEY_HEADER, apiKey);
                    // Note that setting the field mask to * is OK for testing, but discouraged in
                    // production.
                    // For example, for ComputeRoutes, set the field mask to
                    // "routes.distanceMeters,routes.duration,routes.polyline.encodedPolyline"
                    // in order to get the route distances, durations, and encoded polylines.
                    headers.put(FIELD_MASK_HEADER, "origin_index,destination_index,distance_meters,duration,condition");
                    super.start(responseListener, headers);
                }
            };
            return call;
        }
    }

    private static final Logger logger = Logger.getLogger(RoutesClient.class.getName());
    private final RoutesGrpc.RoutesBlockingStub blockingStub;

    public RoutesClient(Channel channel) {
        blockingStub = RoutesGrpc.newBlockingStub(channel);
    }

    public static Waypoint createWaypointForLatLng(double lat, double lng) {
        return Waypoint.newBuilder()
                .setLocation(Location.newBuilder().setLatLng(LatLng.newBuilder().setLatitude(lat).setLongitude(lng)))
                .build();
    }

    public void computeRoutes() {
        ComputeRoutesRequest request = ComputeRoutesRequest.newBuilder()
                .setOrigin(createWaypointForLatLng(37.420761, -122.081356))
                .setDestination(createWaypointForLatLng(37.420999, -122.086894)).setTravelMode(RouteTravelMode.DRIVE)
                .setRoutingPreference(RoutingPreference.TRAFFIC_AWARE).setComputeAlternativeRoutes(true)
                .setRouteModifiers(
                        RouteModifiers.newBuilder().setAvoidTolls(false).setAvoidHighways(true).setAvoidFerries(true))
                .setPolylineQuality(PolylineQuality.OVERVIEW).build();
        ComputeRoutesResponse response;
        try {
            logger.info("About to send request: " + request.toString());
            response = blockingStub.withDeadlineAfter(2000, TimeUnit.MILLISECONDS).computeRoutes(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Response: " + response.toString());
    }

    public void computeRouteMatrix() {
        ComputeRouteMatrixRequest request = ComputeRouteMatrixRequest.newBuilder()
                .addOrigins(RouteMatrixOrigin.newBuilder().setWaypoint(createWaypointForLatLng(37.420761, -122.081356))
                        .setRouteModifiers(RouteModifiers.newBuilder().setAvoidTolls(false).setAvoidHighways(true)
                                .setAvoidFerries(true)))
                .addOrigins(RouteMatrixOrigin.newBuilder().setWaypoint(createWaypointForLatLng(37.403184, -122.097371)))
                .addOrigins(RouteMatrixOrigin.newBuilder().setWaypoint(createWaypointForLatLng(30.716882567922273, 103.95536670441692))
                        .setRouteModifiers(RouteModifiers.newBuilder().setAvoidTolls(true).setAvoidFerries(true).setAvoidFerries(true)))
                .addDestinations(RouteMatrixDestination.newBuilder()
                        .setWaypoint(createWaypointForLatLng(37.420999, -122.086894)))
                .addDestinations(RouteMatrixDestination.newBuilder()
                        .setWaypoint(createWaypointForLatLng(37.383047, -122.044651)))
                .setTravelMode(RouteTravelMode.DRIVE).setRoutingPreference(RoutingPreference.TRAFFIC_AWARE).build();
        Iterator elements;
        try {
            logger.info("About to send request: " + request.toString());
            elements = blockingStub.withDeadlineAfter(2000, TimeUnit.MILLISECONDS).computeRouteMatrix(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }

        while (elements.hasNext()) {
            RouteMatrixElement element = (RouteMatrixElement)elements.next();
            System.out.println(element.getOriginIndex() + " " + element.getDestinationIndex() + " " +
            element.getDuration() + " " +
            element.getDistanceMeters() + " " +
                    element.getCondition());

            logger.info("Element response: " + element.toString());

        }
    }

    public static void main(String[] args) throws Exception {
        String apiKey = "AIzaSyDw1_NBRgD0_BROwJu7LqUdbV9ZYzW5uVQ";//System.getenv("AIzaSyDw1_NBRgD0_BROwJu7LqUdbV9ZYzW5uVQ");

        // The standard TLS port is 443
        Channel channel = NettyChannelBuilder.forAddress("routes.googleapis.com", 443).build();
        channel = ClientInterceptors.intercept(channel, new RoutesInterceptor(apiKey));

        RoutesClient client = new RoutesClient(channel);
        //client.computeRoutes();
        client.computeRouteMatrix();
    }
}