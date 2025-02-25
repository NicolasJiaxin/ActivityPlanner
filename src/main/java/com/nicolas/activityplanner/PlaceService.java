package com.nicolas.activityplanner;

import com.google.maps.routing.v2.*;
import com.google.rpc.StatusProto;
import com.google.type.LatLng;
import com.nicolas.activityplanner.algorithms.Clustering;
import com.nicolas.activityplanner.algorithms.Plan;
import com.nicolas.activityplanner.algorithms.Tsp;
import io.grpc.*;
import io.grpc.netty.NettyChannelBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
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

    public Plan[] getPlans(int days, boolean minimizeTime) {
        // 0: distance matrix 1: static duration matrix 2: duration matrix
        long[][][] costs = computeDistance();

        // Cluster by time or static duration specified by minimizeTime
        // Do not include the first place(home place) in the clusters
        int minOpt = minimizeTime ? 1 : 0;
        List<Integer>[] clusters = Clustering.cluster(costs[minOpt], days, true);

        // Add the first place(home place) to all clusters
        for (int i = 0; i < clusters.length; i++) {
            // Simpler to add and swap since the order does not matter for the rest of the elements
            clusters[i].add(0);
            Collections.swap(clusters[i], 0, clusters[i].size() - 1);
        }

        // Get tour for each day (cluster)
        Plan[] plans = new Plan[days];
        for (int i = 0; i < clusters.length; i++) {

            // Tsp
            long[][] clusterCosts = getClusterCosts(costs[minOpt], clusters[i]);

            Tsp.setup(clusterCosts);
            Tsp.solve();
            int[] tour = Tsp.getBestTour();
            long travelCost = Tsp.getBestTourCost();

            // Create the plan according to the Tsp
            Plan plan = new Plan();

            // Determine what is travelCost about
            if (minimizeTime) {
                plan.setTravelTimeCost(travelCost);
            } else {
                plan.setDistanceCost(travelCost);
            }

            // Add first place to list
            int idxCluster = tour[0];
            int idxPlaces = clusters[i].get(idxCluster);
            plan.addPlaceToItinerary(placesList.get(idxPlaces));

            int prevIdxPlaces = idxPlaces;
            int firstPlaceIdx = idxPlaces;
            // Cost for the other metrics
            int visitTimeCost = 0;
            long otherTravelCost = 0;   // distanceCost if minimizeTime, travelTimeCost else
            int otherOpt = minimizeTime ? 0 : 1;
            for (int j = 1; j < tour.length; j++) {
                // Tour is indexed for the cluster
                idxCluster = tour[j];
                // From the cluster, get the actual number of the place
                idxPlaces = clusters[i].get(idxCluster);
                // From list of places, add the place to the plan
                Place place = placesList.get(idxPlaces);

                plan.addPlaceToItinerary(place);
                visitTimeCost += place.getVisitDuration();
                otherTravelCost += costs[otherOpt][prevIdxPlaces][idxPlaces];

                prevIdxPlaces = idxPlaces;
            }
            // Re-add the first place to complete the tour
            plan.addPlaceToItinerary(plan.getPlaceByIndex(0));
            otherTravelCost += costs[otherOpt][prevIdxPlaces][firstPlaceIdx];
            if (minimizeTime) {
                plan.setDistanceCost(otherTravelCost);
            } else {
                plan.setTravelTimeCost(otherTravelCost);
            }
            plan.setVisitTimeCost(visitTimeCost);
            plans[i] = plan;
        }

        return plans;
    }

    public long[][] getClusterCosts(long[][] Costs, List<Integer> cluster) {
        long[][] clusterCosts = new long[cluster.size()][cluster.size()];

        for (int i = 0; i < cluster.size(); i++) {
            for (int j = 0; j < cluster.size(); j++) {
                clusterCosts[i][j] = Costs[cluster.get(i)][cluster.get(j)];
            }
        }

        return clusterCosts;
    }

    public long[][][] computeDistance() {
        String apiKey = System.getenv("GoogleApiKey");

        // The standard TLS port is 443
        Channel channel = NettyChannelBuilder.forAddress("routes.googleapis.com", 443).build();
        channel = ClientInterceptors.intercept(channel, new RoutesClient.RoutesInterceptor(apiKey));

        RoutesClient routesClient = new RoutesClient(channel);
        for (Place place : placesList) {
            routesClient.addWaypoint(place.getLatitude(), place.getLongitude());
        }
        return routesClient.computeRouteMatrix();
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
                        headers.put(FIELD_MASK_HEADER, "*");
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
                        .setRouteModifiers(RouteModifiers.newBuilder().setAvoidTolls(true).setAvoidFerries(true)))
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
