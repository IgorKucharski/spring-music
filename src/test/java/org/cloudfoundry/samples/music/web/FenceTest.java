package org.cloudfoundry.samples.music.web;

/**
 * The Fence — anti-corruption layer tests.
 *
 * PURPOSE: Verify that internal monolith fields do not leak into the new
 * Album Catalog Service's public API after extraction (The Cut).
 *
 * STATUS: Waiting for The Cut (service extraction) to be completed.
 * Once the new service is running, fill in NEW_SERVICE_BASE_URL and
 * activate the tests below by removing the @Ignore annotations.
 *
 * KNOWN INTERNAL FIELDS TO GUARD (from Album.java):
 *   - albumId  : internal seed-data identifier, must NOT appear in new service API
 *
 * BOUNDARY RULE (from ADR-001):
 *   The monolith's data model must not leak into the new service's public shape.
 *   A test that fails loudly if a monolith field name appears in the new service's
 *   API response is the forcing function.
 *
 * Run with: ./gradlew test --tests "*.FenceTest"
 */

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FenceTest {

    // TODO: set to new service URL once The Cut is complete
    private static final String NEW_SERVICE_BASE_URL = "http://localhost:8081";

    /**
     * The primary fence: 'albumId' is an internal monolith field and must
     * NOT be present in the new Album Catalog Service's GET /albums response.
     *
     * If this test goes red after The Cut, the anti-corruption layer is broken.
     */
    @Ignore("Activate once The Cut is complete — set NEW_SERVICE_BASE_URL above")
    @Test
    public void newService_albumsEndpoint_doesNotExposeInternalAlbumIdField() throws Exception {
        String response = java.net.http.HttpClient.newHttpClient()
                .send(
                        java.net.http.HttpRequest.newBuilder()
                                .uri(java.net.URI.create(NEW_SERVICE_BASE_URL + "/albums"))
                                .GET().build(),
                        java.net.http.HttpResponse.BodyHandlers.ofString()
                )
                .body();

        assertThat(
                "Internal 'albumId' field must not leak into new service API",
                response,
                not(containsString("\"albumId\""))
        );
    }

    /**
     * Secondary fence: the new service API must use the standard 'id' field,
     * not any renamed variant of the monolith's internal identifier.
     */
    @Ignore("Activate once The Cut is complete")
    @Test
    public void newService_albumsEndpoint_exposesCleanIdField() throws Exception {
        String response = java.net.http.HttpClient.newHttpClient()
                .send(
                        java.net.http.HttpRequest.newBuilder()
                                .uri(java.net.URI.create(NEW_SERVICE_BASE_URL + "/albums"))
                                .GET().build(),
                        java.net.http.HttpResponse.BodyHandlers.ofString()
                )
                .body();

        assertThat(
                "New service must expose 'id' field in album responses",
                response,
                containsString("\"id\"")
        );
    }
}
