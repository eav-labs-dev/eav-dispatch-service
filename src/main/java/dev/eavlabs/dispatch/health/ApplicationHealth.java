package dev.eavlabs.dispatch.health;

/**
 * Public health payload for reviewers and deployment probes.
 *
 * @param service stable service identifier
 * @param status service availability state
 * @param version application version
 */
public record ApplicationHealth(String service, String status, String version) {
}
