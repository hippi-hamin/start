package com.finalpro.start.util.kakaoUtil;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoDirections {
	private List<Route> routes;

	public List<Route> getRoutes() {
		return routes;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Route {
		private List<Section> sections;

		public List<Section> getSections() {
			return sections;
		}

		@JsonIgnoreProperties(ignoreUnknown = true)
		public static class Section {
			private List<Road> roads;

			public List<Road> getRoads() {
				return roads;
			}

			@JsonIgnoreProperties(ignoreUnknown = true)
			public static class Road {
				private List<Double> vertexes;

				public List<Double> getVertexes() {
					return vertexes;
				}
			}

		}

	}

}
