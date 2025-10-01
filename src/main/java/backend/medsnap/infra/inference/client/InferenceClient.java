package backend.medsnap.infra.inference.client;

import backend.medsnap.infra.inference.dto.response.InferenceResponse;

public interface InferenceClient {

    InferenceResponse verify(String imageUrl);
}
