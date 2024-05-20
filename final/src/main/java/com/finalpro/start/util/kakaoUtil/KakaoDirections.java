package com.finalpro.start.util.kakaoUtil;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoDirections {
    private List<Route> routes;

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Route {
        private Summary summary;
        private List<Section> sections;

        public Summary getSummary() {
            return summary;
        }

        public void setSummary(Summary summary) {
            this.summary = summary;
        }

        public List<Section> getSections() {
            return sections;
        }

        public void setSections(List<Section> sections) {
            this.sections = sections;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Summary {
            private double distance;
            private int duration;

            public double getDistance() {
                return distance;
            }

            public void setDistance(double distance) {
                this.distance = distance;
            }

            public int getDuration() {
                return duration;
            }

            public void setDuration(int duration) {
                this.duration = duration;
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Section {
            private List<Road> roads;

            public List<Road> getRoads() {
                return roads;
            }

            public void setRoads(List<Road> roads) {
                this.roads = roads;
            }

            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Road {
                private List<Double> vertexes;

                public List<Double> getVertexes() {
                    return vertexes;
                }

                public void setVertexes(List<Double> vertexes) {
                    this.vertexes = vertexes;
                }
            }
        }
    }
}
