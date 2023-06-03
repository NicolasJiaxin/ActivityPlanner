package com.nicolas.activityplanner;

import com.google.maps.routing.v2.*;
import com.google.rpc.StatusProto;
import com.google.type.LatLng;
import com.nicolas.activityplanner.algorithms.Clustering;
import io.grpc.*;
import io.grpc.netty.NettyChannelBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class PlaceService {
    private static List<Place> placesList = new ArrayList<>();

    public List<Place> getPlacesList() {
        return placesList;
    }

    public void setPlacesList(List<Place> places) {
        placesList = new ArrayList<>(places);
    }

    public void computeDistance() {
        String apiKey = "AIzaSyDw1_NBRgD0_BROwJu7LqUdbV9ZYzW5uVQ";//System.getenv("AIzaSyDw1_NBRgD0_BROwJu7LqUdbV9ZYzW5uVQ");

        // The standard TLS port is 443
        Channel channel = NettyChannelBuilder.forAddress("routes.googleapis.com", 443).build();
        channel = ClientInterceptors.intercept(channel, new RoutesClient.RoutesInterceptor(apiKey));

        RoutesClient routesClient = new RoutesClient(channel);
        for (Place place : placesList) {
            routesClient.addWaypoint(place.getLatitude(), place.getLongitude());
        }
        long[][][] matrices = routesClient.computeRouteMatrix();
        Clustering.cluster(matrices[2], 5, true);
    }

    private static class RoutesClient {
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
            public ClientCall interceptCall(MethodDescriptor method,
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
        private final List<Waypoint> waypoints;

        public RoutesClient(Channel channel) {
            blockingStub = RoutesGrpc.newBlockingStub(channel);
            waypoints = new ArrayList<>();
        }

        private static Waypoint createWaypointForLatLng(double lat, double lng) {
            return Waypoint.newBuilder()
                    .setLocation(Location.newBuilder().setLatLng(LatLng.newBuilder().setLatitude(lat).setLongitude(lng)))
                    .build();
        }

        public void addWaypoint(double lat, double lng) {
            waypoints.add(createWaypointForLatLng(lat,lng));
        }

        public long[][][] computeRouteMatrix() {
            System.out.println("Waypoints count: " + waypoints.size());
            ComputeRouteMatrixRequest.Builder builder  = ComputeRouteMatrixRequest.newBuilder()
                    .setTravelMode(RouteTravelMode.DRIVE).setRoutingPreference(RoutingPreference.TRAFFIC_AWARE);
            for (Waypoint waypoint : waypoints) {
                builder.addOrigins(RouteMatrixOrigin.newBuilder().setWaypoint(waypoint)
                        .setRouteModifiers(RouteModifiers.newBuilder().setAvoidTolls(true).setAvoidFerries(true).setAvoidFerries(true)))
                        .addDestinations(RouteMatrixDestination.newBuilder()
                                .setWaypoint(waypoint));
            }

            ComputeRouteMatrixRequest request = builder.build();
            System.out.println("Origins: " + request.getOriginsCount() + " Destinations: " + request.getDestinationsCount());

            Iterator elements;
            try {
                logger.info("About to send request: " + request.toString());
                elements = blockingStub.withDeadlineAfter(2000, TimeUnit.MILLISECONDS).computeRouteMatrix(request);
            } catch (StatusRuntimeException e) {
                logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
                return null;
            }

            // 0: distance matrix
            // 1: static duration matrix
            // 2: duration matrix
            long[][][] matrices = new long[3][request.getOriginsCount()][request.getDestinationsCount()];

            while (elements.hasNext()) {
                RouteMatrixElement element = (RouteMatrixElement) elements.next();
                if (element.getStatus().getCode() == 0 && element.getCondition() == RouteMatrixElementCondition.ROUTE_EXISTS) {
                    int i = element.getOriginIndex();
                    int j = element.getDestinationIndex();
                    matrices[0][i][j] = element.getDistanceMeters();
                    matrices[1][i][j] = element.getStaticDuration().getSeconds();
                    matrices[2][i][j] = element.getDuration().getSeconds();
                    logger.info("Element response: " + element.toString());
                } else {
                    logger.log(Level.WARNING, "Error for entry: " + element.getOriginIndex() + " " + element.getDestinationIndex());
                }
            }

            return matrices;
        }
    }
}
